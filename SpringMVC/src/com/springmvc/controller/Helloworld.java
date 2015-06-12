package com.springmvc.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
 
/*
 * author: Crunchify.com
 * 
 */
 
@Controller
public class Helloworld {
 
	@RequestMapping("/welcome")
	public ModelAndView helloWorld(@PathVariable Map<String, String> pathVars) {
 
		return new ModelAndView("welcome", "message", "");
	}
}