package models;

import java.util.List;

import lombok.Data;

@Data
public class DatalistModel {
	private List<Person> persons;
	private List<Firestation> firestations;
	private List<Medicalrecord> medicalrecords;
}
