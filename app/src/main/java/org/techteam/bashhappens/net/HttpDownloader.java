package org.techteam.bashhappens.net;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

public class HttpDownloader {

    public static class Request {
        private String url;
        private List<UrlParams> params;
        private List<Header> headers;


        public Request(String url, List<UrlParams> params, List<Header> headers) {
            this.url = url;
            this.params = params;
            this.headers = headers;
        }

        public String getUrl() {
            return url;
        }

        public List<UrlParams> getParams() {
            return params;
        }

        public List<Header> getHeaders() {
            return headers;
        }
    }

    public static String httpGet(String url) throws IOException {
        return httpGet(url, null, null);
    }

    public static String httpGet(Request request) throws IOException {
        return httpGet(request.getUrl(), request.getParams(), request.getHeaders());
    }

    public static String httpGet(String url, List<UrlParams> params, List<Header> headers) throws IOException {
        URL urlObj = constructUrl(url, params);
        HttpURLConnection connection = null;
        InputStream in = null;

        try {
            connection = (HttpURLConnection) urlObj.openConnection();

            connection.setRequestMethod("GET");
            if (headers != null) {
                for (Header h : headers) {
                    connection.setRequestProperty(h.getName(), h.getValue());
                }
            }
            connection.connect();

            String res = null;
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                in = connection.getInputStream();
                res = handleInputStream(in);
            }
            return res;
        } finally {
            if (in != null) {
                in.close();
            }

            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static String httpPost(Request request) throws IOException {
        return httpPost(request.getUrl(), request.getParams(), request.getHeaders());
    }

    public static String httpPost(String url, List<UrlParams> data, List<Header> headers) throws IOException {
        URL urlObj = constructUrl(url, null);
        HttpURLConnection connection = null;
        InputStream in = null;
        OutputStream out = null;

        try {
            connection = (HttpURLConnection) urlObj.openConnection();

            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            if (headers != null) {
                for (Header h : headers) {
                    connection.setRequestProperty(h.getName(), h.getValue());
                }
            }

            connection.connect();

            String dataString = constructParams(data);
            out = new BufferedOutputStream(connection.getOutputStream());
            out.write(dataString.getBytes());
            //clean up
            out.flush();

            String res = null;
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                in = connection.getInputStream();
                res = handleInputStream(in);
            }
            return res;
        } finally {
            if (in != null) {
                in.close();
            }

            if (out != null) {
                out.close();
            }

            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static URL constructUrl(String url, List<UrlParams> params) throws MalformedURLException {
        if (params == null || params.isEmpty()) {
            return new URL(url);
        }
        return new URL(url + "?" + constructParams(params));
    }

    private static String constructParams(List<UrlParams> params) {
        String newUrl = "";
        for (UrlParams p : params) {
            newUrl += p.getKey() + "=" + p.getValue() + "&";
        }
        return newUrl.substring(0, newUrl.length() - 1);
    }


    private static String handleInputStream(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }
}
