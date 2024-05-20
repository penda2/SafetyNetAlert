package controllers;

import java.io.IOException;
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
import models.Medicalrecord;
import services.MedicalRecordsService;

@RestController
@RequestMapping("/medicalrecord")
public class MedicalRecordsController {

	@Autowired
	MedicalRecordsService medicalRecordsService;

	// CRUD operations for medical records

	// Get list of medical records
	@GetMapping
	public List<Medicalrecord> getMedicalrecords() throws IOException {
		return medicalRecordsService.getMedicalrecords();
	}

	// Create medical record
	@PostMapping
	public ResponseEntity<String> addMedicalRecord(@RequestBody @Valid Medicalrecord newMedicalrecord) {
		try {
			medicalRecordsService.addMedicalrecord(newMedicalrecord);
			return ResponseEntity.status(HttpStatus.CREATED).body("Medical record added successfully");
		} catch (InvalidRequestException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Failed to add medical record: " + e.getMessage());
		}
	}

	// Update Medical record
	@PutMapping("/{firstName}/{lastName}")
	public ResponseEntity<String> updateMedicalRecord(@PathVariable String firstName, @PathVariable String lastName,
			@RequestBody @Valid Medicalrecord updatedMedicalrecord) throws IOException {
		try {
			medicalRecordsService.updateMedicalRecord(firstName, lastName, updatedMedicalrecord);
			return ResponseEntity.status(HttpStatus.CREATED).body("Medical record updated successfully");
		} catch (InvalidRequestException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Failed to update medical record: " + e.getMessage());
		}
	}

	@DeleteMapping("/{firstName}/{lastName}")
	public ResponseEntity<String> deleteMedicalRecord(@PathVariable String firstName, @PathVariable String lastName) {
		medicalRecordsService.deleteMedicalrecord(firstName, lastName);
		return ResponseEntity.ok("Medical record deleted successfully!");
	}
}
