package ru.krus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.krus.account.Account;
import ru.krus.account.AccountService;
import ru.krus.support.web.MessageHelper;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
public class CarServiceController {

    @Autowired
    private CarServiceService carServiceService;

    @Autowired
    private  AccountService accountService;

    public CarServiceController(CarServiceService carServiceService, AccountService accountService) {
        this.accountService = accountService;
        this.carServiceService = carServiceService;
    }

    @GetMapping("carservice/add")
    @Secured({"ROLE_MANAGER"})
    public String add(Model model, Principal principal, RedirectAttributes ra) {
        Account user = accountService.findOneByEmail(principal.getName());
        if (user.getCarService() != null) {
            MessageHelper.addWarningAttribute(ra, "carservice.add.warning");
            return "redirect:/carservice/current";
        }
        model.addAttribute("carservice", new CarService());
        return "carservice/add";
    }

    @PostMapping("carservice/add")
    @Secured({"ROLE_MANAGER"})
    public String save(@Valid @ModelAttribute CarService carService, Principal principal, Model model, RedirectAttributes ra) {
        Account user = accountService.findOneByEmail(principal.getName());
        CarService newCarService = carServiceService.save(carService, user);
        model.addAttribute("carservice", newCarService);
        MessageHelper.addSuccessAttribute(ra, "carservice.add.success");
        return "carservice/current";
    }

    @GetMapping("carservice/current")
    @Secured({"ROLE_MANAGER"})
    public String current(Model model, Principal principal) {
        Account user = accountService.findOneByEmail(principal.getName());
        if (user.getCarService() == null ) {
            model.addAttribute("carservice", new CarService());
            return "redirect:/carservice/add";
        }
        CarService carService = carServiceService.findById(user.getCarService().getId());
        model.addAttribute("carservice", carService);
        return "carservice/current";
    }

    @GetMapping("carservice/delete/{id}")
    @Secured({"ROLE_MANAGER", "ROLE_ADMIN"})
    public String delete(@PathVariable("id") Long id, Principal principal, Model model, RedirectAttributes ra) {
        Account user = accountService.findOneByEmail(principal.getName());
        CarService carService = carServiceService.findById(id);
        if (user.getRole().equals("ROLE_MANAGER") && carService.getId() != user.getCarService().getId()) {
            MessageHelper.addErrorAttribute(ra, "delete.error");
            return "redirect:/carservice/current";
        }
        model.addAttribute("carservice", carService);
        return "carservice/delete";
    }

    @PostMapping("carservice/delete/{id}")
    @Secured({"ROLE_MANAGER", "ROLE_ADMIN"})
    public String confirmDelte(@PathVariable("id") Long id, Principal principal, RedirectAttributes ra) {
        carServiceService.delete(id);
        Account user = accountService.findOneByEmail(principal.getName());
        MessageHelper.addSuccessAttribute(ra,"delete.success");
        if (user.getRole().equals("ROLE_MANAGER")) {
            return "redirect:/";
        }
        return "redirect:/carservice/get-all";
    }

    @GetMapping("carservice/get-all")
    @Secured({"ROLE_ADMIN"})
    public String getAll(Model model) {
        List<CarService> carServices = carServiceService.findAll();
        model.addAttribute("carservice", new CarService());
        model.addAttribute("carservices", carServices);
        return "carservice/get-all";
    }

    @PostMapping("carservice/get-all")
    @Secured({"ROLE_ADMIN"})
    public String getAllByFilters(@Valid @ModelAttribute CarService carService, Model model) {
        List<CarService> carServices = carServiceService.findAllByFilters(carService.getTown(), carService.getName(), carService.getPhone());
        model.addAttribute("carservice", carService);
        model.addAttribute("carservices", carServices);
        return "carservice/get-all";
    }

    @GetMapping("carservice/details/{id}")
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public String details(@PathVariable("id") Long id, Model model) {
        CarService carService = carServiceService.findById(id);
        if ( carService == null ) {
            return "redirect:/error/general";
        }
        model.addAttribute("carservice", carService);;
        return "carservice/details";
    }

    @GetMapping("carservice/update/{id}")
    @Secured({"ROLE_MANAGER"})
    public String update(@PathVariable("id") Long id, Model model, Principal principal) {
        Account user = accountService.findOneByEmail(principal.getName());
        if (user.getCarService() == null) {
            return "redirect:carservice/add";
        }
        if (user.getCarService().getId() != id) {
            return "redirect:carservice/current";
        }
        CarService carService = carServiceService.findById(id);
        model.addAttribute("carservice", carService);
        return "carservice/update";
    }

    @PostMapping("carservice/update/{id}")
    @Secured({"ROLE_MANAGER"})
    public String confirmUpdate(@PathVariable("id") Long id, @Valid @ModelAttribute CarService carService, RedirectAttributes ra) {
        carServiceService.update(id, carService);
        MessageHelper.addSuccessAttribute(ra, "update.success");
        return "redirect:/carservice/current";
    }
}
