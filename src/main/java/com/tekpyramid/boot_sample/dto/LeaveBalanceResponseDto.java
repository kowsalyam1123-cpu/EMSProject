package com.tekpyramid.boot_sample.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LeaveBalanceResponseDto {

    private Integer employeeId;

    private Integer paidLeave;

    private Integer sickLeave;

    private Integer casualLeave;

    private Integer totalLeave;
}
