package com.mycompany.laserfiche_file_system;

import com.google.gson.JsonArray;
import com.laserfiche.api.client.model.AccessKey;
import com.laserfiche.repository.api.RepositoryApiClient;
import com.laserfiche.repository.api.RepositoryApiClientImpl;
import com.laserfiche.repository.api.clients.impl.model.Entry;
import com.laserfiche.repository.api.clients.impl.model.EntryType;
import com.laserfiche.repository.api.clients.impl.model.Folder;
import com.laserfiche.repository.api.clients.impl.model.ODataValueContextOfIListOfEntry;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.Reader;

public class file_processor {
    public static void main(String[] args) throws IOException, ParseException{
        String jsonPath = ""; 
        JSONParser parser = new JSONParser();
        List<Entry> entries = new ArrayList<Entry>();

        JSONParser par = new JSONParser();
		Reader reader = new FileReader(jsonPath);

        Object jsonObj = par.parse(reader);
        JSONObject jsonObject = (JSONObject) jsonObj;

        JsonArray entryList = (JsonArray)jsonObject.get("input_entries");
        



      
        

        

        




    }

}
