package com.rydlo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rydlo.entities.PickupLocation;

public interface PickupLocationRepository extends JpaRepository<PickupLocation, Long> {

}
