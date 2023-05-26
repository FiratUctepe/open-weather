package com.example.weather.controller;

import com.example.weather.controller.validation.CityNameConstraint;
import com.example.weather.dto.WeatherDto;
import com.example.weather.service.WeatherService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/weather")
@Validated
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/{city}")
    public ResponseEntity<WeatherDto> getWeather(@PathVariable @NotNull @CityNameConstraint String city){
        return ResponseEntity.ok(weatherService.getWeatherByCityName(city));
    }
}
