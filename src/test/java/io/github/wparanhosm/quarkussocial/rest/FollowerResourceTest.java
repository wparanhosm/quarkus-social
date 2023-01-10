package io.github.wparanhosm.quarkussocial.rest;

import io.github.wparanhosm.quarkussocial.domain.model.Follower;
import io.github.wparanhosm.quarkussocial.domain.model.User;
import io.github.wparanhosm.quarkussocial.domain.model.repository.FollowerRepository;
import io.github.wparanhosm.quarkussocial.rest.dto.FollowerRequest;
import io.github.wparanhosm.quarkussocial.rest.dto.ResponseError;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FollowerResourceTest {


    @Inject
    FollowerRepository _followerRepository;

    Long userId;

    Long followerId;

    Long inexistentUser = 9999L;


    @BeforeEach
    @Transactional
    public void SetUp(){


        User user;
        user = new User();
        user.setName("Waltinho");
        user.setAge(22);
        user.persist();

        this.userId = user.getId();


        User follower;
        follower = new User();
        follower.setName("Neymar");
        follower.setAge(32);
        follower.persist();

        this.followerId = follower.getId();




        User newUser = new User();
        newUser.setName("Novonildo");
        newUser.setAge(27);
        newUser.persist();


        Follower f = new Follower();
        f.setUser(User.findById(userId));
        f.setFollower(newUser);

        _followerRepository.persist(f);
    }

    @Test
    @DisplayName("should return 422 when json is not valid")
    @Order(1)
    public void FollowUserErrorJsonValidation(){
        FollowerRequest request;
        request = new FollowerRequest();
        request.setFollowerId(null);

        var response =
            given()
                .contentType(ContentType.JSON)
                .body(request)
                .pathParam("userId",userId)
            .when()
                .put()
            .then()
                .extract().response();


        assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS,response.getStatusCode());
        assertEquals("Validation Error",response.jsonPath().getString("message"));

        List<Map<String,String>> errors = response.jsonPath().getList("errors");

        assertNotNull(errors.get(0).get("error"));
    }


    @Test
    @DisplayName("should return coflict when try to follow yourself")
    @Order(2)
    public void FollowUserUnableFollowYourselfTest(){

        FollowerRequest request = new FollowerRequest();

        request.setFollowerId(userId);

        var response =
            given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .pathParam("userId",userId)
                    .when()
                    .put()
                    .then()
                    .extract().response();

        assertEquals(Response.Status.CONFLICT.getStatusCode(),response.getStatusCode());
        assertEquals("Conflict",response.jsonPath().getString("message"));

        List<Map<String,String>> errors = response.jsonPath().getList("errors");

        assertNotNull(errors.get(0).get("error"));
        assertEquals(errors.get(0).get("error"),"You can't follow yourself");

    }


    @Test
    @DisplayName("should return 404 when try to follow a inexistent user")
    @Order(3)
    public void FollowUserNotFoundTest(){


        FollowerRequest request = new FollowerRequest();
        request.setFollowerId(followerId);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .pathParam("userId",inexistentUser)
                .when()
                .put()
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }


    @Test
    @DisplayName("should return 404 when follower not found")
    @Order(4)
    public void FollowUserFollowerNotFoundTest(){
        FollowerRequest request = new FollowerRequest();
        request.setFollowerId(inexistentUser);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .pathParam("userId",userId)
                .when()
                .put()
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("should return 204(NO_CONTENT) when follow a user")
    public void FollowUserSuccessTest(){
        FollowerRequest request = new FollowerRequest();
        request.setFollowerId(followerId);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .pathParam("userId",userId)
                .when()
                .put()
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }



    @Test
    @DisplayName("should return 404 when user not found")
    @Order(5)
    public void listFollowersUserNotFoundTest(){
        given()
            .contentType(ContentType.JSON)
            .pathParam("userId",inexistentUser)
        .when()
            .get()
        .then()
            .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }



    @Test
    @DisplayName("should return 200 with all followers")
    @Transactional
    @Order(6)
    public void listFollowersSucessTest(){

        var response =
        given()
            .contentType(ContentType.JSON)
            .pathParam("userId",userId)
        .when()
            .get()
        .then()
            .extract().response();

        assertEquals(Response.Status.OK.getStatusCode(),response.getStatusCode());
        assertNotNull(response.jsonPath().getList("content").get(0));

        assertNotNull(response.jsonPath().getString("followersCount"));
        assert Integer.parseInt(response.jsonPath().getString("followersCount")) > 0;
    }




    @Test
    @DisplayName("should return 404 when try to unfollow a inexistent user")
    @Order(7)
    public void unfollowUserNotFound(){

        given()
            .contentType(ContentType.JSON)
            .pathParam("userId",inexistentUser)
            .queryParam("followerId",followerId)
        .when()
            .delete()
        .then()
            .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("should return 404 when follower doesn't exist")
    @Order(8)
    public void unfollowUserFollowerNotFound(){
        given()
                .contentType(ContentType.JSON)
                .pathParam("userId",userId)
                .queryParam("followerId",inexistentUser)
                .when()
                .delete()
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }


    @Test
    @DisplayName("should return 204(no content) when unfollow a user")
    @Order(9)
    public void unfollowUserSuccessTest(){
        given()
                .contentType(ContentType.JSON)
                .pathParam("userId",userId)
                .queryParam("followerId",followerId)
                .when()
                .delete()
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }
}