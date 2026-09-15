package com.tekpyramid.boot_sample.services;

import com.tekpyramid.boot_sample.dto.*;
import org.springframework.data.domain.Pageable;

public interface LeaveService {

    ResponseDto applyLeave(LeaveRequestDto leaveRequestDto);

//    ResponseDto getallLeaves(String search, Pageable pageable);

    ResponseDto updateLeaveStatus(
            Integer leaveId,
            LeaveStatusRequestDto requestDto
    );

    ResponseDto getLeaveBalance(Integer employeeId);

    ResponseDto getAllLeaves(
            String search,
            Pageable pageable
    );

    ResponseDto cancelLeave(
            Integer employeeId,
            Integer leaveId
    );


    ResponseDto initializeLeaveBalances();
}
