package com.distribuida.books.authors.rest;

import com.distribuida.books.authors.db.Author;
import com.distribuida.books.authors.repo.AuthorRepository;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;


import java.util.List;

@Path("/authors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional
@RequestScoped
public class AuthorRest {

    @Inject
    AuthorRepository rep;

    //books GET
    @GET
    @Operation(summary = "GET ALL", description = "Obtains all authors")
    @APIResponses(
            value = {
                    @APIResponse(responseCode = "200", description = "Authors Listed"),
                    @APIResponse(responseCode = "404", description = "Method GETALL not found")
            }
    )
    @Retry(maxRetries = 3)
    @Timeout(value = 500)
    public List<Author> findAll() {
        return rep.findAll();
    }

    @GET
    @Path("/{id}")
    @Retry(maxRetries = 3)
    @Timeout(value = 500)
    @Operation(summary = "GET", description = "Obtains author by id")
    @APIResponses(
            value = {
                    @APIResponse(responseCode = "200", description = "Author Returned"),
                    @APIResponse(responseCode = "404", description = "Method GET by id not found")
            }
    )
    public Response getById(@PathParam("id") Long id) {
        var book = rep.findById(id);
        if (book == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(book).build();
    }

    @POST
    @Retry(maxRetries = 3)
    @Timeout(value = 500)
    @Operation(summary = "POST", description = "Create an author")
    @APIResponses(
            value = {
                    @APIResponse(responseCode = "200", description = "Auhtor created"),
                    @APIResponse(responseCode = "404", description = "Method POST an author not found")
            }
    )
    public Response create(Author p) {
        rep.create(p);
        return Response.status(Response.Status.CREATED.getStatusCode(), "author created").build();
    }

    @PUT
    @Path("/{id}")
    @Retry(maxRetries = 3)
    @Timeout(value = 500)
    @Operation(summary = "PUT", description = "Update an author")
    @APIResponses(
            value = {
                    @APIResponse(responseCode = "200", description = "Author Updated"),
                    @APIResponse(responseCode = "404", description = "Method PUT not found")
            }
    )
    public Response update(@PathParam("id") Long id, Author authorObj) {
        Author author = rep.findById(id);
        author.setFirstName(authorObj.getFirstName());
        author.setLastName(authorObj.getLastName());

        //rep.persistAndFlush(author);

        return Response.ok().build();
    }

    //books/{id} DELETE
    @DELETE
    @Path("/{id}")
    @Retry(maxRetries = 3)
    @Timeout(value = 500)
    @Operation(summary = "DELETE", description = "Deletes an author by id")
    @APIResponses(
            value = {
                    @APIResponse(responseCode = "200", description = "Author Deleted"),
                    @APIResponse(responseCode = "404", description = "Method DELETED not found")
            }
    )
    public Response delete(@PathParam("id") Long id) {
        rep.delete(id);
        return Response.ok( )
                .build();
    }


}
