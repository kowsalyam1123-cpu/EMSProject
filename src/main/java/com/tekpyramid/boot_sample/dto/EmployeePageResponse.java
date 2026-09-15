package com.tekpyramid.boot_sample.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeePageResponse {

    private List<EmployeeResponseDto> content;

    private int page;

    private int size;

    private long totalElements;

    private int totalPages;
}