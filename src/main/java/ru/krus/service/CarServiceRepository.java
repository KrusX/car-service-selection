package ru.krus.service;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarServiceRepository extends CrudRepository<CarService, Long> {

    CarService findById(Long id);
    CarService findByAccount_Email(String email);
    List<CarService> findAll();
    List<CarService> findAllByTownOrNameOrPhone(String town, String name, String phone);
    void deleteById(Long id);

    @Modifying
    @Query("update CarService cs set cs.town = :town, cs.name = :name, cs.address = :address, cs.phone = :phone, cs.info = :info where cs.id = :id")
    void update(@Param("town") String town, @Param("name") String name,
                @Param("address") String address, @Param("phone") String phone, @Param("info") String info, @Param("id") Long id);

    @Modifying
    @Query("update CarService cs set cs.rateAmount = :rateAmount, cs.rateSum = :rateSum, cs.rating = :rating where cs.id = :id")
    void updateRating(@Param("rateAmount") Integer rateAmount, @Param("rateSum") Double rateSum, @Param("rating") Double rating, @Param("id") Long id);

    @Modifying
    @Query("update CarService cs set cs.closedOrders = :closedOrders where cs.id = :id")
    void updateClosedOrders(@Param("id") Long id, @Param("closedOrders") Integer closedOrders);
}
