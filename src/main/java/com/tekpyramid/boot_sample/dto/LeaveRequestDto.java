package com.tekpyramid.boot_sample.dto;

import com.tekpyramid.boot_sample.entity.LeaveType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class LeaveRequestDto {

    @NotNull(message = "From date is required")
    @FutureOrPresent(message = "From date cannot be in the past")
    private LocalDate fromDate;

    @NotNull(message = "To date is required")
    private LocalDate toDate;

    @NotNull(message = "Leave type is required")
    private LeaveType leaveType;

    private String reason;

    @NotNull(message = "Employee id is required")
    private Integer employeeId;
}
