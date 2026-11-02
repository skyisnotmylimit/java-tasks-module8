package com.epam.jmp.service.rest.controller;

import com.epam.jmp.domain.User;
import com.epam.jmp.dto.UserRequestDto;
import com.epam.jmp.dto.UserResponseDto;
import com.epam.jmp.service.api.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.convert.ConversionService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Controller", description = "REST API for User management")
public class UserController {

    private final UserService userService;
    private final ConversionService conversionService;

    public UserController(UserService userService, ConversionService conversionService) {
        this.userService = userService;
        this.conversionService = conversionService;
    }

    @Operation(summary = "Create a new user", description = "Creates a new user. The id field in the request body is ignored.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        User user = conversionService.convert(userRequestDto, User.class);
        user.setId(null); // id is ignored for creation
        User created = userService.createUser(user);
        UserResponseDto responseDto = conversionService.convert(created, UserResponseDto.class);
        addSelfLink(responseDto);
        return ResponseEntity.created(linkTo(methodOn(UserController.class).getUser(created.getId())).toUri())
                .body(responseDto);
    }

    @Operation(summary = "Update an existing user", description = "Updates a user by id. The id field in the request body is used to identify the user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Request id does not match path id"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(
            @Parameter(description = "User ID") @PathVariable Long id,
            @Valid @RequestBody UserRequestDto userRequestDto) {
        if (userRequestDto.getId() == null || !userRequestDto.getId().equals(id)) {
            throw new IllegalArgumentException("Request body id must match the path id.");
        }
        User user = conversionService.convert(userRequestDto, User.class);
        User updated = userService.updateUser(user);
        UserResponseDto responseDto = conversionService.convert(updated, UserResponseDto.class);
        addSelfLink(responseDto);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "Delete a user", description = "Deletes a user by id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@Parameter(description = "User ID") @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get a user by ID", description = "Retrieves a user by their unique identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUser(@Parameter(description = "User ID") @PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> conversionService.convert(user, UserResponseDto.class))
                .map(dto -> {
                    addSelfLink(dto);
                    addCollectionLink(dto);
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get all users", description = "Retrieves a list of all users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of users",
                    content = @Content(schema = @Schema(
                            implementation = CollectionModel.class,
                            description = "HATEOAS collection containing user resources and navigation links")))
    })
    @GetMapping
    public ResponseEntity<CollectionModel<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = userService.getAllUsers().stream()
                .map(user -> conversionService.convert(user, UserResponseDto.class))
                .peek(this::addSelfLink)
                .collect(Collectors.toList());

        Link selfLink = linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel();
        CollectionModel<UserResponseDto> collectionModel = CollectionModel.of(users, selfLink);
        return ResponseEntity.ok(collectionModel);
    }

    private void addSelfLink(UserResponseDto dto) {
        if (dto != null && dto.getId() != null) {
            dto.add(linkTo(methodOn(UserController.class).getUser(dto.getId())).withSelfRel());
        }
    }

    private void addCollectionLink(UserResponseDto dto) {
        if (dto != null) {
            dto.add(linkTo(methodOn(UserController.class).getAllUsers()).withRel("users"));
        }
    }
}
