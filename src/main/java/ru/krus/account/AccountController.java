package ru.krus.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.krus.support.web.MessageHelper;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
public class AccountController {

    @Autowired
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("account/current")
    @Secured({"ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER"})
    public String currentAccount(Model model, Principal principal) {
        Account account = accountService.findOneByEmail(principal.getName());
        model.addAttribute("account", account);
        return "account/current";
    }

    @GetMapping("account/details/{id}")
    @Secured("ROLE_ADMIN")
    public String details(@PathVariable("id") Long id, Model model) {
        Account account = accountService.findById(id);
        if (account == null) {
            return "redirect:/error/general";
        }
        model.addAttribute("account", account);
        return "account/details";
    }

    @GetMapping("account/get-all")
    @Secured("ROLE_ADMIN")
    public String getAll(Model model){
        List<Account> accounts = accountService.findAll();
        model.addAttribute("account",new Account());
        model.addAttribute("accounts" , accounts);
        return "account/get-all";
    }

    @PostMapping("account/get-all")
    @Secured("ROLE_ADMIN")
    public String getAllByFilters(@Valid @ModelAttribute Account account, Model model){
        List<Account> accounts = accountService.findByFilters(account.getFirstName(),
                                                              account.getSecondName(),
                                                              account.getEmail(),
                                                              account.getPhone(),
                                                              account.getRole());
        model.addAttribute("accounts" , accounts);
        return "account/get-all";
    }

    @GetMapping("account/delete/{id}")
    @Secured({"ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER"})
    public String delete(@PathVariable("id") Long id, Model model, Principal principal, RedirectAttributes ra) {
        Account user = accountService.findOneByEmail(principal.getName());
        if ( user.getRole().equals("ROLE_USER") || user.getRole().equals("ROLE_MANAGER") ) {
            if ( user.getId() != id ) {
                MessageHelper.addErrorAttribute(ra, "request.error");
                return "redirect:/account/current";
            }
        }
        model.addAttribute("account", accountService.findById(id));
        return  "account/delete";
    }

    @PostMapping("account/delete/{id}")
    @Secured({"ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER"})
    public String confirmDelete(@PathVariable("id") Long id, Principal principal, HttpServletRequest request, RedirectAttributes ra) {
        Account user = accountService.findOneByEmail(principal.getName());
        if ( user.getRole().equals("ROLE_USER") || user.getRole().equals("ROLE_MANAGER") ) {
            if ( user.getId() != id ) {
                MessageHelper.addErrorAttribute(ra, "delete.error");
                return "redirect:/account/current";
            }
        }
        accountService.delete(id);
        if (user.getRole().equals("ROLE_ADMIN") && user.getId() != id) {
            MessageHelper.addSuccessAttribute(ra, "delete.success");
            return "redirect:/account/get-all";
        }
        request.getSession().invalidate();
        return "redirect:/";
    }

    @GetMapping("account/update/{id}")
    @Secured({"ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER"})
    public String update(@PathVariable("id") Long id, Model model, Principal principal, RedirectAttributes ra) {
        Account user = accountService.findOneByEmail(principal.getName());
        if ( user.getRole().equals("ROLE_USER") || user.getRole().equals("ROLE_MANAGER")) {
            if ( user.getId() != id ) {
                MessageHelper.addErrorAttribute(ra, "request.error");
                return "redirect:/account/current";
            }
        }
        Account account = accountService.findById(id);
        if (account == null) {
            return "redirect:/error/general";
        }
        model.addAttribute("account", account);
        return "account/update";
    }

    @PostMapping("account/update/{id}")
    @Secured({"ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER"})
    public String confirmUpdate(@PathVariable("id") Long id, @Valid @ModelAttribute("account") Account account, Principal principal, RedirectAttributes ra) {
        Account user = accountService.findOneByEmail(principal.getName());
        if ( user.getRole().equals("ROLE_USER") || user.getRole().equals("ROLE_MANAGER")) {
            if ( user.getId() != id ) {
                MessageHelper.addErrorAttribute(ra, "request.error");
                return "redirect:/account/current";
            }
        }
        accountService.update(id, account);
        MessageHelper.addSuccessAttribute(ra, "update.success");
        if (user.getRole().equals("ROLE_ADMIN") && user.getId() != id) {
            return "redirect:/account/get-all";
        }
        return "redirect:/account/current";
    }

    @GetMapping("account/change-role/{id}")
    @Secured("ROLE_ADMIN")
    public String changeRole(@PathVariable("id") Long id, Model model) {
        Account account = accountService.findById(id);
        if (account == null) {
            return "redirect:/error/general";
        }
        model.addAttribute("account", account);
        return "account/change-role";
    }

    @PostMapping("account/change-role/{id}")
    @Secured("ROLE_ADMIN")
    public String confirmChangeRole(@PathVariable("id") Long id, Account account, RedirectAttributes ra) {
        Account acc = accountService.findById(id);
        if (acc == null) {
            return "redirect:/error/general";
        }
        if (acc.getRole().equals("ROLE_USER")) {
            if (acc.getRequests().size() != 0) {
                MessageHelper.addErrorAttribute(ra, "change.role.error");
                return "redirect:/account/change-role/{id}";
            }
        }
        if (acc.getRole().equals("ROLE_MANAGER")) {
            if (acc.getCarService() != null) {
                MessageHelper.addErrorAttribute(ra, "change.role.error");
                return "redirect:/account/change-role/{id}";
            }
        }
        accountService.changeRole(account.getRole(), id );
        MessageHelper.addSuccessAttribute(ra, "change.role.success");
        return "redirect:/account/get-all";
    }

    @GetMapping("account/change-password/{id}")
    @Secured({"ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER"})
    public String changePassword(@PathVariable("id") Long id, Model model, Principal principal, RedirectAttributes ra) {
        Account user = accountService.findOneByEmail(principal.getName());
        if ( user.getId() != id ) {
            MessageHelper.addErrorAttribute(ra, "request.error");
            return "redirect:/account/current";
        }
        model.addAttribute("changePasswordForm", new ChangePasswordForm());
        model.addAttribute( "acc_id", id);
        return "account/change-password";
    }

    @PostMapping("account/change-password/{id}")
    @Secured({"ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER"})
    public String confirmChangePassword(@PathVariable("id") Long id, ChangePasswordForm form, Model model, RedirectAttributes ra ) {
        try {
            accountService.changePassword(form.getNewPassword(), form.getOldPassword(), id);
            MessageHelper.addSuccessAttribute(ra, "change.password.success");
            return "redirect:/account/current";
        } catch ( UnsupportedOperationException ex) {
            ex.printStackTrace();
            model.addAttribute( "acc_id", id);
            MessageHelper.addErrorAttribute(ra, "change.password.error");
            return "account/change-password";
        }
    }
}
