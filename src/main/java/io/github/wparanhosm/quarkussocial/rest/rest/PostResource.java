package io.github.wparanhosm.quarkussocial.rest.rest;


import io.github.wparanhosm.quarkussocial.domain.model.Follower;
import io.github.wparanhosm.quarkussocial.domain.model.Post;
import io.github.wparanhosm.quarkussocial.domain.model.User;
import io.github.wparanhosm.quarkussocial.domain.model.repository.FollowerRepository;
import io.github.wparanhosm.quarkussocial.domain.model.repository.PostRepository;
import io.github.wparanhosm.quarkussocial.rest.dto.CreatePostRequest;
import io.github.wparanhosm.quarkussocial.rest.dto.FieldError;
import io.github.wparanhosm.quarkussocial.rest.dto.PostResponse;
import io.github.wparanhosm.quarkussocial.rest.dto.ResponseError;
import io.github.wparanhosm.quarkussocial.rest.interceptor.ValidateUserIdEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Path("/users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class PostResource {


    private PostRepository repository;
    private Validator validator;
    private FollowerRepository followerRepository;

    @Inject
    public PostResource(PostRepository repository, Validator validator, FollowerRepository followerRepository){

        this.repository = repository;
        this.validator = validator;
        this.followerRepository = followerRepository;
    }

    @ValidateUserIdEntity(names = {"userId"})
    @POST
    @Transactional
    public Response savePost(@PathParam("userId") Long id, CreatePostRequest request){


        Set<ConstraintViolation<CreatePostRequest>> violations = validator.validate(request);
        if(!violations.isEmpty())
            return ResponseError.createFromValidation(violations).withStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);

        User user = User.findById(id);

        Post post = new Post();
        post.setText(request.getText());
        post.setUser(user);
        repository.persist(post);

        return Response.status(Response.Status.CREATED).build();
    }

    @ValidateUserIdEntity(names = {"userId"})
    @GET
    public Response listPosts(@PathParam("userId") Long id, @HeaderParam("followerId") Long followerId){


        if(followerId == null)
            return ResponseError
                    .createFromSingleFieldError(
                            "Header Request Error",
                            new FieldError(
                                    "followerId",
                                    "You need to declare the variable followerId on Header"))
                    .withStatusCode(Response.Status.BAD_REQUEST.getStatusCode());


        User user = User.findById(id);
        User follower = User.findById(followerId);

        if(follower == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        if(!followerRepository.follows(follower,user))
            return ResponseError
                    .createFromSingleFieldError(
                            "Follower Error",
                            new FieldError(
                                    "id",
                                    "You need to Follow this person to view his/her posts!"
                            ))
                    .withStatusCode(Response.Status.FORBIDDEN.getStatusCode());

        PanacheQuery<Post> query = repository
                .find(
                        "user",
                        Sort.by("dt_post",
                        Sort.Direction.Descending),user
                );

        List<PostResponse> list = query
                .list()
                .stream()
                .map(PostResponse::fromEntity)
                .collect(Collectors.toList());

        return Response.ok(list).build();
    }

}
