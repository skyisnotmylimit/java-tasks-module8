package com.epam.jmp.service.rest.controller;

import com.epam.jmp.domain.Subscription;
import com.epam.jmp.dto.SubscriptionRequestDto;
import com.epam.jmp.dto.SubscriptionResponseDto;
import com.epam.jmp.service.api.SubscriptionService;
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
@RequestMapping("/api/v1/subscriptions")
@Tag(name = "Subscription Controller", description = "REST API for Subscription management")
public class ServiceController {

    private final SubscriptionService subscriptionService;
    private final ConversionService conversionService;

    public ServiceController(SubscriptionService subscriptionService, ConversionService conversionService) {
        this.subscriptionService = subscriptionService;
        this.conversionService = conversionService;
    }

    @Operation(summary = "Create a new subscription", description = "Creates a new subscription for a user. The id field is ignored.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Subscription created successfully",
                    content = @Content(schema = @Schema(implementation = SubscriptionResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<SubscriptionResponseDto> createSubscription(
            @Valid @RequestBody SubscriptionRequestDto subscriptionRequestDto) {
        Subscription subscription = conversionService.convert(subscriptionRequestDto, Subscription.class);
        subscription.setId(null); // id is ignored for creation
        Subscription created = subscriptionService.createSubscription(subscription);
        SubscriptionResponseDto responseDto = conversionService.convert(created, SubscriptionResponseDto.class);
        addSelfLink(responseDto);
        return ResponseEntity.created(
                linkTo(methodOn(ServiceController.class).getSubscription(created.getId())).toUri())
                .body(responseDto);
    }

    @Operation(summary = "Update an existing subscription", description = "Updates a subscription by id. The id field in the request body is used.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Subscription updated successfully",
                    content = @Content(schema = @Schema(implementation = SubscriptionResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Request id does not match path id"),
            @ApiResponse(responseCode = "404", description = "Subscription not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionResponseDto> updateSubscription(
            @Parameter(description = "Subscription ID") @PathVariable Long id,
            @Valid @RequestBody SubscriptionRequestDto subscriptionRequestDto) {
        if (subscriptionRequestDto.getId() == null || !subscriptionRequestDto.getId().equals(id)) {
            throw new IllegalArgumentException("Request body id must match the path id.");
        }
        Subscription subscription = conversionService.convert(subscriptionRequestDto, Subscription.class);
        Subscription updated = subscriptionService.updateSubscription(subscription);
        SubscriptionResponseDto responseDto = conversionService.convert(updated, SubscriptionResponseDto.class);
        addSelfLink(responseDto);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "Delete a subscription", description = "Deletes a subscription by id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Subscription deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Subscription not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscription(@Parameter(description = "Subscription ID") @PathVariable Long id) {
        subscriptionService.deleteSubscription(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get a subscription by ID", description = "Retrieves a subscription by its unique identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Subscription found",
                    content = @Content(schema = @Schema(implementation = SubscriptionResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Subscription not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionResponseDto> getSubscription(@Parameter(description = "Subscription ID") @PathVariable Long id) {
        return subscriptionService.getSubscriptionById(id)
                .map(sub -> conversionService.convert(sub, SubscriptionResponseDto.class))
                .map(dto -> {
                    addSelfLink(dto);
                    addCollectionLink(dto);
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get all subscriptions", description = "Retrieves a list of all subscriptions")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of subscriptions",
                    content = @Content(schema = @Schema(
                            implementation = CollectionModel.class,
                            description = "HATEOAS collection containing subscription resources and navigation links")))
    })
    @GetMapping
    public ResponseEntity<CollectionModel<SubscriptionResponseDto>> getAllSubscriptions() {
        List<SubscriptionResponseDto> subscriptions = subscriptionService.getAllSubscriptions().stream()
                .map(sub -> conversionService.convert(sub, SubscriptionResponseDto.class))
                .peek(this::addSelfLink)
                .collect(Collectors.toList());

        Link selfLink = linkTo(methodOn(ServiceController.class).getAllSubscriptions()).withSelfRel();
        CollectionModel<SubscriptionResponseDto> collectionModel = CollectionModel.of(subscriptions, selfLink);
        return ResponseEntity.ok(collectionModel);
    }

    private void addSelfLink(SubscriptionResponseDto dto) {
        if (dto != null && dto.getId() != null) {
            dto.add(linkTo(methodOn(ServiceController.class).getSubscription(dto.getId())).withSelfRel());
        }
    }

    private void addCollectionLink(SubscriptionResponseDto dto) {
        if (dto != null) {
            dto.add(linkTo(methodOn(ServiceController.class).getAllSubscriptions()).withRel("subscriptions"));
        }
    }
}
