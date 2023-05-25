package com.example.weather.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.web.JsonPath;

public record Current(
        Integer tempature
){
}
