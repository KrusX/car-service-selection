package ru.krus.account;

import javax.persistence.*;

import java.util.HashSet;
import java.util.Set;

import ru.krus.request.Request;
import ru.krus.service.CarService;


@SuppressWarnings("serial")
@Entity
@Table(name = "accounts")
public class Account implements java.io.Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column( name =  "first_name", nullable = false)
	private String firstName;

	@Column( name =  "second_name", nullable = false)
	private  String  secondName;

	@Column(name = "email", unique = true, nullable = false)
	private String email;

	@Column( name =  "phone", nullable = false)
	private String phone;

	@Column( name =  "password", nullable = false)
	private String password;

	@Column( name =  "role", nullable = false)
	private String role;

	@OneToOne(mappedBy = "account")
	private CarService carService;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE , mappedBy = "account")
	private Set<Request> requests = new HashSet<>();

    protected Account() {

	}

	public Account(String firstName, String secondName, String email, String phone, String password, String role) {
		this.firstName = firstName;
    	this.secondName = secondName;
    	this.email = email;
    	this.phone = phone;
		this.password = password;
		this.role = role;
	}

	public Long getId() {
		return id;
	}

	public  String getFirstName() { return  firstName; }

	public void setFirstName(String firstName) { this.firstName = firstName; }

	public  String getSecondName() { return  secondName; }

	public void setSecondName(String secondNameName) { this.secondName = secondName; }

    public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() { return phone; }

	public  void setPhone(String phone) {this.phone = phone; }

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public CarService getCarService() {
		return carService;
	}

	public void setCarService(CarService carService) {
		this.carService = carService;
	}

	public Set<Request> getRequests() {
		return requests;
	}

	public void setRequests(Set<Request> requests) {
		this.requests = requests;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
