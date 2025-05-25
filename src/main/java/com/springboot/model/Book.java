package com.springboot.model;

public class Book {


    private String author;
    private String title;
    private String year;
    private String copies;
    private String id;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public String getCopies() { return copies; }
    public void setCopies(String copies) { this.copies = copies; }

    public Integer getQuantity() {
        return copies != null ? Integer.valueOf(copies) : 0;
    }

    public void setQuantity(Integer quantity) {
        this.copies = String.valueOf(quantity);
    }

}
