package com.data.covid.repositories;


import com.data.covid.models.Country;
import com.data.covid.models.CovidVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface CovidVariantRepository extends JpaRepository<CovidVariant, Long> {
    Optional<CovidVariant> findById(Long id);
    Optional<List<CovidVariant>> findByName(String name);
    Optional<CovidVariant> findByNameAndCountryCode(String name, Country country);
    boolean existsByName(String name);

}
