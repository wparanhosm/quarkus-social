package io.github.wparanhosm.quarkussocial.rest.dto;

import io.github.wparanhosm.quarkussocial.domain.model.Follower;
import io.github.wparanhosm.quarkussocial.domain.model.User;
import io.quarkus.arc.All;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class FollowerResponse {
    private Long id;
    private String name;

    public FollowerResponse(Follower follower){
        this(follower.getId(),follower.getFollower().getName());
    }
}
