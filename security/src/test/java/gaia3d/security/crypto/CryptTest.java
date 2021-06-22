package gaia3d.security.crypto;

import org.junit.jupiter.api.Test;

public class CryptTest {

	/**
	 * 암복호화 테스트
	 */
	@Test
	public void encryptAndDecrypt() {
		System.out.println("url : " + Crypt.encrypt("jdbc:postgresql://localhost:15432/mago3d"));
		System.out.println("user : " + Crypt.encrypt("postgres"));
		System.out.println("password : " + Crypt.encrypt("postgres"));
		
//		System.out.println(Crypt.decrypt("GvEa084OoJKPfNVpNHbfs/KHGXnmV1yqVqZU7yr5tl8KYzgd7JnC4jBQstOh1a5b"));
	}
}
