package com.example.firebase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.FileInputStream;
import java.io.InputStream;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.auth.oauth2.GoogleCredentials;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


@SpringBootApplication
public class SpringbootFirebaseTokenApplication {

	public static void main(String[] args) {

		Log logger = LogFactory.getLog(SpringbootFirebaseTokenApplication.class);

		SpringApplication.run(SpringbootFirebaseTokenApplication.class, args);
		
		try {
				logger.info("Initialize the Firebase App........");
				InputStream serviceAccount =  SpringbootFirebaseTokenApplication.class.getClassLoader().getResourceAsStream("big-query-164316-firebase-adminsdk-6wzkr-ef4d464d61.json");


				FirebaseOptions options = new FirebaseOptions.Builder()
				.setCredentials(GoogleCredentials.fromStream(serviceAccount))
				.setDatabaseUrl("https://big-query-164316.firebaseio.com")
				.build();

				FirebaseApp.initializeApp(options);

				logger.info("Initialize the Firebase App is complete.....");

			} catch (Exception e) {
			e.printStackTrace();
			}

		}

}
