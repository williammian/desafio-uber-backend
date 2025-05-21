package br.com.wm.ubersfmovies.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.wm.ubersfmovies.model.MovieLocation;
import br.com.wm.ubersfmovies.service.MovieLocationService;

@RestController
@RequestMapping("/movies")
public class MovieLocationController {

	@Autowired
	private MovieLocationService service;
	
	@GetMapping
	public List<MovieLocation> getMovies(@RequestParam Optional<String> title) {
		return title.map(service::filterByTitle)
				.orElseGet(service::getAllMovies);
	}
	
	@GetMapping("/autocomplete")
	public List<String> autocomplete(@RequestParam("q") String prefix) {
		return service.autocomplete(prefix);
	}
	
}
