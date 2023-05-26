package com.example.weather.service;

import com.example.weather.constants.Constants;
import com.example.weather.dto.WeatherDto;
import com.example.weather.dto.WeatherResponse;
import com.example.weather.model.WeatherEntity;
import com.example.weather.repository.WeatherRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class WeatherService {

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
        if(weatherEntityOptional.get().getUpdatedTime().isBefore(LocalDateTime.now().minusMinutes(30))){
            return WeatherDto.convert(getWeatherFromWeatherStack(city));
        }
        return  weatherEntityOptional.map(weather -> {
            if(weatherEntityOptional.get().getUpdatedTime().isBefore(LocalDateTime.now().minusMinutes(30))){
                return WeatherDto.convert(getWeatherFromWeatherStack(city));
            }
            return WeatherDto.convert(weather);
        }).orElseGet(() -> WeatherDto.convert(getWeatherFromWeatherStack(city)));
    }

    private WeatherEntity getWeatherFromWeatherStack(String city){
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(getWeatherStackUrl(city),String.class);
        try {
            WeatherResponse weatherResponse = objectMapper.readValue(responseEntity.getBody(),WeatherResponse.class);
            return saveWeatherEntity(city,weatherResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String getWeatherStackUrl(String city){
        return Constants.API_URL+Constants.ACCESS_KEY_PARAM+Constants.API_KEY+Constants.QUERY_KEY_PARAM+city;
    }


    private  WeatherEntity saveWeatherEntity(String city,WeatherResponse weatherResponse){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        WeatherEntity weatherEntity = new WeatherEntity(city,
                                    weatherResponse.location().name(),
                                    weatherResponse.location().country(),
                                    weatherResponse.current().temperature(),
                                    LocalDateTime.now(),
                                    LocalDateTime.parse(weatherResponse.location().localTime(),dateTimeFormatter));
        return weatherRepository.save(weatherEntity);
    }
}
