package be.unamur.snail.register;

public class EnvVariables {
    public String getEnvVariable(String variable) {
        return System.getenv(variable);
    }
}
