package ru.krus.response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.krus.account.Account;
import ru.krus.account.AccountService;
import ru.krus.request.Request;
import ru.krus.request.RequestService;
import ru.krus.service.CarService;
import ru.krus.service.CarServiceService;
import ru.krus.support.web.Message;
import ru.krus.support.web.MessageHelper;

import javax.validation.Valid;
import java.security.Principal;

@Controller
public class ResponseController {

    @Autowired
    private ResponseService responseService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private CarServiceService carServiceService;

    @Autowired
    private RequestService requestService;

    public ResponseController(ResponseService responseService, AccountService accountService, CarServiceService carServiceService, RequestService requestService) {
        this.responseService = responseService;
        this.accountService = accountService;
        this.carServiceService = carServiceService;
        this.requestService = requestService;
    }

    @GetMapping("response/add/{request_id}")
    @Secured({"ROLE_MANAGER"})
    public String add(@PathVariable("request_id") Long request_id, Model model, Principal principal) {
        Request request = requestService.findById(request_id);
        CarService carService = carServiceService.findByAccountEmail(principal.getName());
        if (carService == null) {
            return "redirect:/carservice/add";
        }
        if ( request == null || request.getClosed() ) {
            return  "redirect:/";
        }
        if ( responseService.findByCarServiceAndRequest(carService, request) != null ) {
            return "redirect:/";
        }
        model.addAttribute("request_id",request_id);
        model.addAttribute("response", new Response());
        return "response/add";
    }

    @PostMapping("response/add/{request_id}")
    @Secured({"ROLE_MANAGER"})
    public String save(@PathVariable("request_id") Long request_id, @Valid @ModelAttribute Response response, Principal principal, RedirectAttributes ra) {
        Request request = requestService.findById(request_id);
        CarService carService = carServiceService.findByAccountEmail(principal.getName());
        responseService.save(response, request, carService);
        MessageHelper.addSuccessAttribute(ra, "response.add.success");
        return "redirect:/request/details/{request_id}";
    }

    @GetMapping("response/details/{id}")
    @Secured({"ROLE_MANAGER", "ROLE_ADMIN", "ROLE_USER"})
    public String details(@PathVariable("id") Long id, Model model) {
        Response response = responseService.findById(id);
        model.addAttribute("response", response);
        return "response/details";
    }

    @GetMapping("response/delete/{id}")
    @Secured({"ROLE_MANAGER", "ROLE_ADMIN"})
    public String delete(@PathVariable("id") Long id, Principal principal, RedirectAttributes ra) {
        Response response = responseService.findById(id);
        if ( response == null ) {
            return "redirect:/error/general";
        }
        Account user = accountService.findOneByEmail(principal.getName());
        if(user.getRole().equals("ROLE_MANAGER") && user.getId() != response.getCarService().getAccount().getId()) {
            MessageHelper.addErrorAttribute(ra, "delete.error");
        }
        responseService.delete(id);
        MessageHelper.addSuccessAttribute(ra, "delete.success");
        return "redirect:/";
    }
}
