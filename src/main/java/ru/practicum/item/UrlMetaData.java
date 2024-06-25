package ru.practicum.item;

import java.time.Instant;

interface UrlMetadata {
    String getNormalUrl();
    String getResolvedUrl();
    String getMimeType();
    String getTitle();
    boolean isHasImage();
    boolean isHasVideo();
    Instant getDateResolved();
    UrlMetadata retrieve(String url);
}
