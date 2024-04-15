package com.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UiSuccessContainer {
    /**
     * Request successful or not.
     */
    private boolean success;

    /**
     * Error message.
     */
    private String message;
}
