package io.github.wparanhosm.quarkussocial.rest;

import io.github.wparanhosm.quarkussocial.rest.dto.CreateUserRequest;
import io.github.wparanhosm.quarkussocial.rest.dto.ResponseError;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;


import javax.ws.rs.core.Response;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserResourceTest {

    @TestHTTPResource("/users")
    URL apiurl;

    @Test
    @DisplayName("should create an user successfully")
    @Order(1)
    public void createUserTest(){
        CreateUserRequest request = new CreateUserRequest();
        request.setAge(18);
        request.setName("Fulano");

        var response =
            given()
                    .contentType(ContentType.JSON)
                    .body(request)
            .when()
                .post(apiurl)
                .then()
            .extract().response();


        assertEquals(201,response.getStatusCode());
        assertNotNull(response.jsonPath().getString("id"));

    }

    @Test
    @DisplayName("should return error when json is not valid")
    @Order(2)
    public void createUserErrorJsonValidation(){
        CreateUserRequest request = new CreateUserRequest();
        request.setAge(null);
        request.setName(null);

        var response =
                given()
                        .contentType(ContentType.JSON)
                        .body(request)
                        .when()
                        .post(apiurl)
                        .then()
                        .extract().response();


        assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS,response.getStatusCode());
        assertEquals("Validation Error",response.jsonPath().getString("message"));

        List<Map<String,String>> errors = response.jsonPath().getList("errors");

        assertNotNull(errors.get(0).get("error"));
    }


    @Test
    @DisplayName("should list all users")
    @Order(3)
    public void listAllUsersTest(){
        var response =
            given()
                .contentType(ContentType.JSON)
            .when()
                .get(apiurl)
            .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .body("size()", Matchers.greaterThan(1));

    }
}