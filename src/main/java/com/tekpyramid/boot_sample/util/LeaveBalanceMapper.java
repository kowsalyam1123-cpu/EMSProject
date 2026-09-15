package com.tekpyramid.boot_sample.util;

import com.tekpyramid.boot_sample.entity.Employee;
import com.tekpyramid.boot_sample.entity.LeaveBalance;
import com.tekpyramid.boot_sample.entity.LeaveType;

public class LeaveBalanceMapper {

    public static LeaveBalance toEntity(
            Employee employee,
            LeaveType leaveType,
            Integer remainingDays) {

        LeaveBalance balance = new LeaveBalance();

        balance.setEmployee(employee);
        balance.setLeaveType(leaveType);
        balance.setRemainingDays(remainingDays);

        return balance;
    }
}