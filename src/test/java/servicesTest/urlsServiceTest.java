package servicesTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import config.JsonFileReader;
import models.DatalistModel;
import models.Firestation;
import models.Medicalrecord;
import models.Person;
import services.FireStationService;
import services.UrlsService;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = urlsServiceTest.class)
public class urlsServiceTest {

	@InjectMocks
	private UrlsService urlsService;

	@Mock
	private JsonFileReader jsonFileReader;

	@Mock
	private Person person;

	@Mock
	private Medicalrecord medicalrecord;

	@Mock
	FireStationService fireStationService;

	public List<Person> persons;

	public List<Medicalrecord> medicalrecords;
	
	// Unit tests implementation for UrlsService methods

	@BeforeEach
	public void setUp() throws IOException {
		List<Firestation> firestations = Arrays.asList(
				Firestation.builder().address("1509 Culver St").station("3").build(),
				Firestation.builder().address("29 15th St").station("2").build());

		List<Person> filteredPersons = Arrays.asList(
				Person.builder().firstName("John").lastName("Boyd").address("1509 Culver St").city("Culver")
						.zip("97451").phone("841-874-6513").email("jaboyd@email.com").build(),
				Person.builder().firstName("Roger").lastName("Boyd").address("1509 Culver St").city("Culver")
						.zip("97451").phone("841-874-6514").email("roboyd@email.com").build(),
				Person.builder().firstName("Jacob").lastName("Boyd").address("1509 Culver St").city("Culver")
						.zip("97451").phone("841-874-6512").email("coboyd@email.com\"").build());

		List<Medicalrecord> medicalrecords = Arrays.asList(
				Medicalrecord.builder().firstName("John").lastName("Boyd").birthdate("03/06/1984")
						.medications(Arrays.asList("aznol:350mg", "hydrapermazol:100mg"))
						.allergies(Arrays.asList("nillacilan")).build(),
				Medicalrecord.builder().firstName("Jacob").lastName("Boyd").birthdate("03/06/1989")
						.medications(Arrays.asList()).allergies(Arrays.asList()).build(),
				Medicalrecord.builder().firstName("Roger").lastName("Boyd").birthdate("09/06/2017")
						.medications(Arrays.asList()).allergies(Arrays.asList()).build());

		DatalistModel mockData = new DatalistModel();
		mockData.setPersons(filteredPersons);
		mockData.setFirestations(firestations);
		mockData.setMedicalrecords(medicalrecords);

		when(jsonFileReader.loadData()).thenReturn(mockData);
	}

	@Test
	public void testPersonListByFireStation() {
		String station = "3";
		Map<String, Object> result = urlsService.getPersonByStationNumber(station);

		assertNotNull(result);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> personsList = (List<Map<String, Object>>) result.get("Persons");
		assertEquals(3, personsList.size());

		assertEquals(2, result.get("Adults"));
		assertEquals(1, result.get("Children"));
	}

	@Test
	public void testGetChildrenByAddress() {
		String address = "1509 Culver St";
		List<Map<String, Object>> childrenList = urlsService.getChildrenByAddress(address);

		assertNotNull(childrenList);
		assertFalse(childrenList.isEmpty());
		assertEquals(1, childrenList.size());

		for (Map<String, Object> childInfo : childrenList) {
			assertTrue(childInfo.containsKey("FirstName"));
			assertTrue(childInfo.containsKey("LastName"));
			assertTrue(childInfo.containsKey("Age"));
			assertTrue((int) childInfo.get("Age") < 18);
		}
	}

	@Test
	public void testGetPhoneNumbersByStation() {
		String station = "3";
		Map<String, Object> result = urlsService.getPhoneNumbersByStation(station);

		assertNotNull(result);
		@SuppressWarnings("unchecked")
		List<String> phoneNumbers = (List<String>) result.get("Phone numbers");
		assertEquals(3, phoneNumbers.size());
		assertTrue(phoneNumbers.contains("841-874-6512"));
	}

	@Test
	public void testGetPersonsByAddress() {
		String address = "1509 Culver St";
		List<Map<String, Object>> result = urlsService.getPersonsWithAgeByAddress(address);
		assertNotNull(result);
		assertEquals(3, result.size());

		for (Map<String, Object> personInfo : result) {
			assertTrue(personInfo.containsKey("FirstName"));
			assertTrue(personInfo.containsKey("Age"));
			assertTrue(personInfo.containsKey("Medications"));
		}
	}

	@Test
	public void testGetPersonsInAddress() {
		String station = "3";
		List<Map<String, Object>> result = urlsService.getPersonsInAddress(station);

		assertNotNull(result);
		assertEquals(3, result.size());

		for (Map<String, Object> personInfo : result) {
			assertTrue(personInfo.containsKey("Person infos"));
			assertTrue(personInfo.containsKey("Phone"));
			assertTrue(personInfo.containsKey("Age"));
		}
	}

	@Test
	public void testGetFamilyInfo() {
		String firstName = "John";
		String lastName = "Boyd";
		List<Map<String, Object>> result = urlsService.getFamilyInfo(firstName, lastName);

		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertEquals(3, result.size());

		Map<String, Object> familyInfo = result.get(0);
		assertEquals(lastName, familyInfo.get("lastName"));
		assertEquals("1509 Culver St", familyInfo.get("address"));
		assertEquals(39, familyInfo.get("age"));
	}

	@Test
	public void testGetEmailsByCity() {
		String city = "Culver";
		Map<String, Object> result = urlsService.getEmailsByCity(city);

		assertNotNull(result);
		@SuppressWarnings("unchecked")
		List<String> uniqueEmails = (List<String>) result.get("Emails");
		assertEquals(3, uniqueEmails.size());
	}

	@Test
	public void testLoadDataFromFileThrowsRuntimeException() throws IOException {
		when(jsonFileReader.loadData()).thenThrow(new IOException("Simulated IO Exception"));

		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			urlsService.getEmailsByCity("Culver");
		});

		String expectedMessage = "Error loading data from JSON";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
		assertNotNull(exception.getCause());
		assertTrue(exception.getCause() instanceof IOException);
		verify(jsonFileReader, times(1)).loadData();
	}
}
