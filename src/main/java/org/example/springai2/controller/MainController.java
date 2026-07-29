package org.example.springai2.controller;

import lombok.RequiredArgsConstructor;
import org.example.springai2.dto.ChatDTO;
import org.example.springai2.service.ChatService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class MainController {
    private final ChatService chatService;

    @GetMapping
    public String index() {
        return "index";
    }

    @PostMapping
    public String chat(ChatDTO dto, RedirectAttributes redirectAttributes) {
        String result = chatService.chat(dto);
        redirectAttributes.addFlashAttribute("result", result);
        return "redirect:/";
    }
}
