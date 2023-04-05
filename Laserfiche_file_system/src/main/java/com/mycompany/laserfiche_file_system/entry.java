package com.mycompany.laserfiche_file_system;

public class entry {
    private String name;
    private String type;
    private String path;
    private String repositoryID;
    private String entryID;
    private long Length;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public long getLength() {
        return Length;
    }

    public void setLength(long length) {
        this.Length = length;
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRepositoryID() {
        return repositoryID;
    }

    public void setRepositoryID(String repositoryID) {
        this.repositoryID = repositoryID;
    }

    public String getEntryID() {
        return entryID;
    }

    public void setEntryID(String entryID) {
        this.entryID = entryID;
    }
    
   // constructors for remote and local entries
    entry(String type, String path){
        this.type = type;
        this.path = path;
    }
    entry(String type, String repositoryID, String entryID){
        this.type = type;
        this.repositoryID = repositoryID;
        this.entryID = entryID;
    }
}
