package com.pymerstan.server.repository;

import com.pymerstan.server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByRoles_Name(String roleName);
    List<User> findByRoles_NameIn(Collection<String> roleNames);
    boolean existsByUsername(String username);
}