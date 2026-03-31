package com.example.URLShortner;

import com.example.URLShortner.util.Base62Encoder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class UrlShortnerApplication {

	public static void main(String[] args) {
		SpringApplication.run(UrlShortnerApplication.class, args);

		// Temporary test — remove after verifying
//		Base62Encoder encoder = new Base62Encoder();
//		System.out.println(encoder.encode(1));        // b
//		System.out.println(encoder.encode(100));      // bM
//		System.out.println(encoder.encode(100523));   // some short code
	}

}
