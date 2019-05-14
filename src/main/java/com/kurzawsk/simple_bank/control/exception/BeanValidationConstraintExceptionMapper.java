package com.kurzawsk.simple_bank.control.exception;


import com.kurzawsk.simple_bank.entity.dto.ErrorDTO;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class BeanValidationConstraintExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException e) {
        String[] message = e.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .toArray(String[]::new);

        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(ErrorDTO.builder().message(message).build())
                .build();
    }
}
