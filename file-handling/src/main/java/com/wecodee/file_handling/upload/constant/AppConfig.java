package com.wecodee.file_handling.upload.constant;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setPropertyCondition(Conditions.isNotNull()) // configures to map only notnull fields from src to dest
                .setMatchingStrategy(MatchingStrategies.STRICT); // maps only exact fields
        return modelMapper;
    }
}
