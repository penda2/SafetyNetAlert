package controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import exceptions.InvalidRequestException;
import jakarta.validation.Valid;
import models.Firestation;
import services.FireStationService;

@RestController
@RequestMapping("/firestation")
public class FireStationController {

	@Autowired
	FireStationService fireStationService;

	// CRUD operations for fire station

	// Get all fire stations
	@GetMapping
	public List<Firestation> getFirestations() {
		return fireStationService.getAllFirestations();
	}

	// Create fire station
	@PostMapping
	public ResponseEntity<String> createFireStation(@RequestBody @Valid Firestation newFirestation) {
		try {
			fireStationService.createFirestation(newFirestation);
			return ResponseEntity.status(HttpStatus.CREATED).body("Fire station added successfully !");
		} catch (InvalidRequestException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Please complete required fields !" + e.getMessage());
		}
	}

	// Update fire station
	@PutMapping("/{station}")
	public ResponseEntity<String> updateFireStation(@PathVariable String station,
			@RequestBody @Valid Firestation updatedFirestation) {
		try {
			fireStationService.updateFirestation(station, updatedFirestation);
			return ResponseEntity.status(HttpStatus.CREATED).body("Fire station updated successfully !");
		} catch (InvalidRequestException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Failed to update fire station : " + e.getMessage());
		}
	}

	// Delete fire station
	@DeleteMapping("/{station}")
	public ResponseEntity<String> deleteFireStation(@PathVariable String station) {
		fireStationService.deleteFireStation(station);
		return ResponseEntity.ok("Fire station deleted successfully !");
	}
}
