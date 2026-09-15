package com.tekpyramid.boot_sample.dto;

import com.tekpyramid.boot_sample.entity.LeaveType;
import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LeaveResponseDto {

    private Integer leaveId;
    private LocalDate fromDate;
    private LocalDate toDate;
    private LeaveType leaveType;
    private String reason;
    private String status;
    private Integer employeeId;
    private String employeeName;
}
