package io.github.wparanhosm.quarkussocial.rest;

import io.github.wparanhosm.quarkussocial.domain.model.Follower;
import io.github.wparanhosm.quarkussocial.domain.model.User;
import io.github.wparanhosm.quarkussocial.domain.model.repository.FollowerRepository;
import io.github.wparanhosm.quarkussocial.domain.model.repository.PostRepository;
import io.github.wparanhosm.quarkussocial.rest.dto.CreatePostRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.vertx.core.cli.annotations.Description;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestHTTPEndpoint(PostResource.class)
class PostResourceTest {


    Long userId;
    Long followerId;
    Long notAFollowerId;
    @Inject
    FollowerRepository _followerRepository;
    @Inject
    PostRepository _postRepository;

    @BeforeEach
    @Transactional
    public void setUp(){

        var user = new User();

        user.setAge(18);
        user.setName("Waltinho");
        user.persist();
        userId = user.getId();


        var follower = new User();
        follower.setAge(30);
        follower.setName("Joaquina");
        follower.persist();
        followerId = follower.getId();


        var notAFollower = new User();
        notAFollower.setAge(30);
        notAFollower.setName("Ricardo");
        notAFollower.persist();
        notAFollowerId = notAFollower.getId();


        Follower userFollow = new Follower();
        userFollow.setUser(user);
        userFollow.setFollower(follower);

        _followerRepository.persist(userFollow);
    }

    @Test
    @DisplayName("should create a user post")
    @Order(1)
    public void createPostTest(){
        var postRequest = new CreatePostRequest();
        postRequest.setText("O ousado chegou!");


        given()
            .contentType(ContentType.JSON)
            .body(postRequest)
            .pathParam("userId",userId)
        .when()
            .post()
        .then()
            .statusCode(Response.Status.CREATED.getStatusCode());
    }


    @Test
    @DisplayName("should return 404 when try to make a post to a inexistent user test")
    @Order(2)
    public void createPostNotFound(){
        var postRequest = new CreatePostRequest();
        postRequest.setText("O ousado chegou!");

        var inexistentUserId = 99999;

        given()
                .contentType(ContentType.JSON)
                .body(postRequest)
                .pathParam("userId",inexistentUserId)
                .when()
                .post()
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }



    @Test
    @DisplayName("should return 404 when user doesn't exist")
    @Order(3)
    public void listAllPostsNotFound(){

        Long inexistentUserId = 99999L;

        Header header = new Header("followerId",followerId.toString());

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId",inexistentUserId)
                .header(header)
                .when()
                .get()
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }



    @Test
    @DisplayName("should return bad request when Follower header is not present")
    @Order(4)
    public void listAllPostsFollowerHeaderNotSendTest(){

        var response =
        given()
                .contentType(ContentType.JSON)
                .pathParam("userId",userId)
                .when()
                .get()
                .then()
                .extract().response();


        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),response.getStatusCode());
        assertEquals("Header Request Error",response.jsonPath().getString("message"));

        List<Map<String,String>> errors = response.jsonPath().getList("errors");

        assertNotNull(errors.get(0).get("error"));
    }


    @Test
    @DisplayName("should return 404 when follower doesn't exist")
    @Order(5)
    public void listAllPostsFollowerDoesNotExist(){
        long inexistentUserId = 99999L;

        Header header = new Header("followerId", Long.toString(inexistentUserId));

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId",userId)
                .header(header)
                .when()
                .get()
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("should return 403 when user is not a follower")
    @Order(6)
    public void listAllPostsNotAFollower() {

        Header header = new Header("followerId", Long.toString(notAFollowerId));

        var response =
                given()
                        .contentType(ContentType.JSON)
                        .pathParam("userId", userId)
                        .header(header)
                        .when()
                        .get()
                        .then()
                        .extract().response();

        assertEquals(Response.Status.FORBIDDEN.getStatusCode(),response.getStatusCode());
        assertEquals("Follower Error",response.jsonPath().getString("message"));

        List<Map<String,String>> errors = response.jsonPath().getList("errors");

        assertNotNull(errors.get(0).get("error"));
    }


    @Test
    @DisplayName("should return 200 with all posts of user")
    @Order(7)
    public void listAllPosts(){
        Header header = new Header("followerId",followerId.toString());

        var response =
        given()
                .contentType(ContentType.JSON)
                .pathParam("userId",userId)
                .header(header)
                .when()
                .get()
                .then()
                .extract().response();


        assertEquals(Response.Status.OK.getStatusCode(), response.getStatusCode());
    }
}