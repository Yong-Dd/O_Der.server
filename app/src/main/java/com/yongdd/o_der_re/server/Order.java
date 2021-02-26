package com.yongdd.o_der_re.server;

import java.util.ArrayList;

public class Order {
   String userId;
   ArrayList<Payment> orderMenus;
   int totalPrice;
   String orderDate;
   String orderReceivedTime;
   String orderAcceptedTime;
   String orderCompletedTime;
   String orderMemo;

   public Order(){}

    public Order(String userId, ArrayList<Payment> orderMenus, int totalPrice, String orderDate, String orderReceivedTime, String orderAcceptedTime, String orderCompletedTime, String orderMemo) {
        this.userId = userId;
        this.orderMenus = orderMenus;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
        this.orderReceivedTime = orderReceivedTime;
        this.orderAcceptedTime = orderAcceptedTime;
        this.orderCompletedTime = orderCompletedTime;
        this.orderMemo = orderMemo;
    }

    public String getOrderMemo() {
        return orderMemo;
    }

    public void setOrderMemo(String orderMemo) {
        this.orderMemo = orderMemo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ArrayList<Payment> getOrderMenus() {
        return orderMenus;
    }

    public void setOrderMenus(ArrayList<Payment> orderMenus) {
        this.orderMenus = orderMenus;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderReceivedTime() {
        return orderReceivedTime;
    }

    public void setOrderReceivedTime(String orderReceivedTime) {
        this.orderReceivedTime = orderReceivedTime;
    }

    public String getOrderAcceptedTime() {
        return orderAcceptedTime;
    }

    public void setOrderAcceptedTime(String orderAcceptedTime) {
        this.orderAcceptedTime = orderAcceptedTime;
    }

    public String getOrderCompletedTime() {
        return orderCompletedTime;
    }

    public void setOrderCompletedTime(String orderCompletedTime) {
        this.orderCompletedTime = orderCompletedTime;
    }
}
