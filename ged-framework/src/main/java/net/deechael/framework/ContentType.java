package net.deechael.framework;

import org.jetbrains.annotations.NotNull;

public enum ContentType {

    TEXT_HTML(".html", "text/html"),
    TEXT_PLAIN(".txt", "text/plain"),
    TEXT_JAVASCRIPT(".js", "text/javascript"),
    TEXT_CSS(".css", "text/css"),
    TEXT_XML(".xml", "text/xml"),
    IMAGE_GIF(".gif", "image/gif"),
    IMAGE_JPEG(".jpd", "image/jpeg"),
    IMAGE_PNG(".png", "image/png"),
    APPLICATION_JS(".js", "application/x-javascript"),
    APPLICATION_XHTML(".xhtml", "application/xhtml+xml"),
    APPLICATION_XML(".xml", "application/xml"),
    APPLICATION_ATOM_XML(".xml", "application/atom+xml"),
    APPLICATION_JSON(".json", "application/json"),
    APPLICATION_PDF(".pdf", "application/pdf"),
    APPLICATION_WORD(".doc", "application/msword"),
    APPLICATION_XLS(".doc", "application/vnd.ms-excel"),
    APPLICATION_PPT(".ppt", "application/vnd.ms-powerpoint"),
    APPLICATION_APK(".apk", "application/vnd.android.package-archive"),
    APPLICATION_IPA(".ipa", "application/vnd.iphone"),
    APPLICATION_ICO(".ico", "application/x-ico"),
    APPLICATION_TORRENT(".torrent", "application/x-bittorrent"),
    APPLICATION_OCTET_STREAM(".*", "application/octet-stream"),
    MULTIPART_FORM_DATA("", "multipart/form-data"),
    VIDEO_AVI(".avi", "video/avi"),
    VIDEO_MP4(".mp4", "video/mpeg4"),
    AUDIO_WAV(".wav", "audio/wav"),
    AUDIO_MID(".midi", "audio/mid"),
    AUDIO_MP3(".mp3", "audio/mp3"),
    // todo
    ;


    private final String suffix;

    private final String contentType;

    ContentType(String suffix, String contentType) {
        this.suffix = suffix;
        this.contentType = contentType;
    }

    @NotNull
    public String getSuffix() {
        return suffix;
    }

    @NotNull
    public String getContentType() {
        return contentType;
    }

}