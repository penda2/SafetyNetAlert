package services;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import config.JsonFileReader;
import models.DatalistModel;
import models.Firestation;
import models.Medicalrecord;
import models.Person;

@Service
public class UrlsService {

	Logger logger = LogManager.getLogger(JsonFileReader.class);

	private JsonFileReader jsonFileReader;

	public UrlsService(JsonFileReader jsonFileReader) {
		this.jsonFileReader = jsonFileReader;
	}

	private DatalistModel data;

	private void loadDataFromFile() {
		try {
			data = jsonFileReader.loadData();
		} catch (IOException e) {
			throw new RuntimeException("Error loading data from JSON", e);
		}
	}

	// service implementing the get request controller for other endpoints

	// get filtered persons by Fire Station
	public Map<String, Object> getPersonByStationNumber(String station) {
		if (data == null) {
			loadDataFromFile();
		}
		List<Person> filteredPersons = getFilteredPersonsByFireStation(station);
		Map<String, Object> result = new HashMap<>();
		List<Map<String, Object>> personsList = new ArrayList<>();

		int totalAdults = 0;
		int totalChildren = 0;

		for (Person person : filteredPersons) {

			Map<String, Object> personInfo = new LinkedHashMap<>();
			personInfo.put("FirstName", person.getFirstName());
			personInfo.put("LastName", person.getLastName());
			personInfo.put("Address", person.getAddress());
			personInfo.put("Phone", person.getPhone());

			int childrenCount = countChildren(person);
			int adultsCount = countAdults(person);

			totalChildren += childrenCount;
			totalAdults += adultsCount;
			personsList.add(personInfo);
		}

		result.put("Persons", personsList);
		result.put("Adults", totalAdults);
		result.put("Children", totalChildren);

		logger.info("Persons with adults and children count by station : " + result);
		return result;
	}

	// Get children by address
	public List<Map<String, Object>> getChildrenByAddress(String address) {
		if (data == null) {
			loadDataFromFile();
		}
		List<Map<String, Object>> childrenList = new ArrayList<>();
		List<Person> personsAtAddress = data.getPersons().stream()
				.filter(person -> person.getAddress().equalsIgnoreCase(address)).collect(Collectors.toList());

		List<Medicalrecord> medicalRecordsAtAddress = data.getMedicalrecords().stream()
				.filter(medicalRecord -> personsAtAddress.stream()
						.anyMatch(person -> medicalRecord.getFirstName().equalsIgnoreCase(person.getFirstName())
								&& medicalRecord.getLastName().equalsIgnoreCase(person.getLastName())))
				.collect(Collectors.toList());

		LocalDate currentDate = LocalDate.now();
		for (Medicalrecord medicalRecord : medicalRecordsAtAddress) {
			LocalDate birthdate = LocalDate.parse(medicalRecord.getBirthdate(),
					DateTimeFormatter.ofPattern("dd/MM/yyyy"));
			int age = Period.between(birthdate, currentDate).getYears();

			if (age < 18) {
				Map<String, Object> childInfo = new LinkedHashMap<>();
				childInfo.put("FirstName", medicalRecord.getFirstName());
				childInfo.put("LastName", medicalRecord.getLastName());
				childInfo.put("Age", age);
				childrenList.add(childInfo);
			}
		}
		if (childrenList.isEmpty()) {
			return new ArrayList<>();
		} else {
			logger.info("List of children by address : " + childrenList);
			return childrenList;
		}
	}

	// get phone numbers by station number
	public Map<String, Object> getPhoneNumbersByStation(String station) {
		if (data == null) {
			loadDataFromFile();
		}
		List<Person> filteredPersons = getFilteredPersonsByFireStation(station);
		Map<String, Object> result = new HashMap<>();
		List<String> uniquePhoneNumbers = new ArrayList<>();
		filteredPersons.forEach(person -> {
			String phoneNumber = person.getPhone();
			if (!uniquePhoneNumbers.contains(phoneNumber)) {
				uniquePhoneNumbers.add(phoneNumber);
			}
		});
		result.put("Phone numbers", uniquePhoneNumbers);
		logger.info("List of phone numbers by station : " + result);
		return result;
	}

	// get person info by address
	public List<Map<String, Object>> getPersonsWithAgeByAddress(String address) {
		if (data == null) {
			loadDataFromFile();
		}
		List<Person> personsAtAddress = data.getPersons().stream()
				.filter(person -> person.getAddress().equalsIgnoreCase(address)).collect(Collectors.toList());

		List<Medicalrecord> medicalRecordsAtAddress = data.getMedicalrecords().stream()
				.filter(medicalRecord -> personsAtAddress.stream()
						.anyMatch(person -> medicalRecord.getFirstName().equalsIgnoreCase(person.getFirstName())
								&& medicalRecord.getLastName().equalsIgnoreCase(person.getLastName())))
				.collect(Collectors.toList());

		List<Map<String, Object>> personsByAddress = new ArrayList<>();
		LocalDate currentDate = LocalDate.now();

		for (Person person : personsAtAddress) {
			String station = getFireStationByAddress(address);

			Medicalrecord medicalRecord = medicalRecordsAtAddress.stream()
					.filter(mr -> mr.getFirstName().equalsIgnoreCase(person.getFirstName())
							&& mr.getLastName().equalsIgnoreCase(person.getLastName()))
					.findFirst().orElse(null);

			if (medicalRecord != null) {
				LocalDate birthdate = LocalDate.parse(medicalRecord.getBirthdate(),
						DateTimeFormatter.ofPattern("dd/MM/yyyy"));
				int age = Period.between(birthdate, currentDate).getYears();

				Map<String, Object> personInfo = new LinkedHashMap<>();
				personInfo.put("Station", station);
				personInfo.put("FirstName", person.getFirstName());
				personInfo.put("LastName", person.getLastName());
				personInfo.put("Phone", person.getPhone());
				personInfo.put("Age", age);
				personInfo.put("Medications", medicalRecord.getMedications());
				personInfo.put("Allergies", medicalRecord.getAllergies());

				personsByAddress.add(personInfo);
			}
		}
		logger.info("List of persons by address with medications : " + personsByAddress);
		return personsByAddress;
	}

	// Get list of persons grouped by address with their medical records
	public List<Map<String, Object>> getPersonsInAddress(String station) {
		if (data == null) {
			loadDataFromFile();
		}
		List<Person> personsAtStation = getFilteredPersonsByFireStation(station);

		List<Map<String, Object>> personsByStation = new ArrayList<>();
		LocalDate currentDate = LocalDate.now();

		for (Person person : personsAtStation) {
			List<Medicalrecord> matchingMedicalRecords = getMatchingMedicalRecords(person);

			for (Medicalrecord medicalRecord : matchingMedicalRecords) {
				LocalDate birthdate = LocalDate.parse(medicalRecord.getBirthdate(),
						DateTimeFormatter.ofPattern("dd/MM/yyyy"));
				int age = Period.between(birthdate, currentDate).getYears();

				StringBuilder personInfoBuilder = new StringBuilder();
				personInfoBuilder.append(person.getFirstName()).append(" ").append(person.getLastName());
				personInfoBuilder.append(" - Medications: ").append(String.join(", ", medicalRecord.getMedications()));
				personInfoBuilder.append(", Allergies: ").append(String.join(", ", medicalRecord.getAllergies()));
				String personInfo = personInfoBuilder.toString();

				Map<String, Object> personMap = new LinkedHashMap<>();
				personMap.put("Person infos", personInfo);
				personMap.put("Phone", person.getPhone());
				personMap.put("Age", age);

				personsByStation.add(personMap);
			}
		}
		logger.info("Listed persons in household by station : " + personsByStation);
		return personsByStation;
	}

	// Get persons info grouped by last name
	public List<Map<String, Object>> getFamilyInfo(String firstName, String lastName) {
		if (data == null) {
			loadDataFromFile();
		}
		List<Person> personslist = data.getPersons();
		List<Map<String, Object>> personsByName = new ArrayList<>();
		LocalDate currentDate = LocalDate.now();

		for (Person person : personslist) {
			if (person.getLastName().equalsIgnoreCase(lastName)) {
				List<Medicalrecord> matchingMedicalRecords = getMatchingMedicalRecords(person);

				for (Medicalrecord medicalRecord : matchingMedicalRecords) {
					LocalDate birthdate = LocalDate.parse(medicalRecord.getBirthdate(),
							DateTimeFormatter.ofPattern("dd/MM/yyyy"));
					int age = Period.between(birthdate, currentDate).getYears();

					Map<String, Object> personMap = new LinkedHashMap<>();
					personMap.put("firstName", person.getFirstName());
					personMap.put("lastName", person.getLastName());
					personMap.put("address", person.getAddress());
					personMap.put("age", age);
					personMap.put("email", person.getEmail());
					personMap.put("medications", medicalRecord.getMedications());
					personMap.put("allergies", medicalRecord.getAllergies());
					personsByName.add(personMap);
				}
			}
		}
		logger.info("List of persons sorted by family name : " + personsByName);
		return personsByName;
	}

	// Get list of email address by city / filtered email
	public Map<String, Object> getEmailsByCity(String city) {
		if (data == null) {
			loadDataFromFile();
		}
		List<Person> persons = data.getPersons();
		Map<String, Object> result = new HashMap<>();
		List<String> uniqueEmails = new ArrayList<>();
		persons.forEach(person -> {
			String email = person.getEmail();
			if (!uniqueEmails.contains(email)) {
				uniqueEmails.add(email);
			}
		});
		result.put("Emails", uniqueEmails);
		logger.info("List of email address in city : " + result);
		return result;
	}

	// filter persons by fire station number
	private List<Person> getFilteredPersonsByFireStation(String station) {
		if (data == null) {
			loadDataFromFile();
		}
		List<Person> filteredPersons = data.getPersons().stream()
				.filter(person -> isPersonUnderFireStation(person, station)).collect(Collectors.toList());
		return filteredPersons;
	}

	// Filter person address with fire station address
	private boolean isPersonUnderFireStation(Person person, String station) {
		return data.getFirestations().stream()
				.anyMatch(firestation -> firestation.getAddress().equalsIgnoreCase(person.getAddress())
						&& firestation.getStation().equalsIgnoreCase(station));
	}

	// Get person matching MedicalRecords
	private List<Medicalrecord> getMatchingMedicalRecords(Person person) {
		return data.getMedicalrecords().stream()
				.filter(medicalRecord -> medicalRecord.getFirstName().equalsIgnoreCase(person.getFirstName())
						&& medicalRecord.getLastName().equalsIgnoreCase(person.getLastName()))
				.collect(Collectors.toList());
	}

	// get fire station associated with address
	private String getFireStationByAddress(String address) {
		return data.getFirestations().stream().filter(firestation -> firestation.getAddress().equalsIgnoreCase(address))
				.map(Firestation::getStation).findFirst().orElse("");
	}

	// Count persons by age
	private int countPersonsByAge(List<Medicalrecord> medicalRecords, Predicate<Integer> ageCondition) {
		LocalDate currentDate = LocalDate.now();
		int count = 0;

		for (Medicalrecord medicalRecord : medicalRecords) {
			LocalDate birthdate = LocalDate.parse(medicalRecord.getBirthdate(),
					DateTimeFormatter.ofPattern("dd/MM/yyyy"));
			int age = Period.between(birthdate, currentDate).getYears();
			if (ageCondition.test(age)) {
				count++;
			}
		}
		return count;
	}

	// implements Children count
	private int countChildren(Person person) {
		List<Medicalrecord> matchingMedicalRecords = getMatchingMedicalRecords(person);
		Predicate<Integer> isChild = age -> age < 18;
		return countPersonsByAge(matchingMedicalRecords, isChild);
	}

	// implements adults count
	private int countAdults(Person person) {
		List<Medicalrecord> matchingMedicalRecords = getMatchingMedicalRecords(person);
		Predicate<Integer> isAdult = age -> age >= 18;
		return countPersonsByAge(matchingMedicalRecords, isAdult);
	}
}
