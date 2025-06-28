package com.example.Restaurant.service;

import com.example.Restaurant.dto.CreateOrderRequest;
import com.example.Restaurant.dto.OrderDTO;
import com.example.Restaurant.model.Order; // Import Order model for potential internal use

import java.util.List;

public interface OrderService {
    /**
     * Creates a new order based on the provided request.
     *
     * @param createOrderRequest The DTO containing order details.
     * @return The created OrderDTO.
     */
    OrderDTO createOrder(CreateOrderRequest createOrderRequest);

    /**
     * Retrieves an order by its ID.
     *
     * @param id The ID of the order.
     * @return The OrderDTO if found, or null/throw exception if not.
     */
    OrderDTO getOrderById(Long id);

    /**
     * Retrieves all orders.
     *
     * @return A list of all OrderDTOs.
     */
    List<OrderDTO> getAllOrders();

    /**
     * Updates an existing order.
     * Note: Updating order items within an existing order can be complex and might require
     * specific business logic (e.g., disallowing changes after a certain status).
     * For simplicity, this method will handle basic order field updates for now.
     *
     * @param id The ID of the order to update.
     * @param orderDTO The DTO containing updated order details.
     * @return The updated OrderDTO.
     */
    OrderDTO updateOrder(Long id, OrderDTO orderDTO);

    /**
     * Deletes an order by its ID.
     *
     * @param id The ID of the order to delete.
     */
    void deleteOrder(Long id);

    // Optional: Methods for status updates, etc., can be added later
    // OrderDTO updateOrderStatus(Long id, OrderStatus newStatus);
}