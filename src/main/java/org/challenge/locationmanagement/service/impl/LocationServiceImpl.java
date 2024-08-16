package org.challenge.locationmanagement.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.challenge.locationmanagement.dto.LocationDto;
import org.challenge.locationmanagement.entity.Location;
import org.challenge.locationmanagement.exception.ResourceNotFoundException;
import org.challenge.locationmanagement.mapper.LocationMapper;
import org.challenge.locationmanagement.repository.LocationRepository;
import org.challenge.locationmanagement.service.LocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class LocationServiceImpl implements LocationService {

    private LocationRepository locationRepository;
    private static final Logger logger = LoggerFactory.getLogger(LocationServiceImpl.class);

    @Override
    @Transactional
    public LocationDto createLocation(LocationDto locationDto) {

        Location location = LocationMapper.mapToLocation(locationDto);
        Location savedLocation = locationRepository.save(location);

        logger.info("createLocation: Location created with ID: {}", savedLocation.getId());

        return LocationMapper.mapToLocationDto(savedLocation);
    }

    @Override
    public LocationDto getLocationById(Long locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> {
                    logger.error("getLocationById: Location not found with ID: {}", locationId);
                    return new ResourceNotFoundException("Location is not exists with given id: " + locationId);
                });


        logger.info("getLocationById: Location found with ID: {}", locationId);
        return LocationMapper.mapToLocationDto(location);
    }

    @Override
    public Page<LocationDto> getAllLocations(Pageable pageable) {

        Sort sort = pageable.getSort().isSorted() ? pageable.getSort() : Sort.by("createdAt").ascending();

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort
        );

        Page<Location> locationPage = locationRepository.findAll(sortedPageable);
        Page<LocationDto> locationDtoPage = locationPage.map(LocationMapper::mapToLocationDto);

        logger.info("getAllLocations: Retrieved {} locations", locationDtoPage.getTotalElements());
        return locationDtoPage;
    }

    @Override
    @Transactional
    public LocationDto updateLocation(Long locationId, LocationDto updatedLocation) {

        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> {
                    String errorMessage = "Location does not exist with given id: " + locationId;
                    logger.error("updateLocation: {}", errorMessage);
                    return new ResourceNotFoundException(errorMessage);
                });

        location.setName(updatedLocation.getName());
        location.setNeighborhood(updatedLocation.getNeighborhood());
        location.setCity(updatedLocation.getCity());
        location.setState(updatedLocation.getState());

        Location updatedLocationObj = locationRepository.saveAndFlush(location);
        logger.info("updateLocation: Location with ID {} updated successfully", locationId);

        return LocationMapper.mapToLocationDto(updatedLocationObj);
    }

    @Override
    @Transactional
    public void deleteLocation(Long locationId) {

        locationRepository.findById(locationId).orElseThrow(() -> {
            logger.error("deleteLocation: Location with ID {} not found", locationId);
            return new ResourceNotFoundException("Location is not exists with given id: " + locationId);
        });

        locationRepository.deleteById(locationId);
        logger.info("deleteLocation: Location with ID {} deleted successfully", locationId);
    }


}
