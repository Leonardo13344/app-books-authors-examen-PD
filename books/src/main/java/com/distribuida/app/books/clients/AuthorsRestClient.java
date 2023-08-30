package com.distribuida.app.books.clients;

import com.distribuida.app.books.dtos.AuthorDto;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@Path("/authors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RegisterRestClient
public interface AuthorsRestClient {

    @GET
    @Retry(maxRetries = 3)
    @Timeout(value = 500)
    List<AuthorDto> findAll();

    @GET
    @Retry(maxRetries = 3)
    @Timeout(value = 500)
    @Path("/{id}")
    AuthorDto getById(@PathParam("id") Integer id);
}
