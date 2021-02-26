package com.yongdd.o_der_re.server;

public class OrderClient {
    String customerName;
    String customerPhoneNumber;
    String orderId;
    Order order;

    public OrderClient(String customerName, String customerPhoneNumber, String orderId, Order order) {
        this.customerName = customerName;
        this.customerPhoneNumber = customerPhoneNumber;
        this.orderId = orderId;
        this.order = order;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }

    public void setCustomerPhoneNumber(String customerPhoneNumber) {
        this.customerPhoneNumber = customerPhoneNumber;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
