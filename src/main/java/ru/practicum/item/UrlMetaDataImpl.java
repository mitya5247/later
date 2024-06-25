package ru.practicum.item;

import com.sun.net.httpserver.HttpExchange;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import ru.practicum.exception.ItemRetrieverException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;

import static javax.management.remote.JMXConnectorFactory.connect;


@Value
@Builder(toBuilder = true)
class UrlMetaDataImpl implements UrlMetadata {
    private static final Logger log = LoggerFactory.getLogger(UrlMetaDataImpl.class);
    String normalUrl;
    String resolvedUrl;
    String mimeType;
    String title;
    boolean hasImage;
    boolean hasVideo;
    Instant dateResolved;

    @Override
    public UrlMetadata retrieve(String url) {
        final URI uri;
        try {
            uri = URI.create(url);
        } catch (URISyntaxException e) {
            throw new ItemRetrieverException("The URL is malformed: " + urlString, e);
        }
       HttpResponse<Void> response = connect(uri, "HEAD");

        if (response != null) {
            String contentType = response.headers().toString();
            MediaType mediaType = MediaType.parseMediaType(contentType);
        }

        return null;
    }

    private HttpResponse<Void> connect(URI uri, String header) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .headers(header)
                .build();
        HttpResponse.BodyHandler<Void> bodyHandler = HttpResponse.BodyHandlers.discarding();

        try {
            HttpResponse<Void> response = client.send(request, bodyHandler);
            return response;
        } catch (IOException | InterruptedException e) {
            log.info(e.getMessage());
        }
        return null;
    }
}
