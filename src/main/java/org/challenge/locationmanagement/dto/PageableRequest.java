package org.challenge.locationmanagement.dto;


import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This class is utilized to customize the documentation for pagination requests
 * in order to omit the 'sort' parameter from the examples provided in the Swagger UI.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageableRequest {

    @Min(value = 0, message = "Page number must be 0 or greater.")
    private Integer page;

    @Min(value = 1, message = "Page size must be 1 or greater.")
    private Integer size;
}