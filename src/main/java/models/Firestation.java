package models;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Firestation {
    @NotBlank(message = "Address must not be empty !")
	private String address;
    @NotBlank(message = "Station must not be empty !")
    private String station;
}
