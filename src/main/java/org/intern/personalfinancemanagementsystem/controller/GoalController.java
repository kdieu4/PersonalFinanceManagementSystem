package org.intern.personalfinancemanagementsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.intern.personalfinancemanagementsystem.base.ApiResponse;
import org.intern.personalfinancemanagementsystem.base.RestApiV1;
import org.intern.personalfinancemanagementsystem.constant.SuccessMessage;
import org.intern.personalfinancemanagementsystem.constant.UrlConstant;
import org.intern.personalfinancemanagementsystem.domain.dto.request.GoalRequest;
import org.intern.personalfinancemanagementsystem.domain.dto.response.GoalResponse;
import org.intern.personalfinancemanagementsystem.domain.dto.response.PageResponse;
import org.intern.personalfinancemanagementsystem.security.CustomUserDetails;
import org.intern.personalfinancemanagementsystem.service.GoalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestApiV1
@Validated
@Tag(name = "goals")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GoalController {
    GoalService goalService;

    @Operation(summary = "Them muc tieu")
    @PostMapping(UrlConstant.Goal.PREFIX)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<UUID>> addGoal(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody GoalRequest request
    ) {
        UUID response = goalService.addGoal(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(SuccessMessage.Goal.ADD_GOAL_SUCCESSFULLY, response));
    }

    @Operation(summary = "Lay danh sach muc tieu")
    @GetMapping(UrlConstant.Goal.PREFIX)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<PageResponse<List<GoalResponse>>>> getAllGoal(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam(defaultValue = "0") @Min(0) int pageNo,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int pageSize
    ) {
        PageResponse<List<GoalResponse>> response = goalService.getAllGoal(principal.getId(), pageNo, pageSize);
        return ResponseEntity.ok(ApiResponse.success(SuccessMessage.Goal.GET_ALL_GOALS_SUCCESSFULLY, response));
    }

    @Operation(summary = "Xem chi tiet muc tieu")
    @GetMapping(UrlConstant.Goal.BY_ID)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<GoalResponse>> getGoalById(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable UUID goalId
    ) {
        GoalResponse response = goalService.getGoalById(principal.getId(), goalId);
        return ResponseEntity.ok(ApiResponse.success(SuccessMessage.Goal.GET_DETAIL_GOAL_SUCCESSFULLY, response));
    }

    @Operation(summary = "Sua muc tieu")
    @PutMapping(UrlConstant.Goal.BY_ID)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<Void>> updateGoalById(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable UUID goalId,
            @Valid @RequestBody GoalRequest request
    ) {
        goalService.updateGoalById(principal.getId(), goalId, request);
        return ResponseEntity.ok(ApiResponse.success(SuccessMessage.Goal.UPDATE_GOAL_SUCCESSFULLY, null));
    }

    @Operation(summary = "Xoa muc tieu")
    @DeleteMapping(UrlConstant.Goal.BY_ID)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<Void>> deleteGoalById(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable UUID goalId
    ) {
        goalService.softDeleteGoalById(principal.getId(), goalId);
        return ResponseEntity.ok(ApiResponse.success(SuccessMessage.Goal.DELETE_GOAL_SUCCESSFULLY, null));
    }
}
