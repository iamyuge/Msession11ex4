package com.example.inventoryservice.event;

import java.time.LocalDateTime;

public class OrderEvent {

    private Long orderId;
    private Long medicineId;
    private Integer quantity;
    private LocalDateTime timestamp;

    public OrderEvent() {
    }

    public OrderEvent(Long orderId, Long medicineId, Integer quantity, LocalDateTime timestamp) {
        this.orderId = orderId;
        this.medicineId = medicineId;
        this.quantity = quantity;
        this.timestamp = timestamp;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(Long medicineId) {
        this.medicineId = medicineId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}