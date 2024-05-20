package services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import config.JsonFileReader;
import exceptions.InvalidRequestException;
import exceptions.notFoundFireStation;
import models.DatalistModel;
import models.Firestation;

@Service
public class FireStationService {

	Logger logger = LogManager.getLogger(JsonFileReader.class);

	private JsonFileReader jsonFileReader;

	public FireStationService(JsonFileReader jsonFileReader) {
		this.jsonFileReader = jsonFileReader;
	}

	private DatalistModel data;
	private boolean dataLoaded = false;

	// Initialize data
	public void loadDataFromFile() {
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

	// Get list of fire stations
	public List<Firestation> getAllFirestations() {
		loadDataFromFile(); // Ensure data is loaded
		logger.info("All fire stations : " + data.getFirestations());
		return data.getFirestations();
	}

	// Get fire station by station number
	private List<Firestation> firestations = new ArrayList<>();

	public Firestation findByStation(String station) {
		return data.getFirestations().stream().filter(fs -> fs.getStation().equalsIgnoreCase(station)).findFirst()
				.orElse(null);
	}

	// Create new fire station
	public Firestation createFirestation(Firestation newFirestation) {
		loadDataFromFile();
		if (findByStation(newFirestation.getStation()) != null || findByStation(newFirestation.getAddress()) != null) {
			logger.error("This fire station already exists : " + newFirestation);
			throw new InvalidRequestException(" Check if fire station already exist !! ");
		}
		firestations.add(newFirestation);
		logger.info("New fire station created sucessfully : " + newFirestation);
		return newFirestation;
	}

	// Update fire station
	public Firestation updateFirestation(String station, Firestation updatedFirestation) {
		loadDataFromFile();
		Firestation existingFirestation = findByStation(station);
		if (existingFirestation == null) {
			throw new notFoundFireStation("Firestation number " + station + " not found !");
		}
		existingFirestation.setAddress(updatedFirestation.getAddress());
		logger.info("Updated fire station : " + updatedFirestation);
		return existingFirestation;
	}

	// Delete fire station
	public void deleteFireStation(String station) {
		loadDataFromFile();
		List<Firestation> firestations = data.getFirestations();
		Firestation existingFirestation = findByStation(station);

		if (existingFirestation == null) {
			logger.error("Fire station number " + station + " not found");
			throw new notFoundFireStation("Fire station number " + station + " not found!");
		}
		firestations.remove(existingFirestation);
		logger.info("Fire station number " + station + " deleted successfully !");
	}
}
