package com.food.delivery.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.food.delivery.model.Address;

public class InMemoryAddressRepository implements AddressRepository {
	private ConcurrentHashMap<Long, Address> addressStore = new ConcurrentHashMap<>();
	private AtomicLong idGen = new AtomicLong(0);
	
	@Override
	public Address save(Address address) {
		// TODO Auto-generated method stub
		if(address.getAddressId() == null) {
			address.setAddressId(idGen.incrementAndGet());
		}
		addressStore.put(address.getAddressId(), address);
		return address;
	}

	@Override
	public List<Address> findAll() {
		// TODO Auto-generated method stub
		return new ArrayList<>(addressStore.values());
	}

	@Override
	public void delete(Long id) {
		// TODO Auto-generated method stub
		addressStore.remove(id);
		
	}

	@Override
	public Optional<Address> findById(Long id) {
		// TODO Auto-generated method stub
		if(!addressStore.containsKey(id)) {
			return Optional.empty();
		}
		
		return Optional.of(addressStore.get(id));
		
	}
}
