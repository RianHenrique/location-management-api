package org.challenge.locationmanagement.mapper;

import org.challenge.locationmanagement.dto.LocationDto;
import org.challenge.locationmanagement.entity.Location;

public class LocationMapper {

    public static LocationDto mapToLocationDto(Location location) {
        return new LocationDto(
                location.getId(),
                location.getName(),
                location.getNeighborhood(),
                location.getCity(),
                location.getState(),
                location.getCreatedAt(),
                location.getUpdatedAt()
        );
    }

    public static Location mapToLocation(LocationDto locationDto) {
        return new Location(
                locationDto.getName(),
                locationDto.getNeighborhood(),
                locationDto.getCity(),
                locationDto.getState()
        );
    }
}