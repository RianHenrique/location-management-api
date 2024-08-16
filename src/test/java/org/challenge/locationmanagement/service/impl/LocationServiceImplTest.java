package org.challenge.locationmanagement.service.impl;

import org.challenge.locationmanagement.dto.LocationDto;
import org.challenge.locationmanagement.entity.Location;
import org.challenge.locationmanagement.exception.ResourceNotFoundException;
import org.challenge.locationmanagement.mapper.LocationMapper;
import org.challenge.locationmanagement.repository.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class LocationServiceImplTest {

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private LocationServiceImpl locationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Create Location successfully")
    void createLocationSuccess() {
        // Arrange
        LocationDto locationDto = new LocationDto(null, "Name", "Neighborhood", "City", "State", null, null);

        // Crie a instância de Location com valores simulados
        Location location = new Location("Name", "Neighborhood", "City", "State" );
        location.setId(1L); // Valor gerado automaticamente
        location.setCreatedAt(LocalDateTime.now().minusDays(1)); // Valor simulado
        location.setUpdatedAt(LocalDateTime.now()); // Valor simulado

        // Configure o mock do repositório
        when(locationRepository.save(any(Location.class))).thenReturn(location);

        // Act
        LocationDto result = locationService.createLocation(locationDto);

        // Crie o DTO esperado com os valores simulados para id, createdAt, e updatedAt
        LocationDto expectedDto = new LocationDto(1L, "Name", "Neighborhood", "City", "State", location.getCreatedAt(), location.getUpdatedAt());

        // Assert
        assertNotNull(result);
        assertEquals(expectedDto.getId(), result.getId());
        assertEquals(expectedDto.getCreatedAt(), result.getCreatedAt());
        assertEquals(expectedDto.getUpdatedAt(), result.getUpdatedAt());
        assertEquals(expectedDto.getName(), result.getName());
        assertEquals(expectedDto.getNeighborhood(), result.getNeighborhood());
        assertEquals(expectedDto.getCity(), result.getCity());
        assertEquals(expectedDto.getState(), result.getState());

        // Verifique interações com mocks
        verify(locationRepository, times(1)).save(any(Location.class));

    }

    @Test
    @DisplayName("Should return LocationDto when Location is found by ID")
    void getLocationByIdSuccess() {
        // Arrange
        Long locationId = 1L;
        LocalDateTime now = LocalDateTime.now();

        Location location = new Location("Name", "Neighborhood", "City", "State" );
        location.setId(locationId);
        location.setCreatedAt(now.minusDays(1));
        location.setUpdatedAt(now);

        LocationDto expectedDto = new LocationDto(1L, "Name", "Neighborhood", "City", "State", location.getCreatedAt(), location.getUpdatedAt());

        // Mock behavior
        when(locationRepository.findById(locationId)).thenReturn(java.util.Optional.of(location));

        // Act
        LocationDto result = locationService.getLocationById(locationId);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDto.getId(), result.getId());
        assertEquals(expectedDto.getName(), result.getName());
        assertEquals(expectedDto.getNeighborhood(), result.getNeighborhood());
        assertEquals(expectedDto.getCity(), result.getCity());
        assertEquals(expectedDto.getState(), result.getState());
        assertEquals(expectedDto.getCreatedAt(), result.getCreatedAt());
        assertEquals(expectedDto.getUpdatedAt(), result.getUpdatedAt());

        // Verify interactions with mock
        verify(locationRepository, times(1)).findById(locationId);

    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when Location is not found by ID")
    void getLocationById_notFound() {
        // Arrange
        Long locationId = 1L;

        // Mock behavior
        when(locationRepository.findById(locationId)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            locationService.getLocationById(locationId);
        });

        // Assert
        assertEquals("Location is not exists with given id: " + locationId, exception.getMessage());

        // Verify interactions with mock
        verify(locationRepository, times(1)).findById(locationId);
    }

    @Test
    @DisplayName("Should return paginated list of LocationDto when there are locations in the repository")
    void getAllLocations_success() {
        // Arrange
        Location location1 = new Location("Name1", "Neighborhood1", "City1", "State1");
        location1.setId(1L);
        location1.setCreatedAt(LocalDateTime.now().minusDays(2));
        location1.setUpdatedAt(LocalDateTime.now().minusDays(1));

        Location location2 = new Location("Name2", "Neighborhood2", "City2", "State2");
        location2.setId(2L);
        location2.setCreatedAt(LocalDateTime.now().minusDays(1));
        location2.setUpdatedAt(LocalDateTime.now());

        List<Location> locations = Arrays.asList(location1, location2);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").ascending());

        Page<Location> locationPage = new PageImpl<>(locations, pageable, locations.size());
        Page<LocationDto> locationDtoPage = locationPage.map(LocationMapper::mapToLocationDto);

        // Mock behavior
        when(locationRepository.findAll(pageable)).thenReturn(locationPage);

        // Act
        Page<LocationDto> result = locationService.getAllLocations(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());

        for (int i = 0; i < locationDtoPage.getContent().size(); i++) {
            LocationDto expectedDto = locationDtoPage.getContent().get(i);
            LocationDto actualDto = result.getContent().get(i);

            assertEquals(expectedDto.getId(), actualDto.getId());
            assertEquals(expectedDto.getName(), actualDto.getName());
            assertEquals(expectedDto.getNeighborhood(), actualDto.getNeighborhood());
            assertEquals(expectedDto.getCity(), actualDto.getCity());
            assertEquals(expectedDto.getState(), actualDto.getState());
            assertEquals(expectedDto.getCreatedAt(), actualDto.getCreatedAt());
            assertEquals(expectedDto.getUpdatedAt(), actualDto.getUpdatedAt());
        }

        // Verify ordering by createdAt
        assertTrue(result.getContent().get(0).getCreatedAt().isBefore(result.getContent().get(1).getCreatedAt()));

        // Verify interactions with mock
        verify(locationRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Should update location successfully")
    void updateLocation_success() {
        // Arrange
        Long locationId = 1L;

        Location existingLocation = new Location("OldName", "OldNeighborhood", "OldCity", "OldState");
        existingLocation.setId(locationId);
        existingLocation.setCreatedAt(LocalDateTime.now().minusDays(2));
        existingLocation.setUpdatedAt(LocalDateTime.now().minusDays(1));

        LocationDto updatedLocationDto = new LocationDto(null, "NewName", "NewNeighborhood", "NewCity", "NewState", null, null);

        Location updatedLocation = new Location("NewName", "NewNeighborhood", "NewCity", "NewState");
        updatedLocation.setId(locationId);
        updatedLocation.setCreatedAt(existingLocation.getCreatedAt()); // Retain original createdAt
        updatedLocation.setUpdatedAt(LocalDateTime.now()); // Update updatedAt

        LocationDto updatedLocationDtoResult = new LocationDto(locationId, "NewName", "NewNeighborhood", "NewCity", "NewState", updatedLocation.getCreatedAt(), updatedLocation.getUpdatedAt());

        // Mock behavior
        when(locationRepository.findById(locationId)).thenReturn(java.util.Optional.of(existingLocation));
        when(locationRepository.saveAndFlush(existingLocation)).thenReturn(updatedLocation);


        // Act
        LocationDto result = locationService.updateLocation(locationId, updatedLocationDto);

        // Assert
        assertNotNull(result);
        assertEquals(updatedLocationDtoResult.getId(), result.getId());
        assertEquals(updatedLocationDtoResult.getName(), result.getName());
        assertEquals(updatedLocationDtoResult.getNeighborhood(), result.getNeighborhood());
        assertEquals(updatedLocationDtoResult.getCity(), result.getCity());
        assertEquals(updatedLocationDtoResult.getState(), result.getState());
        assertEquals(updatedLocationDtoResult.getCreatedAt(), result.getCreatedAt());
        assertEquals(updatedLocationDtoResult.getUpdatedAt(), result.getUpdatedAt());

        // Verify interactions with mocks
        verify(locationRepository, times(1)).findById(locationId);
        verify(locationRepository, times(1)).saveAndFlush(existingLocation);

    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when location does not exist")
    void updateLocation_locationNotFound() {
        // Arrange
        Long locationId = 1L;
        LocationDto updatedLocationDto = new LocationDto(null, "NewName", "NewNeighborhood", "NewCity", "NewState", null, null);

        // Mock behavior
        when(locationRepository.findById(locationId)).thenReturn(java.util.Optional.empty());

        // Act and Assert
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            locationService.updateLocation(locationId, updatedLocationDto);
        }, "Expected updateLocation() to throw, but it didn't");

        assertEquals("Location does not exist with given id: " + locationId, thrown.getMessage());

        // Verify interactions with mocks
        verify(locationRepository, times(1)).findById(locationId);
        verify(locationRepository, never()).saveAndFlush(any(Location.class));
    }

    @Test
    @DisplayName("Should delete location successfully")
    void deleteLocation_success() {
        // Arrange
        Long locationId = 1L;
        Location existingLocation = new Location("Name", "Neighborhood", "City", "State");
        existingLocation.setId(locationId);

        // Mock behavior
        when(locationRepository.findById(locationId)).thenReturn(java.util.Optional.of(existingLocation));

        // Act
        locationService.deleteLocation(locationId);

        // Assert
        verify(locationRepository, times(1)).findById(locationId);
        verify(locationRepository, times(1)).deleteById(locationId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when location does not exist")
    void deleteLocation_locationNotFound() {
        // Arrange
        Long locationId = 1L;

        // Mock behavior
        when(locationRepository.findById(locationId)).thenReturn(java.util.Optional.empty());

        // Act and Assert
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            locationService.deleteLocation(locationId);
        }, "Expected deleteLocation() to throw, but it didn't");

        assertEquals("Location is not exists with given id: " + locationId, thrown.getMessage());

        // Verify interactions with mocks
        verify(locationRepository, times(1)).findById(locationId);
        verify(locationRepository, never()).deleteById(locationId);
    }

}