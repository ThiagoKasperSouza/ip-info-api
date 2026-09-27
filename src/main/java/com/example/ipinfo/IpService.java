package com.example.ipinfo;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

/**
 * Gathers publicly available information about an IP address using the
 * free ip-api.com endpoint (no API key required). When no IP is supplied,
 * the caller's own public IP is resolved via api.ipify.org.
 */
public final class IpService {

    private static final String IP_API_BASE = "http://ip-api.com/json/";
    private static final String FIELDS =
            "status,message,country,countryCode,region,regionName,city,zip,lat,lon,timezone,isp,org,as,query";
    private static final String IPIFY = "https://api.ipify.org";

    private final HttpClient client;

    public IpService() {
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    public IpInfo lookup(String ip) throws Exception {
        String target = (ip == null || ip.isBlank()) ? resolveOwnIp() : ip;
        String url = IP_API_BASE + target + "?fields=" + FIELDS;

        HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(8))
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

        Map<String, Object> data = Json.parseObject(resp.body());
        return fromMap(target, data);
    }

    private String resolveOwnIp() throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create(IPIFY))
                .timeout(Duration.ofSeconds(8))
                .GET()
                .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        return resp.body().trim();
    }

    private static IpInfo fromMap(String requestedIp, Map<String, Object> d) {
        return new IpInfo(
                requestedIp,
                str(d, "status"),
                str(d, "message"),
                str(d, "country"),
                str(d, "countryCode"),
                str(d, "region"),
                str(d, "regionName"),
                str(d, "city"),
                str(d, "zip"),
                num(d, "lat"),
                num(d, "lon"),
                str(d, "timezone"),
                str(d, "isp"),
                str(d, "org"),
                str(d, "as"),
                str(d, "as")
        );
    }

    private static String str(Map<String, Object> m, String k) {
        Object v = m.get(k);
        return v == null ? null : String.valueOf(v);
    }

    private static Double num(Map<String, Object> m, String k) {
        Object v = m.get(k);
        return v instanceof Number n ? n.doubleValue() : null;
    }
}
