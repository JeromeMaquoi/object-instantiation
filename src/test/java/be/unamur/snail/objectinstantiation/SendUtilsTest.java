package be.unamur.snail.objectinstantiation;

import be.unamur.snail.register.SendUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SendUtilsTest {
    private SendUtils.HttpClientService httpClientService;

    private static final String apiURL = "http://localhost:8080/api/v1/constructor-entities";

    private Map<String, String> payload;

    private String constructorSignature;
    private String constructorName;
    private String constructorClassName;
    private String constructorFileName;
    private String attributeName;
    private String attributeType;

    @BeforeEach
    void setUp() {
        httpClientService = mock(SendUtils.HttpClientService.class);

        constructorSignature = "constructorSignature";
        constructorName = "constructorName";
        constructorClassName = "constructorClassName";
        constructorFileName = "constructorFileName";
        attributeName = "attributeName";
        attributeType = "attributeType";

        payload = new HashMap<>();
        payload.put("constructorSignature", constructorSignature);
        payload.put("constructorName", constructorName);
        payload.put("constructorClassName", constructorClassName);
        payload.put("constructorFileName", constructorFileName);
        payload.put("attributeName", attributeName);
        payload.put("attributeType", attributeType);
    }

    @Test
    void prepareSuccessfulPostTest() {
        try (MockedStatic<SendUtils.HttpClientService> mockedStatic = mockStatic(SendUtils.HttpClientService.class)) {
            mockedStatic.when(() -> SendUtils.HttpClientService.post(eq(apiURL), any(Map.class))).thenReturn("Success");
            SendUtils.prepare(null, constructorSignature, constructorName, constructorClassName, constructorFileName, attributeName, attributeType);

            mockedStatic.verify(() -> SendUtils.HttpClientService.post(eq(apiURL), eq(payload)), times(1));
        }
    }

}
