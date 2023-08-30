package com.distribuida.app.books.rest;

import com.distribuida.app.books.clients.AuthorsRestClient;
import com.distribuida.app.books.db.Book;
import com.distribuida.app.books.dtos.AuthorDto;
import com.distribuida.app.books.dtos.BookDto;
import com.distribuida.app.books.repo.BookRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Path("/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional
@ApplicationScoped
public class BookRest {

    @Inject
    BookRepository rep;

    @Inject
    @RestClient
    AuthorsRestClient clientAuthors;

    static BookDto fromBook(Book obj) {
        BookDto dto = new BookDto();
        dto.setId(obj.getId());
        dto.setIsbn(obj.getIsbn());
        dto.setTitle(obj.getTitle());
        dto.setPrice(obj.getPrice());
        dto.setAuthorId(obj.getAuthorId());
        return dto;
    }

    @GET
    @Operation(
            summary = "Lista todos los libros",
            description = "Lista todos los libros ordenados por nombre")
    @APIResponses(
            value = {
                    @APIResponse(responseCode = "200", description = "Books Returned"),
                    @APIResponse(responseCode = "404", description = "Method GET all not found")
            }
    )
    @Retry(maxRetries = 3)
    @Timeout(value = 500)
    public List<BookDto> findAll() {
        return rep.findAll().stream()
                .map(BookRest::fromBook)
                .peek(dto -> {
                    AuthorDto authorDto = clientAuthors.getById(dto.getAuthorId());
                    String aname = String.format("%s, %s", authorDto.getLastName(),
                            authorDto.getFirstName());
                    dto.setAuthorName(aname);
                })
                .collect(Collectors.toList());
    }

    @GET
    @Path("/{id}")
    @Retry(maxRetries = 3)
    @Timeout(value = 500)
    @Operation(summary = "GET", description = "Obtains a book by id")
    @APIResponses(
            value = {
                    @APIResponse(responseCode = "200", description = "Book by id returned"),
                    @APIResponse(responseCode = "404", description = "Method GET by id not found")
            }
    )
    public Response getById(@PathParam("id") Long id) {
        var book = rep.findById(id);

        if (book == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // proxy manual
//        var config = ConfigProvider.getConfig();
//        var url = String.format("http://%s:%s",
//                config.getValue("app.authors.host", String.class),
//                config.getValue("app.authors.port", String.class)
//        );
//
//        AuthorsRestClient proxy = RestClientBuilder.newBuilder()
//                .baseUri(URI.create(url))
//                .connectTimeout(400, TimeUnit.MILLISECONDS)
//                .build(AuthorsRestClient.class);

        BookDto dto = fromBook(book);

        AuthorDto authorDto = clientAuthors.getById(book.getAuthorId());
        String aname = String.format("%s, %s", authorDto.getLastName(),
                authorDto.getFirstName());
        dto.setAuthorName(aname);

        return Response.ok(dto).build();
    }

    @POST
    @Retry(maxRetries = 3)
    @Timeout(value = 500)
    @Operation(summary = "POST", description = "Create a book")
    @APIResponses(
            value = {
                    @APIResponse(responseCode = "200", description = "Book created"),
                    @APIResponse(responseCode = "404", description = "Method POST not found or something else...")
            }
    )
    public Response create(Book p) {
        rep.create(p);

        return Response.status(Response.Status.CREATED.getStatusCode(), "book created").build();
    }

    @PUT
    @Path("/{id}")
    @Retry(maxRetries = 3)
    @Timeout(value = 500)
    @Operation(summary = "PUT", description = "Update a book by id")
    @APIResponses(
            value = {
                    @APIResponse(responseCode = "200", description = "Book updated"),
                    @APIResponse(responseCode = "404", description = "Method PUT by id not found")
            }
    )
    public Response update(@PathParam("id") Long id, Book bookObj) {
        Book book = rep.findById(id);

        book.setIsbn(bookObj.getIsbn());
        book.setPrice(bookObj.getPrice());
        book.setTitle(bookObj.getTitle());

        //rep.persistAndFlush(book);

        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    @Retry(maxRetries = 3)
    @Timeout(value = 500)
    @Operation(summary = "DELETE", description = "Delete a book by id")
    @APIResponses(
            value = {
                    @APIResponse(responseCode = "200", description = "Book deleted"),
                    @APIResponse(responseCode = "404", description = "Method DELETE by id not found or something else...")
            }
    )
    public Response delete(@PathParam("id") Long id) {
        rep.delete(id);

        return Response.ok()
                .build();
    }


}
