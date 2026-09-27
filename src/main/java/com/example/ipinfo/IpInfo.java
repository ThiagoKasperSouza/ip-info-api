package com.example.ipinfo;

/**
 * Publicly available information about a given IP address,
 * gathered from external geo/IP lookup APIs.
 */
public record IpInfo(
        String ip,
        String status,
        String message,
        String country,
        String countryCode,
        String region,
        String regionName,
        String city,
        String postalCode,
        Double latitude,
        Double longitude,
        String timezone,
        String isp,
        String organization,
        String as,
        String asName
) {
}
