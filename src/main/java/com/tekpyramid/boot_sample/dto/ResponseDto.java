package com.tekpyramid.boot_sample.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseDto {
    private Boolean error;
    private String message;
    private Object data;
}
