package models;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Medicalrecord {
	@NotBlank(message = "FirstName must not be empty !")
	private String firstName;

	@NotBlank(message = "LastName must not be empty !")
	private String lastName;

	@NotBlank(message = "Birthdate must not be empty !")
	private String birthdate;

	private List<String> medications;

	private List<String> allergies;
}
