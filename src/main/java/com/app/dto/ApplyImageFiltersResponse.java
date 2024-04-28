package com.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ApplyImageFiltersResponse {
    /**
     * ИД запроса в системе.
     */
    private String requestId;
}
