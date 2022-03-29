package ru.krus.response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.krus.request.Request;
import ru.krus.service.CarService;

import java.util.List;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ResponseService {

    @Autowired
    private ResponseRepository responseRepository;

    public ResponseService( ResponseRepository responseRepository ) {
        this.responseRepository = responseRepository;
    }

    @Transactional
    public void save(Response response, Request request, CarService carService) {
        response.setRequest(request);
        response.setCarService(carService);
        responseRepository.save(response);
    }

    @Transactional
    public void delete(Long id) {
        responseRepository.deleteById(id);
    }

    @Transactional
    public Response findById(Long id) {
        return  responseRepository.findById(id);
    }

    @Transactional
    public Response findByCarServiceAndRequest(CarService carService, Request request) {
        return  responseRepository.findByCarServiceAndRequest(carService.getId(), request.getId());
    }

    @Transactional
    public List<Response> findAll(){
        return responseRepository.findAll();
    }

    @Transactional
    public List<Response> findAllByCarService(CarService carService){
        return responseRepository.findAllByCarService(carService);
    }

    @Transactional
    public List<Response> findAllByRequest(Request request) {
        return responseRepository.findAllByRequest(request);
    }
}
