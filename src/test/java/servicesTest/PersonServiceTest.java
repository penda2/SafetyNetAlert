package servicesTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

import config.JsonFileReader;
import models.DatalistModel;
import models.Person;
import services.PersonService;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = PersonServiceTest.class)
public class PersonServiceTest {

	@InjectMocks
	private PersonService personService;

	@Mock
	private JsonFileReader jsonFileReader;

	@Mock
	private Person person;

	@Mock
	private DatalistModel datalistModel;

	public List<Person> persons;
	
	// Unit tests implementation for PesonService methods

	@Test
	public void testGetAllPersons() throws IOException {
		List<Person> testPersons = Arrays.asList(
				Person.builder().firstName("John").lastName("Boyd").address("1509 Culver St").city("Culver")
						.zip("97451").phone("841-874-6512").email("jaboyd@email.com").build(),
				Person.builder().firstName("Jacob").lastName("Boyd").address("1509 Culver St").city("Culver")
						.zip("97451").phone("841-874-6512").email("jaboyd@email.com\"").build());

		DatalistModel mockData = new DatalistModel();
		mockData.setPersons(testPersons);

		when(jsonFileReader.loadData()).thenReturn(mockData);

		List<Person> allPersons = personService.getAllPersons();

		assertNotNull(allPersons);
		assertEquals(2, allPersons.size());
		verify(jsonFileReader, times(1)).loadData();
	}

	@Test
	public void testCreatePerson() throws IOException {
		Person newPerson = Person.builder().firstName("Marie").lastName("Boyd").address("1509 Culver St").city("Culver")
				.zip("97451").phone("841-874-6512").email("jaboyd@email.com").build();

		DatalistModel mockData = new DatalistModel();
		when(jsonFileReader.loadData()).thenReturn(mockData);

		Person createdPerson = personService.addPerson(newPerson);

		assertNotNull(createdPerson);
		assertEquals(newPerson.getFirstName(), createdPerson.getFirstName());
		assertEquals(newPerson.getLastName(), createdPerson.getLastName());
		verify(jsonFileReader, times(1)).loadData();
	}

	@Test
	public void testUpdatePerson() throws IOException {
		List<Person> persons = Arrays.asList(
				Person.builder().firstName("John").lastName("Boyd").address("1509 Culver St").city("Culver")
						.zip("97451").phone("841-874-6512").email("jaboyd@email.com").build(),
				Person.builder().firstName("Jacob").lastName("Boyd").address("1509 Culver St").city("Culver")
						.zip("97451").phone("841-874-6512").email("jaboyd@email.com\"").build());

		Person updatedPersonData = Person.builder().firstName("John").lastName("Boyd").address("1509 Culver Sainte")
				.city("Culver").zip("97451").phone("841-874-6582").email("jaboyd@email.com").build();

		DatalistModel mockData = new DatalistModel();
		mockData.setPersons(persons);

		when(jsonFileReader.loadData()).thenReturn(mockData);

		personService.updatePerson("John", "Boyd", updatedPersonData);

		List<Person> updatedPersons = mockData.getPersons();
		assertEquals(2, updatedPersons.size());

		Person updatedPerson = updatedPersons.stream()
				.filter(p -> p.getFirstName().equals("John") && p.getLastName().equals("Boyd")).findFirst()
				.orElse(null);

		assertNotNull(updatedPerson);
		assertEquals("1509 Culver Sainte", updatedPerson.getAddress());
		assertEquals("841-874-6582", updatedPerson.getPhone());
		verify(jsonFileReader, times(1)).loadData();
	}

	@Test
	public void testDeletePerson() throws IOException {
		Person person1 = Person.builder().firstName("John").lastName("Boyd").address("1509 Culver St").city("Culver")
				.zip("97451").phone("841-874-6512").email("jaboyd@email.com").build();

		Person person2 = Person.builder().firstName("Jacob").lastName("Boyd").address("1509 Culver St").city("Culver")
				.zip("97451").phone("841-874-6512").email("jacob@example.com").build();

		DatalistModel mockData = new DatalistModel();
		mockData.setPersons(Arrays.asList(person1, person2));

		when(jsonFileReader.loadData()).thenReturn(mockData);

		personService.deletePerson("John", "Boyd");

		assertFalse(mockData.getPersons().stream()
				.anyMatch(p -> p.getFirstName().equals("John") && p.getLastName().equals("Boyd")));
	}
}
