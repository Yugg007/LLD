package com.food.delivery.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.food.delivery.model.DeliveryPartner;

public class InMemoryDeliveryPartnerRepository implements DeliveryPartnerRepository {
    private final ConcurrentHashMap<Long, DeliveryPartner> partnerStore = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(0);

    @Override
    public DeliveryPartner save(DeliveryPartner partner) {
        if (partner.getPartnerId() == null) {
            partner.setPartnerId(idGen.incrementAndGet());
        }
        partnerStore.put(partner.getPartnerId(), partner);
        return partner;
    }

    @Override
    public List<DeliveryPartner> findAll() {
        return new ArrayList<>(partnerStore.values());
    }

    @Override
    public void delete(Long id) {
        partnerStore.remove(id);
    }

    @Override
    public Optional<DeliveryPartner> findById(Long id) {
        return Optional.ofNullable(partnerStore.get(id));
    }
}
