package models;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Person {
	
    @NotBlank(message = "First name must not be empty !")
    private String firstName;
    
    @NotBlank(message = "Last name must not be empty !")
    private String lastName;
    
    @NotBlank(message = "Address must not be empty !")
    private String address;
    
    @NotBlank(message = "City must not be empty !")
    private String city;
    
    @NotBlank(message = "Zip must not be empty !")
    private String zip;
    
    @NotBlank(message = "Phone number must not be empty !")
    private String phone;
    
    @NotBlank(message = "Email must not be empty !")
    private String email;
}
