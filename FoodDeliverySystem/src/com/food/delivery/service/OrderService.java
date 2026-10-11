package com.food.delivery.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.food.delivery.model.DeliveryPartner;
import com.food.delivery.model.Inventory;
import com.food.delivery.model.Item;
import com.food.delivery.model.Order;
import com.food.delivery.model.OrderStatus;
import com.food.delivery.model.User;
import com.food.delivery.repository.InMemoryOrderRepository;
import com.food.delivery.repository.OrderRepository;

public class OrderService {
	private final OrderRepository orderRepository;
	private final InventoryService inventoryService;
	private final DeliveryPartnerService deliveryPartnerService;

	public OrderService() {
		this.orderRepository = new InMemoryOrderRepository();
		this.inventoryService = new InventoryService();
		this.deliveryPartnerService = new DeliveryPartnerService();
	}

	public OrderService(OrderRepository orderRepository, InventoryService inventoryService,
			DeliveryPartnerService deliveryPartnerService) {
		this.orderRepository = orderRepository;
		this.inventoryService = inventoryService;
		this.deliveryPartnerService = deliveryPartnerService;
	}


	public Order placeOrder(User user, Inventory inventory, Item item, long quantity) {
		if (user == null || inventory == null || item == null) {
			throw new IllegalArgumentException("User, inventory and item are required to place an order");
		}

		boolean reserved = inventoryService.tryReserveItem(inventory, item, quantity);
		if (!reserved) {
			throw new IllegalStateException("Insufficient inventory for item " + item.getItemName());
		}

		Order order = new Order(user, inventory, item, quantity, Instant.now(), OrderStatus.PENDING_ASSIGNMENT);
		order = orderRepository.save(order);

		try {
			Optional<DeliveryPartner> assigned = deliveryPartnerService.assignAvailablePartner(order);
			if (assigned.isPresent()) {
				order.setDeliveryPartner(assigned.get());
				order.setStatus(OrderStatus.ASSIGNED);
				orderRepository.save(order);
			}
			return order;
		} catch (RuntimeException e) {
			inventoryService.restoreReservedItem(inventory, item, quantity);
			throw new IllegalStateException("Order placement failed while assigning delivery partner", e);
		}
	}

	public List<Order> getAllOrders() {
		return orderRepository.findAll();
	}

	public Optional<Order> getOrder(Long orderId) {
		return orderRepository.findById(orderId);
	}
	
	
	

}
