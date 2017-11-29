package com.navjot.deepak.manpreet.pdfsociety.Models;

public class Pdf {

    private String pdfname;
    private String description;
    private String uid;
    private String download_url;
    private String username;
    private double pdfsize;
    private String uploaddate;
    private String pdfKey;
    private String categories;

    public Pdf() {
    }

    public Pdf(String pdfname, String description, String uid, String download_url, String username, double pdfsize, String uploaddate, String pdfKey, String categories) {
        this.pdfname = pdfname;
        this.description = description;
        this.uid = uid;
        this.download_url = download_url;
        this.username = username;
        this.pdfsize = pdfsize;
        this.uploaddate = uploaddate;
        this.pdfKey = pdfKey;
        this.categories = categories;
    }

    public String getPdfname() {
        return pdfname;
    }

    public void setPdfname(String pdfname) {
        this.pdfname = pdfname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDownload_url() {
        return download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getPdfsize() {
        return pdfsize;
    }

    public void setPdfsize(double pdfsize) {
        this.pdfsize = pdfsize;
    }

    public String getUploaddate() {
        return uploaddate;
    }

    public void setUploaddate(String uploaddate) {
        this.uploaddate = uploaddate;
    }

    public String getPdfKey() {
        return pdfKey;
    }

    public void setPdfKey(String pdfKey) {
        this.pdfKey = pdfKey;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    @Override
    public String toString() {
        return "Pdf{" +
                "pdfname='" + pdfname + '\'' +
                ", description='" + description + '\'' +
                ", uid='" + uid + '\'' +
                ", download_url='" + download_url + '\'' +
                ", username='" + username + '\'' +
                ", pdfsize=" + pdfsize +
                ", uploaddate='" + uploaddate + '\'' +
                ", pdfKey='" + pdfKey + '\'' +
                ", categories='" + categories + '\'' +
                '}';
    }
}
