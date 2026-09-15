package com.tekpyramid.boot_sample.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EmployeeRequestV3 {

    private String name;

    private String email;

    private Integer age;

    private String departmentName;

    private String city;

    private String state;

    private String country;



}