package com.yongdd.o_der_re.server;

public class OrderClient {
    String customerName;
    String customerPhoneNumber;
    Order order;

    public OrderClient(String customerName, String customerPhoneNumber, Order order) {
        this.customerName = customerName;
        this.customerPhoneNumber = customerPhoneNumber;
        this.order = order;
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
