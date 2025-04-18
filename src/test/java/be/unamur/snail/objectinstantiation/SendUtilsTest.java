package be.unamur.snail.objectinstantiation;

import be.unamur.snail.register.AttributeEntityDTO;
import be.unamur.snail.register.ConstructorEntityDTO;
import be.unamur.snail.register.HttpClientService;
import be.unamur.snail.register.SendUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class SendUtilsTest {
    private static final String FAKE_API_URL = "http://test-send-utils-api-fake-url/";

    private String constructorSignature;
    private String constructorClassName;
    private String constructorFileName;
    private String attributeName;
    private String attributeType;
    private String attributeActualType;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        SendUtils.setApiURL(FAKE_API_URL);

        constructorSignature = "constructorSignature";
        constructorClassName = "constructorClassName";
        constructorFileName = "constructorFileName";
        attributeName = "attributeName";
        attributeType = "attributeType";
        attributeActualType = "java.lang.String";

        SendUtils.initConstructorEntityDTO(constructorSignature, constructorClassName, constructorFileName);
    }

    @Test
    void addAttributeWorkingTest() {
        SendUtils.addAttribute(attributeName, attributeType, attributeActualType);
        ConstructorEntityDTO constructorEntityDTO = SendUtils.getConstructorEntityDTO();
        assertNotNull(constructorEntityDTO);
        assertEquals(constructorSignature, constructorEntityDTO.getSignature());
        assertEquals(constructorClassName, constructorEntityDTO.getClassName());
        assertEquals(constructorFileName, constructorEntityDTO.getFileName());

        List<AttributeEntityDTO> attributeEntities = new ArrayList<>(constructorEntityDTO.getAttributeEntities());

        assertNotNull(attributeEntities);
        assertEquals(1, attributeEntities.size());
        assertEquals(attributeName, attributeEntities.get(0).getName());
        assertEquals(attributeType, attributeEntities.get(0).getType());
        assertEquals(attributeActualType, attributeEntities.get(0).getActualType());
    }

    /*@Test
    void sendWorkingTest() {
        try (MockedStatic<HttpClientService> mockedHttpClientServiceMock = mockStatic(HttpClientService.class)) {
            SendUtils.addAttribute(attributeName, attributeType, attributeActualType);
            String jsonPayload = "{\"name\":\"name\",\"signature\":\"signature\",\"className\":\"className\",\"fileName\":\"fileName\",\"attributeEntities\":[{\"name\":\"attributeName\",\"type\":\"attributeType\"}]}";

            mockedHttpClientServiceMock.when(() -> HttpClientService.post(FAKE_API_URL, jsonPayload)).thenReturn("Success");

            SendUtils.send();

            mockedHttpClientServiceMock.verify(() -> HttpClientService.post(eq(FAKE_API_URL), anyString()), times(1));
        }
    }*/

    /*@Test
    void sendThrowsExceptionTest() {
        try(MockedStatic<HttpClientService> mockedHttpClientServiceMock = mockStatic(HttpClientService.class)) {
            mockedHttpClientServiceMock.when(() -> HttpClientService.post(eq(SendUtils.getApiURL()), anyString())).thenThrow(new RuntimeException("HTTP error"));

            // Assert that an exception is thrown when calling send
            RuntimeException thrown = assertThrows(RuntimeException.class, SendUtils::send);

            assertEquals("HTTP error", thrown.getMessage());
        }
    }*/
}
