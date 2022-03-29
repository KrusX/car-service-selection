package ru.krus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.krus.account.Account;

import javax.persistence.Lob;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CarServiceService {

    @Autowired
    private CarServiceRepository carServiceRepository;

    public CarServiceService(CarServiceRepository carServiceRepository) {
        this.carServiceRepository = carServiceRepository;
    }

    @Transactional
    public CarService save(CarService carService, Account account) {
        carService.setAccount(account);
        carServiceRepository.save(carService);
        return carService;
    }

    @Transactional
    public CarService findById(Long id) {
        return carServiceRepository.findById(id);
    }

    @Transactional
    public CarService findByAccountEmail(String email) {
        return carServiceRepository.findByAccount_Email(email);
    }

    @Transactional
    public List<CarService> findAll() {
        return carServiceRepository.findAll();
    }

    @Transactional
    public List<CarService> findAllByFilters(String town, String name, String phone) {
        if ( town.equals("") && name.equals("") && phone.equals("")) {
            return carServiceRepository.findAll();
        }
        return carServiceRepository.findAllByTownOrNameOrPhone(town, name, phone);
    }

    @Transactional
    public void delete(Long id) {
        carServiceRepository.deleteById(id);
    }

    @Transactional
    public void update(Long id, CarService carService) {
        carServiceRepository.update(carService.getTown(), carService.getName(), carService.getAddress(), carService.getPhone(), carService.getInfo(), id);
    }

    @Transactional
    public void updateClosedOrders(CarService carService) {
        carService.setClosedOrders();
        carServiceRepository.updateClosedOrders(carService.getId(), carService.getClosedOrders());
    }

    @Transactional
    public void updateRating(CarService carService, Integer rate) {
         carService.setRateAmount();
         carService.setRateSum(rate);
         carService.setRating();
         carServiceRepository.updateRating(carService.getRateAmount(), carService.getRateSum(), carService.getRating(), carService.getId());
    }
 }
