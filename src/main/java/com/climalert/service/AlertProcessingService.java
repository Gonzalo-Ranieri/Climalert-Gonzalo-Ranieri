package com.climalert.service;

import com.climalert.model.WeatherRecord;
import com.climalert.repository.WeatherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AlertProcessingService {

    private static final Logger log = LoggerFactory.getLogger(AlertProcessingService.class);

    private final WeatherRepository weatherRepository;
    private final JavaMailSender mailSender;

    @Value("${weather.alerts.recipients}")
    private String[] recipients;

    public AlertProcessingService(WeatherRepository weatherRepository, JavaMailSender mailSender) {
        this.weatherRepository = weatherRepository;
        this.mailSender = mailSender;
    }

    @Scheduled(fixedRate = 60000) // Cada 1 minuto (60000 ms)
    public void processAlerts() {
        log.info("Iniciando procesamiento y análisis de alertas meteorológicas...");
        try {
            Optional<WeatherRecord> latestRecordOpt = weatherRepository.findTopByOrderByTimestampDesc();
            if (latestRecordOpt.isEmpty()) {
                log.info("No se encontraron registros climáticos para evaluar.");
                return;
            }

            WeatherRecord record = latestRecordOpt.get();
            double temp = record.getTemperature();
            int humidity = record.getHumidity();

            log.info("Evaluando último registro - Temp: {} °C, Humedad: {}%", temp, humidity);

            // Alerta: Temperatura > 35° y Humedad > 60%
            if (temp > 35.0 && humidity > 60) {
                log.warn("¡Condiciones críticas detectadas! Temp: {} °C, Humedad: {}%. Disparando alerta...", temp, humidity);
                sendAlertEmail(record);
            } else {
                log.info("Condiciones climáticas estables dentro de los límites seguros.");
            }
        } catch (Exception e) {
            log.error("Error al procesar las alertas climáticas: {}", e.getMessage(), e);
        }
    }

    private void sendAlertEmail(WeatherRecord record) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(recipients);
            message.setSubject("ALERTA METEOROLÓGICA: Condiciones Climáticas Críticas Detectadas");
            
            String body = String.format(
                    "Se han detectado condiciones climáticas inusuales/peligrosas.\n\n" +
                    "Detalle Completo del Clima Registrado:\n" +
                    "- Temperatura: %.2f °C (Límite: > 35.0 °C)\n" +
                    "- Humedad: %d%% (Límite: > 60%%)\n" +
                    "- Fecha y Hora del Registro: %s\n\n" +
                    "Este es un correo automático del sistema Climalert.",
                    record.getTemperature(),
                    record.getHumidity(),
                    record.getTimestamp()
            );
            
            message.setText(body);
            message.setFrom("alertas@clima.com");

            mailSender.send(message);
            log.info("Notificación por correo enviada con éxito a: {}", String.join(", ", recipients));
        } catch (Exception e) {
            log.error("Fallo al enviar el correo electrónico de alerta: {}", e.getMessage(), e);
        }
    }
}
