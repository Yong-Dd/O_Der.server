package com.yongdd.o_der_re.server;

public class OrderId {
    String orderId;
    Order order;

    public OrderId(String orderId, Order order) {
        this.orderId = orderId;
        this.order = order;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
