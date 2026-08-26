package com.agroo.agroo.dto.request;

import com.agroo.agroo.model.enums.AlertType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    private AlertType alertType;
    private String location;
    private Boolean isUrgent;
    private String expiresAt;
}