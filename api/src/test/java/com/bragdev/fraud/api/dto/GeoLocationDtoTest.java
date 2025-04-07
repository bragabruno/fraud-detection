package com.bragdev.fraud.api.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GeoLocationDtoTest {

    @Test
    public void testConstructorAndGetters() {
        // Arrange
        double latitude = 37.7749;
        double longitude = -122.4194;
        
        // Act
        GeoLocationDto location = new GeoLocationDto();
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        
        // Assert
        assertEquals(latitude, location.getLatitude(), 0.0001);
        assertEquals(longitude, location.getLongitude(), 0.0001);
    }
    
    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        GeoLocationDto location1 = new GeoLocationDto();
        location1.setLatitude(37.7749);
        location1.setLongitude(-122.4194);
        
        GeoLocationDto location2 = new GeoLocationDto();
        location2.setLatitude(37.7749);
        location2.setLongitude(-122.4194);
        
        GeoLocationDto location3 = new GeoLocationDto();
        location3.setLatitude(40.7128);
        location3.setLongitude(-74.0060);
        
        // Assert
        assertEquals(location1, location2);
        assertEquals(location1.hashCode(), location2.hashCode());
        
        assertNotEquals(location1, location3);
        assertNotEquals(location1.hashCode(), location3.hashCode());
    }
    
    @Test
    public void testToString() {
        // Arrange
        GeoLocationDto location = new GeoLocationDto();
        location.setLatitude(37.7749);
        location.setLongitude(-122.4194);
        
        // Act
        String toString = location.toString();
        
        // Assert
        assertTrue(toString.contains("37.7749"));
        assertTrue(toString.contains("-122.4194"));
    }
    
    @Test
    public void testSetters() {
        // Arrange
        GeoLocationDto location = new GeoLocationDto();
        
        // Act
        location.setLatitude(37.7749);
        location.setLongitude(-122.4194);
        
        // Change values
        location.setLatitude(40.7128);
        location.setLongitude(-74.0060);
        
        // Assert
        assertEquals(40.7128, location.getLatitude(), 0.0001);
        assertEquals(-74.0060, location.getLongitude(), 0.0001);
    }
}