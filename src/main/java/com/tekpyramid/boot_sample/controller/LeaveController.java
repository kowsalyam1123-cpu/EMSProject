package com.tekpyramid.boot_sample.controller;

import com.tekpyramid.boot_sample.dto.*;
import com.tekpyramid.boot_sample.services.LeaveService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/leaves")
public class LeaveController {

    private final LeaveService leaveService;

    public LeaveController(
            LeaveService leaveService) {

        this.leaveService = leaveService;
    }

    @PostMapping
    public ResponseDto applyLeave(
            @Valid
            @RequestBody LeaveRequestDto dto) {

        return leaveService.applyLeave(dto);
    }

    @GetMapping
    public ResponseDto getAllLeaves(
            @RequestParam(required = false)
            String search,

            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.ASC
            )
            Pageable pageable) {

        return leaveService.getAllLeaves(
                search,
                pageable
        );
    }

    @PutMapping("/{leaveId}/status")
    public ResponseDto updateLeaveStatus(
            @PathVariable Integer leaveId,

            @Valid
            @RequestBody LeaveStatusRequestDto dto) {

        return leaveService.updateLeaveStatus(
                leaveId,
                dto
        );
    }

    @PutMapping("/{leaveId}/cancel")
    public ResponseDto cancelLeave(
            @PathVariable Integer leaveId,

            @RequestParam Integer employeeId) {

        return leaveService.cancelLeave(
                employeeId,
                leaveId
        );
    }

    @GetMapping("/balance/{employeeId}")
    public ResponseDto getLeaveBalance(
            @PathVariable Integer employeeId) {

        return leaveService.getLeaveBalance(
                employeeId
        );
    }

    @PostMapping("/balance/initialize")
    public ResponseDto initializeLeaveBalances() {
        return leaveService.initializeLeaveBalances();
    }
}