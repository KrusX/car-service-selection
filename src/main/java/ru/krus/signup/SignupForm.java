package ru.krus.signup;

import org.hibernate.validator.constraints.*;

import ru.krus.account.Account;

public class SignupForm {

	private static final String NOT_BLANK_MESSAGE = "{notBlank.message}";
	private static final String EMAIL_MESSAGE = "{email.message}";
	private static final String EMAIL_EXISTS_MESSAGE = "{email-exists.message}";

    @NotBlank(message = SignupForm.NOT_BLANK_MESSAGE)
	@Email(message = SignupForm.EMAIL_MESSAGE)
	@EmailExists(message = SignupForm.EMAIL_EXISTS_MESSAGE)
	private String email;

    private String firstName;

	private String secondName;

	private  String phone;

	private String role;

    @NotBlank(message = SignupForm.NOT_BLANK_MESSAGE)
	private String password;

    public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public  String getFirstName() { return firstName; }

	public  void setFirstName(String firstName) { this.firstName = firstName; }

	public  String getSecondName() { return secondName; }

	public  void setSecondName(String secondName) { this.secondName = secondName; }

	public  String getPhone() { return phone; }

	public  void setPhone(String phone) { this.phone = phone;}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() { return role; }

	public void setRole(String role) { this.role = role; }

	public Account createAccount() {
        return new Account(getFirstName(), getSecondName(), getEmail(), getPhone(), getPassword(), getRole());
	}
}
