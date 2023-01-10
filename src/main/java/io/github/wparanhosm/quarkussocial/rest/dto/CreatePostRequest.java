package io.github.wparanhosm.quarkussocial.rest.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreatePostRequest {

    @NotBlank(message = "Text is required")
    private String text;
}
