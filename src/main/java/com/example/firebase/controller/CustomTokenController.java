package com.example.firebase.controller;

//import org.springframework.web.bind.annotation.*;
import java.util.Date;

import com.highmarkhealth.diginn.pojo.NewToken;

//import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.util.Assert;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import org.springframework.cloud.gcp.pubsub.PubSubAdmin;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.FirebaseApp;

import com.google.cloud.pubsub.v1.Subscriber;
import java.util.ArrayList;
import com.google.firebase.auth.FirebaseAuthException; 

import java.util.concurrent.ExecutionException;
import java.security.GeneralSecurityException;
import java.io.IOException;

//import com.highmarkhealth.diginn.controller.CustomTokenAuthVerifier;


/**
 * 
 */

@RestController
public class CustomTokenController {

        private static final Log logger = LogFactory.getLog(CustomTokenController.class);
        
       
        /**
         * 
         */
        @ResponseBody
        @ApiOperation(
        value = "Creates new custom token.",
        notes = "New new custom token using Firebase SDK",
        produces = MediaType.APPLICATION_JSON_VALUE)
        @ApiResponses(value = {@ApiResponse(code = 200, message = "New token is created.") })
        
       
        @RequestMapping(value = "/v1/token/new", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<String> createNewToken(@RequestBody NewToken token) {
                
                String customToken="";
                try {
                logger.info("createNewToken() - start.");
                
                if (null != token) {
                        Assert.notNull(token.getToken(),"Token name is required value.");
                }                 

                // [START custom_token]
                String uid = token.getToken();

                //FirebaseApp.initializeApp();

                 customToken = FirebaseAuth.getInstance().createCustomTokenAsync(uid).get();
                 logger.info("Token for uid "+uid+" and token is "+ customToken);
                // Send token back to client
                // [END custom_token]
                } catch (InterruptedException ie) {
                        logger.error("createNewToken()",ie); 
                }
                catch (ExecutionException ee) {
                        logger.error("createNewToken()",ee); 
                }

                logger.info("createNewToken() - end.");
                return  ResponseEntity.ok(customToken);
        }


        @RequestMapping(value = "/v1/token/verify", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<String> verifyToken(@RequestBody NewToken token) {
                
                String uid="";
                try {
                logger.info("verifyToken() - start.");
                
                if (null != token) {
                        Assert.notNull(token.getToken(),"Token name is required value.");
                }                 

               

                CustomTokenAuthVerifier authVerifier = new CustomTokenAuthVerifier();

                authVerifier.verifyToken(token.getToken());

                logger.info("Token for uid "+uid+" and token is "+token.getToken());

                // Send token back to client
                // [END custom_token]
                } catch (GeneralSecurityException gse) {
                        logger.error("verifyToken()",gse);
                }
                 catch (IOException ioe) {
                        logger.error("verifyToken()",ioe);
                }
                
                logger.info("verifyToken() - end.");
                return  ResponseEntity.ok(uid);
        }



       
}

