package com.noteapp.dto.note;

public class FileDTO {
    private String fileName;
    private String title;
    private String contentType;
    private byte[] fileBytes;

    public byte[] getFileByte() {
        return fileBytes;
    }

    public void setFileByte(byte[] fileBytes) {
        this.fileBytes = fileBytes;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

}
