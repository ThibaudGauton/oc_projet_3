package com.oc.projet3.rental.repository;

import com.oc.projet3.rental.model.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepository implements CrudRepository<User, Long> {
    @PersistenceContext
    private EntityManager entityManager;

    public Optional<User> findByUsername(String username) {
        String jpql = "SELECT u FROM User u WHERE u.name = :username OR u.email = :username";
        TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
        query.setParameter("username", username);

        return query.getResultStream().findFirst(); // returns Optional<User>
    }

    public Optional<User> findByNameOrEmail(String name, String email) {
        String jpql = "SELECT u FROM User u WHERE u.name = :name OR u.email = :email";
        TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
        query.setParameter("name", name);
        query.setParameter("email", email);

        return query.getResultStream().findFirst(); // returns Optional<User>
    }

    @Override
    public <S extends User> S save(S entity) {
        if (entity.getId() == null) {
            // New entity, persist it
            entityManager.persist(entity);
            return entity;
        } else {
            // Existing entity, merge it
            return entityManager.merge(entity);
        }
    }

    @Override
    public <S extends User> Iterable<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<User> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public Iterable<User> findAll() {
        return null;
    }

    @Override
    public Iterable<User> findAllById(Iterable<Long> longs) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public void delete(User entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends User> entities) {

    }

    @Override
    public void deleteAll() {

    }
}