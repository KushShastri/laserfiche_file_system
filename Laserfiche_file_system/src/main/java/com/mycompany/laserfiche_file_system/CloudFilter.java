/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

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

/**
 *
 * @author Shane
 */
public class CloudFilter {

    public static List<Entry> Name(List<Entry> e, String key) {
        List<Entry> ret = new ArrayList<Entry>();
        for (Entry current : e) {
            if (current.getName().contains(key)) {
                ret.add(current);
            }
        }
        return ret;
    }

    public static List<Entry> Length(List<Entry> e, long length, String operator) {
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

    public static List<Entry> Content(List<Entry> e, String key) throws IOException {
        List<Entry> ret = new ArrayList<Entry>();
        for (Entry current : e) {
            if (!(current.getEntryType()).toString().equals(("Folder"))) {
                File file = toFile(current.getId());
                if (file.isFile()) {
                    List<String> data = Files.readAllLines(Path.of(file.getAbsolutePath()));
                    for (String s : data) {
                        if (s.contains(key)) {
                            ret.add(current);
                            break;
                        }
                    }
                }
            }
        }
        return ret;
    }

    public List<Entry> Count(List<Entry> entries, int min, String key) throws IOException {
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
