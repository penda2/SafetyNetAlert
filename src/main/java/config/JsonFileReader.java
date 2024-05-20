package config;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.io.OutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import com.jsoniter.output.JsonStream;

import models.DatalistModel;
import models.Firestation;
import models.Medicalrecord;
import models.Person;

@Component
public class JsonFileReader {
	
	// implementation of reading the data.json file

	Logger logger = LogManager.getLogger(JsonFileReader.class);

	String filePath = "/data.json";

	public DatalistModel loadData() throws IOException {
		DatalistModel data = new DatalistModel();

		try (InputStream inputStream = getClass().getResourceAsStream(filePath)) {
			if (inputStream == null) {
				throw new IOException("File not found: " + filePath);
			}

			byte[] bytes = inputStream.readAllBytes();

			JsonIterator jsonIterator = JsonIterator.parse(bytes);
			Any json = jsonIterator.readAny();

			// Load persons, fire stations & medical records
			Any persons = json.get("persons");
			data.setPersons(buildPersonsFromJson(persons));

			Any firestations = json.get("firestations");
			data.setFirestations(buildFireStationsFromJson(firestations));

			Any medicalRecords = json.get("medicalrecords");
			data.setMedicalrecords(buildMedicalRecordsFromJson(medicalRecords));

		} catch (IOException e) {
			logger.error("Error reading JSON file", e);
			throw new IOException("Error reading JSON file: " + e.getMessage(), e);
		}
		return data;
	}

	private List<Person> buildPersonsFromJson(Any persons) {
		List<Person> personList = new ArrayList<>();

		for (Any personData : persons.asList()) {
			Person person = Person.builder().firstName(personData.get("firstName").toString())
					.lastName(personData.get("lastName").toString()).phone(personData.get("phone").toString())
					.zip(personData.get("zip").toString()).address(personData.get("address").toString())
					.city(personData.get("city").toString()).email(personData.get("email").toString()).build();

			personList.add(person);
		}
		return personList;
	}

	private List<Firestation> buildFireStationsFromJson(Any firestations) {
		List<Firestation> fireStationList = new ArrayList<>();

		for (Any fireStationData : firestations.asList()) {
			Firestation firestation = Firestation.builder().address(fireStationData.get("address").toString())
					.station(fireStationData.get("station").toString()).build();
			fireStationList.add(firestation);
		}
		return fireStationList;
	}

	private List<Medicalrecord> buildMedicalRecordsFromJson(Any medicalRecords) {
		List<Medicalrecord> medicalRecordList = new ArrayList<>();

		for (Any medicalRecordData : medicalRecords.asList()) {
			Medicalrecord medicalRecord = Medicalrecord.builder()
					.firstName(medicalRecordData.get("firstName").toString())
					.lastName(medicalRecordData.get("lastName").toString())
					.birthdate(medicalRecordData.get("birthdate").toString())
					.medications(buildStringListFromAny(medicalRecordData.get("medications")))
					.allergies(buildStringListFromAny(medicalRecordData.get("allergies"))).build();
			medicalRecordList.add(medicalRecord);
		}
		return medicalRecordList;
	}

	// build String List for medications & allergies in medicalRecord 
	private List<String> buildStringListFromAny(Any any) {
		List<String> stringList = new ArrayList<>();
		for (Any item : any.asList()) {
			stringList.add(item.toString());
		}
		return stringList;
	}

	// saving data to file 
	public void saveData(OutputStream data) throws IOException {
		try (OutputStream outputStream = new FileOutputStream(filePath);
				OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
			JsonStream.serialize(writer, data);
			logger.info("Data saved successfully to " + filePath);
		} catch (IOException e) {
			logger.error("Error saving data to " + filePath + ": " + e.getMessage());
			throw e;
		}
	}
}
