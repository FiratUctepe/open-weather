package com.example.weather.service;

import com.example.weather.dto.WeatherDto;
import com.example.weather.dto.WeatherResponse;
import com.example.weather.model.WeatherEntity;
import com.example.weather.repository.WeatherRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class WeatherService {

    public static final String API_URL = "http://api.weatherstack.com/current?access_key=ef576d864633e9ebe3cde456c00cd2e4&query=";
    private final WeatherRepository weatherRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public WeatherService(WeatherRepository weatherRepository, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.weatherRepository = weatherRepository;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public WeatherDto getWeatherByCityName(String city){
        Optional<WeatherEntity> weatherEntityOptional = weatherRepository.findFirstByRequestedCityNameOrderByUpdatedTimeDesc(city);

        if(!weatherEntityOptional.isPresent()) {
            return WeatherDto.convert(getWeatherFromWeatherStack(city));
        }
        return  null;
    }

    private WeatherEntity getWeatherFromWeatherStack(String city){
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(API_URL + city,String.class);

        try {
            WeatherResponse weatherResponse = objectMapper.readValue(responseEntity.getBody(),WeatherResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}
