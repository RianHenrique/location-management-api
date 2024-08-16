package org.challenge.locationmanagement.service;

import org.challenge.locationmanagement.dto.LocationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface LocationService {
    LocationDto createLocation(LocationDto locationDto);

    LocationDto getLocationById(Long locationId);

    Page<LocationDto> getAllLocations(Pageable pageable);

    LocationDto updateLocation(Long locationId, LocationDto updatedLocation);

    void deleteLocation(Long locationId);
}
