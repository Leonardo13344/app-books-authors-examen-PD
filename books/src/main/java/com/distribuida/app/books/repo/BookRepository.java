package com.distribuida.app.books.repo;

import com.distribuida.app.books.db.Book;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@ApplicationScoped
public class BookRepository {

    @PersistenceContext
    EntityManager em;

    public List<Book> findAll() {
        return em.createQuery("SELECT b FROM Book b", Book.class).getResultList();
    }

    public Book findById(Long id) {
        return em.find(Book.class, id);
    }

    public void create(Book p) {
        em.persist(p);
    }

    public void update(Book p) {
        em.merge(p);
    }

    public void delete(Long id) {
        em.remove(findById(id));
    }

}
