/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.api;

import com.google.gson.*;
import de.rexlmanu.api.handler.ApplicationHandler;
import lombok.Getter;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class Request {

    private static final JsonParser PARSER = new JsonParser();
    private static final String API_POINT = "https://api.buildarchitects.team";

    private HttpClient httpClient;
    private ApplicationHandler applicationHandler;

    private String apiToken;

    public Request(String apiToken) {
        this.httpClient = HttpClients.createDefault();

        this.applicationHandler = new ApplicationHandler(this);
        this.apiToken = apiToken;
    }

    public JsonElement get(String endpoint, Map<String, String> parameters) {
        RequestBuilder builder = RequestBuilder.get().setUri(API_POINT + endpoint);
        parameters.forEach(builder::addParameter);
        builder.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.apiToken);
        try {
            HttpResponse response = this.httpClient.execute(builder.build());
            if (response.getStatusLine().getStatusCode() != 200) return JsonNull.INSTANCE;
            return PARSER.parse(EntityUtils.toString(response.getEntity()));
        } catch (IOException e) {
            return JsonNull.INSTANCE;
        }
    }

    public JsonElement get(String endpoint) {
        return this.get(endpoint, new HashMap<>());
    }

    public JsonElement post(String endpoint, Map<String, String> parameters) {
        HttpPost httpPost = new HttpPost(API_POINT + endpoint);
        httpPost.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.apiToken);
        List<NameValuePair> pairs = new ArrayList<>();
        parameters.forEach((name, value) -> pairs.add(new BasicNameValuePair(name, value)));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(pairs));
            HttpResponse response = this.httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() != 200) return JsonNull.INSTANCE;
            return PARSER.parse(EntityUtils.toString(response.getEntity()));
        } catch (IOException e) {
            return JsonNull.INSTANCE;
        }
    }

}
