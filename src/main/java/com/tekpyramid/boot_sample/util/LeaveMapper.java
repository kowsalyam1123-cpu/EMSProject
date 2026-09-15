package com.tekpyramid.boot_sample.util;

import com.tekpyramid.boot_sample.dto.LeaveRequestDto;
import com.tekpyramid.boot_sample.dto.LeaveResponseDto;
import com.tekpyramid.boot_sample.entity.Employee;
import com.tekpyramid.boot_sample.entity.Leave;
;
public class LeaveMapper {


    public static Leave dtoToEntity(LeaveRequestDto dto) {
        Leave leave = new Leave();
        leave.setFromDate(dto.getFromDate());
        leave.setToDate(dto.getToDate());
        leave.setLeaveType(dto.getLeaveType());
        leave.setReason(dto.getReason());
        return leave;
    }

    public static LeaveResponseDto entityToDto(Leave leave) {
        LeaveResponseDto dto = new LeaveResponseDto();

        dto.setLeaveId(leave.getId());
        dto.setFromDate(leave.getFromDate());
        dto.setToDate(leave.getToDate());
        dto.setLeaveType(leave.getLeaveType());
        dto.setReason(leave.getReason());
        dto.setStatus(leave.getStatus());

        if (leave.getEmployee() != null) {
            Employee employee = leave.getEmployee();
            dto.setEmployeeId(employee.getId());
            dto.setEmployeeName(employee.getName());
        }

        return dto;
    }
}
