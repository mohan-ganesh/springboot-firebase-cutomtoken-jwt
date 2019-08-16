package com.example.firebase.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.security.GeneralSecurityException;
import java.io.IOException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import java.net.URI;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.HttpResponse;



import java.security.PublicKey;
import java.util.Map;
import com.google.gson.JsonElement;
import java.io.InputStream;
import java.io.ByteArrayInputStream;

import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;


import io.jsonwebtoken.Jwts;

import io.jsonwebtoken.Claims;


//@Service
public class CustomTokenAuthVerifier implements CustomTokenVerifier  {


    private static final Log logger = LogFactory.getLog(CustomTokenAuthVerifier.class);
    private static final String pubKeyUrl = "https://www.googleapis.com/robot/v1/metadata/x509/securetoken@system.gserviceaccount.com";

     /**
     *
     * @param token
     * @return
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public boolean verifyToken(String token) throws GeneralSecurityException, IOException {

        final String methodName = "verifyToken() - ";

        logger.info(methodName+"start.");

        token = token.toString().trim();
        // get public keys
        JsonObject publicKeys = getPublicKeysJson();

         // verify count
         int size = publicKeys.entrySet().size();
         int count = 0;

         // get json object as map
        // loop map of keys finding one that verifies
        for (Map.Entry<String, JsonElement> entry: publicKeys.entrySet()) {
            // log
            logger.info("attempting jwt id token validation with: ");

            try {
                // trying next key
                count++;

                // get public key
                PublicKey publicKey = getPublicKey(entry);
                logger.info(methodName+"public key string is:"+publicKey.toString());

                try { 
                    Claims claims = Jwts.parser().setSigningKey(getPublicKeyAsAString(entry).getBytes("UTF-8")).parseClaimsJws(token).getBody();
                    logger.info("claims =" + claims.toString());
                     } catch (Exception e) {
                          logger.error(methodName+"step 1 "+e.getMessage());
                        }
                // validate claim set
                Jwts.parser().setSigningKey(publicKey).parse(token);

                Claims claims =  Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token).getBody();

                logger.info("claims =" + claims.toString());

                logger.info("claims uid=" + claims.getId());
                // success, we can return
                return true;
            } catch(Exception e) {
                // log
                logger.info("Firebase id token verification error: ");
                logger.info(e.getMessage());
                // claims may have been tampered with
                // if this is the last key, return false
                if (count == size) {
                    return false;
                }
            }
        }


        return true;
    
    }


    /**
     *
     * @param entry
     * @return
     * @throws GeneralSecurityException
     */
    private PublicKey getPublicKey(Map.Entry<String, JsonElement> entry) throws GeneralSecurityException, IOException {
    
        String publicKeyPem = entry.getValue().getAsString();

        logger.info("before:="+publicKeyPem);

        publicKeyPem = publicKeyPem.replaceAll("-----BEGIN (.*)-----", "")
                .replaceAll("\n-----END CERTIFICATE-----\n\n\n","")
                .replaceAll("\n-----END (.*)----", "")
                .replaceAll("-----END (.*)----", "")
                .replaceAll("\r\n", "")
                .replaceAll("\n", "")
                .replaceAll("=\n\n","=")
                //.replaceAll(" ", "")
                .trim();

        logger.info("after:="+publicKeyPem);

        // generate x509 cert
        InputStream inputStream = new ByteArrayInputStream(entry.getValue().getAsString().getBytes("UTF-8"));
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate)cf.generateCertificate(inputStream);
        return cert.getPublicKey();
    }


    /**
     *
     * @param entry
     * @return
     * @throws GeneralSecurityException
     */
    private String getPublicKeyAsAString(Map.Entry<String, JsonElement> entry) throws GeneralSecurityException, IOException {
    
        String publicKeyPem = entry.getValue().getAsString();

        logger.info("before:="+publicKeyPem);

        publicKeyPem = publicKeyPem.replaceAll("-----BEGIN (.*)-----", "")
                .replaceAll("\n-----END CERTIFICATE-----\n\n\n","")
                .replaceAll("\n-----END (.*)----", "")
                .replaceAll("-----END (.*)----", "")
                .replaceAll("\r\n", "")
                .replaceAll("\n", "")
                .replaceAll("=\n\n","=")
                //.replaceAll(" ", "")
                .trim();

        logger.info("after:="+publicKeyPem);

        // generate x509 cert
        publicKeyPem = entry.getValue().getAsString();
        
        return publicKeyPem;
    }

    


     /**
     *
     * @return
     * @throws IOException
     */
    private JsonObject getPublicKeysJson() throws IOException {

        logger.info("getPublicKeysJson()-start.");
        // get public keys
        URI uri = URI.create(pubKeyUrl);
        GenericUrl url = new GenericUrl(uri);
        HttpTransport http = new NetHttpTransport();
        HttpResponse response = http.createRequestFactory().buildGetRequest(url).execute();

        // store json from request
        String json = response.parseAsString();

        logger.info("getPublicKeysJson()-parseAsString is done.");

        // disconnect
        response.disconnect();

        // parse json to object
        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();

        logger.info("getPublicKeysJson()-converted to JsonObject.");

        return jsonObject;
    }

}