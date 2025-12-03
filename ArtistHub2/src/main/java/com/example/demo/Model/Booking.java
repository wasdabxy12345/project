package com.example.demo.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int artistId;
    private int customerId;
    private String artistName;
    private String customerName;
    private String date;
    private String status;

    public Booking() {
    }

    public Booking(int id, String customerName, String artistName, String date, String status) {
        this.id = id;
        this.customerName = customerName;
        this.artistName = artistName;
        this.date = date;
        this.status = status;
    }
}
