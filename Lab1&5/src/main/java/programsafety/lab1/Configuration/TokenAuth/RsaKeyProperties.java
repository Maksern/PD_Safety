package programsafety.lab1.Configuration.TokenAuth;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@ConfigurationProperties(prefix = "rsa")
public record RsaKeyProperties(RSAPrivateKey privateKey, RSAPublicKey publicKey) {
}
