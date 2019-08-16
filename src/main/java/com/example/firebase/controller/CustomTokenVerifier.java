package com.example.firebase.controller;

import java.security.GeneralSecurityException;
import java.io.IOException;

public interface CustomTokenVerifier {

    public boolean verifyToken(String token) throws GeneralSecurityException, IOException;
}