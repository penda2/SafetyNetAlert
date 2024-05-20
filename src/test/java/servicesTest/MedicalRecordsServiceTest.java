package servicesTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
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
import models.Medicalrecord;
import services.MedicalRecordsService;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = MedicalRecordsServiceTest.class)
public class MedicalRecordsServiceTest {

	@InjectMocks
	private MedicalRecordsService medicalRecordsService;

	@Mock
	private JsonFileReader jsonFileReader;

	@Mock
	private DatalistModel datalistModel;

	@Mock
	private Medicalrecord medicalrecord;

	public List<Medicalrecord> medicalrecords;
	
	// Unit tests implementation for MedicalRecordsService methods

	@Test
	public void testGetMedicalRecords() throws IOException {
		List<Medicalrecord> testMedicalrecords = Arrays.asList(
				Medicalrecord.builder().firstName("John").lastName("Boyd").birthdate("03/06/1984")
						.medications(Arrays.asList("aznol:350mg", "hydrapermazol:100mg"))
						.allergies(Arrays.asList("nillacilan")).build(),
				Medicalrecord.builder().firstName("Jonanathan").lastName("Marrack").birthdate("01/03/1989")
						.medications(Arrays.asList()).allergies(Arrays.asList()).build());
		DatalistModel mockData = new DatalistModel();
		mockData.setMedicalrecords(testMedicalrecords);

		when(jsonFileReader.loadData()).thenReturn(mockData);

		List<Medicalrecord> allMedicalrecords = medicalRecordsService.getMedicalrecords();
		assertNotNull(allMedicalrecords);
		assertEquals(2, allMedicalrecords.size());
		verify(jsonFileReader, times(1)).loadData();
	}

	@Test
	public void testCreateMedicalRecords() throws IOException {
		Medicalrecord newMedicalrecord = Medicalrecord.builder().firstName("Felicia").lastName("Boyd")
				.birthdate("01/08/1986").medications(Arrays.asList("tetracyclaz:650mg"))
				.allergies(Arrays.asList("xilliathal")).build();

		DatalistModel mockData = new DatalistModel();
		when(jsonFileReader.loadData()).thenReturn(mockData);

		Medicalrecord createdMedicalrecord = medicalRecordsService.addMedicalrecord(newMedicalrecord);

		assertNotNull(createdMedicalrecord);
		assertEquals(newMedicalrecord.getFirstName(), createdMedicalrecord.getFirstName());
		assertEquals(newMedicalrecord.getLastName(), createdMedicalrecord.getLastName());
		verify(jsonFileReader, times(1)).loadData();
	}

	@Test
	public void testUpdateMedicalRecords() throws IOException {
		List<Medicalrecord> medicalrecords = Arrays.asList(
				Medicalrecord.builder().firstName("John").lastName("Boyd").birthdate("03/06/1984")
						.medications(Arrays.asList("aznol:350mg", "hydrapermazol:100mg"))
						.allergies(Arrays.asList("nillacilan")).build(),
				Medicalrecord.builder().firstName("Jonanathan").lastName("Marrack").birthdate("01/03/1989")
						.medications(Arrays.asList()).allergies(Arrays.asList()).build());
		Medicalrecord updateMedicalrecordData = Medicalrecord.builder().firstName("John").lastName("Boyd")
				.birthdate("03/06/1996").medications(Arrays.asList("aznol:350mg", "hydrapermazol:100mg"))
				.allergies(Arrays.asList("nillacilan")).build();

		DatalistModel mockData = new DatalistModel();
		mockData.setMedicalrecords(medicalrecords);

		when(jsonFileReader.loadData()).thenReturn(mockData);

		medicalRecordsService.updateMedicalRecord("John", "Boyd", updateMedicalrecordData);

		List<Medicalrecord> updatedMedicalrecords = mockData.getMedicalrecords();
		assertEquals(2, updatedMedicalrecords.size());

		Medicalrecord updatedMedicalrecord = updatedMedicalrecords.stream()
				.filter(p -> p.getFirstName().equals("John") && p.getLastName().equals("Boyd")).findFirst()
				.orElse(null);

		assertNotNull(updatedMedicalrecord);
		assertEquals("03/06/1996", updatedMedicalrecord.getBirthdate());
		verify(jsonFileReader, times(1)).loadData();
	}

	@Test
	public void testDeleteMedicalRecords() throws IOException {
		List<Medicalrecord> medicalrecords = new ArrayList<>(Arrays.asList(
				Medicalrecord.builder().firstName("John").lastName("Boyd").birthdate("03/06/1984")
						.medications(Arrays.asList("aznol:350mg", "hydrapermazol:100mg"))
						.allergies(Arrays.asList("nillacilan")).build(),
				Medicalrecord.builder().firstName("Jonanathan").lastName("Marrack").birthdate("01/03/1989")
						.medications(Arrays.asList()).allergies(Arrays.asList()).build()));
		DatalistModel mockData = new DatalistModel();
		mockData.setMedicalrecords(medicalrecords);

		when(jsonFileReader.loadData()).thenReturn(mockData);

		List<Medicalrecord> result = medicalRecordsService.deleteMedicalrecord("John", "Boyd");

		assertNotNull(result);
		assertEquals(1, result.size());
		verify(jsonFileReader, times(1)).loadData();
	}
}
