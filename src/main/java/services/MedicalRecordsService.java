package services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import config.JsonFileReader;
import exceptions.InvalidRequestException;
import models.DatalistModel;
import models.Medicalrecord;

@Service
public class MedicalRecordsService {
	Logger logger = LogManager.getLogger(JsonFileReader.class);

	private JsonFileReader jsonFileReader;

	public MedicalRecordsService(JsonFileReader jsonFileReader) {
		this.jsonFileReader = jsonFileReader;
	}

	private DatalistModel data;
	private boolean dataLoaded = false;

	// Initialize data
	private void loadDataFromFile() {
		if (!dataLoaded) {
			try {
				data = jsonFileReader.loadData();
				dataLoaded = true;
			} catch (IOException e) {
				logger.error("Error loading data from JSON");
				throw new RuntimeException("Error loading data from JSON", e);
			}
		}
	}

	// get list of medical records
	public List<Medicalrecord> getMedicalrecords() throws IOException {
		loadDataFromFile();
		logger.info(" List of medical records : " + data.getMedicalrecords());
		return data.getMedicalrecords();
	}

	// Create medical record
	public Medicalrecord addMedicalrecord(Medicalrecord newMedicalrecord) {
		loadDataFromFile();
		List<Medicalrecord> existingMedicalrecords = data.getMedicalrecords();

		if (existingMedicalrecords == null) {
			existingMedicalrecords = new ArrayList<>();
			data.setMedicalrecords(existingMedicalrecords);
		}

		boolean personExists = existingMedicalrecords.stream()
				.anyMatch(person -> person.getFirstName().equals(newMedicalrecord.getFirstName()));

		if (!personExists) {
			existingMedicalrecords.add(newMedicalrecord);
		} else {
			logger.error("Person to add already exists!!" + newMedicalrecord);
			throw new InvalidRequestException("Person to add already exists!!");
		}
		logger.info("Created person : " + newMedicalrecord);
		return newMedicalrecord;
	}

	// Update medical record
	public void updateMedicalRecord(String firstName, String lastName, Medicalrecord updatedMedicalrecord) {
		loadDataFromFile();
		List<Medicalrecord> medicalrecords = data.getMedicalrecords();
		for (Medicalrecord medicalrecord : medicalrecords) {
			logger.info("Updated medical record : " + updatedMedicalrecord);

			if (medicalrecord.getFirstName().equals(firstName) && medicalrecord.getLastName().equals(lastName)) {
				medicalrecord.setBirthdate(updatedMedicalrecord.getBirthdate());
				medicalrecord.setMedications(updatedMedicalrecord.getMedications());
				medicalrecord.setAllergies(updatedMedicalrecord.getAllergies());
				break;
			}
		}
	}

	// Delete medical record
	public List<Medicalrecord> deleteMedicalrecord(String firstName, String lastName) {
		loadDataFromFile();
		List<Medicalrecord> medicalrecords = data.getMedicalrecords();

		medicalrecords.removeIf(medicalrecord -> medicalrecord.getFirstName().equals(firstName)
				&& medicalrecord.getLastName().equals(lastName));

		if (medicalrecords.isEmpty()) {
			logger.info("No medical record found for deletion!");
		} else {
			logger.info("Medical record deleted successfully!");
		}
		return medicalrecords;
	}
}
