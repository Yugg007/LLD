package com.food.delivery.model;

import java.time.Instant;

public class Order {
    private Long orderId;
    private User user;
    private Inventory inventory;
    private Item item;
    private Long quantity;
    private Instant orderAt;
    private OrderStatus status;
    private DeliveryPartner deliveryPartner;

    public Order(User user, Inventory inventory, Item item, Long quantity, Instant orderAt, OrderStatus status) {
        this.user = user;
        this.inventory = inventory;
        this.item = item;
        this.quantity = quantity;
        this.orderAt = orderAt;
        this.status = status;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Instant getOrderAt() {
        return orderAt;
    }

    public void setOrderAt(Instant orderAt) {
        this.orderAt = orderAt;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public DeliveryPartner getDeliveryPartner() {
        return deliveryPartner;
    }

    public void setDeliveryPartner(DeliveryPartner deliveryPartner) {
        this.deliveryPartner = deliveryPartner;
    }
}
