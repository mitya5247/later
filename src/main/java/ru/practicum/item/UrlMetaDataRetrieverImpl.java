package ru.practicum.item;

import lombok.Builder;
import lombok.Value;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.MimeType;
import ru.practicum.exception.ItemRetrieverException;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;

import static javax.management.remote.JMXConnectorFactory.connect;


@Value
@Builder(toBuilder = true)
class UrlMetaDataRetrieverImpl implements UrlMetadata {


    @Override
    public String getNormalUrl() {
        return UrlMetadataImpl.builder().normalUrl;
    }

    @Override
    public String getResolvedUrl() {
        return UrlMetadataImpl.builder().resolvedUrl;
    }

    @Override
    public String getMimeType() {
        return UrlMetadataImpl.builder().mimeType;
    }

    @Override
    public String getTitle() {
        return UrlMetadataImpl.builder().title;
    }

    @Override
    public boolean isHasImage() {
        return UrlMetadataImpl.builder().hasImage;
    }

    @Override
    public boolean isHasVideo() {
        return UrlMetadataImpl.builder().hasVideo;
    }

    @Override
    public Instant getDateResolved() {
        return UrlMetadataImpl.builder().dateResolved;
    }

    @Override
    public UrlMetadata retrieve(String url) {
        return UrlMetadataImpl.builder().build();
    }

    @lombok.Value
    @Builder(toBuilder = true)
    static class UrlMetadataImpl implements UrlMetadata {
        String normalUrl;
        String resolvedUrl;
        String mimeType;
        String title;
        boolean hasImage;
        boolean hasVideo;
        Instant dateResolved;

        @Override
        public UrlMetadata retrieve(String urlString) {
            final URI uri;
            try {
                uri = new URI(urlString);
            } catch (URISyntaxException e) {
                // Если адрес не соответствует правилам URI адресов, то генерируем исключение.
                throw new ItemRetrieverException("The URL is malformed: " + urlString, e);
            }

            try {
                HttpResponse<Void> resp = connect(uri, "HEAD", HttpResponse.BodyHandlers.discarding());

                String contentType = resp.headers()
                        .firstValue(HttpHeaders.CONTENT_TYPE)
                        .orElse("*");

                MediaType mediaType = MediaType.parseMediaType(contentType);

                final UrlMetadataImpl result;

                if (mediaType.isCompatibleWith(MimeType.valueOf("text/*"))) {
                    result = handleText(resp.uri());
                } else if (mediaType.isCompatibleWith(MimeType.valueOf("image/*"))) {
                    result = handleImage(resp.uri());
                } else if (mediaType.isCompatibleWith(MimeType.valueOf("video/*"))) {
                    result = handleVideo(resp.uri());
                } else {
                    throw new ItemRetrieverException("The content type [" + mediaType
                            + "] at the specified URL is not supported.");
                }

                return result.toBuilder()
                        .normalUrl(urlString)
                        .resolvedUrl(resp.uri().toString())
                        .mimeType(mediaType.getType())
                        .dateResolved(Instant.now())
                        .build();
            } catch (IOException | InterruptedException e) {
                e.getCause();
            }


            return null;
        }

        private <T> HttpResponse<T> connect(URI url,
                                            String method,
                                            HttpResponse.BodyHandler<T> responseBodyHandler) throws IOException, InterruptedException {
            //делаем запрос к данному url

            HttpRequest.BodyPublisher publisher = HttpRequest.BodyPublishers.noBody();
            HttpClient client = HttpClient.newBuilder().build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .method("HEAD", publisher)
                    .build();
            HttpResponse<T> response = client.send(request, responseBodyHandler);
            return response;
        }

        private UrlMetadataImpl handleText(URI url) {
            //заполняем поля для случая, когда страница содержит текст (в том числе html)
            // Отправим get-запрос, чтобы получить содержимое
            HttpResponse<String> resp = null;
            try {
                resp = connect(url, "GET", HttpResponse.BodyHandlers.ofString());
            } catch (IOException e) {
                throw new ItemRetrieverException("handle text", e.getCause());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // воспользуемся библиотекой Jsoup для парсинга содержимого
            Document doc = Jsoup.parse(resp.body());

            // ищем в полученном документе html-тэги, говорящие, что он
            // содержит видео или аудио информацию
            Elements imgElements = doc.getElementsByTag("img");
            Elements videoElements = doc.getElementsByTag("video");

            // добавляем полученные данные в ответ. В том числе находим заголовок
            // полученной страницы.
            return UrlMetadataImpl.builder()
                    .title(doc.title())
                    .hasImage(!imgElements.isEmpty())
                    .hasVideo(!videoElements.isEmpty())
                    .build();
        }

        private UrlMetadataImpl handleVideo(URI url) {
            //заполняем поля для случая, когда страница содержит видео
            String name = new File(url).getName();
            return UrlMetadataImpl.builder()
                    .title(name)
                    .hasVideo(true)
                    .build();
        }

        private UrlMetadataImpl handleImage(URI url) {
            //заполняем поля для случая, когда страница содержит изображение
            String name = new File(url).getName();
            return UrlMetadataImpl.builder()
                    .title(name)
                    .hasImage(true)
                    .build();
        }
    }
}
