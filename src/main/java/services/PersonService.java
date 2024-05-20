package services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import config.JsonFileReader;
import exceptions.InvalidRequestException;
import models.DatalistModel;
import models.Person;

@Service
@Validated
public class PersonService {

	Logger logger = LogManager.getLogger(JsonFileReader.class);

	private JsonFileReader jsonFileReader;

	@Autowired
	public PersonService(JsonFileReader jsonFileReader) {
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

	// Get list of persons
	public List<Person> getAllPersons() throws IOException {
		loadDataFromFile();
		logger.info("List of persons : " + data.getPersons());
		return data.getPersons();
	}

	// Create person
	public Person addPerson(Person newPerson) {
		loadDataFromFile();
		List<Person> existingPersons = data.getPersons();

		if (existingPersons == null) {
			existingPersons = new ArrayList<>();
			data.setPersons(existingPersons);
		}
		boolean personExists = existingPersons.stream()
				.anyMatch(person -> person.getFirstName().equals(newPerson.getFirstName()));

		if (!personExists) {
			existingPersons.add(newPerson);
		} else {
			logger.error("Person to add already exists!!" + newPerson);
			throw new InvalidRequestException("Person to add already exists!!");
		}
		logger.info("Created person : " + newPerson);
		return newPerson;
	}

	// Update person
	public void updatePerson(String firstName, String lastName, Person updatedPersonData) {
		loadDataFromFile();
		List<Person> persons = data.getPersons();
		for (Person person : persons) {
			logger.info("Person updated : " + updatedPersonData);

			if (person.getFirstName().equals(firstName) && person.getLastName().equals(lastName)) {
				person.setAddress(updatedPersonData.getAddress());
				person.setCity(updatedPersonData.getCity());
				person.setZip(updatedPersonData.getZip());
				person.setPhone(updatedPersonData.getPhone());
				person.setEmail(updatedPersonData.getEmail());
				break;
			}
		}
		data.setPersons(persons);
	}

	// Delete person
	public void deletePerson(String firstName, String lastName) throws IOException {
		loadDataFromFile();
		List<Person> persons = new ArrayList<>(data.getPersons());

		for (Iterator<Person> iterator = persons.iterator(); iterator.hasNext();) {
			Person person = iterator.next();
			if (person.getFirstName().equals(firstName) && person.getLastName().equals(lastName)) {
				iterator.remove();
				break;
			}
		}
		data.setPersons(persons);
	}
}
