package com.kurzawsk.simple_bank.boundary;


import com.kurzawsk.simple_bank.control.TransferService;
import com.kurzawsk.simple_bank.entity.dto.TransferDTO;
import com.kurzawsk.simple_bank.entity.dto.TransferRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/")
public class TransferResource {

    private static final String TRANSFERS = "/transfers";
    private static final String ID = "/{id}";

    private final TransferService transferService;

    @Context
    private UriInfo uriInfo;

    @Inject
    public TransferResource(TransferService transferService) {
        this.transferService = transferService;
    }

    @GET
    @Path(TRANSFERS + ID)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Return transfer by id",
            responses = {
                    @ApiResponse(description = "The transfer",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = TransferDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Transfer with such an id does not exist"),
                    @ApiResponse(responseCode = "400", description = "Transfer id is not specified")}
    )
    public Response getTransfer(@NotNull(message = "Transfer id must not be null") @PathParam("id") Long id) {
        return Response.ok()
                .entity(transferService.find(id))
                .build();
    }

    @POST
    @Path(TRANSFERS)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Creates a transfer - by transferring money from source to target account", responses = {
            @ApiResponse(description = "The transfer",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransferDTO.class)),
                    headers = @Header(name = "location", description = "URI of created transfer")),
            @ApiResponse(responseCode = "400", description = "Transfer request is not valid or source account does not have enough resources to realize such a transfer"),
            @ApiResponse(responseCode = "404", description = "At least one account specified in request does not exist")
    })
    public Response transfer(@Valid TransferRequestDTO transferRequestDTO) throws InterruptedException {
        TransferDTO resultTransferDTO = transferService.transfer(transferRequestDTO);
        return Response.created(uriInfo.getAbsolutePathBuilder().path(String.valueOf(resultTransferDTO.getId())).build())
                .entity(resultTransferDTO)
                .build();
    }

}
