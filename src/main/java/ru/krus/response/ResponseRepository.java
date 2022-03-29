package ru.krus.response;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.krus.request.Request;
import ru.krus.service.CarService;

import java.util.List;

public interface ResponseRepository extends CrudRepository<Response, Long> {

    Response findById(Long id);
    List<Response> findAll();
    List<Response> findAllByCarService(CarService carService);
    List<Response> findAllByRequest(Request request);
    void deleteById(Long id);

    @Query("select res from Response res where res.carService.id = :service_id and res.request.id = :request_id")
    Response findByCarServiceAndRequest(@Param("service_id") Long service_id, @Param("request_id") Long request_id);
}
