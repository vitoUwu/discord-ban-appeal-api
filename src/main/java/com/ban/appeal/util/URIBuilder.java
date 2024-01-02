package com.ban.appeal.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class URIBuilder {
    private final Map<String, String> params = new HashMap<>();
    private String uri;

    public URIBuilder uri(String uri) {
        this.uri = uri;
        return this;
    }

    public URIBuilder addParam(String key, String value) {
        this.params.put(key, URLEncoder.encode(value, StandardCharsets.UTF_8));
        return this;
    }

    public URIBuilder addParams(Map<String, String> params) {
        for (Map.Entry<String, String> entry : params.entrySet()) {
            this.params.put(entry.getKey(), URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }

        return this;
    }

    public boolean hasParam(String key) {
        return this.params.containsKey(key);
    }

    public URIBuilder removeParam(String key) {
        if (this.hasParam(key)) {
            this.params.remove(key);
        }

        return this;
    }

    private String getParamsAsString() {
        StringBuilder params = new StringBuilder();
        for (Map.Entry<String, String> entry : this.params.entrySet()) {
            String param = entry.getKey() + "=" + entry.getValue() + "&";
            params.append(param);
        }

        return params.substring(0, params.length() - 1);
    }

    public URI build() throws URISyntaxException {
        StringBuilder _uri = new StringBuilder(this.uri);

        if (this.params.isEmpty()) {
            return new URI(_uri.toString());
        }

        _uri.append("?");
        String params = getParamsAsString();
        _uri.append(params);

        return new URI(_uri.toString());
    }
}
