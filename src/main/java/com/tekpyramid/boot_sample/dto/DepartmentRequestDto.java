package com.tekpyramid.boot_sample.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartmentRequestDto {

    @NotBlank(message = "Department name is required")
    private String departmentName;
}
