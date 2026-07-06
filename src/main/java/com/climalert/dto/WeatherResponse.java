package com.climalert.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record WeatherResponse(Current current) {

    public record Current(
            @JsonProperty("temp_c") double tempC,
            int humidity
    ) {}
}
