package org.pet.project.model.dto.api.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class Sys {

    private String country;
    private Long sunrise;
    private Long sunset;
}
