package ru.krus.request;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends CrudRepository<Request, Long> {

    Request findById(Long id);
    List<Request> findAllByOrderByCreatedDesc();
    List<Request> findAllByClosedIsFalseOrderByCreatedDesc();
    List<Request> findAllByTownOrCarTypeOrBrandOrModelOrCarYearOrderByCreatedDesc(String town, String carType, String brand, String model, Integer carYear );
    List<Request> findAllByAccount_EmailOrderByCreatedDesc(String email);
    List<Request> findAllByAccount_EmailAndClosedIsFalseOrderByCreatedDesc(String email);
    void deleteById(Long id);

    @Modifying
    @Query("update Request req set req.closed = true where req.id = :id")
    void changeClosedTrue(@Param("id") Long id);

    @Modifying
    @Query("update Request req set req.closed = false where req.id = :id")
    void changeClosedFalse(@Param("id") Long id);

    @Query("select req from Request req " +
            "where ( req.town = :town or req.carType = :carType or req.brand = :brand or req.model = :model or req.carYear = :carYear) and req.closed = false " +
            "order by req.created desc ")
    List<Request> findAllActiveByFilters(@Param("town")String town, @Param("carType")String carType,
                                         @Param("brand")String brand, @Param("model")String model, @Param("carYear")Integer carYear);
}
