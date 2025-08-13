package org.vwf.file_handling.upload.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor @Getter
public abstract class AppConstants {
    public static final String
            FILE = "FILE",
            IMAGE = "IMAGE";

    public static final List<String> FORMATS_LIST = List.of("octet-stream",
            "jpg", "jpeg", "png", "webp", "gif", "mp3", "aac",
            "flac", "mp4", "mpeg", "av1", "mkv", "zip", "pdf");

    public static final Map<String, Integer> IMG_EXTENSIONS_ALLOWED = Map.of(
            ".png", 1, ".jpg", 2,
            ".jpeg", 3, ".gif", 4,
            ".webp", 5);

    public static final String CHAR = "Character";
    public static final String DIGIT = "Digit";
    public static final String SPECIAL = "Special";
    public static final String WHITESPACE = "Whitespace";

}
