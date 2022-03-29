package ru.krus.account;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.krus.service.CarService;

import java.util.List;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {

	Account findOneByEmail(String email);
	Account findById(Long id);
	List<Account> findAll();
	List<Account> findByFirstNameOrSecondNameOrEmailOrPhoneOrRole(String firstName, String secondName, String email, String phone, String role);
	void deleteAccountById(Long id);

	@Query("select count(a) > 0 from Account a where a.email = :email")
	boolean exists(@Param("email") String email);

	@Modifying
	@Query("update Account acc set acc.role = :role where acc.id = :id")
	void changeRole(@Param("role") String role, @Param("id") Long id);

	@Modifying
	@Query("update Account acc set acc.firstName = :firstName, acc.secondName = :secondName, acc.phone = :phone where acc.id = :id")
	void update(@Param("id") Long id, @Param("firstName") String firstName, @Param("secondName") String secondName, @Param("phone") String phone);

	@Modifying
	@Query("update Account acc set acc.password = :password where  acc.id = :id")
	void  changePassword(@Param("password") String password, @Param("id") Long id);
}
