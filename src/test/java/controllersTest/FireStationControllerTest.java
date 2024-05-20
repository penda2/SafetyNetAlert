package controllersTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import controllers.FireStationController;
import exceptions.InvalidRequestException;
import models.Firestation;
import services.FireStationService;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = FireStationControllerTest.class)
public class FireStationControllerTest {

	@InjectMocks
	private FireStationController fireStationController;

	@Mock
	private FireStationService fireStationService;

	// Unit tests implementation for FireStationController methods

	@Test
	public void testGetFirestations() {
		List<Firestation> firestations = Arrays.asList(
				Firestation.builder().address("1509 Culver St").station("3").build(),
				Firestation.builder().address("29 15th St").station("2").build());

		when(fireStationService.getAllFirestations()).thenReturn(firestations);

		List<Firestation> result = fireStationController.getFirestations();

		assertEquals(firestations, result);
	}

	@Test
	public void testCreateFireStation_ValidInput() {
		Firestation newFirestation = Firestation.builder().address("908 73rd St").station("1").build();
		ResponseEntity<String> response = fireStationController.createFireStation(newFirestation);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals("Fire station added successfully !", response.getBody());
		verify(fireStationService, times(1)).createFirestation(newFirestation);
	}

	@Test
	public void testCreateFireStation_InvalidRequestException() {
		Firestation newFirestation = Firestation.builder().station("1").address("123 Main St").build();

		doThrow(new InvalidRequestException("Please complete required fields")).when(fireStationService)
				.createFirestation(newFirestation);

		ResponseEntity<String> response = fireStationController.createFireStation(newFirestation);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertTrue(response.getBody().contains("Please complete required fields"));
	}

	@Test
	public void updateFireStation() {
		Firestation updatedFirestation = Firestation.builder().address("2509 Culver Sainte").station("3").build();
		Firestation returnedFirestation = Firestation.builder().address("2509 Culver Sainte").station("3").build();

		when(fireStationService.updateFirestation(eq("3"), eq(updatedFirestation))).thenReturn(returnedFirestation);

		ResponseEntity<String> response = fireStationController.updateFireStation("3", updatedFirestation);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals("Fire station updated successfully !", response.getBody());

		verify(fireStationService, times(1)).updateFirestation("3", updatedFirestation);
	}

	@Test
	public void testUpdateFireStation_InvalidRequestException() {
		String station = "1";

		Firestation updatedFirestation = Firestation.builder().station("1").address("456 Oak St").build();

		doThrow(new InvalidRequestException("Invalid fire station data")).when(fireStationService)
				.updateFirestation(station, updatedFirestation);

		ResponseEntity<String> response = fireStationController.updateFireStation(station, updatedFirestation);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertTrue(response.getBody().contains("Failed to update fire station"));
		assertTrue(response.getBody().contains("Invalid fire station data"));
	}

	@Test
	public void deleteFireStation() {
		String stationId = "3";

		doNothing().when(fireStationService).deleteFireStation(stationId);

		ResponseEntity<String> response = fireStationController.deleteFireStation(stationId);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Fire station deleted successfully !", response.getBody());

		verify(fireStationService, times(1)).deleteFireStation(stationId);
	}
}
