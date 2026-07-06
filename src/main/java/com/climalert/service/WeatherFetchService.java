package com.climalert.service;

import com.climalert.dto.WeatherResponse;
import com.climalert.model.WeatherRecord;
import com.climalert.repository.WeatherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.time.LocalDateTime;

@Service
public class WeatherFetchService {

    private static final Logger log = LoggerFactory.getLogger(WeatherFetchService.class);

    private final WeatherRepository weatherRepository;
    private final RestClient restClient;

    @Value("${weather.api.url}")
    private String apiUrl;

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.location}")
    private String location;

    public WeatherFetchService(WeatherRepository weatherRepository, RestClient restClient) {
        this.weatherRepository = weatherRepository;
        this.restClient = restClient;
    }

    @Scheduled(fixedRate = 300000) // Cada 5 minutos (300000 ms)
    public void fetchAndSaveWeather() {
        log.info("Obteniendo clima actual para: {}", location);
        try {
            // Se realiza la llamada GET al proveedor meteorológico
            WeatherResponse response = restClient.get()
                    .uri(apiUrl + "/current.json?key={key}&q={q}", apiKey, location)
                    .retrieve()
                    .body(WeatherResponse.class);

            if (response != null && response.current() != null) {
                double temp = response.current().tempC();
                int humidity = response.current().humidity();

                WeatherRecord record = new WeatherRecord(temp, humidity, LocalDateTime.now());
                weatherRepository.save(record);

                log.info("Datos del clima guardados exitosamente. Temp: {} °C, Humedad: {}%", temp, humidity);
            } else {
                log.warn("La respuesta del servicio de clima fue nula o incompleta.");
            }
        } catch (Exception e) {
            log.error("Error al obtener o guardar la información climática: {}", e.getMessage(), e);
        }
    }
}
