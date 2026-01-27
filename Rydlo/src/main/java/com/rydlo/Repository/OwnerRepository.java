package com.rydlo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rydlo.entities.Owner;

public interface OwnerRepository extends JpaRepository<Owner, Long> {

	boolean existsByGstNumber(String gstNumber);
    java.util.Optional<Owner> findByUser_Id(Long userId);

}
