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
import models.Person;
import services.PersonService;

@RestController
@RequestMapping("/person")
public class PersonController {

	@Autowired
	private PersonService personService;

	// CRUD operations for person

	// get list of persons
	@GetMapping
	public List<Person> getAllPersons() throws IOException {
		return personService.getAllPersons();
	}

	// Create person
	@PostMapping
	public ResponseEntity<String> addPerson(@RequestBody @Valid Person newPerson) {
		try {
			personService.addPerson(newPerson);
			return ResponseEntity.status(HttpStatus.CREATED).body("Person added successfully");
		} catch (InvalidRequestException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to add person: " + e.getMessage());
		}
	}

	// Update person
	@PutMapping("/{firstName}/{lastName}")
	public ResponseEntity<String> updatePerson(@PathVariable String firstName, @PathVariable String lastName,
			@RequestBody @Valid Person updatedPersonData) throws IOException {
		try {
			personService.updatePerson(firstName, lastName, updatedPersonData);
			return ResponseEntity.status(HttpStatus.CREATED).body("Person updated successfully");
		} catch (InvalidRequestException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update person: " + e.getMessage());
		}
	}

	//Delete person 
	@DeleteMapping("/{firstName}/{lastName}")
	public ResponseEntity<String> deletePerson(@PathVariable String firstName, @PathVariable String lastName) {
		try {
			personService.deletePerson(firstName, lastName);
			return ResponseEntity.ok("Person deleted successfully!");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Person not found: " + firstName + " " + lastName);
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to delete person: " + e.getMessage());
		}
	}
}
