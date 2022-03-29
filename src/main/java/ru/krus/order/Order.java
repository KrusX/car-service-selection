package ru.krus.order;

import ru.krus.request.Request;
import ru.krus.response.Response;
import ru.krus.service.CarService;

import javax.persistence.*;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

@SuppressWarnings("serial")
@Entity
@Table(name = "orders")
public class Order implements java.io.Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    private Long id;

    @Column(name = "created", nullable = false)
    private Instant created;

    @Column(name = "closed")
    private Instant closed;

    @Column(name = "rate")
    private Integer rate;

    @Column(name = "order_is_closed")
    private Boolean orderIsClosed;

    @Column(name = "service_close", nullable = false)
    private Boolean serviceClose;

    @Column(name = "customer_accept", nullable = false)
    private Boolean customerAccept;

    @Column(name = "service_delete_order", nullable = false)
    private Boolean serviceDeleteOrderStatus;

    @Column(name = "customer_delete_order", nullable = false)
    private Boolean customerDeleteOrderStatus;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "days", nullable = false)
    private Integer days;

    @Column(name = "start_date", nullable = false)
    private Date startDate;

    @Column(name = "info", columnDefinition="TEXT")
    private String info;

    @OneToOne
    @JoinColumn(name="request_Id", unique = true, updatable = false)
    private Request request;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE, targetEntity = CarService.class)
    @JoinColumn(name = "carService_id", nullable = false)
    private CarService carService;

    public Order() {
        this.setServiceDeleteOrderStatus(false);
        this.setCustomerDeleteOrderStatus(false);
        this.setServiceClose(false);
        this.setCustomerAccept(false);
        this.created = Instant.now();
    }

    public Order(Request request,
                 CarService carService,
                 Integer price,
                 Integer days,
                 Date startDate,
                 String info){
        this.request = request;
        this.carService = carService;
        this.price = price;
        this.days = days;
        this.startDate = startDate;
        this.info = info;
        this.created = Instant.now();
        this.setServiceDeleteOrderStatus(false);
        this.setCustomerDeleteOrderStatus(false);
        this.setServiceClose(false);
        this.setCustomerAccept(false);
        this.setOrderIsClosed(false);
    }

    public Long getId() { return id; }

    public Instant getCreated() { return created; }

    public Instant getClosed() { return closed; }

    public void setClosed(Instant closed) { this.closed = closed; }

    public Boolean getServiceClose() { return serviceClose; }

    public void setServiceClose(Boolean serviceClose) { this.serviceClose = serviceClose; }

    public Boolean getCustomerAccept() { return customerAccept; }

    public void setCustomerAccept(Boolean customerAccept) { this.customerAccept = customerAccept; }

    public Boolean getServiceDeleteOrderStatus() { return serviceDeleteOrderStatus; }

    public void setServiceDeleteOrderStatus(Boolean serviceDeleteOrderStatus) { this.serviceDeleteOrderStatus = serviceDeleteOrderStatus; }

    public Boolean getCustomerDeleteOrderStatus() { return customerDeleteOrderStatus; }

    public void setCustomerDeleteOrderStatus(Boolean customerDeleteOrderStatus) { this.customerDeleteOrderStatus = customerDeleteOrderStatus; }

    public Integer getPrice() { return price; }

    public void setPrice(Integer price) { this.price = price; }

    public Integer getDays() { return days; }

    public void setDays(Integer days) { this.days = days; }

    public Date getStartDate() { return startDate; }

    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public String getInfo() { return info; }

    public void setInfo(String info) { this.info = info; }

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    public Boolean getOrderIsClosed() {
        return orderIsClosed;
    }

    public void setOrderIsClosed(Boolean orderIsClosed) {
        this.orderIsClosed = orderIsClosed;
    }

    public CarService getCarService() { return carService; }

    public void setCarService(CarService carService) { this.carService = carService; }

    public Request getRequest() { return request; }

    public void setRequest(Request request) { this.request = request; }
}
