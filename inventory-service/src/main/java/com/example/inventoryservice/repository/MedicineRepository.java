package com.example.inventoryservice.repository;

import com.example.inventoryservice.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {
}