/*
 * Copyright 2013 Bazaarvoice, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bazaarvoice.lassie.screenboard;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Client for accessing Datadog's screenboards.
 * Both the application key and the api key are obtained from the datadog site.
 */
public class DataDogScreenboardClient {
    private final String _applicationKey;
    private final String _apiKey;
    private final Client _httpClient;
    private final URI _datadogApiUrl;
    private static final URI DEFAULT_DATADOG_API_URL = URI.create("https://app.datadoghq.com/api/v1/screen");

    /**
     * Full constructor that allows the user to provide their own client and URI.
     *
     * @param applicationKey datadog applicationKey
     * @param apiKey         datadog apiKey
     * @param client         the http client that makes the requests
     * @param uri            The uri to the datadog endpoints
     */
    public DataDogScreenboardClient(String applicationKey, String apiKey, URI uri, Client client) {
        _applicationKey = checkNotNull(applicationKey, "application key is null");
        _apiKey = checkNotNull(apiKey, "apiKey is null");
        _httpClient = checkNotNull(client, "client is null");
        _datadogApiUrl = checkNotNull(uri, "URI is null");
    }

    /**
     * Constructor that allows the user to use the client with a specific client.
     *
     * @param applicationKey datadog applicationkey
     * @param apiKey         datadog apiKey
     * @param client         the http client that makes the requests
     */
    public DataDogScreenboardClient(String applicationKey, String apiKey, Client client) {
        this(applicationKey, apiKey, DEFAULT_DATADOG_API_URL, client);
    }

    /**
     * Constructor that allows the user to use the client with a specific uri.
     *
     * @param applicationKey datadog applicationKey
     * @param apiKey         datadog api key
     * @param uri            The uri to the datadog endpoints
     */
    public DataDogScreenboardClient(String applicationKey, String apiKey, URI uri) {
        _applicationKey = checkNotNull(applicationKey, "application key is null");
        _apiKey = checkNotNull(apiKey, "apiKey is null");
        _datadogApiUrl = checkNotNull(uri, "URI is null");

        _httpClient = Client.create();
    }

    /**
     * Basic constructor that allows the user to use the client with only their datadog credentials.
     *
     * @param applicationKey datadog application key
     * @param apiKey         datadog apiKey
     */
    public DataDogScreenboardClient(String applicationKey, String apiKey) {
        this(applicationKey, apiKey, DEFAULT_DATADOG_API_URL);
    }

    /**
     * Create a new screenboard.
     *
     * @param board The screenboard to be created.
     * @return The id of the created board.
     * @throws DataDogScreenboardException an error occurs creating the screenboard
     */
    public int create(Board board) throws DataDogScreenboardException {
        try {
            return apiResource()
                    .post(ScreenboardResponse.class, board)
                    .getId();
        } catch (UniformInterfaceException ex) {
            throw createError(ex, false);
        }
    }

    /**
     * Update an existing screenboard.
     *
     * @param screenboardID The ID of the screenboard to be updated.
     * @param board         The board that will replace the current board.
     * @throws ScreenboardNotFoundException the specified screenboard does not exist
     * @throws DataDogScreenboardException  an error occurs updating the screenboard
     */
    public void update(int screenboardID, Board board) throws DataDogScreenboardException {
        try {
            apiResource("" + screenboardID)
                    .put(board);
        } catch (UniformInterfaceException ex) {
            throw createError(ex, true);
        }
    }

    /**
     * Delete an existing screenboard.
     * <p/>
     * This method has no effect if the screenboard does not exist.
     *
     * @param screenboardID The ID of the screenboard to be deleted
     * @throws DataDogScreenboardException an error occurs deleting the screenboard
     */
    public void delete(int screenboardID) throws DataDogScreenboardException {
        try {
            apiResource("" + screenboardID)
                    .delete(ScreenboardResponse.class);
        } catch (UniformInterfaceException ex) {
            if (ex.getResponse().getClientResponseStatus() != ClientResponse.Status.NOT_FOUND) {
                throw createError(ex, true);
            }
        }
    }

    /**
     * Get an existing screenboard.
     *
     * @param screenboardID ID of the screenboard
     * @return The board matching the ID
     * @throws ScreenboardNotFoundException the specified screenboard does not exist
     * @throws DataDogScreenboardException  an error occurs retrieving the screenboard
     */
    public Board get(int screenboardID) throws DataDogScreenboardException {
        try {
            return apiResource("" + screenboardID)
                    .get(Board.class);
        } catch (UniformInterfaceException ex) {
            throw createError(ex, true);
        }
    }

    /**
     * Gets a URL of an existing screenboard that can be used to publicly view the board in a browser.
     * <p/>
     * The public URL does not require any authentication to view. This makes it
     * ideal for embedding the screenboard in other tools.
     *
     * @param screenboardID ID of the screenboard
     * @return The URL of the screenboard
     * @throws ScreenboardNotFoundException if the specified screenboard does not exist
     * @throws DataDogScreenboardException  if an error occurs retrieving the public screenboard url
     */
    public String getPublicUrl(int screenboardID) throws DataDogScreenboardException {
        try {
            return apiResource("/share/" + screenboardID)
                    .get(ScreenboardUrlResponse.class)
                    .getUrl();
        } catch (UniformInterfaceException ex) {
            throw createError(ex, true);
        }
    }

    private static DataDogScreenboardException createError(UniformInterfaceException ex, boolean enableNotFound) {
        ClientResponse response = ex.getResponse();
        ErrorResponse errorInfo = response.getEntity(ErrorResponse.class);

        if (enableNotFound && response.getClientResponseStatus() == ClientResponse.Status.NOT_FOUND) {
            return new ScreenboardNotFoundException(errorInfo.getErrors());
        } else {
            return new DataDogScreenboardException(errorInfo.getErrors(), ex);
        }
    }

    private WebResource.Builder apiResource(String... path) {
        UriBuilder resourceUrl = UriBuilder.fromUri(_datadogApiUrl);

        for (String p : path) {
            resourceUrl.path(p);
        }

        return _httpClient.resource(resourceUrl.build())
                .queryParam("api_key", _apiKey)
                .queryParam("application_key", _applicationKey)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE);
    }

    public URI getDatadogApiUrl() {
        return _datadogApiUrl;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class ErrorResponse {
        @JsonProperty("errors")
        private List<String> _errors;

        private List<String> getErrors() {
            return _errors;
        }
    }

    /**
     * mainly used for Jackson deserialization of responses from datadog.
     * The error parameter is used to catch the error response from Datadog.
     * It should only have data when there is a server side error
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class ScreenboardResponse {
        @JsonProperty("id")
        private int _id;
        @JsonProperty("board")
        private Board _board;

        private Board getBoard() {
            return _board;
        }

        private void setBoard(Board board) {
            _board = board;
        }

        private int getId() {
            return _id;
        }

        private void setId(int id) {
            _id = id;
        }
    }

    /** mainly used for Jackson deserialization of responses from datadog. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class ScreenboardUrlResponse {
        @JsonProperty("id")
        private int _id;
        @JsonProperty("public_url")
        private String _url;

        private int getId() {
            return _id;
        }

        private String getUrl() {
            return _url;
        }

        private void setId(int id) {
            _id = id;
        }

        private void setUrl(String url) {
            _url = checkNotNull(url, "url is null");
        }
    }
}
