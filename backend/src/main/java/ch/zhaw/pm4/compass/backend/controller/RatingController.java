package ch.zhaw.pm4.compass.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.zhaw.pm4.compass.backend.model.dto.RatingDto;
import ch.zhaw.pm4.compass.backend.service.RatingService;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Rating Controller", description = "Rating Enpoint")
@RestController
@RequestMapping("/rating")
public class RatingController {
	@Autowired
	private RatingService ratingService;

	@PostMapping(produces = "application/json")
	public ResponseEntity<RatingDto> createRating(@RequestBody RatingDto rating) {
		return ResponseEntity.ok(ratingService.createRating(rating));
	}
}
