package com.sbommaker.web;

import com.sbommaker.model.ProductRelease;
import com.sbommaker.service.ComponentService;
import com.sbommaker.service.ReleaseService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/releases")
public class ReleaseController {

    private final ReleaseService releaseService;
    private final ComponentService componentService;

    public ReleaseController(ReleaseService releaseService, ComponentService componentService) {
        this.releaseService = releaseService;
        this.componentService = componentService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("releases", releaseService.findAll());
        return "releases/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("release", new ProductRelease());
        return "releases/form";
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute("release") ProductRelease release,
                         BindingResult result, RedirectAttributes flash) {
        if (result.hasErrors()) return "releases/form";
        releaseService.save(release);
        flash.addFlashAttribute("success", "Release created.");
        return "redirect:/releases";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("release", releaseService.findById(id));
        model.addAttribute("allComponents", componentService.findAll());
        return "releases/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("release", releaseService.findById(id));
        return "releases/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("release") ProductRelease release,
                         BindingResult result, RedirectAttributes flash) {
        if (result.hasErrors()) return "releases/form";
        release.setId(id);
        releaseService.save(release);
        flash.addFlashAttribute("success", "Release updated.");
        return "redirect:/releases/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes flash) {
        releaseService.delete(id);
        flash.addFlashAttribute("success", "Release deleted.");
        return "redirect:/releases";
    }

    @PostMapping("/{id}/items")
    public String addItem(@PathVariable Long id,
                          @RequestParam Long componentVersionId,
                          RedirectAttributes flash) {
        releaseService.addComponentVersion(id, componentVersionId);
        flash.addFlashAttribute("success", "Component version added to release.");
        return "redirect:/releases/" + id;
    }

    @PostMapping("/{id}/items/{itemId}/delete")
    public String removeItem(@PathVariable Long id, @PathVariable Long itemId,
                             RedirectAttributes flash) {
        releaseService.removeItem(id, itemId);
        flash.addFlashAttribute("success", "Component removed from release.");
        return "redirect:/releases/" + id;
    }
}
