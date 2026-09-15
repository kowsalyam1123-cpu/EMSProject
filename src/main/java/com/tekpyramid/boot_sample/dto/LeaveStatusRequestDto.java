package com.tekpyramid.boot_sample.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class LeaveStatusRequestDto {

    @NonNull
    private String status;
}