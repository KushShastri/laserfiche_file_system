package com.mycompany.laserfiche_file_system;

import com.google.gson.JsonArray;
import com.laserfiche.api.client.model.AccessKey;
import com.laserfiche.repository.api.RepositoryApiClient;
import com.laserfiche.repository.api.RepositoryApiClientImpl;
import com.laserfiche.repository.api.clients.impl.model.Entry;
import com.laserfiche.repository.api.clients.impl.model.EntryType;
import com.laserfiche.repository.api.clients.impl.model.Folder;
import com.laserfiche.repository.api.clients.impl.model.ODataValueContextOfIListOfEntry;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.io.FileReader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class MainProgram {

    public static void main(String args[]) {
        String servicePrincipalKey = "5w-5Sbp5T2eyBsduFo-g";
        String accessKeyBase64 = "ewoJImN1c3RvbWVySWQiOiAiMTQwMTM1OTIzOCIsCgkiY2xpZW50SWQiOiAiMDcwYzllYTYtMzQwZS00ODdmLTlmNzItM2YyNjQ0NWNkZWZmIiwKCSJkb21haW4iOiAibGFzZXJmaWNoZS5jYSIsCgkiandrIjogewoJCSJrdHkiOiAiRUMiLAoJCSJjcnYiOiAiUC0yNTYiLAoJCSJ1c2UiOiAic2lnIiwKCQkia2lkIjogInlpUkFKeGZ0eVpZVXk1TFBFYUhmTF9MRS03RWZjSW5nQ3NCVGtxa09yb28iLAoJCSJ4IjogImJ2ZmpDQU9acUdYeVhLdXNELUpEdFkzRVhwNms5WWtTOFZWYzRicER2OFEiLAoJCSJ5IjogInRmYXBLSDc4Qm45LUp5aVZQeDRrQWVDZFlqSjN6RWxHLVZGeU9lS0dNUUEiLAoJCSJkIjogIlNXei1kTUI1bTktWWtkNFJiLVFyMllYbE9BVlpYV0loV3hxVi1QTkFBWEkiLAoJCSJpYXQiOiAxNjc3Mjk3NzE1Cgl9Cn0=";
        AccessKey accessKey = AccessKey.createFromBase64EncodedAccessKey(accessKeyBase64);

        RepositoryApiClient client = RepositoryApiClientImpl.createFromAccessKey(servicePrincipalKey, accessKey);

        String jsonPath = "C:\\Users\\Shane\\laserfiche_file_system\\Laserfiche_file_system\\src\\main\\java\\com\\mycompany\\laserfiche_file_system\\Test Scenario.json";

        try {
            // Read the JSON file
            FileReader reader = new FileReader(jsonPath);
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(reader);

            // Get the processing elements array
            JSONArray processingElements = (JSONArray) jsonObject.get("processing_elements");
            String type = "";
            JSONArray entries;
            JSONArray param;
            String pValue = "";
            String filePath = "";
            String etype = "";
            String entryid = "";
            String repoid = "";

            List<Entry> entryList = new ArrayList<Entry>();
            FileProcessor fp = new FileProcessor();

            // Iterate over the processing elements and create Java objects for each one
            for (Object processingElementObj : processingElements) {
                JSONObject processingElementJson = (JSONObject) processingElementObj;
                type = (String) processingElementJson.get("type");
                entries = (JSONArray) processingElementJson.get("input_entries");
                param = (JSONArray) processingElementJson.get("parameters");

                if (type.toLowerCase().equals("list")) {
                    for (Object entriesObj : entries) {
                        JSONObject inputEntryJson = (JSONObject) entriesObj;
                        etype = (String) inputEntryJson.get("type");
                        entryid = (String) inputEntryJson.get("entryId");
                        repoid = (String) inputEntryJson.get("repositoryId");
                        filePath = (String) inputEntryJson.get("path");
                    }
                    for (Object paramObj : param) {
                        JSONObject paramJson = (JSONObject) paramObj;
                        pValue = (String) paramJson.get("value");

                    }
                    if (etype.equals("local")) {
                        Entry en = new Entry();
                        en.setFullPath(filePath);
                        entryList.add(en);
                        entryList = fp.listLocal(entryList, Integer.parseInt(pValue));
                    } else {
                        fp.setRepo(repoid);
                        Entry entry = client.getEntriesClient().getEntry(repoid, Integer.parseInt(entryid), null)
                                .join();
                        entryList.add(entry);
                        entryList = fp.listRemote(entryList, Integer.parseInt(pValue));
                    }

                } else if (type.toLowerCase().contains("filter".toLowerCase())) {
                    String val1 = "";
                    String val2 = "";

                    JSONObject paramJson = (JSONObject) param.get(0);
                    val1 = (String) paramJson.get("value");
                    switch (type.toLowerCase()) {
                        case "namefilter":
                            entryList = fp.NameFilter(entryList, val1);
                            break;
                        case "lengthfilter":
                            paramJson = (JSONObject) param.get(1);
                            val2 = (String) paramJson.get("value");

                            if (etype.equals("local")) {
                                entryList = fp.LocalLengthFilter(entryList, Long.parseLong(val1), val2);
                            } else {
                                entryList = fp.CloudLengthFilter(entryList, Long.parseLong(val1), val2);
                            }
                            break;
                        case "contentfilter":
                            if (etype.equals("local")) {
                                entryList = fp.LocalContentFilter(entryList, val1);
                            } else {
                                entryList = fp.CloudContentFilter(entryList, val1);
                            }
                            break;
                        case "countfilter":
                            paramJson = (JSONObject) param.get(1);
                            val2 = (String) paramJson.get("value");
                            if (etype.equals("local")) {
                                entryList = fp.LocalCountFilter(entryList, Integer.parseInt(val1), val2);
                            } else {
                                entryList = fp.CloudCountFilter(entryList, Integer.parseInt(val1), val2);
                            }
                            break;
                        default:
                            System.out.println("Error: Invalid Filter");
                    }

                } else if (type.toLowerCase().equals("rename")) {

                    String val = "";
                    JSONObject paramJson = (JSONObject) param.get(0);
                    val = (String) paramJson.get("value");

                    if (etype.equals("local")) {
                        entryList = fp.Rename1(entryList, val);
                    } else {
                        entryList = fp.Rename2(entryList, val);
                    }
                } else if (type.toLowerCase().equals("split")) {
                    String val = "";
                    JSONObject paramJson = (JSONObject) param.get(0);
                    val = (String) paramJson.get("value");
                    if (etype.equals("local")) {
                        fp.splitLocalEntry(entryList, Integer.parseInt(val));
                    } else {
                        fp.splitRemoteEntry(entryList, Integer.parseInt(val));

                    }
                } else if (type.toLowerCase().equals("print")) {
                    if (etype.equals("local")) {
                        fp.printlocalentry(entryList);
                    } else {
                        fp.printremoteentry(entryList);
                    }

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
