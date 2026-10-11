package com.food.delivery;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.food.delivery.model.Inventory;
import com.food.delivery.model.Item;
import com.food.delivery.model.Order;
import com.food.delivery.model.Restaurant;
import com.food.delivery.model.User;
import com.food.delivery.repository.InMemoryOrderRepository;
import com.food.delivery.service.DeliveryPartnerService;
import com.food.delivery.service.InventoryService;
import com.food.delivery.service.ItemService;
import com.food.delivery.service.OrderService;
import com.food.delivery.service.RestaurantService;
import com.food.delivery.service.UserService;

public class Demo {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        UserService userService = new UserService();
        RestaurantService restaurantService = new RestaurantService();
        ItemService itemService = new ItemService();
        InventoryService inventoryService = new InventoryService();
        DeliveryPartnerService deliveryPartnerService = new DeliveryPartnerService();
        OrderService orderService = new OrderService(new InMemoryOrderRepository(), inventoryService, deliveryPartnerService);

        User user1 = userService.saveUser("User1");
        User user2 = userService.saveUser("User2");

        Restaurant restaurant1 = restaurantService.saveRestaurant("R1");

        Item menuItem = itemService.saveItem("I1", 6L, restaurant1);

        Inventory inventory = inventoryService.saveInventory("Inv1");
        Item stockItem = new Item(menuItem.getItemName(), 20L, menuItem.getRestaurant(), menuItem.getItemId());
        inventoryService.addItemToInventory(inventory, stockItem);

        deliveryPartnerService.savePartner("Driver1");
        deliveryPartnerService.savePartner("Driver2");

        ExecutorService executor = Executors.newFixedThreadPool(3);
        List<Callable<Order>> tasks = new ArrayList<>();

        tasks.add(() -> orderService.placeOrder(user1, inventory, menuItem, 3L));
        tasks.add(() -> orderService.placeOrder(user2, inventory, menuItem, 4L));
        tasks.add(() -> orderService.placeOrder(user1, inventory, menuItem, 2L));

        for (Future<Order> future : executor.invokeAll(tasks)) {
            Order order = future.get();
            System.out.println("Order " + order.getOrderId() + " status=" + order.getStatus()
                    + " assignedTo=" + (order.getDeliveryPartner() != null ? order.getDeliveryPartner().getName() : "none"));
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
    }
}
