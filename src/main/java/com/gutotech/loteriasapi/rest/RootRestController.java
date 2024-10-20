package com.gutotech.loteriasapi.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gutotech.loteriasapi.service.ResultadoService;

@RestController
@RequestMapping("/")
public class RootRestController {
	
	@Autowired
	private ResultadoService resultadoService;

	@GetMapping(value = "/")
	public void redirectToSwagger(HttpServletResponse response) throws IOException {
		response.sendRedirect("/swagger-ui/");
	}
	
	@GetMapping(value = "/reset")
	public void reset() {
		resultadoService.reset();
	}

}
