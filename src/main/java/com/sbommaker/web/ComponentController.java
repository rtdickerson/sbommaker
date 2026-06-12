package com.sbommaker.web;

import com.sbommaker.model.ComponentVersion;
import com.sbommaker.model.SoftwareComponent;
import com.sbommaker.service.ComponentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/components")
public class ComponentController {

    private final ComponentService componentService;

    public ComponentController(ComponentService componentService) {
        this.componentService = componentService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("components", componentService.findAll());
        return "components/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("component", new SoftwareComponent());
        return "components/form";
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute("component") SoftwareComponent component,
                         BindingResult result, RedirectAttributes flash) {
        if (result.hasErrors()) return "components/form";
        componentService.save(component);
        flash.addFlashAttribute("success", "Component created.");
        return "redirect:/components";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        SoftwareComponent component = componentService.findById(id);
        model.addAttribute("component", component);
        model.addAttribute("newVersion", new ComponentVersion());
        return "components/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("component", componentService.findById(id));
        return "components/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("component") SoftwareComponent component,
                         BindingResult result, RedirectAttributes flash) {
        if (result.hasErrors()) return "components/form";
        component.setId(id);
        componentService.save(component);
        flash.addFlashAttribute("success", "Component updated.");
        return "redirect:/components/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes flash) {
        componentService.delete(id);
        flash.addFlashAttribute("success", "Component deleted.");
        return "redirect:/components";
    }

    @PostMapping("/{id}/versions")
    public String addVersion(@PathVariable Long id,
                             @Valid @ModelAttribute("newVersion") ComponentVersion version,
                             BindingResult result, Model model, RedirectAttributes flash) {
        if (result.hasErrors()) {
            model.addAttribute("component", componentService.findById(id));
            return "components/detail";
        }
        componentService.addVersion(id, version);
        flash.addFlashAttribute("success", "Version added.");
        return "redirect:/components/" + id;
    }

    @PostMapping("/{id}/versions/{versionId}/delete")
    public String deleteVersion(@PathVariable Long id, @PathVariable Long versionId,
                                RedirectAttributes flash) {
        componentService.deleteVersion(versionId);
        flash.addFlashAttribute("success", "Version deleted.");
        return "redirect:/components/" + id;
    }
}
