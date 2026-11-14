package com.iharshashkou.internetstore.controller;

import com.iharshashkou.internetstore.DTO.customDesign.*;
import com.iharshashkou.internetstore.repository.UserRepository;
import com.iharshashkou.internetstore.service.customDesign.CustomDesignService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/custom-designs")
@RequiredArgsConstructor
public class CustomDesignController {

    private final CustomDesignService customDesignService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<CreateCustomDesignResponse> createDesign(
            @Valid @RequestBody CreateCustomDesignRequest request,
            Authentication authentication
    ) {
        Long userId = isAuthenticatedUser(authentication)
                ? getUserId(authentication)
                : null;

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(customDesignService.createDesign(userId, request));
    }

    @GetMapping("/guest/{designId}")
    public ResponseEntity<CustomDesignResponse> getGuestDesign(
            @PathVariable Long designId,
            @RequestHeader("X-Guest-Token") String guestAccessToken
    ) {
        return ResponseEntity.ok(
                customDesignService.getGuestDesign(
                        designId,
                        guestAccessToken
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<CustomDesignResponse>> getUserDesigns(
            Authentication authentication
    ) {
        Long userId = getUserId(authentication);

        return ResponseEntity.ok(
                customDesignService.getUserDesigns(userId)
        );
    }

    @GetMapping("/{designId}")
    public ResponseEntity<CustomDesignResponse> getUserDesign(
            @PathVariable Long designId,
            Authentication authentication
    ) {
        Long userId = getUserId(authentication);

        return ResponseEntity.ok(
                customDesignService.getUserDesign(userId, designId)
        );
    }

    @PostMapping("/{designId}/submit")
    public ResponseEntity<CustomDesignResponse> submitDesign(
            @PathVariable Long designId,
            @RequestHeader(value = "X-Guest-Token", required = false)
            String guestAccessToken,
            Authentication authentication
    ) {
        Long userId = isAuthenticatedUser(authentication)
                ? getUserId(authentication)
                : null;

        return ResponseEntity.ok(
                customDesignService.submitDesign(
                        designId,
                        userId,
                        guestAccessToken
                )
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<List<CustomDesignAdminResponse>> getAllDesigns() {
        return ResponseEntity.ok(
                customDesignService.getAllDesigns()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/admin/{designId}/review")
    public ResponseEntity<CustomDesignAdminResponse> reviewDesign(
            @PathVariable Long designId,
            @Valid @RequestBody ReviewCustomDesignRequest request
    ) {
        return ResponseEntity.ok(
                customDesignService.reviewDesign(designId, request)
        );
    }

    private boolean isAuthenticatedUser(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }

    private Long getUserId(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Пользователь не найден"
                ))
                .getId();
    }
}
