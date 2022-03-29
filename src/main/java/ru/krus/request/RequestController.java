package ru.krus.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.krus.account.Account;
import ru.krus.account.AccountService;
import ru.krus.response.Response;
import ru.krus.response.ResponseService;
import ru.krus.service.CarService;
import ru.krus.service.CarServiceService;
import ru.krus.support.web.MessageHelper;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class RequestController {

    @Autowired
    private RequestService requestService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ResponseService responseService;

    @Autowired
    private CarServiceService carServiceService;

    public RequestController(RequestService requestService, AccountService accountService, ResponseService responseService, CarServiceService carServiceService) {
        this.requestService = requestService;
        this.accountService = accountService;
        this.responseService = responseService;
        this.carServiceService = carServiceService;
    }

    @GetMapping("request/add")
    @Secured({"ROLE_USER"})
    public String requestAdd(Model model) {
        model.addAttribute( "request", new Request());
        return "request/add";
    }

    @PostMapping("request/add")
    @Secured({"ROLE_USER"})
    public String requestSave(@Valid @ModelAttribute Request request, Principal principal, RedirectAttributes ra) {
        Account account = accountService.findOneByEmail(principal.getName());
        requestService.save(request, account);
        MessageHelper.addSuccessAttribute(ra, "request.add.success");
        return "redirect:/";
    }

    @GetMapping("request/get-all-current")
    @Secured({"ROLE_USER"})
    public String getAllCurrent(Model model, Principal principal){
        List<Request> requests = requestService.findAllCurrent(principal.getName());
        model.addAttribute("requests", requests);
        return "request/get-all-current";
    }

    @GetMapping("request/get-all-current-active")
    @Secured({"ROLE_USER"})
    public String getAllCurrentActive(Model model, Principal principal){
        List<Request> requests = requestService.findAllCurrentActive(principal.getName());
        model.addAttribute("requests", requests);
        return "request/get-all-current-active";
    }

    @GetMapping("request/get-all")
    @Secured({"ROLE_ADMIN", "ROLE_MANAGER"})
    public String getAll(Model model, Principal principal) {
        Account user = accountService.findOneByEmail(principal.getName());
        model.addAttribute("request", new Request());
        if (user.getRole().equals("ROLE_MANAGER") ) {
            List<Request> requests = requestService.findAllActive();
            model.addAttribute("requests", requests);
            return "request/get-all";
        }
        List<Request> requests = requestService.findAll();
        model.addAttribute("requests", requests);
        return "request/get-all";
    }

    @PostMapping("request/get-all")
    @Secured({"ROLE_ADMIN", "ROLE_MANAGER"})
    public String getAllByFilters(@Valid @ModelAttribute Request request, Model model, Principal principal) {
        Account user = accountService.findOneByEmail(principal.getName());
        if (user.getRole().equals("ROLE_MANAGER") ) {
            List<Request> requests = requestService.findAllByFiltersActive( request.getTown(),
                                                                            request.getCarType(),
                                                                            request.getBrand(),
                                                                            request.getModel(),
                                                                            request.getCarYear());
            model.addAttribute("requests", requests);
            return "request/get-all";
        }
        List<Request> requests = requestService.findAllByFilters( request.getTown(),
                                                                  request.getCarType(),
                                                                  request.getBrand(),
                                                                  request.getModel(),
                                                                  request.getCarYear());
        model.addAttribute("requests", requests);
        return "request/get-all";
    }

    @GetMapping("request/details/{id}")
    @Secured({"ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER"})
    public String details(@PathVariable Long id, Model model, Principal principal, RedirectAttributes ra) {
        Account user = accountService.findOneByEmail(principal.getName());
        Request request = requestService.findById(id);
        if (request == null) {
            MessageHelper.addErrorAttribute(ra, "request.error");
            return "redirect:/";
        }
        List<Response> responses = new ArrayList<>();
        if (user.getRole().equals("ROLE_USER")) {
            if (request.getAccount().getId() != user.getId()){
                MessageHelper.addErrorAttribute(ra, "request.error");
                return "redirect:/request/get-all-current-active";
            }
        }
        if (user.getRole().equals("ROLE_MANAGER")) {
            CarService carService = carServiceService.findByAccountEmail(principal.getName());
            Response response = responseService.findByCarServiceAndRequest(carService, request);
            if (response != null) {responses.add(response);}
            model.addAttribute("responses", responses);
            model.addAttribute("request", request);
            return "request/details";
        }
        responses = responseService.findAllByRequest(request);
        model.addAttribute("responses", responses);
        model.addAttribute("request", request);
        return "request/details";
    }

    @GetMapping("request/delete/{id}")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public String delete(@PathVariable("id") Long id, Principal principal, Model model, RedirectAttributes ra){
        Account user = accountService.findOneByEmail(principal.getName());
        Request request = requestService.findById(id);
        if (request == null) {
            MessageHelper.addErrorAttribute(ra, "request.error");
            return "redirect:/";
        }
        if (user.getRole().equals("ROLE_USER")) {
            if (request.getAccount().getId() != user.getId()) {
                MessageHelper.addErrorAttribute(ra, "request.error");
                return "request/get-all-current-active";
            }
        }
        model.addAttribute("request", request);
        return "request/delete";
    }

    @PostMapping("request/delete/{id}")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public String confirmDelete(@PathVariable("id") Long id, Principal principal, Model model, RedirectAttributes ra) {
        try {
            requestService.delete(id);
            Account user = accountService.findOneByEmail(principal.getName());
            MessageHelper.addSuccessAttribute(ra, "delete.success");
            if ( user.getRole().equals("ROLE_USER")) {
                return "redirect:/request/get-all-current-active";
            }
            return "redirect:/request/get-all";
        } catch (IllegalAccessException e) {
            MessageHelper.addErrorAttribute(ra, "delete.error");
            return "redirect:/";
        }
    }
}
