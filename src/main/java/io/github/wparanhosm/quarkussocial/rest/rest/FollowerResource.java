package io.github.wparanhosm.quarkussocial.rest.rest;

import io.github.wparanhosm.quarkussocial.domain.model.Follower;
import io.github.wparanhosm.quarkussocial.domain.model.User;
import io.github.wparanhosm.quarkussocial.domain.model.repository.FollowerRepository;
import io.github.wparanhosm.quarkussocial.rest.dto.*;
import io.github.wparanhosm.quarkussocial.rest.interceptor.ValidateUserIdEntity;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Path("/users/{userId}/follower")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FollowerResource {
    private FollowerRepository repository;
    private Validator validator;

    @Inject
    public FollowerResource(FollowerRepository repository, Validator validator) {

        this.repository = repository;
        this.validator = validator;

    }


    @ValidateUserIdEntity(names = {"userId"})
    @PUT
    @Transactional
    public Response followUser(@PathParam("userId") Long userId, FollowerRequest request){


        Set<ConstraintViolation<FollowerRequest>> violations = validator.validate(request);
        if(!violations.isEmpty())
            return ResponseError.createFromValidation(violations).withStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);


        if(Objects.equals(userId, request.getFollowerId()))
            return ResponseError
                    .createFromSingleFieldError("Conflict",
                                                    new FieldError("id",
                                                            "You can't follow yourself"
                                                    ))
                    .withStatusCode(Response.Status.CONFLICT.getStatusCode());

        User user = User.findById(userId);

        User userFollower = User.findById(request.getFollowerId());

        if(userFollower == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }


        if(!repository.follows(userFollower,user)){

            Follower follower = new Follower();

            follower.setUser(user);
            follower.setFollower(userFollower);

            repository.persist(follower);

        }

        return Response.status(Response.Status.NO_CONTENT).build();

    }


    @ValidateUserIdEntity(names = {"userId"})
    @GET
    public Response listFollowers(@PathParam("userId") Long userId){
        var list = repository.findByUser(userId);

        FollowerPerUserResponse response = new FollowerPerUserResponse();
        List<FollowerResponse> followerResponseList = list.stream().map(FollowerResponse::new).collect(Collectors.toList());

        response.setFollowersCount(list.size());
        response.setContent(followerResponseList);

        return  Response.status(Response.Status.OK).entity(response).build();
    }

    @ValidateUserIdEntity(names = {"userId"})
    @DELETE
    @Transactional
    public  Response unfollowUser(@PathParam("userId") Long userId, @QueryParam("followerId") Long followerId){

       User follower = User.findById(followerId);

       if(follower == null)
           return Response.status(Response.Status.NOT_FOUND).build();

        repository.deleteByFollowerAndUser(followerId, userId);
        return  Response.status(Response.Status.NO_CONTENT).build();

    }
}
