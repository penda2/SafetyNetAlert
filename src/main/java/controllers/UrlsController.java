package controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import services.UrlsService;

@RestController
public class UrlsController {

	@Autowired
	private UrlsService UrlsService;
	
	// Controller implementation for other end points of get requests

	// get persons by station number (info + age)
	@GetMapping("firestation/{station}")
	public ResponseEntity<Map<String, Object>> getPersonByStationNumber(@PathVariable String station) {
		Map<String, Object> persons = UrlsService.getPersonByStationNumber(station);
		return ResponseEntity.ok(persons);
	}

	// Get children by address
	@GetMapping("childAlert/{address}")
	public ResponseEntity<List<Map<String, Object>>> getChildrenByAddress(@PathVariable String address) {
		List<Map<String, Object>> persons = UrlsService.getChildrenByAddress(address);
		return ResponseEntity.ok(persons);
	}

	// get phone numbers by station number
	@GetMapping("/phoneAlert/{station}")
	public ResponseEntity<Map<String, Object>> getPhoneNumbersByStation(@PathVariable String station) {
		Map<String, Object> phoneNumbers = UrlsService.getPhoneNumbersByStation(station);
		return ResponseEntity.ok(phoneNumbers);
	}

	// Get list of persons by address and their station number
	@GetMapping("/fire/{address}")
	public ResponseEntity<List<Map<String, Object>>> getPersonsWithAgeByAddress(@PathVariable String address) {
		List<Map<String, Object>> persons = UrlsService.getPersonsWithAgeByAddress(address);
		return ResponseEntity.ok(persons);
	}

	// Get list of persons grouped by address with their medical records
	@GetMapping("/flood/stations/{station}")
	public ResponseEntity<List<Map<String, Object>>> getPersonsInAddress(@PathVariable String station) {
		List<Map<String, Object>> persons = UrlsService.getPersonsInAddress(station);
		return ResponseEntity.ok(persons);
	}

	// Get persons info grouped by last name
	@GetMapping("/personInfo/{firstName}/{lastName}")
	public ResponseEntity<List<Map<String, Object>>> getFamilyInfo(@PathVariable String firstName,
			@PathVariable String lastName) {
		List<Map<String, Object>> persons = UrlsService.getFamilyInfo(firstName, lastName);
		return ResponseEntity.ok(persons);
	}

	// Get list of email address by city / filtered email
	@GetMapping("/communityEmail/{city}")
	public ResponseEntity<Map<String, Object>> getEmailByCity(@PathVariable String city) {
		Map<String, Object> emails = UrlsService.getEmailsByCity(city);
		return ResponseEntity.ok(emails);
	}
}
