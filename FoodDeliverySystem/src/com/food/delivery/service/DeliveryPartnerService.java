package com.food.delivery.service;

import java.util.List;
import java.util.Optional;

import com.food.delivery.model.DeliveryPartner;
import com.food.delivery.model.Order;
import com.food.delivery.repository.DeliveryPartnerRepository;
import com.food.delivery.repository.InMemoryDeliveryPartnerRepository;

public class DeliveryPartnerService {
    private final DeliveryPartnerRepository repository;

    public DeliveryPartnerService() {
        this.repository = new InMemoryDeliveryPartnerRepository();
    }

    public DeliveryPartner savePartner(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Partner name cannot be blank");
        }
        DeliveryPartner partner = new DeliveryPartner(name);
        return repository.save(partner);
    }

    public Optional<DeliveryPartner> assignAvailablePartner(Order order) {
        if (order == null || order.getOrderId() == null) {
            return Optional.empty();
        }

        List<DeliveryPartner> partners = repository.findAll();
        for (DeliveryPartner partner : partners) {
            if (partner.assignToOrder(order.getOrderId())) {
                repository.save(partner);
                return Optional.of(partner);
            }
        }
        return Optional.empty();
    }

    public void releasePartner(DeliveryPartner partner) {
        if (partner != null) {
            partner.release();
            repository.save(partner);
        }
    }
}
