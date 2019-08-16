package com.example.firebase.servlet;


import java.io.IOException;

import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletInputStream;

import com.google.api.client.json.JsonParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.pubsub.model.PubsubMessage;

// data store
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;


@WebServlet(
    name = "ReceiveMessageServlet",
    description = "Example Servlet Using Annotations",
    urlPatterns = {"/v1/process/incoming/message"}
  )
  public class ReceiveMessageServlet extends HttpServlet {  
    
    private static final Log logger = LogFactory.getLog(ReceiveMessageServlet.class);

      @Override
      protected void doPost(HttpServletRequest request, 
        HttpServletResponse response) throws ServletException, IOException {
    
        final String methodName= "ReceiveMessageServlet:doPost() - ";
        logger.info(methodName+"start.");

        ServletInputStream inputStream = request.getInputStream();  
        
        // Parse the JSON message to the POJO model class
        JsonParser parser = JacksonFactory.getDefaultInstance().createJsonParser(inputStream);
        parser.skipToKey("message");
        PubsubMessage message = parser.parseAndClose(PubsubMessage.class);
        logger.info(methodName+"messageId={}"+message.getMessageId());
        
        try {
        // Store the message in the datastore
        Entity messageToStore = new Entity("PubsubMessage");
        messageToStore.setProperty("messageId", message.getMessageId());
        messageToStore.setProperty("publishTime", message.getPublishTime());
        messageToStore.setProperty("message", new String(message.decodeData(), "UTF-8"));
        messageToStore.setProperty("receipt-time", System.currentTimeMillis());
        DatastoreService datastore =  DatastoreServiceFactory.getDatastoreService();
        datastore.put(messageToStore);
        } catch (Exception exp ) {
            logger.error(methodName,exp);
        }

        // Acknowledge the message by returning a success code
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().close();
      }
  }
