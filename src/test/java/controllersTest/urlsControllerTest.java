package controllersTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import controllers.UrlsController;
import services.UrlsService;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = urlsControllerTest.class)
public class urlsControllerTest {
	
	@InjectMocks
	private UrlsController UrlsController;
	
	@Mock
	private UrlsService urlsService;
	
	// Unit tests implementation for UrlsController methods
	
	@Test
	public void testGetPersonByStationNumber() {
        ResponseEntity<Map<String, Object>> response = UrlsController.getPersonByStationNumber("3");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
	
	@Test
	public void testGetChildrenByAddress() {
		ResponseEntity<List<Map<String, Object>>> response = UrlsController.getChildrenByAddress("1509 Culver St");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
	}
	
	@Test
	public void testGetPhoneNumbersByStation() throws Exception {
		ResponseEntity<Map<String, Object>> response = UrlsController.getPhoneNumbersByStation("2");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
	}
	
	@Test
	public void testGetPersonsWithAgeByAddress() throws Exception {
		ResponseEntity<List<Map<String, Object>>> response = UrlsController.getPersonsWithAgeByAddress("834 Binoc Ave");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
	}
	
	@Test
	public void testGetPersonsInAddress() throws Exception {
		ResponseEntity<List<Map<String, Object>>> response = UrlsController.getPersonsInAddress("908 73rd St");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
	}

	@Test
	public void testGetFamilyInfo() throws Exception {
		ResponseEntity<List<Map<String, Object>>> response = UrlsController.getFamilyInfo("Felicia", "Boyd");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
	}
	
	@Test
	public void testGetEmailByCity() {
		ResponseEntity<Map<String, Object>> response = UrlsController.getEmailByCity("Culver");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
	}
}
