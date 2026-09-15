package com.tekpyramid.boot_sample.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LeavePageResponse {

    private List<LeaveResponseDto> content;

    private int page;

    private int size;

    private long totalElements;

    private int totalPages;
}