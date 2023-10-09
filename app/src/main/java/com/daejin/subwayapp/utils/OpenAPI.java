package com.daejin.subwayapp.utils;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

public class OpenAPI {

    public JSONArray searchStation(String name) throws IOException, JSONException {
        String format = "json";
        String apiUrl = "http://openAPI.seoul.go.kr:8088";
        String key = "745478546a686f6a31303570596e5162";
        String service = "SearchInfoBySubwayNameService";

        StringBuilder urlBuilder = new StringBuilder(apiUrl);
        urlBuilder.append("/" + URLEncoder.encode(key, "UTF-8"));
        urlBuilder.append("/" + URLEncoder.encode(format, "UTF-8"));
        urlBuilder.append("/" + URLEncoder.encode(service, "UTF-8"));
        urlBuilder.append("/" + URLEncoder.encode("1", "UTF-8"));
        urlBuilder.append("/" + URLEncoder.encode("999", "UTF-8"));

        urlBuilder.append("/" + URLEncoder.encode(name, "UTF-8"));

        return parseJson(urlBuilder, service);
    }

    public JSONArray searchSchedule(String station_cd, String week_tag, String inout_tag)
    throws IOException, JSONException {
        String format = "json";
        String apiUrl = "http://openAPI.seoul.go.kr:8088";
        String key = "745478546a686f6a31303570596e5162";
        String service = "SearchSTNTimeTableByIDService";

        StringBuilder urlBuilder = new StringBuilder(apiUrl);
        urlBuilder.append("/" + URLEncoder.encode(key, "UTF-8"));
        urlBuilder.append("/" + URLEncoder.encode(format, "UTF-8"));
        urlBuilder.append("/" + URLEncoder.encode(service, "UTF-8"));
        urlBuilder.append("/" + URLEncoder.encode("1", "UTF-8"));
        urlBuilder.append("/" + URLEncoder.encode("999", "UTF-8"));

        urlBuilder.append("/" + URLEncoder.encode(station_cd, "UTF-8"));
        urlBuilder.append("/" + URLEncoder.encode(week_tag, "UTF-8"));
        urlBuilder.append("/" + URLEncoder.encode(inout_tag, "UTF-8"));

        return parseJson(urlBuilder, service);
    }

    public JSONArray parseJson(StringBuilder urlBuilder, String service) throws IOException, JSONException {
        URL url = new URL(urlBuilder.toString());
        System.out.println("url : " + url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        String original = sb.toString();

        JSONObject jsonObject_1 = new JSONObject(original);
        String temp1 = jsonObject_1.getString(service);

        JSONObject jsonObject_2 =  new JSONObject(temp1);
        String row = jsonObject_2.getString("row");

        JSONArray jsonArray = jsonObject_2.getJSONArray("row");
        System.out.println(jsonArray);
        //해당 오브젝트-키의 값 출력 System.out.println(jsonArray.getJSONObject(0).getString("STATION_CD"));

        return jsonArray;
    }
}
