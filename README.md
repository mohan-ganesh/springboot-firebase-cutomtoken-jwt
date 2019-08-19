# springboot-firebase-cutomtoken-jwt
Springboot/Firebase Sample For Create and Verify Custom JWT Tokens

mvn compile <br>
mvn spring-boot:run <br>
mvn clean package appengine:run <br>
mvn clean package appengine:deploy <br>

SpringbootFirebaseTokenApplication - replace your own firebase / GCP private key file for local environmnet.  If we are deployng this app in GCP, just the Firebase.Initilize() should be good enough, as long as we provision the IAM access for service account that app is running on. 

