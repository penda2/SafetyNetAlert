package controllersTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import controllers.PersonController;
import exceptions.InvalidRequestException;
import models.Person;
import services.PersonService;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = PersonControllerTest.class)
public class PersonControllerTest {

	@InjectMocks
	private PersonController personController;

	@Mock
	private PersonService personService;

	// Unit tests implementation for PersonController methods

	@Test
	public void testGetPersons() throws IOException {
		List<Person> persons = Arrays.asList(
				Person.builder().firstName("John").lastName("Boyd").address("1509 Culver St").city("Culver")
						.zip("97451").phone("841-874-6512").email("jaboyd@email.com").build(),
				Person.builder().firstName("Jacob").lastName("Boyd").address("1509 Culver St").city("Culver")
						.zip("97451").phone("841-874-6512").email("jaboyd@email.com").build());

		when(personService.getAllPersons()).thenReturn(persons);

		List<Person> result = personController.getAllPersons();

		assertEquals(persons, result);
	}

	@Test
	public void testCreatePerson() {
		Person newPerson = Person.builder().firstName("Lily").lastName("Cooper").address("489 Manchester St")
				.city("Culver").zip("97451").phone("841-874-9845").email("lily@email.com").build();
		ResponseEntity<String> response = personController.addPerson(newPerson);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		verify(personService, times(1)).addPerson(newPerson);
	}

	@Test
	public void testAddPerson_InvalidRequestException() {
		Person newPerson = Person.builder().firstName("Lily").lastName("Cooper").address("489 Manchester St")
				.city("Culver").zip("97451").phone("841-874-9845").email("lily@email.com").build();

		doThrow(new InvalidRequestException("Invalid person data")).when(personService).addPerson(newPerson);

		ResponseEntity<String> response = personController.addPerson(newPerson);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertTrue(response.getBody().contains("Failed to add person"));
		assertTrue(response.getBody().contains("Invalid person data"));
	}

	@Test
	public void testUpdatePerson() throws IOException {
		String firstName = "John";
		String lastName = "Boyd";

		Person updatedPerson = Person.builder().firstName("John").lastName("Boyd").address("1509 Culver Sainte")
				.city("Culver").zip("97451").phone("841-874-6512").email("jaboyd@email.com").build();

		doNothing().when(personService).updatePerson(eq(firstName), eq(lastName), any(Person.class));

		ResponseEntity<String> response = personController.updatePerson(firstName, lastName, updatedPerson);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals("Person updated successfully", response.getBody());
		verify(personService, times(1)).updatePerson(eq(firstName), eq(lastName), eq(updatedPerson));
	}

	@Test
	public void testUpdatePerson_InvalidRequestException() throws IOException {
		String firstName = "John";
		String lastName = "Doe";

		Person updatedPerson = Person.builder().firstName("John").lastName("Doe").address("456 Oak St")
				.city("Springfield").zip("54321").phone("555-4321").email("john.doe@example.com").build();

		doThrow(new InvalidRequestException("Invalid person data")).when(personService).updatePerson(firstName,
				lastName, updatedPerson);

		ResponseEntity<String> response = personController.updatePerson(firstName, lastName, updatedPerson);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertTrue(response.getBody().contains("Failed to update person"));
		assertTrue(response.getBody().contains("Invalid person data"));
	}

	@Test
	public void deletePerson() throws IOException {
		String firstName = "John";
		String lastName = "Boyd";

		doNothing().when(personService).deletePerson(eq(firstName), eq(lastName));

		ResponseEntity<String> response = personController.deletePerson(firstName, lastName);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Person deleted successfully!", response.getBody());
		verify(personService, times(1)).deletePerson(eq(firstName), eq(lastName));
	}

	@Test
	public void testDeletePerson_PersonNotFound() throws IOException {
		String firstName = "David";
		String lastName = "Kane";

		doThrow(new IllegalArgumentException("Person not found: " + firstName + " " + lastName)).when(personService)
				.deletePerson(eq(firstName), eq(lastName));

		ResponseEntity<String> response = personController.deletePerson(firstName, lastName);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertTrue(response.getBody().contains("Person not found"));
		verify(personService, times(1)).deletePerson(eq(firstName), eq(lastName));
	}

	@Test
	public void testDeletePerson_InternalServerError() throws IOException {
		String firstName = "John";
		String lastName = "Wayne";

		doThrow(new IOException("Error deleting person")).when(personService).deletePerson(eq(firstName), eq(lastName));

		ResponseEntity<String> response = personController.deletePerson(firstName, lastName);

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		assertTrue(response.getBody().contains("Failed to delete person"));
		verify(personService, times(1)).deletePerson(eq(firstName), eq(lastName));
	}
}
