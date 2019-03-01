package propra2.leihOrDie.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import propra2.leihOrDie.dataaccess.ItemRepository;
import propra2.leihOrDie.dataaccess.PictureRepository;
import propra2.leihOrDie.dataaccess.SessionRepository;
import propra2.leihOrDie.dataaccess.UserRepository;
import propra2.leihOrDie.form.ItemForm;
import propra2.leihOrDie.form.LoanForm;
import propra2.leihOrDie.model.Item;
import propra2.leihOrDie.model.Picture;
import propra2.leihOrDie.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ItemController {
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PictureRepository pictureRepository;
    @Autowired
    SessionRepository sessionRepository;

    @PostMapping("/item/create")
    public String newItem(Model model, @Valid ItemForm form, BindingResult bindingResult, @CookieValue(value="SessionID", defaultValue="") String sessionId) {
        User user = sessionRepository.findUserBySessionCookie(sessionId);

        if (bindingResult.hasErrors()) {
            return "create-item";
        }

        Item item = new Item(form.getName(), form.getDescription(), form.getCost(), form.getDeposit(), true,
                form.getAvailableTime(), user.getAddress(), user, form.getSoldPrice());
        saveItem(item);

        return "redirect:/item/" + item.getId() + "/uploadphoto";
    }

    @GetMapping("/item/create")
    public String newItem(Model model, ItemForm form) {
        return "create-item";
    }


    @GetMapping("/item/edit/{id}")
    public String editItem(Model model, @PathVariable Long id, ItemForm form) {
        Item item = itemRepository.findById(id).get();

        form.setName(item.getName());
        form.setDescription(item.getDescription());
        form.setCost(item.getCost());
        form.setDeposit(item.getDeposit());
        form.setAvailability(item.isAvailability());
        form.setAvailableTime(item.getAvailableTime());

        return "edit-item";
    }

    @PostMapping("/item/edit/{id}")
    public String editItem(Model model, @PathVariable Long id, @Valid ItemForm form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "edit-item";
        }
        Item item = itemRepository.findById(id).get();

        item.setName(form.getName());
        item.setDescription(form.getDescription());
        item.setCost(form.getCost());
        item.setDeposit(form.getDeposit());
        item.setAvailability(form.isAvailability());
        item.setAvailableTime(form.getAvailableTime());

        saveItem(item);

        return "redirect:/borrowall/" + id + "/";
    }

    @GetMapping("/borrowall/{id}")
    public String showItem(Model model, @PathVariable Long id, LoanForm form){
        Item item = itemRepository.findById(id).get();

        loadItemIntoForm(model, item);

        List<Picture> pictureList = pictureRepository.findPicturesOfItem(id);


        List<String> urlList = buildUrls(pictureList);
        int size = urlList.size();
        model.addAttribute("numOfPictures", size);
        if(size > 1) {
            model.addAttribute("firstPic", urlList.get(0));
            urlList.remove(0);
        }
        model.addAttribute("pictures", urlList);

        return "item-detail.html";
    }

    @GetMapping("/borrowall")
    public String listAllItems(Model model) {
        model.addAttribute("items", itemRepository.findAll());
        return "item-list";
    }

    private List<String> buildUrls(List<Picture> pictureList) {
        List<String> urlList = new ArrayList<>();

        if(pictureList.size() == 0) {
            return urlList;
        }

        for(Picture picture: pictureList) {
            urlList.add(buildUrl(picture));
        }

        return urlList;
    }

    private String buildUrl(Picture picture) {
        String raw = "/images/";
        String idString = picture.getId().toString();

        return raw + idString + picture.getType();
    }

    private void loadItemIntoForm(Model model, Item item) {
        model.addAttribute("name", item.getName());
        model.addAttribute("description", item.getDescription());
        model.addAttribute("location", item.getUser().getAddress().getCity());
        model.addAttribute("availableTime", item.getAvailableTime());
        model.addAttribute("deposit", item.getDeposit());
        model.addAttribute("cost", item.getCost());
        model.addAttribute("username", item.getUser().getUsername());
        model.addAttribute("isAvailable", item.isAvailability());
        model.addAttribute("itemID", item.getId());
        model.addAttribute("soldPrice", item.getSoldPrice());
    }

    private void saveItem(Item item) {
        itemRepository.save(item);
    }
}
