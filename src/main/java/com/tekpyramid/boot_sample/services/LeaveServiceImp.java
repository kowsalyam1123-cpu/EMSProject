package com.tekpyramid.boot_sample.services;

import com.tekpyramid.boot_sample.dto.*;
import com.tekpyramid.boot_sample.entity.Employee;
import com.tekpyramid.boot_sample.entity.Leave;
import com.tekpyramid.boot_sample.entity.LeaveType;
import com.tekpyramid.boot_sample.exception.DataNotFoundException;
import com.tekpyramid.boot_sample.repository.EmployeeRepository;
import com.tekpyramid.boot_sample.repository.LeaveRepository;
import com.tekpyramid.boot_sample.util.LeaveMapper;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tekpyramid.boot_sample.entity.LeaveBalance;
import com.tekpyramid.boot_sample.repository.LeaveBalanceRepository;
import com.tekpyramid.boot_sample.util.LeaveBalanceMapper;

import java.util.ArrayList;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class LeaveServiceImp implements LeaveService {

    private final LeaveRepository leaveRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;

//    public LeaveServiceImp(
//            LeaveRepository leaveRepository,
//            EmployeeRepository employeeRepository) {
//
//        this.leaveRepository = leaveRepository;
//        this.employeeRepository = employeeRepository;
//    }


    public LeaveServiceImp(
            LeaveRepository leaveRepository,
            EmployeeRepository employeeRepository,
            LeaveBalanceRepository leaveBalanceRepository) {

        this.leaveRepository = leaveRepository;
        this.employeeRepository = employeeRepository;
        this.leaveBalanceRepository = leaveBalanceRepository;
    }


    // =========================================================
    // APPLY LEAVE
    // =========================================================

    @Override
    @Transactional
    public ResponseDto applyLeave(LeaveRequestDto dto) {

        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() ->
                        new DataNotFoundException("Employee Not Found"));

        if (dto.getToDate().isBefore(dto.getFromDate())) {

            throw new IllegalArgumentException(
                    "To date cannot be before from date");
        }

        long days = ChronoUnit.DAYS.between(
                dto.getFromDate(),
                dto.getToDate()
        ) + 1;

        checkLeaveBalance(
                employee,
                dto.getLeaveType(),
                days
        );

        Leave leave = LeaveMapper.dtoToEntity(dto);

        leave.setStatus("PENDING");

        leave.setEmployee(employee);

        Leave savedLeave = leaveRepository.save(leave);

        return ResponseDto.builder()
                .error(false)
                .message("Leave applied successfully")
                .data(LeaveMapper.entityToDto(savedLeave))
                .build();
    }

    // =========================================================
    // CHECK LEAVE BALANCE
    // =========================================================

    private void checkLeaveBalance(
            Employee employee,
            LeaveType leaveType,
            long days) {

        switch (leaveType) {

            case SICK_LEAVE:

                if (employee.getSickLeave() < days) {

                    throw new IllegalArgumentException(
                            "Insufficient sick leave balance. Available: "
                                    + employee.getSickLeave()
                                    + ", Requested: "
                                    + days
                    );
                }

                break;

            case CASUAL_LEAVE:

                if (employee.getCasualLeave() < days) {

                    throw new IllegalArgumentException(
                            "Insufficient casual leave balance. Available: "
                                    + employee.getCasualLeave()
                                    + ", Requested: "
                                    + days
                    );
                }

                break;

            case PAID_LEAVE:

                if (employee.getPaidLeave() < days) {

                    throw new IllegalArgumentException(
                            "Insufficient paid leave balance. Available: "
                                    + employee.getPaidLeave()
                                    + ", Requested: "
                                    + days
                    );
                }

                break;

            case UNPAID_LEAVE:

                // Unpaid leave does not have a balance
                break;
        }
    }

    // =========================================================
    // UPDATE LEAVE STATUS
    // =========================================================

    @Override
    @Transactional
    public ResponseDto updateLeaveStatus(
            Integer leaveId,
            LeaveStatusRequestDto requestDto) {

        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() ->
                        new DataNotFoundException(
                                "Leave not found"));

        String oldStatus = leave.getStatus();

        String newStatus =
                requestDto.getStatus().toUpperCase();

        if (!newStatus.equals("APPROVED")
                && !newStatus.equals("REJECTED")) {

            throw new IllegalArgumentException(
                    "Status must be APPROVED or REJECTED");
        }

        if (oldStatus.equals("APPROVED")
                || oldStatus.equals("REJECTED")) {

            throw new IllegalArgumentException(
                    "Leave is already " + oldStatus);
        }

        Employee employee = leave.getEmployee();

        long days = ChronoUnit.DAYS.between(
                leave.getFromDate(),
                leave.getToDate()
        ) + 1;

        // =====================================================
        // IF ADMIN APPROVES
        // =====================================================

        if (newStatus.equals("APPROVED")) {

            checkLeaveBalance(
                    employee,
                    leave.getLeaveType(),
                    days
            );

            deductLeaveBalance(
                    employee,
                    leave.getLeaveType(),
                    days
            );

            employeeRepository.save(employee);
        }

        // =====================================================
        // UPDATE LEAVE STATUS
        // =====================================================

        leave.setStatus(newStatus);

        Leave updatedLeave =
                leaveRepository.save(leave);

        return ResponseDto.builder()
                .error(false)
                .message("Leave status updated successfully")
                .data(LeaveMapper.entityToDto(updatedLeave))
                .build();
    }

    // =========================================================
    // DEDUCT LEAVE BALANCE
    // =========================================================

    private void deductLeaveBalance(
            Employee employee,
            LeaveType leaveType,
            long days) {

        int leaveDays = (int) days;

        switch (leaveType) {

            case SICK_LEAVE:

                employee.setSickLeave(
                        employee.getSickLeave() - leaveDays
                );

                break;

            case CASUAL_LEAVE:

                employee.setCasualLeave(
                        employee.getCasualLeave() - leaveDays
                );

                break;

            case PAID_LEAVE:

                employee.setPaidLeave(
                        employee.getPaidLeave() - leaveDays
                );

                break;

            case UNPAID_LEAVE:

                // No balance deduction
                break;
        }
    }

    // =========================================================
    // GET LEAVE BALANCE
    // =========================================================

    @Override
    public ResponseDto getLeaveBalance(Integer employeeId) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() ->
                        new DataNotFoundException(
                                "Employee Not Found"));

        int totalLeave =
                employee.getPaidLeave()
                        + employee.getSickLeave()
                        + employee.getCasualLeave();

        LeaveBalanceResponseDto response =
                LeaveBalanceResponseDto.builder()
                        .employeeId(employee.getId())
                        .paidLeave(employee.getPaidLeave())
                        .sickLeave(employee.getSickLeave())
                        .casualLeave(employee.getCasualLeave())
                        .totalLeave(totalLeave)
                        .build();

        return ResponseDto.builder()
                .error(false)
                .message("Leave balance fetched successfully")
                .data(response)
                .build();
    }

    // =========================================================
    // GET ALL LEAVES
    // =========================================================

    @Override
    public ResponseDto getAllLeaves(
            String search,
            Pageable pageable) {

        Page<Leave> leavePage;

        if (search == null || search.isBlank()) {

            leavePage =
                    leaveRepository.findAll(pageable);

        } else {

            leavePage =
                    leaveRepository
                            .findByEmployee_NameContainingIgnoreCase(
                                    search.trim(),
                                    pageable
                            );
        }

        List<LeaveResponseDto> content =
                leavePage.getContent()
                        .stream()
                        .map(LeaveMapper::entityToDto)
                        .toList();

        LeavePageResponse response =
                new LeavePageResponse();

        response.setContent(content);

        response.setPage(
                leavePage.getNumber()
        );

        response.setSize(
                leavePage.getSize()
        );

        response.setTotalElements(
                leavePage.getTotalElements()
        );

        response.setTotalPages(
                leavePage.getTotalPages()
        );

        return ResponseDto.builder()
                .error(false)
                .message("Leave fetched successfully")
                .data(response)
                .build();
    }

    // =========================================================
    // CANCEL LEAVE
    // =========================================================

    @Override
    public ResponseDto cancelLeave(
            Integer employeeId,
            Integer leaveId) {

        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() ->
                        new DataNotFoundException(
                                "Leave is not there"));

        if (leave.getEmployee().getId()
                != employeeId) {

            throw new DataNotFoundException(
                    "Leave does not belong to this employee");
        }

        if (!leave.getStatus()
                .equals("PENDING")) {

            throw new DataNotFoundException(
                    "Leave is not in pending status");
        }

        leaveRepository.delete(leave);

        return ResponseDto.builder()
                .error(false)
                .message("Leave cancelled successfully")
                .data(LeaveMapper.entityToDto(leave))
                .build();
    }



    @Override

    public ResponseDto initializeLeaveBalances() {

        List<Employee> employees = employeeRepository.findAll();

        List<LeaveBalance> balances = new ArrayList<>();

        for (Employee employee : employees) {

            // PAID LEAVE
            LeaveBalance paid =
                    LeaveBalanceMapper.toEntity(
                            employee,
                            LeaveType.PAID_LEAVE,
                            12
                    );

            // SICK LEAVE
            LeaveBalance sick =
                    LeaveBalanceMapper.toEntity(
                            employee,
                            LeaveType.SICK_LEAVE,
                            10
                    );

            // CASUAL LEAVE
            LeaveBalance casual =
                    LeaveBalanceMapper.toEntity(
                            employee,
                            LeaveType.CASUAL_LEAVE,
                            8
                    );

            balances.add(paid);
            balances.add(sick);
            balances.add(casual);
        }

        leaveBalanceRepository.saveAll(balances);

        return ResponseDto.builder()
                .error(false)
                .message("Leave balances initialized successfully")
                .data(balances.size() + " leave balance records created")
                .build();
    }
}

