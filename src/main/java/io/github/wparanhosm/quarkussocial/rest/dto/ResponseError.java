package io.github.wparanhosm.quarkussocial.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.ConstraintViolation;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class ResponseError {
    public static final int UNPROCESSABLE_ENTITY_STATUS  = 422;
    private String message;
    private Collection<FieldError> errors;

    public static <T> ResponseError createFromValidation(Set<ConstraintViolation<T>> violations){

       List<FieldError> errors = violations
                .stream()
                .map(cv -> new FieldError(cv.getPropertyPath().toString(),cv.getMessage()))
                .collect(Collectors.toList());

       String message = "Validation Error";

        return new ResponseError(message,errors);
    }

    public static ResponseError createFromSingleFieldError(String message,FieldError error){

        List<FieldError> errors = new ArrayList<>();
        errors.add(error);

        return new ResponseError(message,errors);
    }

    public Response withStatusCode(int code){
        return Response.status(code).entity(this).build();
    }
}
