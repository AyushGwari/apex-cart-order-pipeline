package com.apexcart.order.exception;

public class OrderNotFoundException extends RuntimeException{
    public OrderNotFoundException(Long id){
        super("Order not found with this ID: "+id);
    }
}
