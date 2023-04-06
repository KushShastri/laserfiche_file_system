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

public class FileProcessor {
    //Acess the API
       public static String servicePrincipalKey = "5w-5Sbp5T2eyBsduFo-g";
       public static String accessKeyBase64 = "ewoJImN1c3RvbWVySWQiOiAiMTQwMTM1OTIzOCIsCgkiY2xpZW50SWQiOiAiMDcwYzllYTYtMzQwZS00ODdmLTlmNzItM2YyNjQ0NWNkZWZmIiwKCSJkb21haW4iOiAibGFzZXJmaWNoZS5jYSIsCgkiandrIjogewoJCSJrdHkiOiAiRUMiLAoJCSJjcnYiOiAiUC0yNTYiLAoJCSJ1c2UiOiAic2lnIiwKCQkia2lkIjogInlpUkFKeGZ0eVpZVXk1TFBFYUhmTF9MRS03RWZjSW5nQ3NCVGtxa09yb28iLAoJCSJ4IjogImJ2ZmpDQU9acUdYeVhLdXNELUpEdFkzRVhwNms5WWtTOFZWYzRicER2OFEiLAoJCSJ5IjogInRmYXBLSDc4Qm45LUp5aVZQeDRrQWVDZFlqSjN6RWxHLVZGeU9lS0dNUUEiLAoJCSJkIjogIlNXei1kTUI1bTktWWtkNFJiLVFyMllYbE9BVlpYV0loV3hxVi1QTkFBWEkiLAoJCSJpYXQiOiAxNjc3Mjk3NzE1Cgl9Cn0=";
       public static String repositoryId = "";
       public static AccessKey accessKey = AccessKey.createFromBase64EncodedAccessKey(accessKeyBase64);

       public static RepositoryApiClient client = RepositoryApiClientImpl.createFromAccessKey(servicePrincipalKey, accessKey);

      public void setRepo(String repo){
        repositoryId = repo; 
      }
        
        
    //Downloads an online file
    public File toFile(int id) {
        // get access to the API
        AccessKey accessKey = AccessKey.createFromBase64EncodedAccessKey(accessKeyBase64);

        RepositoryApiClient client = RepositoryApiClientImpl.createFromAccessKey(servicePrincipalKey, accessKey);
        int entryIdToDownload = id;
        final String FILE_NAME = "DownloadedFile.txt";
        File exportedFile = new File(FILE_NAME);
        Consumer<InputStream> consumer = inputStream -> {
            try (FileOutputStream outputStream = new FileOutputStream(exportedFile)) {
                byte[] buffer = new byte[1024];
                while (true) {
                    int length = inputStream.read(buffer);
                    if (length == -1) {
                        break;
                    }
                    outputStream.write(buffer, 0, length);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        client.getEntriesClient().exportDocument(repositoryId, entryIdToDownload, null, consumer).join();
        client.close();
        return exportedFile;
    }

       
       

    
//****************************************************************Filter Elements********************************************************
public List<Entry> NameFilter(List<Entry> e, String key) {
    List<Entry> ret = new ArrayList<Entry>();
    for (Entry current : e) {
        if (current.getName().toLowerCase().contains(key.toLowerCase())) {
            ret.add(current);
        }
    }
    return ret;
}

public List<Entry> LocalLengthFilter(List<Entry> e, long length, String operator) {
    List<Entry> ret = new ArrayList<Entry>();
    for (Entry current : e) {
        File file = new File(current.getFullPath());
        if (file.isFile()) {
            switch (operator) {
                case ("EQ"):
                    if (file.length() == length) {
                        ret.add(current);
                    }
                    break;
                case ("NEQ"):
                    if (file.length() != length) {
                        ret.add(current);
                    }
                    break;
                case ("GT"):
                    if (file.length() > length) {
                        ret.add(current);
                    }
                    break;
                case ("GTE"):
                    if (file.length() >= length) {
                        ret.add(current);
                    }
                    break;
                case ("LT"):
                    if (file.length() < length) {
                        ret.add(current);
                    }
                    break;
                case ("LTE"):
                    if (file.length() <= length) {
                        ret.add(current);
                    }
                    break;
                default:
                    System.out.println("Invalid Operator");
            }
        }
    }
    return ret;
}

public List<Entry> LocalContentFilter(List<Entry> e, String key) throws IOException {
    List<Entry> ret = new ArrayList<Entry>();
    for (Entry current : e) {
        File file = new File(current.getFullPath());
        if (file.isFile()) {
            List<String> data = Files.readAllLines(Path.of(file.getAbsolutePath()));
            for (String s : data) {
                if (s.toLowerCase().contains(key.toLowerCase())) {
                    ret.add(current);
                    break;
                }
            }
        }
    }
    return ret;
}

public List<Entry> LocalCountFilter(List<Entry> entrys, int min, String key) throws IOException {
    List<Entry> ret = new ArrayList<Entry>();
    int count = 0;

    for (Entry current : entrys) {
        count = 0;
        File file = new File(current.getFullPath());
        if (file.isFile()) {
            try {
                FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));

                String strLine;
                while ((strLine = br.readLine()) != null) {
                    int startIndex = strLine.indexOf(key);
                    while (startIndex != -1) {
                        count++;
                        startIndex = strLine.indexOf(key, startIndex + key.length());
                    }
                }
                in.close();

            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
            if (count >= min) {
                ret.add(current);
            }
        }
    }

    return ret;

}
//Local name filter can be used for cloud filter
public List<Entry> CloudLengthFilter(List<Entry> e, long length, String operator) {
    List<Entry> ret = new ArrayList<Entry>();
    for (Entry current : e) {
        if (!(current.getEntryType()).toString().equals(("Folder"))) {
            File file = toFile(current.getId());
            if (file.isFile()) {
                switch (operator) {
                    case ("EQ"):
                        if (file.length() == length) {
                            ret.add(current);
                        }
                        break;
                    case ("NEQ"):
                        if (file.length() != length) {
                            ret.add(current);
                        }
                        break;
                    case ("GT"):
                        if (file.length() > length) {
                            ret.add(current);
                        }
                        break;
                    case ("GTE"):
                        if (file.length() >= length) {
                            ret.add(current);
                        }
                        break;
                    case ("LT"):
                        if (file.length() < length) {
                            ret.add(current);
                        }
                        break;
                    case ("LTE"):
                        if (file.length() <= length) {
                            ret.add(current);
                        }
                        break;
                    default:
                        System.out.println("Invalid Operator");
                }
            }
        }
    }
    return ret;
}

public List<Entry> CloudContentFilter(List<Entry> e, String key) throws IOException {
    List<Entry> ret = new ArrayList<Entry>();
    for (Entry current : e) {
        if (!(current.getEntryType()).toString().equals(("Folder"))) {
            File file = toFile(current.getId());
            if (file.isFile()) {
                List<String> data = Files.readAllLines(Path.of(file.getAbsolutePath()));
                for (String s : data) {
                    if (s.toLowerCase().contains(key.toLowerCase())) {
                        ret.add(current);
                        break;
                    }
                }
            }
        }
    }
    return ret;
}

public List<Entry> CloudCountFilter(List<Entry> entries, int min, String key) throws IOException {
    List<Entry> ret = new ArrayList<Entry>();
    int count = 0;

    for (Entry current : entries) {
        count = 0;
        if (!(current.getEntryType()).toString().equals(("Folder"))) {
            File file = toFile(current.getId());
            if (file.isFile()) {
                try {
                    FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
                    DataInputStream in = new DataInputStream(fstream);
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));

                    String str;
                    while ((str = br.readLine()) != null) {
                        int start = str.indexOf(key);
                        while (start != -1) {
                            count++;
                            start = str.indexOf(key, start + key.length());
                        }
                    }
                    in.close();

                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage());
                }
                if (count >= min) {
                    ret.add(current);
                }
            }
        }
    }

    return ret;

}

//**************************************************Split Processing element***********************************************************
public List<File> splitLocalEntry(List<Entry> entries, int Lines) throws IOException {
    List<File> generatedEntries = new ArrayList<File>();
    for (Entry entry : entries){
        File file = new File(entry.getFullPath());
        if (file.isFile()){
            BufferedReader reader = new BufferedReader(new FileReader(file));
            List<String> lines = new ArrayList<String>();
            String line;
            while ((line = reader.readLine()) != null){
                lines.add(line);
            }
            reader.close();
            String filename = file.getName().replaceFirst("[.][^.]+$", "");
            String ext = file.getName().substring(filename.length());
            int parts = (lines.size() + Lines - 1) / Lines;
            for (int i = 0; i < parts; i++){
                String partFilename = String.format("%s.part%d%s", filename, i + 1, ext);
                File partFile = new File(file.getParent(), partFilename);
                FileWriter writer = new FileWriter(partFilename);
                int start = i * Lines;
                int end = Math.min((i + 1) * Lines, lines.size());
                for (int j = start; j < end; j++){
                    writer.write(lines.get(j) + "\n");
                }
                writer.close();
                generatedEntries.add(partFile);
            }
        }
        else if (file.isDirectory()){
            continue;
        }
    }
    return generatedEntries;
}

public List<File> splitRemoteEntry(List<Entry> entries, int Lines) throws Exception{
   
    List<File> generatedEntries = new ArrayList<File>();
    for (Entry token : entries){

        if (!token.getEntryType().toString().equals("Folder")){
            File file = toFile(token.getId());
            BufferedReader reader = new BufferedReader(new FileReader(file));
            List<String> lines = new ArrayList<String>();
            String line;
            while ((line = reader.readLine()) != null){
                lines.add(line);
            }
            reader.close();
            String filename = file.getName().replaceFirst("[.][^.]+$", "");
            String ext = file.getName().substring(filename.length());
            int parts = (lines.size() + Lines - 1) / Lines;
            for (int i = 0; i < parts; i++){
                String partFilename = String.format("%s.part%d%s", filename, i + 1, ext);
                File partFile = new File(file.getParent(), partFilename);
                FileWriter writer = new FileWriter(partFilename);
                int start = i * Lines;
                int end = Math.min((i + 1) * Lines, lines.size());
                for (int j = start; j < end; j++){
                    writer.write(lines.get(j) + "\n");
                }
                writer.close();
                generatedEntries.add(partFile);
            }
        }
        else if (token.getEntryType().toString().equals("Directory")){
            continue;
        }
    }
    return generatedEntries;
}
//************************************************List Processing Element***************************************************************
public List<Entry> listLocal(List<Entry> entries, int max) {
    List<Entry> ans = new ArrayList<Entry>();
    for (Entry outer : entries) {

        String outerPath = outer.getFullPath();
        File file = new File(outerPath);
        
        if(!file.isDirectory()){
            continue;
        }
        
        File[] fileNames = file.listFiles();

        for (int i = 0; i < Math.min(fileNames.length, max); i++) {
            Entry temp = new Entry();
            temp.setFullPath(fileNames[i].getAbsolutePath());
            temp.setName(fileNames[i].getName());
            ans.add(temp);
        }
    }
    return ans;
}

public List<Entry> listRemote(List<Entry> entries, int max) {    
    List<Entry> ans = new ArrayList<Entry>();
        //separates the root entries from the list
    for(Entry outter: entries){

        //getting the outter most ID
        int rootEntryId = outter.getId();
        
        //grabs all the OG files and then spits out a list of inners from root
        ODataValueContextOfIListOfEntry result = client.getEntriesClient().getEntryListing(repositoryId, rootEntryId, true, null, null, null, null, null, "name", null, null, null).join();
        List<Entry> rootInners = result.getValue();

        for(Entry inner : rootInners){
            rootEntryId = inner.getId();
            Entry compare = new Entry();
            compare.setEntryType(EntryType.DOCUMENT);
            if(inner.getEntryType() == compare.getEntryType()){
                continue;
            }

            for (int i = 0; i < Math.min(rootInners.size(), max); i++) {
                //here we take the inner entries and we add them to the list
                ans.add(inner);
            }
        }

    }
    return ans;
}
//***********************************************Rename Processing Element**************************************************************
public List<Entry> Rename1 (List<Entry> entries, String suffix){
       
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
    
    public List<Entry> Rename2 (List<Entry> entries, String suffix){
        
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

//************************************************Print Processing element**************************************************************
public List <Entry> printlocalentry(List <Entry> x){ //receives a local directory and prints requirements 

    //check if the entry is a file or a folder 

    for(Entry thisentry:x){ //increment through the list of entries ot get each individual entry's information 
        File check=new File(thisentry.getFullPath()); //create a file with the path of the entry 

        if(check.isFile() || check.isDirectory()){ //if it is a file or directory 
            System.out.println("Name: "+thisentry.getName()+"....absolute path: "+thisentry.getFullPath());

            //if it's a folder, print the size of 0
            if(check.isDirectory()){
                System.out.print("...length: 0 bytes");
            }
            else{ //if it's a folder, use the length function to get teh 
                System.out.println("...length: "+check.length()+" bytes");
            }
            System.out.println("\n"); //space out each entry's info
        }
    }

    return x;//return teh list of entries 
}


public List <Entry> printremoteentry(List <Entry> x){ //receives a file from the API, prints the relevant information

    for(Entry thisentry: x){
        System.out.println("Entry ID: "+thisentry.getId()+"....Name: "+thisentry.getName()+"....absolute path: "+thisentry.getFullPath());

        if(thisentry.getEntryType() != null){
        if(!(thisentry.getEntryType().toString()).equals("Folder")){ //checks if the entry is not a folder (directory), it's gonna read the length
            System.out.println("...length: "+remotelength(thisentry.getId()));
        }
    }
        else{ //if the entry is a folder (directory), it returns a length of 0
        System.out.println("...length :"+0);
        }
    }

    return x; //return the list of entries passed in
}


public long remotelength(int id){ //receives the id to find specific file and get its length  
    
    
    // Download an ENTRY 
      int entryIdToDownload = id;

    //create an empty file with the same pathname that the downloaded file. This variable is made so you can get length 
    File temp=new File("DownloadFile.txt"); //ENTER PATHNAME OF FILE BEING READ HERE  

      final String FILE_NAME = "DownloadFile.txt"; //ENTER PATHNAME HERE TOO
      Consumer<InputStream> consumer = inputStream -> {
          File exportedFile = new File(FILE_NAME);

          try (FileOutputStream outputStream = new FileOutputStream(exportedFile)) {
              byte[] buffer = new byte[1024];
              while (true) {
                  int length = inputStream.read(buffer);
                  if (length == -1) {
                      break;
                  }
                  outputStream.write(buffer, 0, length);
              }
          } catch (Exception e) {
              e.printStackTrace();
          } finally {
              try {
                  inputStream.close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
      };
 
    
    //gets an entry object and its information
    client.getEntriesClient()
            .exportDocument(repositoryId, entryIdToDownload, null, consumer)
            .join();
    client.close(); //close the file


    long LengthOfFile= temp.length();
    return LengthOfFile;

}

}
