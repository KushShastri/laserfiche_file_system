package com.mycompany.laserfiche_file_system;
import com.laserfiche.api.client.model.AccessKey;
import com.laserfiche.repository.api.RepositoryApiClient;
import com.laserfiche.repository.api.RepositoryApiClientImpl;
import com.laserfiche.repository.api.clients.impl.model.Entry;
import com.laserfiche.repository.api.clients.impl.model.ODataValueContextOfIListOfEntry;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Rename {
    public static List<Entry> Rename1 (List<Entry> entries, String suffix){
       
        List <Entry> NewEntries = new ArrayList <> ();
              
        for (Entry entry: entries){
    
            String temp = entry.getName().substring(0, 
                        entry.getName().length()-4);
            
                   temp += suffix + ".txt";
                   entry.setName(temp);
                   NewEntries.add(entry);          
        } 
        
        return NewEntries;
     
        }
        
        public static List<Entry> Rename2 (List<Entry> entries, String suffix){
            
            String servicePrincipalKey = "5w-5Sbp5T2eyBsduFo-g";
            String accessKeyBase64 = "ewoJImN1c3RvbWVySWQiOiAiMTQwMTM1OTIzOCIsCgkiY2xpZW50SWQiOiAiMDcwYzllYTYtMzQwZS00ODdmLTlmNzItM2YyNjQ0NWNkZWZmIiwKCSJkb21haW4iOiAibGFzZXJmaWNoZS5jYSIsCgkiandrIjogewoJCSJrdHkiOiAiRUMiLAoJCSJjcnYiOiAiUC0yNTYiLAoJCSJ1c2UiOiAic2lnIiwKCQkia2lkIjogInlpUkFKeGZ0eVpZVXk1TFBFYUhmTF9MRS03RWZjSW5nQ3NCVGtxa09yb28iLAoJCSJ4IjogImJ2ZmpDQU9acUdYeVhLdXNELUpEdFkzRVhwNms5WWtTOFZWYzRicER2OFEiLAoJCSJ5IjogInRmYXBLSDc4Qm45LUp5aVZQeDRrQWVDZFlqSjN6RWxHLVZGeU9lS0dNUUEiLAoJCSJkIjogIlNXei1kTUI1bTktWWtkNFJiLVFyMllYbE9BVlpYV0loV3hxVi1QTkFBWEkiLAoJCSJpYXQiOiAxNjc3Mjk3NzE1Cgl9Cn0=";
            String repositoryId = "r-0001d410ba56";
            AccessKey accessKey = AccessKey.createFromBase64EncodedAccessKey(accessKeyBase64);
    
            RepositoryApiClient client = RepositoryApiClientImpl.createFromAccessKey(
                    servicePrincipalKey, accessKey);
            
              int rootEntryId = 1;
            ODataValueContextOfIListOfEntry result = client
                    .getEntriesClient()
                    .getEntryListing(repositoryId, rootEntryId, true, null, null, null, null, null, "name", null, null, null).join();
            
            List<Entry> innerEntries = result.getValue();
            
            
            List <Entry> NewEntries = new ArrayList <> ();
            
            for (Entry entry: innerEntries){
    
                    String temp = entry.getName().substring(0, 
                        entry.getName().length()-4);
            
                   temp += suffix + ".txt";
                   entry.setName(temp);
                   NewEntries.add(entry);          
                } 
            return NewEntries;
        }
    
        
    
        
}
