package com.kurzawsk.simple_bank.boundary;


import com.kurzawsk.simple_bank.control.AccountService;
import com.kurzawsk.simple_bank.control.TransferService;
import com.kurzawsk.simple_bank.entity.dto.AccountDTO;
import com.kurzawsk.simple_bank.entity.dto.TransferDTO;
import com.kurzawsk.simple_bank.entity.dto.TransferFilterType;
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
import java.util.List;

@Path("/")
public class AccountResource {

    private static final String ACCOUNTS = "/accounts";
    private static final String TRANSFERS = "/transfers";
    private static final String ID = "/{id}";

    private final TransferService transferService;
    private final AccountService accountService;

    @Context
    private UriInfo uriInfo;

    @Inject
    public AccountResource(TransferService transferService, AccountService accountService) {
        this.transferService = transferService;
        this.accountService = accountService;
    }

    @GET
    @Path(ACCOUNTS + ID)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Return account by id", responses = {
            @ApiResponse(description = "The account",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountDTO.class))),
            @ApiResponse(responseCode = "400", description = "Account with such id does not exist"),
            @ApiResponse(responseCode = "404", description = "Account id is not specified")})
    public Response getAccount(@NotNull(message = "Account id must not be null") @PathParam("id") Long id) {
        return Response.ok()
                .entity(accountService.find(id))
                .build();
    }

    @GET
    @Path(ACCOUNTS)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Return account by number", responses = {
            @ApiResponse(description = "The account",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountDTO.class))),
            @ApiResponse(responseCode = "400", description = "Account with number id does not exist"),
            @ApiResponse(responseCode = "404", description = "Account number is not specified")})
    public Response get(/*@ApiParam(required = true)*/ @NotNull(message = "Account number must not be null") @QueryParam("number") String number) {
        return Response.ok()
                .entity(accountService.findByNumber(number))
                .build();
    }

    @GET
    @Path(ACCOUNTS + ID + TRANSFERS)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Finds transfers related to specified account id. Return transfers can be narrowed to down to incoming or outgoing", responses = {
            @ApiResponse(description = "The transfers",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransferDTO[].class))),
            @ApiResponse(responseCode = "404", description = "Account with number id does not exist")})
    public Response getTransfers(@NotNull(message = "Account number must not be null") @PathParam("id") long accountOwnerId,
                                 @QueryParam("type") TransferFilterType type) {
        List<TransferDTO> transfers;
        if (TransferFilterType.INCOMING == type) {
            transfers = transferService.findByTargetAccount(accountOwnerId);
        } else if (TransferFilterType.OUTGOING == type) {
            transfers = transferService.findBySourceAccount(accountOwnerId);
        } else {
            transfers = transferService.findByAccount(accountOwnerId);
        }

        return Response.ok()
                .entity(transfers)
                .build();
    }

    @POST
    @Path(ACCOUNTS)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Creates an account", responses = {
            @ApiResponse(description = "The account",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountDTO.class)),
                    headers = @Header(name = "location", description = "URI of created account")),
            @ApiResponse(responseCode = "400", description = "Account request is not valid"),
            @ApiResponse(responseCode = "409", description = "Account with such a number already exists")})
    public Response create(@Valid AccountDTO accountDTO) {
        AccountDTO resultAccountDTO = accountService.createAccount(accountDTO);
        return Response.created(uriInfo.getAbsolutePathBuilder().path(String.valueOf(resultAccountDTO.getId())).build())
                .entity(resultAccountDTO)
                .build();
    }
}
