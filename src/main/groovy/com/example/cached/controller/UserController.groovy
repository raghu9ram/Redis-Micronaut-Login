package com.example.cached.controller

import com.example.cached.handlers.UserNotFound
import com.example.cached.model.UserrModel
import com.example.cached.service.UserService
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Produces
import jakarta.inject.Inject

@Controller("/users")
@Produces(MediaType.APPLICATION_JSON)
class UserController {
    @Inject
    UserService userService

    @Post("/")
    HttpResponse<UserrModel> createAUser(@Body UserrModel userModel) {
        try {
            def user = userService.createAUser(userModel)
            return HttpResponse.created(user)
        } catch (Exception e) {
            return HttpResponse.serverError("Error creating user: ${e.message}")
        }
    }

    @Post("/login")
    HttpResponse<UserrModel>login(@Body UserrModel userrModel) {
        try {
            def user = userService.userLogin(userrModel.email, userrModel.password)
            return HttpResponse.ok(user)
        } catch (UserNotFound e) {
            return HttpResponse.badRequest("Invalid Credentials: ${e.message}")
        } catch (Exception e) {
            return HttpResponse.serverError("An error occured: ${e.message}")
        }
    }
}
