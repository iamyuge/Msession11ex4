package com.example.inventoryservice.controller;

import com.example.inventoryservice.entity.Medicine;
import com.example.inventoryservice.service.MedicineService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/medicines")
public class MedicineController {

    private final MedicineService medicineService;

    public MedicineController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    @GetMapping("/{id}")
    public Medicine getMedicine(@PathVariable Long id) {
        return medicineService.getMedicine(id);
    }

    @PutMapping("/{id}")
    public Medicine updateMedicine(
            @PathVariable Long id,
            @RequestBody Medicine medicine) {

        medicine.setId(id);

        return medicineService.updateMedicine(medicine);
    }

    @PostMapping("/{id}/sell")
    public String sellMedicine(
            @PathVariable Long id,
            @RequestParam Integer quantity) {

        return medicineService.sellMedicine(id, quantity);
    }
}