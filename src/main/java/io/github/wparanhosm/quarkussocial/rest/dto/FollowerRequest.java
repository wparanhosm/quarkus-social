package io.github.wparanhosm.quarkussocial.rest.dto;


import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class FollowerRequest {

    @NotNull(message = "followerId is required")
    private Long followerId;
}
