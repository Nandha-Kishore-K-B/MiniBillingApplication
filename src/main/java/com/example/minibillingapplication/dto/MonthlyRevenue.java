package com.example.minibillingapplication.dto;

public class MonthlyRevenue {
    private int year;
    private int month;
    private double totalRevenue;

    public MonthlyRevenue(int year, int month, double totalRevenue) {
        this.year = year;
        this.month = month;
        this.totalRevenue = totalRevenue;
    }

    // Getters
    public int getYear() { return year; }
    public int getMonth() { return month; }
    public double getTotalRevenue() { return totalRevenue; }

    // Setters
    public void setYear(int year) { this.year = year; }
    public void setMonth(int month) { this.month = month; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
}