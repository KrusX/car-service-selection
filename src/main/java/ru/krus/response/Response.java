package ru.krus.response;

import ru.krus.request.Request;
import ru.krus.service.CarService;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;

@SuppressWarnings("serial")
@Entity
@Table(name = "responses")
public class Response implements java.io.Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    private Long id;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "days", nullable = false)
    private Integer days;

    @Column(name = "start_date", nullable = false)
    private Date startDate;

    @Column(name = "info", columnDefinition="TEXT")
    private String info;

    @Column(name = "created")
    private Instant created;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE, targetEntity = CarService.class)
    @JoinColumn(name = "carService_id", nullable = false)
    private CarService carService;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE, targetEntity = Request.class)
    @JoinColumn(name = "request_id", nullable = false)
    private Request request;

    private String date;
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    public Response() {
        this.created = Instant.now();
    }

    public  Response(Integer price,
                     Integer days,
                     Date startDate,
                     CarService carService,
                     Request request){
        this.price = price;
        this.days = days;
        this.startDate = startDate;
        this.carService = carService;
        this.request = request;
        this.created = Instant.now();
    }

    public Long getId() { return id; }

    public Instant getCreated() { return created; }

    public Integer getPrice() { return price; }

    public void setPrice(Integer price) { this.price = price; }

    public Integer getDays() { return days; }

    public void setDays(Integer days) { this.days = days; }

    public Date getStartDate() { return startDate; }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setStartDate(String date) {
        try {
            this.startDate = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getInfo() { return info; }

    public void setInfo(String info) { this.info = info; }

    public CarService getCarService() { return carService; }

    public void setCarService(CarService carService) { this.carService = carService; }

    public Request getRequest() { return request; }

    public void setRequest(Request request) { this.request = request; }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
        setStartDate(date);
    }
}
