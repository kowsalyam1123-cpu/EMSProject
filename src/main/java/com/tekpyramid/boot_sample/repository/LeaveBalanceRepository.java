package com.tekpyramid.boot_sample.repository;

import com.tekpyramid.boot_sample.entity.LeaveBalance;
import com.tekpyramid.boot_sample.entity.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Integer> {

    Optional<LeaveBalance> findByEmployeeIdAndLeaveType(
            Integer employeeId,
            LeaveType leaveType);
}