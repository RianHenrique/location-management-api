package org.challenge.locationmanagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.challenge.locationmanagement.dto.ErrorResponse;
import org.challenge.locationmanagement.dto.LocationDto;
import org.challenge.locationmanagement.exception.ResourceBadRequestException;
import org.challenge.locationmanagement.service.LocationService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@AllArgsConstructor
@RestController
@RequestMapping("/api/locations")
@Tag(name = "Location API")
public class LocationController {

    private LocationService locationService;


    // Build Add Location REST API
    @Operation(summary = "Create a new location", description = "Creates a new location")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns a JSON with the data of the created location.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LocationDto.class))),
            @ApiResponse(responseCode = "400", description = "The request was invalid. The response body includes an error message detailing the issue.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping
    public ResponseEntity<LocationDto> createLocation(@RequestBody @Valid LocationDto locationDto,
                                                      BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            throw new ResourceBadRequestException(errorMessage);
        }

        LocationDto savedLocation = locationService.createLocation(locationDto);
        return new ResponseEntity<>(savedLocation, HttpStatus.CREATED);
    }


    // Build Get Location REST API
    @Operation(summary = "Retrieve a location by ID", description = "Retrieves a location by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns the details of the location with the specified ID.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LocationDto.class))),
            @ApiResponse(responseCode = "404", description = "The location with the given ID was not found. The response body includes an error message indicating that the location was not found.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("{id}")
    public ResponseEntity<LocationDto> getLocationById(@PathVariable("id") Long locationId) {
        LocationDto locationDto = locationService.getLocationById(locationId);
        return new ResponseEntity<>(locationDto, HttpStatus.OK);
    }

    // Build Get All Locations REST API
    @Operation(summary = "Retrieve all locations", description = "Retrieves a paginated list of locations, ordered by creation date")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns a paginated list of locations ordered by creation date. The response body includes the details of the locations with pagination information."),
            @ApiResponse(responseCode = "500", description = "Indicates that an unexpected error occurred while processing the request. The response body includes an error message with details about the issue.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<Page<LocationDto>> getAllLocations(@ParameterObject Pageable pageable) {
        Page<LocationDto> locations = locationService.getAllLocations(pageable);
        return ResponseEntity.ok(locations);
    }

    // Build Update Location REST API
    @Operation(summary = "Update a location by ID", description = "Updates an existing location by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the location. Returns the updated location details.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LocationDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request. Indicates that the request body has invalid data. The response body includes an error message with details about the validation issues.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found. Indicates that the location with the specified ID does not exist. The response body includes an error message indicating that the location was not found.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("{id}")
    public ResponseEntity<LocationDto> updateLocation( @PathVariable("id") Long locationId,
                                                      @RequestBody @Valid LocationDto locationDto,
                                                       BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            throw new ResourceBadRequestException(errorMessage);
        }

        LocationDto updatedLocation = locationService.updateLocation(locationId, locationDto);
        return ResponseEntity.ok(updatedLocation);
    }

    // Build Delete Location REST API
    @Operation(summary = "Delete a location by ID", description = "Deletes a location by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted the location. Returns a confirmation message.",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Location with ID 1 deleted successfully!"))),
            @ApiResponse(responseCode = "404", description = "Not Found. Indicates that the location with the specified ID does not exist. The response body includes an error message indicating that the location was not found.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteLocation(@PathVariable("id") Long locationId) {
        locationService.deleteLocation(locationId);
        return ResponseEntity.ok(String.format("Location with ID %d deleted successfully!", locationId));
    }


}
