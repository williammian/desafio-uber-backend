package br.com.wm.ubersfmovies.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import br.com.wm.ubersfmovies.model.MovieLocation;

@Service
public class MovieLocationService {

	private final WebClient webClient;
	
	public MovieLocationService(WebClient.Builder builder) {
		this.webClient = builder
				.baseUrl("https://data.sfgov.org/resource/yitu-d5am.json")
				.build();
	}
	
	public List<MovieLocation> getAllMovies() {
		return webClient.get()
				.retrieve()
				.bodyToFlux(MovieLocation.class)
				.collectList()
				.block();
	}
	
	public List<MovieLocation> filterByTitle(String query) {
		return getAllMovies().stream()
				.filter(m -> m.getTitle() != null && m.getTitle().toLowerCase().contains(query.toLowerCase()))
				.collect(Collectors.toList());
	}
	
	public List<String> autocomplete(String prefix) {
		return getAllMovies().stream()
				.map(MovieLocation::getTitle)
				.filter(Objects::nonNull)
				.filter(t -> t.toLowerCase().startsWith(prefix.toLowerCase()))
				.distinct()
				.sorted()
				.limit(10)
				.collect(Collectors.toList());
	}
	
}
