package io.proj3ct.bot_dota_test.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Data
@PropertySource("application.properties")

public class BotConfig {

    @Value("{bot_dota_test}")
    String  botName;
    @Value("{7145975013:AAGd6eKRKwZAns39E2zPTWqesdddOcc0F9Q}")
    String token;



}
