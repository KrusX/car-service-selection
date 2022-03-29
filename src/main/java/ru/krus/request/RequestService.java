package ru.krus.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.krus.account.Account;

import java.util.List;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RequestService {

    @Autowired
    private RequestRepository requestRepository;

    public RequestService(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    @Transactional
    public  Request save(Request request, Account account) {
        request.setAccount(account);
        request.setClosed(false);
        requestRepository.save(request);
        return request;
    }

    @Transactional
    public  Request findById(Long id) {
        return requestRepository.findById(id);
    }

    @Transactional
    public List<Request> findAll(){
        return  requestRepository.findAllByOrderByCreatedDesc();
    }

    @Transactional
    public List<Request> findAllActive() {
        return  requestRepository.findAllByClosedIsFalseOrderByCreatedDesc();
    }

    @Transactional
    public List<Request> findAllCurrent(String email){
        return  requestRepository.findAllByAccount_EmailOrderByCreatedDesc(email);
    }

    @Transactional
    public List<Request> findAllCurrentActive(String email){
        return  requestRepository.findAllByAccount_EmailAndClosedIsFalseOrderByCreatedDesc(email);
    }

    @Transactional
    public  List<Request> findAllByFilters(String town, String carType, String brand, String model, Integer carYear) {
        if (town.equals("") && carType.equals("") && brand.equals("") && model.equals("") && carYear == null ) {
            return findAll();
        }
        return requestRepository.findAllByTownOrCarTypeOrBrandOrModelOrCarYearOrderByCreatedDesc(town, carType, brand, model, carYear);
    }

    @Transactional
    public  List<Request> findAllByFiltersActive(String town, String carType, String brand, String model, Integer carYear) {
        if (town.equals("") && carType.equals("") && brand.equals("") && model.equals("") && carYear == null ) {
            return findAllActive();
        }
        return requestRepository.findAllActiveByFilters(town, carType, brand, model, carYear);
    }

    @Transactional
    public void  delete(Long id) throws IllegalAccessException {
        Request request = requestRepository.findById(id);
        if (request.getClosed()) {
           throw new IllegalAccessException();
        }
        requestRepository.deleteById(id);
    }


    @Transactional
    public void close(Long id) {
        requestRepository.changeClosedTrue(id);
    }

    @Transactional
    public void open(Long id) {
        requestRepository.changeClosedFalse(id);
    }
}
