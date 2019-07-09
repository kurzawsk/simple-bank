package com.kurzawsk.simple_bank.control.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kurzawsk.simple_bank.entity.dto.ErrorDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class DefaultExceptionMapper implements ExceptionMapper<Exception> {

    private static Logger logger = LoggerFactory.getLogger(DefaultExceptionMapper.class);
    private static final String MESSAGE_PREFIX = "An unexpected exception occurred: ";

    @Override
    public Response toResponse(Exception e) {
        if (e instanceof MultiIllegalArgumentException) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ErrorDTO.builder().message(((MultiIllegalArgumentException) e).getMessages()).build()).build();
        }

        if (e instanceof IllegalStateException || e instanceof IllegalArgumentException || e instanceof JsonProcessingException) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ErrorDTO.builder().message(new String[]{e.getMessage()}).build()).build();
        }

        if (e instanceof EntityAlreadyExistsException) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(ErrorDTO.builder().message(new String[]{e.getMessage()}).build()).build();
        }

        if (e instanceof WebApplicationException) {
            return Response.status(((WebApplicationException) e).getResponse().getStatus())
                    .entity(ErrorDTO.builder().message(new String[]{e.getMessage()}).build()).build();
        }

        logger.error("Unhandled exception", e);

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ErrorDTO.builder().message(new String[]{MESSAGE_PREFIX + e.getMessage()}).build()).build();
    }
}
