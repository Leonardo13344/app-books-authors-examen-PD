package com.distribuida.books.authors.repo;

import com.distribuida.books.authors.db.Author;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@ApplicationScoped
public class AuthorRepository {

    @PersistenceContext
    EntityManager em;

    public List<Author> findAll() {
        return em.createQuery("SELECT a FROM Author a", Author.class).getResultList();
    }

    public Author findById(Long id) {
        return em.find(Author.class, id);
    }

    public void create(Author p) {
        em.persist(p);
    }

    public void update(Author p) {
        em.merge(p);
    }

    public void delete(Long id) {
        em.remove(findById(id));
    }



}
