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

import controllers.MedicalRecordsController;
import exceptions.InvalidRequestException;
import models.Medicalrecord;
import services.MedicalRecordsService;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = MedicalRecordsControllerTest.class)
public class MedicalRecordsControllerTest {

	@InjectMocks
	private MedicalRecordsController medicalRecordsController;

	@Mock
	private MedicalRecordsService medicalRecordsService;
	
	// Unit tests implementation for MedicalRecordsController methods
	@Test
	public void testGetMedicalRecords() throws IOException {
		List<Medicalrecord> medicalrecords = Arrays.asList(
				Medicalrecord.builder().firstName("John").lastName("Boyd").birthdate("03/06/1984")
						.medications(Arrays.asList("aznol:350mg", "hydrapermazol:100mg"))
						.allergies(Arrays.asList("nillacilan")).build(),
				Medicalrecord.builder().firstName("Jonanathan").lastName("Marrack").birthdate("01/03/1989")
						.medications(Arrays.asList()).allergies(Arrays.asList()).build());

		when(medicalRecordsService.getMedicalrecords()).thenReturn(medicalrecords);

		List<Medicalrecord> result = medicalRecordsController.getMedicalrecords();

		assertEquals(medicalrecords, result);
	}

	@Test
	public void testCreateMedicalrecord() {
		Medicalrecord newMedicalrecord = Medicalrecord.builder().firstName("Felicia").lastName("Boyd")
				.birthdate("01/08/1986").medications(Arrays.asList("tetracyclaz:650mg"))
				.allergies(Arrays.asList("xilliathal")).build();
		ResponseEntity<String> response = medicalRecordsController.addMedicalRecord(newMedicalrecord);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		verify(medicalRecordsService, times(1)).addMedicalrecord(newMedicalrecord);
	}

	@Test
	public void testAddMedicalRecord_InvalidRequestException() {
		Medicalrecord newMedicalrecord = Medicalrecord.builder().firstName("Felicia").lastName("Boyd")
				.birthdate("01/08/1986").medications(Arrays.asList("tetracyclaz:650mg"))
				.allergies(Arrays.asList("xilliathal")).build();

		doThrow(new InvalidRequestException("Invalid medical record data")).when(medicalRecordsService)
				.addMedicalrecord(newMedicalrecord);

		ResponseEntity<String> response = medicalRecordsController.addMedicalRecord(newMedicalrecord);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertTrue(response.getBody().contains("Failed to add medical record"));
		assertTrue(response.getBody().contains("Invalid medical record data"));
	}

	@Test
	public void testUpdateMedicalrecord() throws IOException {
		String firstName = "Felicia";
		String lastName = "Boyd";

		Medicalrecord updatedMedicalrecord = Medicalrecord.builder().firstName("Felicia").lastName("Boyd")
				.birthdate("01/08/1986").medications(Arrays.asList("tetracyclaz:650mg"))
				.allergies(Arrays.asList("xilliathal")).build();

		doNothing().when(medicalRecordsService).updateMedicalRecord(eq(firstName), eq(lastName),
				any(Medicalrecord.class));

		ResponseEntity<String> response = medicalRecordsController.updateMedicalRecord(firstName, lastName,
				updatedMedicalrecord);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals("Medical record updated successfully", response.getBody());
		verify(medicalRecordsService, times(1)).updateMedicalRecord(eq(firstName), eq(lastName),
				eq(updatedMedicalrecord));
	}

	@Test
	public void testUpdateMedicalrecord_InvalidRequestException() throws IOException {
		String firstName = "Jonanathan";
		String lastName = "Marrack";

		Medicalrecord updatedMedicalrecord = Medicalrecord.builder().firstName("Jonanathan").lastName("Marrack")
				.birthdate("01/03/1989").medications(Arrays.asList()).allergies(Arrays.asList()).build();

		doThrow(new InvalidRequestException("Invalid medicalrecord data")).when(medicalRecordsService)
				.updateMedicalRecord(firstName, lastName, updatedMedicalrecord);

		ResponseEntity<String> response = medicalRecordsController.updateMedicalRecord(firstName, lastName,
				updatedMedicalrecord);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertTrue(response.getBody().contains("Failed to update medical record"));
	}

	@Test
	public void deleteMedicalrecord() throws IOException {
		String firstName = "John";
		String lastName = "Boyd";

		List<Medicalrecord> mockMedicalRecords = Arrays.asList(
				Medicalrecord.builder().firstName("John").lastName("Boyd").birthdate("03/06/1984")
						.medications(Arrays.asList("aznol:350mg", "hydrapermazol:100mg"))
						.allergies(Arrays.asList("nillacilan")).build(),
				Medicalrecord.builder().firstName("Jonanathan").lastName("Marrack").birthdate("01/03/1989")
						.medications(Arrays.asList()).allergies(Arrays.asList()).build());

		when(medicalRecordsService.deleteMedicalrecord(eq(firstName), eq(lastName))).thenReturn(mockMedicalRecords);

		ResponseEntity<String> response = medicalRecordsController.deleteMedicalRecord(firstName, lastName);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Medical record deleted successfully!", response.getBody());
		verify(medicalRecordsService, times(1)).deleteMedicalrecord(eq(firstName), eq(lastName));
	}
}
