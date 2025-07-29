package com.example.Restaurant.service.impl;

import com.example.Restaurant.dto.CreateOrderRequest;
import com.example.Restaurant.dto.OrderDTO;
import com.example.Restaurant.mapper.OrderMapper;
import com.example.Restaurant.mapper.OrderItemMapper;
import com.example.Restaurant.model.Order;
import com.example.Restaurant.model.OrderItem;
import com.example.Restaurant.model.Product;
import com.example.Restaurant.repository.OrderItemRepository;
import com.example.Restaurant.repository.OrderRepository;
import com.example.Restaurant.repository.ProductRepository;
import com.example.Restaurant.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository,
                            ProductRepository productRepository,
                            OrderMapper orderMapper,
                            OrderItemMapper orderItemMapper) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
    }

    @Override
    @Transactional
    public OrderDTO createOrder(CreateOrderRequest createOrderRequest) {
        Order order = orderMapper.toEntity(createOrderRequest);
        order.setOrderDate(LocalDateTime.now());

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new java.util.ArrayList<>();

        if (createOrderRequest.getItems() != null && !createOrderRequest.getItems().isEmpty()) {
            for (com.example.Restaurant.dto.OrderItemRequestDTO itemRequest : createOrderRequest.getItems()) {
                Product product = productRepository.findById(itemRequest.getProductId())
                        .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + itemRequest.getProductId()));

                if (itemRequest.getQuantity() <= 0) {
                    throw new IllegalArgumentException("Quantity for product ID " + itemRequest.getProductId() + " must be greater than zero.");
                }

                OrderItem orderItem = OrderItem.builder()
                        .product(product)
                        .quantity(itemRequest.getQuantity())
                        .priceAtOrder(product.getPrice())
                        .order(order)
                        .build();

                orderItems.add(orderItem);
                totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
            }
        } else {
            throw new IllegalArgumentException("Order must contain at least one item.");
        }


        order.setOrderItems(orderItems);
        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + id));
        return orderMapper.toDto(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orderMapper.toDtoList(orders);
    }

    @Override
    @Transactional
    public OrderDTO updateOrder(Long id, OrderDTO orderDTO) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + id));

        existingOrder.setCustomerName(orderDTO.getCustomerName());
        existingOrder.setCustomerAddress(orderDTO.getCustomerAddress());
        Order updatedOrder = orderRepository.save(existingOrder);
        return orderMapper.toDto(updatedOrder);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new EntityNotFoundException("Order not found with ID: " + id);
        }
        orderRepository.deleteById(id);
    }
}