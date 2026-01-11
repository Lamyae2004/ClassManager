package com.ensa.mobile.gestionDocuments.models;

public class Document {
    private Long id;
    private String title;
    private String type;

    private String fileName;
    private Long fileSize;
    private String uploadDate;

    private Long moduleId;
    private Long classeId;
    private Long profId;
    private String fileUrl;
       // URL complète (avec extension)




    public Document(String title, String type, Long module,Long profId, Long classe, String url, String file) {
        this.title = title;
        this.type = type;
        this.moduleId = module;
        this.profId = profId;
        this.classeId = classe;
        this.fileUrl = url;
        this.fileName = file;
    }




    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getType() { return type; }
    public String getFileName() { return fileName; }
    public Long getFileSize() { return fileSize; }
    public String getUploadDate() { return uploadDate; }
    public Long getModuleId() { return moduleId; }
    public Long getClasseId() { return classeId; }
    public Long getProfId() { return profId; }
    public String getFileUrl() { return fileUrl; }
}
