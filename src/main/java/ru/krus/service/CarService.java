package ru.krus.service;

import java.text.DecimalFormat;
import java.time.ZonedDateTime;

import javax.persistence.*;

import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bson.codecs.StringCodec;
import ru.krus.account.Account;
import ru.krus.order.Order;
import ru.krus.request.Request;
import ru.krus.response.Response;

@SuppressWarnings("serial")
@Entity
@Table(name = "services")
public class CarService implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column( name =  "name", nullable = false)
    private String name;

    @Column( name =  "town", nullable = false)
    private String town;

    @Column( name =  "address", nullable = false)
    private String address;

    @Column( name =  "phone", nullable = false)
    private String phone;

    @Column( name =  "info", nullable = false, columnDefinition="TEXT")
    private String info;

    @Column( name =  "closed_orders")
    private Integer closedOrders;

    @Column( name =  "rate_amount")
    private Integer rateAmount;

    @Column( name =  "rate_sum")
    private Double rateSum;

    @Column( name =  "rating")
    private Double rating;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private Account account;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "carService")
    private Collection<Response> responses;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "carService")
    private Collection<Order> orders;

    public CarService() {
    }

    public CarService(String name,
                      String town,
                      String address,
                      String phone,
                      String info) {
        this.name = name;
        this.town = town;
        this.address = address;
        this.phone = phone;
        this.info = info;
    }

    public Long getId() {
        return id;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getTown() { return town; }

    public void setTown(String town) { this.town = town; }

    public String getAddress() { return address; }

    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }

    public void setPhone(String phone) { this.phone = phone; }

    public String getInfo() { return info; }

    public void setInfo(String info) { this.info = info; }

    public Integer getClosedOrders() { return closedOrders; }

    public void setClosedOrders(Integer closedOrders) { this.closedOrders = closedOrders; }

    public void setClosedOrders() {
        if (this.closedOrders == null) {
            this.closedOrders = 1;
        } else {
            this.closedOrders += 1;
        }
    }

    public Integer getRateAmount() { return rateAmount; }

    public void setRateAmount(Integer rateAmount) { this.rateAmount = rateAmount; }

    public void setRateAmount() {
        if (this.rateAmount == null) {
            this.rateAmount = 1;
        } else {
            this.rateAmount += 1;
        }
    }

    public Double getRateSum() { return rateSum; }

    public void setRateSum(Double rateSum) { this.rateSum = rateSum; }

    public void setRateSum(Integer rate) {
        if (this.rateSum == null) {
            this.rateSum = (double)rate;
        } else {
            this.rateSum += (double)rate;
        }
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) { this.rating = rating; }

    public void setRating() {
        double r = this.rateSum / (double)this.rateAmount;
        this.rating = (double) Math.round(r * 10) / 10;
    }

    public Account getAccount() { return account; }

    public void setAccount( Account account) { this.account = account; }

    public Collection<Response> getResponses() { return responses; }

    public void setResponses(Collection<Response> responses) { this.responses = responses; }

    public Collection<Order> getOrders() { return orders; }

    public void setOrders(Collection<Order> orders) { this.orders = orders; }
}
