package com.example.controller;

import com.example.model.CardSet;
import com.example.model.Note;
import com.example.model.User;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.service.interfaces.CardSetService;
import com.example.service.interfaces.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cardSets")
public class CardSetController {

    private final CardSetService cardSetService;
    private final UserService userService;

    @GetMapping("/new")
    public String showNewCardSetForm(Model model) {
        model.addAttribute("cardSet", new CardSet());
        return "cardSetForm";
    }

    @GetMapping
    public String getAllCardSets(Model model) {
        User user = userService.getCurrentAuthenticatedUser();
        List<CardSet> cardSets = cardSetService.getCardSetsByUser(user);
        model.addAttribute("cardSets", cardSets);
        model.addAttribute("username", user.getEmail());
        return "cardSets";
    }

    @PostMapping
    public String saveCardSet(@ModelAttribute CardSet cardSet, Model model) {
        User user = userService.getCurrentAuthenticatedUser();
        cardSet.setUser(user);

        if (!cardSetService.isCardSetNameUnique(user, cardSet)) {
            model.addAttribute("error", "A card set with this name already exists. Please choose a different name.");
            model.addAttribute("cardSet", cardSet);
            return "cardSetForm";
        }

        cardSetService.saveCardSet(cardSet);
        return "redirect:/cardSets";
    }

    @GetMapping("/{id}/edit")
    public String showEditCardSetForm(@PathVariable Long id, Model model) {
        CardSet cardSet = cardSetService.getCardSetById(id);
        model.addAttribute("cardSet", cardSet);
        return "editCardSetForm";
    }

    @PostMapping("/{id}/update")
    public String updateCardSet(@PathVariable Long id, @ModelAttribute CardSet cardSet, Model model) {
        User user = userService.getCurrentAuthenticatedUser();
        cardSet.setUser(user);

        if (!cardSetService.isCardSetNameUnique(user, cardSet)) {
            model.addAttribute("error", "A card set with this name already exists. Please choose a different name.");
            model.addAttribute("cardSet", cardSet);
            return "editCardSetForm";
        }

        cardSetService.updateCardSet(cardSet);
        return "redirect:/cardSets";
    }

    @GetMapping("/{id}/delete")
    public String deleteCardSet(@PathVariable Long id) {
        cardSetService.deleteCardSetById(id);
        return "redirect:/cardSets";
    }
}