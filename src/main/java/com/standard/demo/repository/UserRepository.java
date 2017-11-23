package com.standard.demo.repository;


import com.standard.demo.domain.User;
import org.springframework.data.repository.CrudRepository;

/**
 * User repository
 */
public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);
}
