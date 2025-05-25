package com.springboot.service;


import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ClearValuesRequest;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleSheetsService {

    private static final String APPLICATION_NAME = "Library";
    private static final String SPREADSHEET_ID = "1ZsOQFkTM-JBYpNkIm-ZAEQezKWIBtm4pE1CHJZmn4r8";
    private static final String RANGE = "Аркуш1";

    public List<List<Object>> readData() throws IOException, GeneralSecurityException {
        Sheets sheetsService = getSheetsService();

        ValueRange response = sheetsService.spreadsheets().values()
                .get(SPREADSHEET_ID, RANGE)
                .execute();

        return response.getValues();
    }

    private Sheets getSheetsService() throws IOException, GeneralSecurityException {
        InputStream credentialsStream = getClass().getClassLoader().getResourceAsStream("credentials.json");
        GoogleCredential credential = GoogleCredential.fromStream(credentialsStream)
                .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));

        return new Sheets.Builder(
                credential.getTransport(),
                credential.getJsonFactory(),
                credential
        ).setApplicationName(APPLICATION_NAME)
                .build();
    }

    public void writeData(List<List<Object>> data) throws IOException, GeneralSecurityException {
        Sheets sheetsService = getSheetsService();


        sheetsService.spreadsheets().values()
                .clear(SPREADSHEET_ID, RANGE, new ClearValuesRequest())
                .execute();

        ValueRange body = new ValueRange().setValues(data);
        sheetsService.spreadsheets().values()
                .update(SPREADSHEET_ID, RANGE, body)
                .setValueInputOption("RAW")
                .execute();
    }


}
