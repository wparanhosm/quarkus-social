package io.github.wparanhosm.quarkussocial.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FieldError {

    private String field;
    private String error;


}
