package com.tekpyramid.boot_sample.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "leave_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Leave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "from_data", nullable = false)
    private LocalDate fromDate;

    @Column(name = "to_data", nullable = false)
    private LocalDate toDate;

    @Enumerated(EnumType.STRING)
    private LeaveType leaveType;

    @Column(length = 500)
    private String reason;

    @Column(nullable = false)
    private String status;

    // @ManyToOne(fetch = FetchType.LAZY)
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
}