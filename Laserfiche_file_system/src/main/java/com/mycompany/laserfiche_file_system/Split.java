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
import java.io.FileReader;
import java.io.FileWriter;


public class Split {

    static String servicePrincipalKey = "5w-5Sbp5T2eyBsduFo-g";
    static String accessKeyBase64 = "ewoJImN1c3RvbWVySWQiOiAiMTQwMTM1OTIzOCIsCgkiY2xpZW50SWQiOiAiMDcwYzllYTYtMzQwZS00ODdmLTlmNzItM2YyNjQ0NWNkZWZmIiwKCSJkb21haW4iOiAibGFzZXJmaWNoZS5jYSIsCgkiandrIjogewoJCSJrdHkiOiAiRUMiLAoJCSJjcnYiOiAiUC0yNTYiLAoJCSJ1c2UiOiAic2lnIiwKCQkia2lkIjogInlpUkFKeGZ0eVpZVXk1TFBFYUhmTF9MRS03RWZjSW5nQ3NCVGtxa09yb28iLAoJCSJ4IjogImJ2ZmpDQU9acUdYeVhLdXNELUpEdFkzRVhwNms5WWtTOFZWYzRicER2OFEiLAoJCSJ5IjogInRmYXBLSDc4Qm45LUp5aVZQeDRrQWVDZFlqSjN6RWxHLVZGeU9lS0dNUUEiLAoJCSJkIjogIlNXei1kTUI1bTktWWtkNFJiLVFyMllYbE9BVlpYV0loV3hxVi1QTkFBWEkiLAoJCSJpYXQiOiAxNjc3Mjk3NzE1Cgl9Cn0=";
    static String repositoryId = "r-0001d410ba56";

    static AccessKey accessKey = AccessKey.createFromBase64EncodedAccessKey(accessKeyBase64);

    static RepositoryApiClient client = RepositoryApiClientImpl.createFromAccessKey(
            servicePrincipalKey, accessKey);


    public static List<File> splitLocalEntry(List<Entry> entries, int Lines) throws IOException {
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

    public static List<File> splitRemoteEntry(List<Entry> entries, int Lines) throws Exception{
        int entryId = 0;

        ODataValueContextOfIListOfEntry result = client
                .getEntriesClient()
                .getEntryListing(repositoryId, entryId, true, null, null, null, null, null, "name", null, null, null).join();

        List<Entry> entry = result.getValue();

        List<File> generatedEntries = new ArrayList<File>();
        for (Entry token : entry){

            if (token.getEntryType().toString()=="File"){
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
            else if (token.getEntryType().toString()=="Directory"){
                continue;
            }
        }
        return generatedEntries;
    }
    public static File toFile(int id) {
        // get access to the API
        String servicePrincipalKey = "5w-5Sbp5T2eyBsduFo-g";
        String accessKeyBase64 = "ewoJImN1c3RvbWVySWQiOiAiMTQwMTM1OTIzOCIsCgkiY2xpZW50SWQiOiAiMDcwYzllYTYtMzQwZS00ODdmLTlmNzItM2YyNjQ0NWNkZWZmIiwKCSJkb21haW4iOiAibGFzZXJmaWNoZS5jYSIsCgkiandrIjogewoJCSJrdHkiOiAiRUMiLAoJCSJjcnYiOiAiUC0yNTYiLAoJCSJ1c2UiOiAic2lnIiwKCQkia2lkIjogInlpUkFKeGZ0eVpZVXk1TFBFYUhmTF9MRS03RWZjSW5nQ3NCVGtxa09yb28iLAoJCSJ4IjogImJ2ZmpDQU9acUdYeVhLdXNELUpEdFkzRVhwNms5WWtTOFZWYzRicER2OFEiLAoJCSJ5IjogInRmYXBLSDc4Qm45LUp5aVZQeDRrQWVDZFlqSjN6RWxHLVZGeU9lS0dNUUEiLAoJCSJkIjogIlNXei1kTUI1bTktWWtkNFJiLVFyMllYbE9BVlpYV0loV3hxVi1QTkFBWEkiLAoJCSJpYXQiOiAxNjc3Mjk3NzE1Cgl9Cn0=";
        String repositoryId = "r-0001d410ba56";

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
}
