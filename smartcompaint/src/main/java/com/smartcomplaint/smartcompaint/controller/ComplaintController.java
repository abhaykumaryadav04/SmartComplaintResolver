package com.smartcomplaint.smartcompaint.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.smartcomplaint.smartcompaint.complaint.AiCategoryResponse;
import com.smartcomplaint.smartcompaint.complaint.AssignComplaintRequest;
import com.smartcomplaint.smartcompaint.complaint.CommentRequest;
import com.smartcomplaint.smartcompaint.complaint.CommentResponse;
import com.smartcomplaint.smartcompaint.complaint.ComplaintCreateRequest;
import com.smartcomplaint.smartcompaint.complaint.ComplaintResponse;
import com.smartcomplaint.smartcompaint.complaint.StatusUpdateRequest;
import com.smartcomplaint.smartcompaint.entity.AppUser;
import com.smartcomplaint.smartcompaint.enums.ComplaintCategory;
import com.smartcomplaint.smartcompaint.enums.ComplaintStatus;
import com.smartcomplaint.smartcompaint.service.ComplaintService;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/complaints")
@Tag(name = "Complaints")
@CrossOrigin(origins = "")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ComplaintResponse create(
            @Valid @ModelAttribute ComplaintCreateRequest request,
            @AuthenticationPrincipal AppUser user
    ) {
        return complaintService.create(request, user);
    }

    @GetMapping
    public Page<ComplaintResponse> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) ComplaintStatus status,
            @RequestParam(required = false) ComplaintCategory category,
            @RequestParam(required = false) String department,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable,
            @AuthenticationPrincipal AppUser user
    ) {
        return complaintService.search(keyword, status, category, department, pageable, user);
    }

    @GetMapping("/{id}")
    public ComplaintResponse get(@PathVariable UUID id, @AuthenticationPrincipal AppUser user) {
        return complaintService.get(id, user);
    }

    @PutMapping("/{id}/status")
    public ComplaintResponse updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody StatusUpdateRequest request,
            @AuthenticationPrincipal AppUser user
    ) {
        return complaintService.updateStatus(id, request, user);
    }

    @PutMapping("/{id}/assign")
    public ComplaintResponse assign(
            @PathVariable UUID id,
            @Valid @RequestBody AssignComplaintRequest request,
            @AuthenticationPrincipal AppUser user
    ) {
        return complaintService.assign(id, request, user);
    }

    @PostMapping("/{id}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse comment(
            @PathVariable UUID id,
            @Valid @RequestBody CommentRequest request,
            @AuthenticationPrincipal AppUser user
    ) {
        return complaintService.addComment(id, request, user);
    }

    @GetMapping("/department/{department}")
    public List<ComplaintResponse> byDepartment(
            @PathVariable String department,
            @AuthenticationPrincipal AppUser user
    ) {
        return complaintService.byDepartment(department, user);
    }

    @GetMapping("/ai-suggest")
    public AiCategoryResponse suggest(
            @RequestParam @NotBlank @Size(min = 5) String title,
            @RequestParam @NotBlank @Size(min = 20) String description,
            @RequestParam(defaultValue = "") String location
    ) {
        return complaintService.suggest(title, description, location);
    }
}
