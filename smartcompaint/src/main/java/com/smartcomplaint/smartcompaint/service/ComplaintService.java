package com.smartcomplaint.smartcompaint.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartcomplaint.smartcompaint.complaint.AiCategoryResponse;
import com.smartcomplaint.smartcompaint.complaint.AssignComplaintRequest;
import com.smartcomplaint.smartcompaint.complaint.CommentRequest;
import com.smartcomplaint.smartcompaint.complaint.CommentResponse;
import com.smartcomplaint.smartcompaint.complaint.ComplaintCreateRequest;
import com.smartcomplaint.smartcompaint.complaint.ComplaintResponse;
import com.smartcomplaint.smartcompaint.complaint.StatusUpdateRequest;
import com.smartcomplaint.smartcompaint.entity.AppUser;
import com.smartcomplaint.smartcompaint.entity.Complaint;
import com.smartcomplaint.smartcompaint.entity.ComplaintComment;
import com.smartcomplaint.smartcompaint.entity.ComplaintHistory;
import com.smartcomplaint.smartcompaint.enums.ComplaintCategory;
import com.smartcomplaint.smartcompaint.enums.ComplaintStatus;
import com.smartcomplaint.smartcompaint.enums.NotificationType;
import com.smartcomplaint.smartcompaint.enums.Role;
import com.smartcomplaint.smartcompaint.exception.BadRequestException;
import com.smartcomplaint.smartcompaint.exception.ResourceNotFoundException;
import com.smartcomplaint.smartcompaint.exception.UnauthorizedException;
import com.smartcomplaint.smartcompaint.repository.ComplaintCommentRepository;
import com.smartcomplaint.smartcompaint.repository.ComplaintHistoryRepository;
import com.smartcomplaint.smartcompaint.repository.ComplaintRepository;
import com.smartcomplaint.smartcompaint.repository.UserRepository;
import com.smartcomplaint.smartcompaint.util.ComplaintSpecifications;
import com.smartcomplaint.smartcompaint.util.DtoMapper;
import com.smartcomplaint.smartcompaint.websocket.WebSocketPublisher;

import java.util.List;
import java.util.UUID;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final ComplaintCommentRepository commentRepository;
    private final ComplaintHistoryRepository historyRepository;
    private final UserRepository userRepository;
    private final AiCategorizationService aiCategorizationService;
    private final FileStorageService fileStorageService;
    private final NotificationService notificationService;
    private final DtoMapper mapper;
    private final WebSocketPublisher webSocketPublisher;

    public ComplaintService(
            ComplaintRepository complaintRepository,
            ComplaintCommentRepository commentRepository,
            ComplaintHistoryRepository historyRepository,
            UserRepository userRepository,
            AiCategorizationService aiCategorizationService,
            FileStorageService fileStorageService,
            NotificationService notificationService,
            DtoMapper mapper,
            WebSocketPublisher webSocketPublisher
    ) {
        this.complaintRepository = complaintRepository;
        this.commentRepository = commentRepository;
        this.historyRepository = historyRepository;
        this.userRepository = userRepository;
        this.aiCategorizationService = aiCategorizationService;
        this.fileStorageService = fileStorageService;
        this.notificationService = notificationService;
        this.mapper = mapper;
        this.webSocketPublisher = webSocketPublisher;
    }

    @Transactional
    public ComplaintResponse create(ComplaintCreateRequest request, AppUser user) {
        AiCategoryResponse ai = aiCategorizationService.suggest(request.title(), request.description(), request.location());
        ComplaintCategory category = request.category() == null ? ai.suggestedCategory() : request.category();

        Complaint complaint = new Complaint();
        complaint.setTitle(request.title());
        complaint.setDescription(request.description());
        complaint.setCategory(category);
        complaint.setPriority(ai.priority());
        complaint.setDepartment(ai.department());
        complaint.setLocation(request.location());
        complaint.setImageUrl(fileStorageService.storeImage(request.image()));
        complaint.setCreatedBy(user);

        Complaint saved = complaintRepository.save(complaint);
        addHistory(saved, null, ComplaintStatus.RAISED, user, "Complaint raised");
        webSocketPublisher.complaintEvent(saved.getId(), "COMPLAINT_RAISED", "New complaint raised: " + saved.getTitle());
        return response(saved);
    }

    public AiCategoryResponse suggest(String title, String description, String location) {
        return aiCategorizationService.suggest(title, description, location);
    }

    @Transactional(readOnly = true)
    public ComplaintResponse get(UUID id, AppUser user) {
        Complaint complaint = findComplaint(id);
        assertCanView(complaint, user);
        return response(complaint);
    }

    @Transactional(readOnly = true)
    public Page<ComplaintResponse> search(
            String keyword,
            ComplaintStatus status,
            ComplaintCategory category,
            String department,
            Pageable pageable,
            AppUser user
    ) {
        Specification<Complaint> spec = ComplaintSpecifications.search(keyword, status, category, department)
                .and(visibilitySpec(user));
        return complaintRepository.findAll(spec, pageable).map(this::response);
    }

    @Transactional(readOnly = true)
    public List<ComplaintResponse> byDepartment(String department, AppUser user) {
        if (user.getRole() == Role.USER) {
            throw new UnauthorizedException("Only staff, department heads, and admins can view department queues");
        }
        return complaintRepository.findTop20ByDepartmentOrderByCreatedAtDesc(department)
                .stream()
                .map(this::response)
                .toList();
    }

    @Transactional
    public ComplaintResponse updateStatus(UUID id, StatusUpdateRequest request, AppUser actor) {
        Complaint complaint = findComplaint(id);
        assertCanMutate(complaint, actor);
        ComplaintStatus from = complaint.getStatus();
        validateStatusFlow(from, request.status(), actor);

        complaint.setStatus(request.status());
        Complaint saved = complaintRepository.save(complaint);
        String note = request.note() == null || request.note().isBlank()
                ? "Status changed to " + request.status()
                : request.note();
        addHistory(saved, from, request.status(), actor, note);
        notificationService.notify(saved.getCreatedBy(), saved, NotificationType.STATUS_CHANGED, note);
        webSocketPublisher.complaintEvent(saved.getId(), "STATUS_CHANGED", note);
        return response(saved);
    }

    @Transactional
    public ComplaintResponse assign(UUID id, AssignComplaintRequest request, AppUser actor) {
        Complaint complaint = findComplaint(id);
        if (actor.getRole() != Role.ADMIN && actor.getRole() != Role.DEPARTMENT_HEAD) {
            throw new UnauthorizedException("Only admins and department heads can assign complaints");
        }

        AppUser staff = null;
        if (request.staffId() != null) {
            staff = userRepository.findById(request.staffId())
                    .orElseThrow(() -> new ResourceNotFoundException("Staff user not found"));
            if (staff.getRole() != Role.STAFF && staff.getRole() != Role.DEPARTMENT_HEAD) {
                throw new BadRequestException("Assigned user must be STAFF or DEPARTMENT_HEAD");
            }
        }

        ComplaintStatus from = complaint.getStatus();
        complaint.setDepartment(request.department());
        complaint.setAssignedTo(staff);
        complaint.setStatus(ComplaintStatus.ASSIGNED);
        Complaint saved = complaintRepository.save(complaint);

        String note = "Assigned to " + request.department() + (staff == null ? "" : " / " + staff.getFullName());
        addHistory(saved, from, ComplaintStatus.ASSIGNED, actor, note);
        notificationService.notify(saved.getCreatedBy(), saved, NotificationType.COMPLAINT_ASSIGNED, note);
        if (staff != null) {
            notificationService.notify(staff, saved, NotificationType.COMPLAINT_ASSIGNED, "You were assigned complaint " + saved.getTitle());
        }
        webSocketPublisher.complaintEvent(saved.getId(), "COMPLAINT_ASSIGNED", note);
        return response(saved);
    }

    @Transactional
    public CommentResponse addComment(UUID id, CommentRequest request, AppUser actor) {
        Complaint complaint = findComplaint(id);
        assertCanView(complaint, actor);

        ComplaintComment comment = new ComplaintComment();
        comment.setComplaint(complaint);
        comment.setAuthor(actor);
        comment.setMessage(request.message());
        ComplaintComment saved = commentRepository.save(comment);

        if (!actor.getId().equals(complaint.getCreatedBy().getId())) {
            notificationService.notify(complaint.getCreatedBy(), complaint, NotificationType.COMMENT_ADDED, "New comment on your complaint");
        }
        webSocketPublisher.complaintEvent(complaint.getId(), "COMMENT_ADDED", "New comment added");
        return mapper.toCommentResponse(saved);
    }

    private Complaint findComplaint(UUID id) {
        return complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found"));
    }

    private ComplaintResponse response(Complaint complaint) {
        return mapper.toComplaintResponse(
                complaint,
                commentRepository.findByComplaintOrderByCreatedAtAsc(complaint),
                historyRepository.findByComplaintOrderByCreatedAtAsc(complaint)
        );
    }

    private void addHistory(Complaint complaint, ComplaintStatus from, ComplaintStatus to, AppUser actor, String note) {
        ComplaintHistory history = new ComplaintHistory();
        history.setComplaint(complaint);
        history.setFromStatus(from);
        history.setToStatus(to);
        history.setChangedBy(actor);
        history.setNote(note);
        historyRepository.save(history);
    }

    private Specification<Complaint> visibilitySpec(AppUser user) {
        return (root, query, cb) -> {
            if (user.getRole() == Role.ADMIN || user.getRole() == Role.DEPARTMENT_HEAD || user.getRole() == Role.STAFF) {
                return cb.conjunction();
            }
            return cb.equal(root.get("createdBy").get("id"), user.getId());
        };
    }

    private void assertCanView(Complaint complaint, AppUser user) {
        if (user.getRole() == Role.USER && !complaint.getCreatedBy().getId().equals(user.getId())) {
            throw new UnauthorizedException("You can view only your own complaints");
        }
    }

    private void assertCanMutate(Complaint complaint, AppUser user) {
        if (user.getRole() == Role.ADMIN || user.getRole() == Role.DEPARTMENT_HEAD || user.getRole() == Role.STAFF) {
            return;
        }
        if (complaint.getCreatedBy().getId().equals(user.getId()) && complaint.getStatus() == ComplaintStatus.RESOLVED) {
            return;
        }
        throw new UnauthorizedException("You are not allowed to update this complaint");
    }

    private void validateStatusFlow(ComplaintStatus from, ComplaintStatus to, AppUser actor) {
        if (from == to) {
            return;
        }
        boolean valid = switch (from) {
            case RAISED -> to == ComplaintStatus.VERIFIED || to == ComplaintStatus.ASSIGNED;
            case VERIFIED -> to == ComplaintStatus.ASSIGNED;
            case ASSIGNED -> to == ComplaintStatus.IN_PROGRESS;
            case IN_PROGRESS -> to == ComplaintStatus.RESOLVED;
            case RESOLVED -> to == ComplaintStatus.CLOSED || to == ComplaintStatus.IN_PROGRESS;
            case CLOSED -> false;
        };
        if (!valid && actor.getRole() != Role.ADMIN) {
            throw new BadRequestException("Invalid status transition from " + from + " to " + to);
        }
    }
}
