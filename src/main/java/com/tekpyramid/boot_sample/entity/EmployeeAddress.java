package com.tekpyramid.boot_sample.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeAddress {
    private String city;
    private String state;
    private String country;

    public void setEmployee(Employee employee) {
    }

    public Object getAd_id() {
        return null;
    }

    public void setAd_id(Object adId) {
    }
}