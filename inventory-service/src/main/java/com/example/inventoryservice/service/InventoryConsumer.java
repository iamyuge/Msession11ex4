package com.example.inventoryservice.service;

import com.example.inventoryservice.entity.Medicine;
import com.example.inventoryservice.entity.Order;
import com.example.inventoryservice.event.OrderEvent;
import com.example.inventoryservice.repository.MedicineRepository;
import com.example.inventoryservice.repository.OrderRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryConsumer {

    private final MedicineRepository medicineRepository;
    private final OrderRepository orderRepository;

    public InventoryConsumer(
            MedicineRepository medicineRepository,
            OrderRepository orderRepository) {
        this.medicineRepository = medicineRepository;
        this.orderRepository = orderRepository;
    }

    @KafkaListener(
            topics = "medicine-stock-events",
            groupId = "inventory-service-group"
    )
    @Transactional
    public void consumeOrderEvent(OrderEvent event) {

        System.out.println("Đã nhận OrderEvent:");
        System.out.println("orderId: " + event.getOrderId());
        System.out.println("medicineId: " + event.getMedicineId());
        System.out.println("quantity: " + event.getQuantity());

        Medicine medicine = medicineRepository
                .findById(event.getMedicineId())
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy thuốc: " + event.getMedicineId()));

        if (medicine.getStock() < event.getQuantity()) {
            throw new RuntimeException("Không đủ số lượng tồn kho");
        }

        medicine.setStock(medicine.getStock() - event.getQuantity());

        medicineRepository.save(medicine);

        Order order = new Order();
        order.setId(event.getOrderId());
        order.setMedicineId(event.getMedicineId());
        order.setQuantity(event.getQuantity());
        order.setTimestamp(event.getTimestamp());

        orderRepository.save(order);

        System.out.println("Đã lưu đơn hàng và trừ tồn kho thành công");
    }
}