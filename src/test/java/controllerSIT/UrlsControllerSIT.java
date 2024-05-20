package controllerSIT;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.SafetyAlerts.api.ApiApplication;

@SpringBootTest(classes = ApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UrlsControllerSIT {

	@LocalServerPort
	private int port;

	@Autowired
	private MockMvc mockMvc;
	
	// Integration tests implementation 

	@Test
	public void testGetPersonByStationNumber() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/firestation/2")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.Persons").isArray())
				.andExpect(jsonPath("$.Persons[0].FirstName", Matchers.is("Jonanathan")))
				.andExpect(jsonPath("$.Adults").isNumber()).andExpect(jsonPath("$.Children").isNumber());
	}

	@Test
	public void testGetChildrenByAddress() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/childAlert/1509 Culver St"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].FirstName").value("Tenley"))
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].Age").value("12"))
				.andExpect(MockMvcResultMatchers.jsonPath("$[1].LastName").value("Boyd"))
				.andExpect(MockMvcResultMatchers.jsonPath("$[1].Age").value("6"));
	}

	@Test
	public void testGetPhoneNumbersByStation() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/phoneAlert/1")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$['Phone numbers']").isArray())
				.andExpect(MockMvcResultMatchers.jsonPath("$['Phone numbers'][0]").value("841-874-6512"))
				.andExpect(MockMvcResultMatchers.jsonPath("$['Phone numbers'][3]").value("841-874-7784"));
	}

	@Test
	public void testGetPersonsWithAgeByAddress() throws Exception {
		String address = "947 E. Rose Dr";
		mockMvc.perform(MockMvcRequestBuilders.get("/fire/" + address)).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].FirstName").value("Brian"))
				.andExpect(MockMvcResultMatchers.jsonPath("$[1].LastName").value("Stelzer"))
				.andExpect(MockMvcResultMatchers.jsonPath("$[1].Age").value(43));
	}

	@Test
	public void testGetPersonsInAddress() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/flood/stations/3"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.jsonPath("$[0]['Person infos']")
						.value("John Boyd - Medications: aznol:350mg, hydrapermazol:100mg, Allergies: nillacilan"))
				.andExpect(MockMvcResultMatchers.jsonPath("$[1].Phone").value("841-874-6513"))
				.andExpect(MockMvcResultMatchers.jsonPath("$[1].Age").value(34));
	}

	@Test
	public void testGetFamilyInfo() throws Exception {
		String firstName = "Sophia";
		String lastName = "Zemicks";
		mockMvc.perform(MockMvcRequestBuilders.get("/personInfo/{firstName}/{lastName}", firstName, lastName))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.jsonPath("$[1].firstName").value("Warren"))
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].address").value("892 Downing Ct"))
				.andExpect(MockMvcResultMatchers.jsonPath("$[1].age").value(38))
				.andExpect(MockMvcResultMatchers.jsonPath("$[1].email").value("ward@email.com"));
	}

	@Test
	public void testGetEmailByCity() throws Exception {
		String city = "Culver";
		mockMvc.perform(MockMvcRequestBuilders.get("/communityEmail/" + city))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$['Emails']").isArray())
				.andExpect(MockMvcResultMatchers.jsonPath("$['Emails'][0]").value("jaboyd@email.com"))
				.andExpect(MockMvcResultMatchers.jsonPath("$['Emails'][7]").value("zarc@email.com"));
	}
}
