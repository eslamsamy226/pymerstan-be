package com.pymerstan.server.repository;

import com.pymerstan.server.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CountryRepository extends JpaRepository<Country, Integer> {
    // Used for the public endpoint to hide disabled countries
    List<Country> findByVisibleTrue();
}