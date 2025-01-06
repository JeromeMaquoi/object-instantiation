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
    private static final String apiURL = "http://localhost:8080/api/v1/constructor-entities";

    private String constructorSignature;
    private String constructorClassName;
    private String constructorFileName;
    private String attributeName;
    private String attributeType;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        constructorSignature = "constructorSignature";
        constructorClassName = "constructorClassName";
        constructorFileName = "constructorFileName";
        attributeName = "attributeName";
        attributeType = "attributeType";

        SendUtils.initConstructorEntityDTO(constructorSignature, constructorClassName, constructorFileName);
    }

    @Test
    void addAttributeWorkingTest() {
        SendUtils.addAttribute(attributeName, attributeType);
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
    }

    @Test
    void sendWorkingTest() {
        try (MockedStatic<HttpClientService> mockedHttpClientServiceMock = mockStatic(HttpClientService.class)) {
            SendUtils.addAttribute(attributeName, attributeType);
            String jsonPayload = "{\"name\":\"name\",\"signature\":\"signature\",\"className\":\"className\",\"fileName\":\"fileName\",\"attributeEntities\":[{\"name\":\"attributeName\",\"type\":\"attributeType\"}]}";

            mockedHttpClientServiceMock.when(() -> HttpClientService.post(apiURL, jsonPayload)).thenReturn("Success");

            SendUtils.send();

            mockedHttpClientServiceMock.verify(() -> HttpClientService.post(eq(apiURL), anyString()), times(1));
        }
    }

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
