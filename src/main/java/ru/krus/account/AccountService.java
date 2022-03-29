package ru.krus.account;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import ru.krus.service.CarService;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class AccountService implements UserDetailsService {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public  AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
		this.accountRepository = accountRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public Account save(Account account) {
		account.setPassword(passwordEncoder.encode(account.getPassword()));
		accountRepository.save(account);
		return account;
	}

	@Transactional
	public void delete(Long id){
		accountRepository.deleteAccountById(id);
	}

	public List<Account> findByFilters(String firstName, String secondName, String email, String phone, String role) {
		if ( email.equals("") && firstName.equals("") && secondName.equals("") && phone.equals("") && role.equals("")) {
			return accountRepository.findAll();
		}
		return accountRepository.findByFirstNameOrSecondNameOrEmailOrPhoneOrRole(firstName, secondName, email, phone, role);
	}

	public Account findOneByEmail(String email) {
		return accountRepository.findOneByEmail(email);
	}

	public Account findById(Long id) {
		return accountRepository.findById(id);
	}

	public List<Account> findAll() {
		return accountRepository.findAll();
	}

	@Transactional
	public void changeRole(String role, Long id) {
		accountRepository.changeRole( role, id);
	}

	@Transactional
	public void changePassword(String newPassword, String oldPassword, Long id) throws UnsupportedOperationException {
		Account account = findById(id);
		if ( !passwordEncoder.matches(oldPassword, account.getPassword())) {
			throw new UnsupportedOperationException();
		}
		newPassword = passwordEncoder.encode(newPassword);
		accountRepository.changePassword(newPassword, id);
	}

	@Transactional
	public void update(Long id, Account account) {
		if (account != null) {
			accountRepository.update(id, account.getFirstName(), account.getSecondName(), account.getPhone());
		}
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Account account = accountRepository.findOneByEmail(email);
		if(account == null) {
			throw new UsernameNotFoundException("user not found");
		}
		return createUser(account);
	}

	public void signin(Account account) {
		SecurityContextHolder.getContext().setAuthentication(authenticate(account));
	}

	private Authentication authenticate(Account account) {
		return new UsernamePasswordAuthenticationToken(createUser(account), null, Collections.singleton(createAuthority(account)));
	}

	private User createUser(Account account) {
		return new User(account.getEmail(), account.getPassword(), Collections.singleton(createAuthority(account)));
	}

	private GrantedAuthority createAuthority(Account account) {
		return new SimpleGrantedAuthority(account.getRole());
	}

}
