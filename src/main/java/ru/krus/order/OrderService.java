package ru.krus.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.krus.request.Request;
import ru.krus.service.CarService;

import java.time.Instant;
import java.util.List;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public void save(Order order, Request request, CarService carService) {
        order.setCarService(carService);
        order.setRequest(request);
        request.setClosed(true);
        orderRepository.save(order);
    }

    @Transactional
    public Order findById( Long id ) {
        return orderRepository.findById(id);
    }

    @Transactional
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public List<Order> findAllActive() { return orderRepository.findAllNotClosed(); }

    public List<Order> findAllClosed() { return orderRepository.findAllClosed(); }

    @Transactional
    public List<Order> findAll(CarService carService) {
        return orderRepository.findAllByCarServiceOrderByCreatedDesc(carService);
    }

    public List<Order> findAllActive(CarService carService) {
        return orderRepository.findAllByCarServiceAndCustomerAcceptIsFalseOrderByCreatedDesc(carService);
    }

    public List<Order> findAllClosed(CarService carService) {
        return orderRepository.findAllByCarServiceAndCustomerAcceptIsTrueOrderByCreatedDesc(carService);
    }

    @Transactional
    public List<Order> findAll(String email) {
        return orderRepository.findAllByRequest_Account_EmailOrderByCreatedDesc(email);
    }

    public List<Order> findAllActive(String email) {
        return orderRepository.findAllByRequest_Account_EmailAndAndCustomerAcceptIsFalseOrderByCreatedDesc(email);
    }

    public List<Order> findAllClosed(String email) {
        return orderRepository.findAllByRequest_Account_EmailAndAndCustomerAcceptIsTrueOrderByCreatedDesc(email);
    }

    @Transactional
    public void delete(Long id) {
        orderRepository.delete(id);
    }

    @Transactional
    public Order changeAccept(Long id, Boolean s, Order order) {
        orderRepository.accept(id, s);
        order.setCustomerAccept(s);
        return order;
    }

    @Transactional
    public Order changeClose(Long id, Boolean s, Order order) {
        orderRepository.close(id, s);
        order.setServiceClose(s);
        return order;
    }

    @Transactional
    public Order changeCustomerDelete(Long id, Boolean s, Order order) {
        orderRepository.customerDeleteOrderStatus(id, s);
        order.setCustomerDeleteOrderStatus(s);
        return order;
    }

    @Transactional
    public Order changeServiceDelete(Long id, Boolean s, Order order) {
        orderRepository.serviceDeleteOrderStatus(id, s);
        order.setServiceDeleteOrderStatus(s);
        return order;
    }

    @Transactional
    public Order closeOrder(Long id, Order order) {
        orderRepository.closeOrder(id, Instant.now());
        order.setClosed(Instant.now());
        return order;
    }

    @Transactional
    public Order updateRate(Order order, Integer rate) {
        orderRepository.updateRate(order.getId(), rate);
        order.setRate(rate);
        return order;
    }
}
