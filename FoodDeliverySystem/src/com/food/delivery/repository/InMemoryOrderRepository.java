package com.food.delivery.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.food.delivery.model.Order;

public class InMemoryOrderRepository implements OrderRepository {
	private ConcurrentHashMap<Long, Order> orderStore = new ConcurrentHashMap<>();
	private AtomicLong idGen = new AtomicLong(0);
	
	@Override
	public Order save(Order order) {
		// TODO Auto-generated method stub
		if(order.getOrderId() == null) {
			order.setOrderId(idGen.incrementAndGet());
		}
		orderStore.put(order.getOrderId(), order);
		return order;
	}

	@Override
	public List<Order> findAll() {
		// TODO Auto-generated method stub
		return new ArrayList<>(orderStore.values());
	}

	@Override
	public void delete(Long id) {
		// TODO Auto-generated method stub
		orderStore.remove(id);
		
	}

	@Override
	public Optional<Order> findById(Long id) {
		// TODO Auto-generated method stub
		if(!orderStore.containsKey(id)) {
			return Optional.empty();
		}
		return Optional.of(orderStore.get(id));
	}

}
