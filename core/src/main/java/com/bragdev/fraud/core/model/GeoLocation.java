package com.bragdev.fraud.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the geographic location where a transaction took place.
 * Used for geographic anomaly detection in fraud analysis.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeoLocation {
    private double latitude;
    private double longitude;
    private String country;
    private String city;
    private String zipCode;
    private String ipAddress;
    
    /**
     * Simplified constructor with just coordinates
     */
    public GeoLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    /**
     * Calculate distance in kilometers between this location and another
     */
    public double distanceFrom(GeoLocation other) {
        if (other == null) return Double.MAX_VALUE;
        
        // Using Haversine formula
        final int R = 6371; // Earth radius in km
        double latDistance = Math.toRadians(other.latitude - this.latitude);
        double lonDistance = Math.toRadians(other.longitude - this.longitude);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(this.latitude)) * Math.cos(Math.toRadians(other.latitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}