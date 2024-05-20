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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import config.JsonFileReader;
import exceptions.notFoundFireStation;
import models.DatalistModel;
import models.Firestation;
import services.FireStationService;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = FireStationServiceTest.class)
public class FireStationServiceTest {

	@InjectMocks
	private FireStationService fireStationService;

	@Mock
	private JsonFileReader jsonFileReader;

	@Mock
	private DatalistModel datalistModel;

	@Mock
	private Firestation firestation;

	public List<Firestation> firestations;
	
	// Unit tests implementation for FireStationService methods

	@BeforeEach
	public void setUp() throws IOException {
		List<Firestation> firestations = Arrays.asList(
				Firestation.builder().address("1509 Culver St").station("3").build(),
				Firestation.builder().address("29 15th St").station("2").build());

		DatalistModel mockData = new DatalistModel();
		mockData.setFirestations(firestations);
		when(jsonFileReader.loadData()).thenReturn(mockData);
	}

	@Test
	public void testGetAllFireStations() throws IOException {
		List<Firestation> allFirestations = fireStationService.getAllFirestations();

		assertNotNull(allFirestations);
		assertEquals(2, allFirestations.size());
		verify(jsonFileReader, times(1)).loadData();
	}

	@Test
	public void testCreateFireStation() throws IOException {
		Firestation newFirestation = Firestation.builder().address("908 73rd St").station("1").build();
		DatalistModel mockData = new DatalistModel();
		mockData.setFirestations(new ArrayList<>());

		Firestation createdFirestation = fireStationService.createFirestation(newFirestation);

		assertNotNull(createdFirestation);
		assertEquals(newFirestation.getAddress(), createdFirestation.getAddress());
		assertEquals(newFirestation.getStation(), createdFirestation.getStation());
		verify(jsonFileReader, times(1)).loadData();
	}

	@Test
	public void testUpdateFireStation() throws IOException {
		List<Firestation> firestations = Arrays.asList(
				Firestation.builder().address("1509 Culver St").station("3").build(),
				Firestation.builder().address("29 15th St").station("2").build());
		Firestation updateFirestationDataFirestation = Firestation.builder().address("2509 Culver Sainte").station("3")
				.build();

		DatalistModel mockData = new DatalistModel();
		mockData.setFirestations(firestations);

		fireStationService.updateFirestation("3", updateFirestationDataFirestation);

		List<Firestation> updatedFirestations = mockData.getFirestations();

		assertEquals(2, updatedFirestations.size());
		assertEquals("2509 Culver Sainte", updateFirestationDataFirestation.getAddress());
		verify(jsonFileReader, times(1)).loadData();
	}

	@Test
	public void testUpdateFireStation_NotFound() throws IOException {
		List<Firestation> firestations = Arrays.asList(
				Firestation.builder().address("1509 Culver St").station("3").build(),
				Firestation.builder().address("29 15th St").station("2").build());

		Firestation updateFirestationDataFirestation = Firestation.builder().address("2509 Culver Sainte").station("3")
				.build();
		DatalistModel mockData = new DatalistModel();
		mockData.setFirestations(firestations);

		String nonExistentStation = "10";

		Exception exception = assertThrows(notFoundFireStation.class, () -> {
			fireStationService.updateFirestation(nonExistentStation, updateFirestationDataFirestation);
		});

		String expectedMessage = "Firestation number " + nonExistentStation + " not found !";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
		verify(jsonFileReader, times(1)).loadData();
	}

	@Test
	public void testDeleteFireStation_StationExists() throws IOException {
		List<Firestation> firestations = new ArrayList<>();
		firestations.add(Firestation.builder().address("1509 Culver St").station("3").build());
		firestations.add(Firestation.builder().address("29 15th St").station("2").build());

		DatalistModel mockData = new DatalistModel();
		mockData.setFirestations(firestations);

		when(jsonFileReader.loadData()).thenReturn(mockData);

		fireStationService.deleteFireStation("3");

		verify(jsonFileReader, times(1)).loadData();

		List<Firestation> updatedFirestations = mockData.getFirestations();
		assertEquals(1, updatedFirestations.size());
		assertFalse(updatedFirestations.stream().anyMatch(f -> f.getStation().equals("3")));
	}

	@Test
	public void testDeleteFireStationNotFound() throws IOException {
		List<Firestation> firestations = new ArrayList<>();
		firestations.add(Firestation.builder().address("1509 Culver St").station("3").build());
		firestations.add(Firestation.builder().address("29 15th St").station("2").build());

		DatalistModel mockData = new DatalistModel();
		mockData.setFirestations(firestations);
		when(jsonFileReader.loadData()).thenReturn(mockData);

		String nonExistentStation = "5";

		notFoundFireStation exception = assertThrows(notFoundFireStation.class, () -> {
			fireStationService.deleteFireStation(nonExistentStation);
		});

		String expectedMessage = "Fire station number " + nonExistentStation + " not found!";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
		verify(jsonFileReader, times(1)).loadData();
	}

	@Test
	public void testLoadDataFromFile_Success() throws IOException {
		DatalistModel testData = new DatalistModel();
		testData.setFirestations(new ArrayList<>());

		when(jsonFileReader.loadData()).thenReturn(testData);

		fireStationService.loadDataFromFile();

		verify(jsonFileReader, times(1)).loadData();
		assertNotNull(fireStationService.getAllFirestations());
	}

	@Test
	public void testLoadDataFromFile_Error() throws IOException {
		when(jsonFileReader.loadData()).thenThrow(new IOException("Simulated loading error"));

		assertThrows(RuntimeException.class, () -> {
			fireStationService.loadDataFromFile();
		});
	}
}
