package propra2.leihOrDie.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import propra2.leihOrDie.dataaccess.ItemRepository;
import propra2.leihOrDie.dataaccess.UserRepository;
import propra2.leihOrDie.model.Address;
import propra2.leihOrDie.model.Item;
import propra2.leihOrDie.model.User;


import javax.validation.Valid;

@Controller
public class ItemController {
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;

    @PostMapping("/item/create")
    public String new_item_post(Model model, @Valid ItemForm form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "create-item";
        }

        /////// dummy User erstellen und in DB speichern
        String pass = "pass";
        Address adr = new Address();
        adr.setCity("Düsseldorf");
        adr.setPostcode(40225);
        adr.setStreet("Universitätsstraße");
        adr.setHouseNumber(1);
        User dummyUser = new User("Max Mustermann", "kk@dd", pass, adr);
        userRepository.save(dummyUser);
        ////////

        Item item = new Item();
        saveItem(item, form.getName(), form.getDescription(), form.getCost(), form.getDeposit(), form.getAvailableTime(), form.getLocation(), dummyUser);

        return "redirect:/borrowall";
    }

    @GetMapping("/item/create")
    public String new_item_get(Model model, ItemForm form) {
        return "create-item";
    }


    @GetMapping("/item/edit/{id}")
    public String edit_item_get(Model model, @PathVariable Long id, ItemForm form) {
        Item item = itemRepository.findById(id).get();

        form.setName(item.getName());
        form.setDescription(item.getDescription());
        form.setCost(item.getCost());
        form.setDeposit(item.getDeposit());
        form.setAvailability(item.isAvailability());
        form.setAvailableTime(item.getAvailableTime());
        form.setLocation(item.getLocation());

        return "edit-item";
    }

    @PostMapping("/item/edit/{id}")
    public String edit_item_post(Model model, @PathVariable Long id, @Valid ItemForm form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "edit-item";
        }
        Item item = itemRepository.findById(id).get();
        loadItemIntoForm(model, item);
        saveItem(item, form.getName(), form.getDescription(), form.getCost(), form.getDeposit(), form.getAvailableTime(), form.getLocation(), item.getUser());

        return "redirect:/";
    }

    @GetMapping("/borrowall/{id}")
    public String show_item_get(Model model, @PathVariable Long id) {
        Item item = itemRepository.findById(id).get();

        loadItemIntoForm(model, item);

        return "item-detail.html";
    }

    @GetMapping("/borrowall")
    public String showItems(Model model) {
        // User is missing has to be added
        model.addAttribute("items", itemRepository.findAll());
        return "item-list";
    }

    private void loadItemIntoForm(Model model, Item item) {
        model.addAttribute("name", item.getName());
        model.addAttribute("description", item.getDescription());
        model.addAttribute("location", item.getLocation());
        model.addAttribute("availableTime", item.getAvailableTime());
        model.addAttribute("deposit", item.getDeposit());
        model.addAttribute("cost", item.getCost());
    }

    private void saveItem(Item item, String name, String description, int cost, int deposit, int availableTime, String location, User user ) {
        item.setName(name);
        item.setDescription(description);
        item.setCost(cost);
        item.setDeposit(deposit);
        item.setAvailableTime(availableTime);
        item.setLocation(location);
        item.setUser(user);
        itemRepository.save(item);
    }
}