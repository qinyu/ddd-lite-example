package com.example.business.rest;

import com.example.TestBase;
import com.example.domain.user.model.User;
import com.example.domain.user.service.UserService;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.Is.isA;

class UserControllerTest extends TestBase {
    @Autowired
    private UserService userService;

    @Test
    void should_register_a_user() {
        Response response = given()
                .contentType("application/json")
                .body(new HashMap<String, Object>() {
                    {
                        put("name", "test1");
                        put("email", "test1@email.com");
                        put("password", "password");
                    }
                })
                .when()
                .post("/users");
        response.then().statusCode(201)
                .body("name", is("test1"))
                .body("id", isA(String.class))
                .body("email", is("test1@email.com"))
                .body("name", is("test1"));
    }

    @Test
    void should_get_user_detail() {
        User user = this.prepareUser("anyName", "anyEmail");

        Response response = givenWithAuthorize(user)
                .when()
                .get("/users/me");
        response.then().statusCode(200)
                .body("id", is(user.getId()))
                .body("name", is(user.getName()))
                .body("email", is(user.getEmail()));

    }

    @Test
    void should_update_user() {
        User user = this.prepareUser("anyName", "anyEmail");
        String newName = "newName";

        Response response = givenWithAuthorize(user)
                .body(new HashMap<String, Object>() {
                    {
                        put("name", newName);
                    }
                })
                .when()
                .put("/users/me");
        response.then().statusCode(200)
                .body("id", is(user.getId()))
                .body("name", is(newName));

        User updatedUser = userService.get(user.getId());
        assertThat(updatedUser.getName(), is(newName));
    }

    @Test
    void should_reset_password() {
        User user = this.prepareUser("anyName", "anyEmail");
        String newPassword = "newPassword";

        givenWithAuthorize(user)
                .body(new HashMap<String, Object>() {
                    {
                        put("password", newPassword);
                    }
                })
                .when()
                .put("/users/me/password")
                .then().statusCode(200);

        given()
                .contentType("application/json")
                .body(new HashMap<String, Object>() {
                    {
                        put("email", user.getEmail());
                        put("password", newPassword);
                    }
                })
                .when()
                .post("/authorizes")
                .then().statusCode(201);
    }

    @Test
    void should_find_users_by_id() {
        User user = this.prepareUser("anyName", "anyEmail");
        User otherUser = this.prepareUser("anyOtherName", "anyOtherEmail");

        given()
                .contentType("application/json")
                .when()
                .get("/users?ids=" + user.getId() + "," + otherUser.getId())
                .then()
                .statusCode(200)
                .body("content.size", is(2))
                .body("content.id", hasItems(user.getId(), otherUser.getId()))
                .body("content.name", hasItems(user.getName(), otherUser.getName()))
                .body("content.email", hasItems(user.getEmail(), otherUser.getEmail()));

        given()
                .contentType("application/json")
                .when()
                .get("/users?ids=" + user.getId())
                .then()
                .statusCode(200)
                .body("content.size", is(1))
                .body("content.id", hasItems(user.getId()))
                .body("content.name", hasItems(user.getName()))
                .body("content.email", hasItems(user.getEmail()));

        given()
                .contentType("application/json")
                .when()
                .get("/users?ids=DOES-NOT-EXIST")
                .then()
                .statusCode(200)
                .body("content.size", is(0));
    }
}
