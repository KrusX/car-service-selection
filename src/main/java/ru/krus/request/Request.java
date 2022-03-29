package ru.krus.request;

import ru.krus.account.Account;
import ru.krus.order.Order;
import ru.krus.response.Response;

import javax.persistence.*;
import java.time.Instant;
import java.util.Collection;

@SuppressWarnings("serial")
@Entity
@Table(name = "requests")
public class Request implements java.io.Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "Id", nullable = false, unique=true)
    private Long id;

    @Column(name = "town", nullable = false)
    private String town;

    @Column(name = "car_type", nullable = false)
    private String carType;

    @Column(name = "brand", nullable = false)
    private String brand;

    @Column(name = "model", nullable = false)
    private String model;

    @Column(name = "car_year", nullable = false)
    private Integer carYear;

    @Column(name = "problem", nullable = false, columnDefinition="TEXT")
    private String problem;

    @Column(name = "closed", nullable = false)
    private Boolean closed;

    @Column(name = "created")
    private Instant created;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE, targetEntity = Account.class)
    @JoinColumn(name = "account_id", nullable = false, insertable=true)
    private Account account;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "request")
    private Collection<Response> responses;

    @OneToOne(mappedBy = "request")
    private Order order;

    public Request() {
        this.created = Instant.now();
    }

    public Request(String town,
                   String carType,
                   String brand,
                   String model,
                   Integer carYear,
                   String problem,
                   Boolean closed,
                   Account account) {
        this.town = town;
        this.carType = carType;
        this.brand = brand;
        this.model = model;
        this.carYear = carYear;
        this.problem = problem;
        this.closed = closed;
        this.account = account;
        this.created = Instant.now();
    }

    public Long getId() { return id; }

    public String getTown() { return town; }

    public void setTown(String town) { this.town = town; }

    public String getCarType() { return carType; }

    public void setCarType(String carType) { this.carType = carType; }

    public String getBrand() { return brand; }

    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }

    public void setModel(String model) { this.model = model; }

    public Integer getCarYear() { return carYear; }

    public void setCarYear(Integer carYear) { this.carYear = carYear; }

    public String getProblem() { return problem; }

    public void setProblem(String problem) { this.problem = problem; }

    public Boolean getClosed() { return closed; }

    public void setClosed(Boolean closed) { this.closed = closed; }

    public Account getAccount() { return account; }

    public void setAccount(Account account) { this.account = account; }

    public Instant getCreated() { return created; }

    public Collection<Response> getResponses() { return responses; }

    public void setResponses(Collection<Response> responses) { this.responses = responses; }

    public Order getOrder() { return order; }

    public void setOrder(Order order) { this.order = order; }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
