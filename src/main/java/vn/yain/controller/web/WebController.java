package vn.yain.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping({"/", "/home"})
    public String home() {
        return "home";
    }

    @GetMapping("/products")
    public String products() {
        return "products";
    }

    @GetMapping("/categories")
    public String categories() {
        return "categories";
    }
}
