package com.example.demo.Model;

public class Booking {

    private int id;
    private String customerName;
    private String artistName;
    private String date;
    private String status;

    public Booking(int id, String customerName, String artistName, String date, String status) {
        this.id = id;
        this.customerName = customerName;
        this.artistName = artistName;
        this.date = date;
        this.status = status;
    }

    // getters + setters
    public int getId() { return id; }
    public String getCustomerName() { return customerName; }
    public String getArtistName() { return artistName; }
    public String getDate() { return date; }
    public String getStatus() { return status; }

    public void setId(int id) { this.id = id; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setArtistName(String artistName) { this.artistName = artistName; }
    public void setDate(String date) { this.date = date; }
    public void setStatus(String status) { this.status = status; }
}
