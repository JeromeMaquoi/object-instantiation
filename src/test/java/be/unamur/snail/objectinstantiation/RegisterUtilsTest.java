package be.unamur.snail.objectinstantiation;

import be.unamur.snail.register.RegisterUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RegisterUtilsTest {
    private RegisterUtils.HttpClientService httpClientService;

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
        httpClientService = mock(RegisterUtils.HttpClientService.class);

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
    void registerSuccessfulPostTest() {
        try (MockedStatic<RegisterUtils.HttpClientService> mockedStatic = mockStatic(RegisterUtils.HttpClientService.class)) {
            mockedStatic.when(() -> RegisterUtils.HttpClientService.post(eq(apiURL), any(Map.class))).thenReturn("Success");
            RegisterUtils.register(null, constructorSignature, constructorName, constructorClassName, constructorFileName, attributeName, attributeType);

            mockedStatic.verify(() -> RegisterUtils.HttpClientService.post(eq(apiURL), eq(payload)), times(1));
        }
    }

}
