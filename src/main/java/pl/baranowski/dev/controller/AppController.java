package pl.baranowski.dev.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AppController {

	@GetMapping("/")
	@ResponseBody
	public String sayHello() {
		return "hello";
	}
}
