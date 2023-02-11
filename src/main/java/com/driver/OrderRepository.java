package com.driver;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class OrderRepository {

    Map<String, Order> orderDB; // <ID, Order>
    Map<String, DeliveryPartner> deliveryPartnerDB; // <ID, DeliveryPartner>
    Map<String, List<String>> pairDB; // <PartnerID, List of OrderID>
    Map<String, String> assignedDB; // <OrderID, partnerID>

    public OrderRepository() {
        this.orderDB = new HashMap<>();
        this.deliveryPartnerDB = new HashMap<>();
        this.pairDB = new HashMap<>();
        this.assignedDB = new HashMap<>();
    }

    public void addOrder(Order order) {
        orderDB.put(order.getId(), order);
    }

    public void addPartner(String partnerId) {
        deliveryPartnerDB.put(partnerId, new DeliveryPartner(partnerId));
    }

    public void addOrderPartnerPair(String orderId, String partnerId) {
        List<String> list = pairDB.getOrDefault(partnerId, new ArrayList<>());
        list.add(orderId);
        pairDB.put(partnerId, list);
        assignedDB.put(orderId, partnerId);
        DeliveryPartner deliveryPartner = deliveryPartnerDB.get(partnerId);
        deliveryPartner.setNumberOfOrders(list.size());
    }

    public Order getOrderById(String orderId) {
        if(orderDB.containsKey(orderId)) {
            return orderDB.get(orderId);
        }
        return null;
    }

    public DeliveryPartner getPartnerById(String partnerId) {
        if(deliveryPartnerDB.containsKey(partnerId)) {
            return deliveryPartnerDB.get(partnerId);
        }
        return null;
    }

    public int getOrderCountByPartnerId(String partnerId) {
        return pairDB.getOrDefault(partnerId, new ArrayList<>()).size();
    }

    public List<String> getOrdersByPartnerId(String partnerId) {
        return pairDB.getOrDefault(partnerId, new ArrayList<>());
    }

    public List<String> getAllOrders() {
        List<String> orderList = new ArrayList<>();
        for(String str: orderDB.keySet()) {
            orderList.add(str);
        }
        return orderList;
    }

    public int getCountOfUnassignedOrders() {
        return orderDB.size() - assignedDB.size();
    }

    public int getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId) {
        int countOfOrders = 0;
        List<String> list = pairDB.get(partnerId);
        int deliveryTime = Integer.parseInt(time.substring(0,2)) * 60 + Integer.parseInt((time.substring(3)));
        for(String str: list) {
            Order order = orderDB.get(str);
            if(order.getDeliveryTime() > deliveryTime) {
                countOfOrders++;
            }
        }
        return countOfOrders;
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId) {
        String time = "";
        List<String> list = pairDB.get(partnerId);
        int deliveryTime = 0;
        for(String str: list) {
            Order order = orderDB.get(str);
            deliveryTime = Math.max(deliveryTime, order.getDeliveryTime());
        }
        int hour = deliveryTime / 60;
        String sHour = "";
        if(hour < 10) {
            sHour = "0" + String.valueOf(hour);
        } else {
            sHour = String.valueOf(hour);
        }
        int min = deliveryTime % 60;
        String sMin = "";
        if(min < 10) {
            sMin = "0" + String.valueOf(min);
        } else {
            sMin = String.valueOf(min);
        }
        time = sHour + ":" + sMin;
        return time;
    }

    public void deletePartnerById(String partnerId) {
        deliveryPartnerDB.remove(partnerId);
        List<String> list = pairDB.getOrDefault(partnerId, new ArrayList<>());
        ListIterator<String> itr = list.listIterator();
        while(itr.hasNext()) {
            String str = itr.next();
            assignedDB.remove(str);
        }
        pairDB.remove(partnerId);
    }

    public void deleteOrderById(String orderId) {
        orderDB.remove(orderId);
        String partnerId = assignedDB.get(orderId);
        assignedDB.remove(orderId);
        List<String> list = pairDB.get(partnerId);
        ListIterator<String> itr = list.listIterator();
        while(itr.hasNext()) {
            String str = itr.next();
            if(str.equals(orderId)) {
                itr.remove();
            }
        }
        pairDB.put(partnerId, list);
    }
}
