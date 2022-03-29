package ru.krus.order;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.krus.service.CarService;

import java.time.Instant;
import java.util.List;

@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {

    Order findById(Long id);
    List<Order> findAllByCarServiceOrderByCreatedDesc(CarService carService);
    List<Order> findAllByCarServiceAndCustomerAcceptIsFalseOrderByCreatedDesc(CarService carService);
    List<Order> findAllByCarServiceAndCustomerAcceptIsTrueOrderByCreatedDesc(CarService carService);
    List<Order> findAllByRequest_Account_EmailOrderByCreatedDesc(String email);
    List<Order> findAllByRequest_Account_EmailAndAndCustomerAcceptIsFalseOrderByCreatedDesc(String email);
    List<Order> findAllByRequest_Account_EmailAndAndCustomerAcceptIsTrueOrderByCreatedDesc(String email);

    @Query( value = "select * from Orders o ORDER BY o.created DESC ", nativeQuery = true)
    List<Order> findAll();

    @Query( value = "select * from Orders o WHERE o.customer_accept == TRUE  ORDER BY o.created DESC ", nativeQuery = true)
    List<Order> findAllClosed();

    @Query( value = "select * from Orders o WHERE o.customer_accept == FALSE ORDER BY o.created DESC ", nativeQuery = true)
    List<Order> findAllNotClosed();

    @Modifying
    @Query("update Order o set  o.customerAccept = :b where o.id = :id")
    void accept(@Param("id") Long id, @Param("b") Boolean b);

    @Modifying
    @Query("update Order o set  o.serviceClose = :b where o.id = :id")
    void close(@Param("id") Long id, @Param("b") Boolean b);

    @Modifying
    @Query("update Order o set  o.serviceDeleteOrderStatus = :b where o.id = :id")
    void serviceDeleteOrderStatus(@Param("id") Long id, @Param("b") Boolean b);


    @Modifying
    @Query("update Order o set  o.customerDeleteOrderStatus = :b where o.id = :id")
    void customerDeleteOrderStatus(@Param("id") Long id, @Param("b") Boolean b);

    @Modifying
    @Query("update Order o set  o.closed = :closed where o.id = :id")
    void closeOrder(@Param("id") Long id, @Param("closed") Instant closed);

    @Modifying
    @Query("update Order o set o.rate = :rate where o.id = :id")
    void updateRate(@Param("id") Long id, @Param("rate") Integer rate);
}
