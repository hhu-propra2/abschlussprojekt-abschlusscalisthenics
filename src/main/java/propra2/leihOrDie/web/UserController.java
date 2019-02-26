package propra2.leihOrDie.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import propra2.leihOrDie.dataaccess.ItemRepository;
import propra2.leihOrDie.dataaccess.LoanRepository;
import propra2.leihOrDie.dataaccess.SessionRepository;
import propra2.leihOrDie.dataaccess.UserRepository;
import propra2.leihOrDie.model.Item;
import propra2.leihOrDie.model.Loan;
import propra2.leihOrDie.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static propra2.leihOrDie.web.ProPayWrapper.getBalanceOfUser;
import static propra2.leihOrDie.web.ProPayWrapper.raiseBalanceOfUser;

@Controller
public class UserController {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/myaccount")
    public String showUserPage(Model model, @CookieValue(value="SessionID", defaultValue="") String sessionId) {
        User user = sessionRepository.findUserBySessionCookie(sessionId);
        String username = user.getUsername();
        if (!isAuthorized(sessionId, username)) {
            return "";
        }
        model.addAttribute("user", username);
        model.addAttribute("pendingitems", getPendingItems(username));
        model.addAttribute("loans", loanRepository.findLoansOfUser(username));
        model.addAttribute("items", itemRepository.findItemsOfUser(username));

        return "user";
    }

    @GetMapping("/user/{username}")
    public String showUser(Model model, @PathVariable String username, @CookieValue(value="SessionID", defaultValue="") String sessionId) {
        model.addAttribute("user", username);
        model.addAttribute("items", itemRepository.findItemsOfUser(username));
        return "other-user";
    }

    @GetMapping("/myaccount/propay")
    public String showPropay(Model model, @CookieValue(value="SessionID", defaultValue="") String sessionId, TransactionForm form) {
        User user = sessionRepository.findUserBySessionCookie(sessionId);
        double bankBalance = getBalanceOfUser(user.getEmail());

        //List<Long> transactionIds = getTransactionsOfUser(username);

        model.addAttribute("bankBalance", bankBalance);
        //model.addAttribute(transactionIds);

        return "user-propay";
    }

    @PostMapping("/myaccount/propay")
    public String doTransaction(Model model, @Valid TransactionForm form, BindingResult bindingResult, @CookieValue(value="SessionID", defaultValue="") String sessionId) {
        if(bindingResult.hasErrors()) {
            User user = sessionRepository.findUserBySessionCookie(sessionId);
            double bankBalance = getBalanceOfUser(user.getEmail());
            model.addAttribute("bankBalance", bankBalance);
            return "user-propay";
        }

        User user = sessionRepository.findUserBySessionCookie(sessionId);

        raiseBalanceOfUser(user.getEmail(), form.getAmount());
        return "redirect:/myaccount/propay";
    }

    private List<Item> collectArtikel(Long[] itemID){
        List<Item> items = new ArrayList<>();
        if (itemID == null)
            return items;
        for (int i=0; i<itemID.length; i++){
    //        items.add(ItemRepository.findById(itemID[i].get());
        }
        return items;
    }

    private List<Loan> getPendingItems(String username) {
        List<Item> itemsOfUser = itemRepository.findItemsOfUser(username);
        List<Loan> loans = new ArrayList<>();
        for (Item i: itemsOfUser) {
            Loan temp = null;
            temp = loanRepository.findLoansOfItem(i.getId()).get(0);
            if (temp.getState().equals("pending")) {
                loans.add(temp);
            }
        }
        return loans;
    }

    private boolean isAuthorized(String sessionId, String username) {
        User user = sessionRepository.findUserBySessionCookie(sessionId);
        return user.getUsername().equals(username);
    }

    private List<Long> getTransactionsOfUser(String username) {
        List<Item> itemsOfUser = itemRepository.findItemsOfUser(username);
        List<Long> reservationIds = new ArrayList<>();

        for (Item item: itemsOfUser) {
            reservationIds = getReservationIdsOfItem(item, reservationIds);
        }

        return reservationIds;
    }

    private List<Long> getReservationIdsOfItem(Item item, List<Long> reservationIds) {
        List<Loan> loansOfItem = loanRepository.findLoansOfItem(item.getId());

        for (Loan loan: loansOfItem) {
            reservationIds.add(loan.getProPayReservationId());
        }

        return reservationIds;
    }
}