package com.example.inventoryservice.service;

import com.example.inventoryservice.entity.Medicine;
import com.example.inventoryservice.repository.MedicineRepository;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class MedicineService {

    private final MedicineRepository medicineRepository;
    private final RedissonClient redissonClient;

    public MedicineService(
            MedicineRepository medicineRepository,
            RedissonClient redissonClient) {
        this.medicineRepository = medicineRepository;
        this.redissonClient = redissonClient;
    }

    @Cacheable(value = "medicines", key = "#id")
    public Medicine getMedicine(Long id) {

        System.out.println(
                "Đang truy vấn PostgreSQL cho medicineId = " + id
        );

        return medicineRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy thuốc với id: " + id
                        )
                );
    }

    @CacheEvict(value = "medicines", key = "#medicine.id")
    public Medicine updateMedicine(Medicine medicine) {

        System.out.println(
                "Đang cập nhật thuốc medicineId = " + medicine.getId()
        );

        return medicineRepository.save(medicine);
    }

    public String sellMedicine(Long id, Integer quantity) {

        RLock lock = redissonClient.getLock(
                "lock:medicine:" + id
        );

        boolean locked = false;

        try {
            locked = lock.tryLock(
                    3,
                    5,
                    TimeUnit.SECONDS
            );

            if (!locked) {
                return "Không thể lấy Lock, vui lòng thử lại";
            }

            Medicine medicine = medicineRepository.findById(id)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Không tìm thấy thuốc với id: " + id
                            )
                    );

            if (medicine.getStock() < quantity) {
                return "Hết hàng";
            }

            medicine.setStock(
                    medicine.getStock() - quantity
            );

            medicineRepository.save(medicine);

            return "Bán thành công " + quantity
                    + " hộp " + medicine.getName()
                    + ". Tồn kho còn: "
                    + medicine.getStock();

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            return "Giao dịch bị gián đoạn";

        } finally {

            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}