package ru.krus.order;

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
import ru.krus.response.Response;
import ru.krus.response.ResponseService;
import ru.krus.service.CarService;
import ru.krus.service.CarServiceService;
import ru.krus.support.web.Message;
import ru.krus.support.web.MessageHelper;

import javax.validation.Valid;
import java.security.Principal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ResponseService responseService;

    @Autowired
    private RequestService requestService;

    @Autowired
    private CarServiceService carServiceService;

    @Autowired
    private AccountService accountService;

    public OrderController(OrderService orderService,
                           ResponseService responseService,
                           RequestService requestService,
                           CarServiceService carServiceService,
                           AccountService accountService) {

        this.orderService = orderService;
        this.requestService = requestService;
        this.responseService = responseService;
        this.carServiceService = carServiceService;
        this.accountService = accountService;
    }

    @GetMapping("order/add/{response_id}")
    @Secured({"ROLE_USER"})
    public String add(@PathVariable("response_id") Long id, RedirectAttributes ra) {
        Response response = responseService.findById(id);

        Order order = new Order();
        order.setPrice(response.getPrice());
        order.setDays(response.getDays());
        order.setStartDate(response.getStartDate());
        order.setInfo(response.getInfo());

        requestService.close(response.getRequest().getId());
        orderService.save(order, response.getRequest(), response.getCarService());
        responseService.delete(id);
        MessageHelper.addSuccessAttribute(ra, "order.add.success");
        return "redirect:/order/get-all";
    }

    @GetMapping("order/get-all")
    @Secured({"ROLE_USER", "ROLE_MANAGER", "ROLE_ADMIN"})
    public String getAll(Principal principal, Model model) {
        Account user = accountService.findOneByEmail(principal.getName());
        List<Order> orders = new ArrayList<>();
        if (user.getRole().equals("ROLE_USER")) {
            orders = orderService.findAll(user.getEmail());
        }
        if (user.getRole().equals("ROLE_MANAGER")) {
            CarService carService = carServiceService.findByAccountEmail(user.getEmail());
            orders = orderService.findAll(carService);
        }
        if (user.getRole().equals("ROLE_ADMIN")) {
            orders = orderService.findAll();
        }
        model.addAttribute("orders", orders);
        return "order/get-all";
    }

    @GetMapping("order/get-all-active")
    @Secured({"ROLE_USER", "ROLE_MANAGER", "ROLE_ADMIN"})
    public String getAllActive(Principal principal, Model model) {
        Account user = accountService.findOneByEmail(principal.getName());
        List<Order> orders = new ArrayList<>();
        if (user.getRole().equals("ROLE_USER")) {
            orders = orderService.findAllActive(user.getEmail());
        }
        if (user.getRole().equals("ROLE_MANAGER")) {
            CarService carService = carServiceService.findByAccountEmail(user.getEmail());
            orders = orderService.findAllActive(carService);
        }
        if (user.getRole().equals("ROLE_ADMIN")) {
            orders = orderService.findAllActive();
        }
        model.addAttribute("orders", orders);
        return "order/get-all-active";
    }

    @GetMapping("order/get-all-closed")
    @Secured({"ROLE_USER", "ROLE_MANAGER", "ROLE_ADMIN"})
    public String getAllClosed(Principal principal, Model model) {
        Account user = accountService.findOneByEmail(principal.getName());
        List<Order> orders = new ArrayList<>();
        if (user.getRole().equals("ROLE_USER")) {
            orders = orderService.findAllClosed(user.getEmail());
        }
        if (user.getRole().equals("ROLE_MANAGER")) {
            CarService carService = carServiceService.findByAccountEmail(user.getEmail());
            orders = orderService.findAllClosed(carService);
        }
        if (user.getRole().equals("ROLE_ADMIN")) {
            orders = orderService.findAllClosed();
        }
        model.addAttribute("orders", orders);
        return "order/get-all-closed";
    }

    @GetMapping("order/details/{id}")
    @Secured({"ROLE_USER", "ROLE_MANAGER", "ROLE_ADMIN"})
    public String details(@PathVariable("id") Long id, Model model, Principal principal) {
        Account user = accountService.findOneByEmail(principal.getName());
        Order order = orderService.findById(id);
        if (user.getRole().equals("ROLE_USER") && user.getId() != order.getRequest().getAccount().getId()) {
            return "redirect:/order/get-all";
        }
        if (user.getRole().equals("ROLE_MANAGER") && user.getId() != order.getCarService().getAccount().getId()) {
            return "redirect:/order/get-all";
        }

        model.addAttribute("order", order);
        return "order/details";
    }

    @GetMapping("order/accept/{id}")
    @Secured({"ROLE_USER"})
    public String accept(@PathVariable("id") Long id, Principal principal, Model model, @ModelAttribute Order o, RedirectAttributes ra) {
        Account user = accountService.findOneByEmail(principal.getName());
        Order order = orderService.findById(id);

        if( order == null || order.getClosed() != null) {
            MessageHelper.addErrorAttribute(ra, "request.error");
            return "redirect:/order/get-all";
        }
        if (user.getId() != order.getRequest().getAccount().getId()) {
            MessageHelper.addErrorAttribute(ra, "request.error");
            return "redirect:/order/get-all";
        }
        if (order.getServiceDeleteOrderStatus()) {
            MessageHelper.addErrorAttribute(ra, "request.error");
            return "redirect:/order/details/{id}";
        }
        if (!order.getServiceClose()) {
            MessageHelper.addErrorAttribute(ra, "request.error");
            return "redirect:/order/details/{id}";
        }

        carServiceService.updateClosedOrders(order.getCarService());

        order = orderService.changeAccept(id, true, order);
        order = orderService.changeCustomerDelete(id, false, order);
        order = orderService.closeOrder(id, order);

        if (o.getRate() != null) {
            carServiceService.updateRating(order.getCarService(), o.getRate());
            order = orderService.updateRate(order, o.getRate());
        }

        MessageHelper.addSuccessAttribute(ra, "order.accept.success");
        model.addAttribute("order", order);
        return "redirect:/order/details/{id}";
    }

    @GetMapping("order/close/{id}")
    @Secured({"ROLE_MANAGER"})
    public String close(@PathVariable("id") Long id, Principal principal, Model model) {
        Order order = orderService.findById(id);
        if( order == null || order.getClosed() != null) {
            return "redirect:/order/get-all";
        }
        Account user = accountService.findOneByEmail(principal.getName());
        if (order.getCarService().getAccount().getId() != user.getId()) {
            return "redirect:/order/get-all";
        }
        order = orderService.changeClose(id, true, order);
        model.addAttribute("order", order);
        return "redirect:/order/details/{id}";
    }

    @GetMapping("order/undo-close/{id}")
    @Secured({"ROLE_MANAGER", "ROLE_USER"})
    public String undoClose(@PathVariable("id") Long id, Principal principal, Model model){
        Order order = orderService.findById(id);
        if( order == null) {
            return "redirect:/order/get-all";
        }
        Account user = accountService.findOneByEmail(principal.getName());
        if (user.getRole().equals("ROLE_MANAGER") &&
                order.getCarService().getAccount().getId() != user.getId()) {
            return "redirect:/order/get-all";
        }
        if (user.getRole().equals("ROLE_USER") &&
                order.getRequest().getAccount().getId() != user.getId()) {
            return "redirect:/order/get-all";
        }
        order = orderService.changeClose(id, false, order);
        model.addAttribute("order", order);
        return "redirect:/order/details/{id}";
    }

    @GetMapping("order/delete/{id}")
    @Secured({"ROLE_USER", "ROLE_MANAGER"})
    public String setDelete(@PathVariable("id") Long id, Principal principal, Model model) {
        Order order = orderService.findById(id);
        if( order == null || order.getClosed() != null) {
            return "redirect:/order/get-all";
        }
        Account user = accountService.findOneByEmail(principal.getName());
        if (user.getRole().equals("ROLE_USER") && user.getId() == order.getRequest().getAccount().getId()) {
            if (order.getServiceDeleteOrderStatus()) {
                orderService.delete(id);
                Request request = requestService.findById(order.getRequest().getId());
                requestService.open(request.getId());
                return "redirect:/";
            }
            order = orderService.changeCustomerDelete(id, true, order);
            model.addAttribute("order", order);
            return "redirect:/order/details/{id}";
        }
        if (user.getRole().equals("ROLE_MANAGER") && user.getId() == order.getCarService().getAccount().getId()) {
            if (order.getCustomerDeleteOrderStatus()) {
                orderService.delete(id);
                Request request = requestService.findById(order.getRequest().getId());
                requestService.open(request.getId());
                return "redirect:/";
            }
            if (order.getServiceClose()) {
                order = orderService.changeClose(id, false, order);
            }
            order = orderService.changeServiceDelete(id, true , order);
            model.addAttribute("order", order);
            return "redirect:/order/details/{id}";
        }
        return "redirect:/";
    }

    @GetMapping("order/undo-delete/{id}")
    @Secured({"ROLE_USER", "ROLE_MANAGER"})
    public String undoDelete(@PathVariable("id") Long id, Principal principal, Model model) {
        Order order = orderService.findById(id);
        if( order == null || order.getClosed() != null) {
            return "redirect:/order/get-all";
        }
        Account user = accountService.findOneByEmail(principal.getName());
        if (user.getRole().equals("ROLE_USER") && user.getId() == order.getRequest().getAccount().getId()) {
            order = orderService.changeCustomerDelete(id, false, order);
            model.addAttribute("order", order);
            return "redirect:/order/details/{id}";
        }
        if (user.getRole().equals("ROLE_MANAGER") && user.getId() == order.getCarService().getAccount().getId()) {
            order = orderService.changeServiceDelete(id, false, order);
            model.addAttribute("order", order);
            return "redirect:/order/details/{id}";
        }
        return "redirect:/";
    }
}
