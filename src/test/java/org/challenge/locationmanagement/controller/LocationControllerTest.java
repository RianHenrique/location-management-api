package org.challenge.locationmanagement.controller;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.challenge.locationmanagement.dto.LocationDto;
import org.challenge.locationmanagement.exception.ResourceBadRequestException;
import org.challenge.locationmanagement.exception.ResourceNotFoundException;
import org.challenge.locationmanagement.service.LocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@SpringBootTest
@AutoConfigureMockMvc
class LocationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LocationService locationService;

    @InjectMocks
    private LocationController locationController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(locationController).setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    @DisplayName("Should create location successfully")
    void createLocation_success() throws Exception {
        // Arrange
        LocationDto locationDto = new LocationDto(null, "Name", "Neighborhood", "City", "State", null, null);
        LocationDto savedLocationDto = new LocationDto(1L, "Name", "Neighborhood", "City", "State", LocalDateTime.now(), LocalDateTime.now());

        // Mock behavior
        when(locationService.createLocation(any(LocationDto.class))).thenReturn(savedLocationDto);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(locationDto);


        // Act
        mockMvc.perform(post("/api/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Name"))
                .andExpect(jsonPath("$.neighborhood").value("Neighborhood"))
                .andExpect(jsonPath("$.city").value("City"))
                .andExpect(jsonPath("$.state").value("State"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt").value(notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.updatedAt").value(notNullValue()));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when validation fails")
    void createLocation_validationError() throws Exception {
        // Arrange
        LocationDto invalidLocationDto = new LocationDto(null, "", "", "", "", null, null); // Assuming empty values are invalid

        // Simulate a validation error
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(invalidLocationDto);

        // Act & Assert
        mockMvc.perform(post("/api/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Get Location By ID - Success")
    void getLocationById_Success() throws Exception {
        Long locationId = 1L;
        LocationDto locationDto = new LocationDto(1L, "Name", "Neighborhood", "City", "State", LocalDateTime.now(), LocalDateTime.now());

        when(locationService.getLocationById(locationId)).thenReturn(locationDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/locations/{id}", locationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Name"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.neighborhood").value("Neighborhood"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.city").value("City"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.state").value("State"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt").value(notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.updatedAt").value(notNullValue()));
    }

    @Test
    @DisplayName("Get All Locations - Success")
    void getAllLocations_Success() throws Exception {
        // Create Pageable instance using PageRequest
        Pageable pageable = PageRequest.of(0, 10);

        // Create sample LocationDto objects
        LocationDto locationDto1 = new LocationDto(1L, "Name1", "Neighborhood1", "City1", "State1", LocalDateTime.now(), LocalDateTime.now());
        LocationDto locationDto2 = new LocationDto(2L, "Name2", "Neighborhood2", "City2", "State2", LocalDateTime.now(), LocalDateTime.now());
        List<LocationDto> locationDtos = Arrays.asList(locationDto1, locationDto2);
        Page<LocationDto> locationPage = new PageImpl<>(locationDtos, pageable, locationDtos.size());

        // Mock the service call
        when(locationService.getAllLocations(pageable)).thenReturn(locationPage);

        // Perform the GET request and validate the response
        mockMvc.perform(MockMvcRequestBuilders.get("/api/locations")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].name").value("Name1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].name").value("Name2"));
    }

    @Test
    @DisplayName("Should update location successfully")
    void updateLocation_success() throws Exception {
        // Arrange
        LocationDto locationDto = new LocationDto(null, "UpdatedName", "UpdatedNeighborhood", "UpdatedCity", "UpdatedState", null, null);
        LocationDto updatedLocationDto = new LocationDto(1L, "UpdatedName", "UpdatedNeighborhood", "UpdatedCity", "UpdatedState", LocalDateTime.now(), LocalDateTime.now());

        // Mock behavior
        when(locationService.updateLocation(anyLong(), any(LocationDto.class))).thenReturn(updatedLocationDto);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(locationDto);

        // Act
        mockMvc.perform(MockMvcRequestBuilders.put("/api/locations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("UpdatedName"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.neighborhood").value("UpdatedNeighborhood"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.city").value("UpdatedCity"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.state").value("UpdatedState"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt").value(notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.updatedAt").value(notNullValue()));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when validation fails")
    void updateLocation_validationError() throws Exception {
        // Arrange
        String invalidLocationDto = "{ \"name\": \"\", \"neighborhood\": \"\", \"city\": \"\", \"state\": \"\" }";

        // Simulate validation error
        when(locationService.updateLocation(anyLong(), any(LocationDto.class)))
                .thenThrow(new ResourceBadRequestException("Invalid input"));

        // Act
        mockMvc.perform(MockMvcRequestBuilders.put("/api/locations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidLocationDto))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should delete location successfully")
    void deleteLocation_success() throws Exception {
        // Arrange
        Long locationId = 1L;
        String successMessage = String.format("Location with ID %d deleted successfully!", locationId);

        // Mock behavior
        doNothing().when(locationService).deleteLocation(locationId);

        // Act
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/locations/{id}", locationId))
                .andExpect(status().isOk())
                .andExpect(content().string(successMessage));
    }

    @Test
    @DisplayName("Should return 404 Not Found when location to delete does not exist")
    void deleteLocation_notFound() throws Exception {
        // Arrange
        Long locationId = 1L;
        String errorMessage = "Location is not exists with given id: " + locationId;

        // Simulate ResourceNotFoundException
        doThrow(new ResourceNotFoundException(errorMessage)).when(locationService).deleteLocation(locationId);

        // Act
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/locations/{id}", locationId))
                .andExpect(status().isNotFound());

    }
}