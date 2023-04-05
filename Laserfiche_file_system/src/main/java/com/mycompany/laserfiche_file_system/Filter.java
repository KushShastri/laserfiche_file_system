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
public class Filter {
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

    public static List<Entry> Content(List<Entry> e, String key) throws IOException {
        List<Entry> ret = new ArrayList<Entry>();
        for (Entry current : e) {
            File file = new File(current.getFullPath());
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
        return ret;
    }

    public static List<Entry> Count(List<Entry> entrys, int min, String key) throws IOException {
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
}
