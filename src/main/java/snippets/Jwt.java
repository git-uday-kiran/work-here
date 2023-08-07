package snippets;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class Jwt {

	public void jwt() {
		byte[] secret = Base64.encodeBase64("this is the example key for the testing jwt tokens".getBytes());
		Key key = Keys.hmacShaKeyFor(secret);

		System.out.println("key: " + new String(key.getEncoded()));

		String id = UUID.randomUUID().toString();
		String issuer = "uday kiran";
		Date issuedAt = Date.from(Instant.now());

		JwtBuilder jwt = Jwts.builder();
		jwt.setId(id);
		jwt.setIssuer(issuer);
		jwt.setIssuedAt(issuedAt);
		jwt.signWith(key);

		String token = jwt.compact();
		System.out.println(token);

		JwtParserBuilder builder = Jwts.parserBuilder();
		builder.requireIssuer("uday kiran");
		builder.setSigningKey(key);

		JwtParser parser = builder.build();

		Jws<Claims> j = parser.parseClaimsJws(token);

		System.out.println("------------------------");
		System.out.println(j);
	}

}
