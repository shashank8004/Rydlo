package com.rydlo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rydlo.entities.Owner;

public interface OwnerRepository extends JpaRepository<Owner, Long> {

    boolean existsByGstNumber(String gstNumber);

    Optional<Owner> findByUser_Id(Long userId);

}
