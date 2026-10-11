package com.food.delivery.model;

import java.util.concurrent.atomic.AtomicBoolean;

public class DeliveryPartner {
    private Long partnerId;
    private final String name;
    private final AtomicBoolean available = new AtomicBoolean(true);
    private volatile Long currentOrderId;

    public DeliveryPartner(String name) {
        this.name = name;
    }

    public Long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Long partnerId) {
        this.partnerId = partnerId;
    }

    public String getName() {
        return name;
    }

    public boolean assignToOrder(Long orderId) {
        if (available.compareAndSet(true, false)) {
            this.currentOrderId = orderId;
            return true;
        }
        return false;
    }

    public void release() {
        this.currentOrderId = null;
        this.available.set(true);
    }

    public boolean isAvailable() {
        return available.get();
    }

    public Long getCurrentOrderId() {
        return currentOrderId;
    }
}
