package net.deechael.framework;

import org.jetbrains.annotations.NotNull;

public enum ContentType {

    TEXT_HTML(new String[]{".html", ".htm"}, "text/html"),
    TEXT_PLAIN(new String[]{".txt"}, "text/plain"),
    TEXT_JAVASCRIPT(new String[]{".js"}, "text/javascript"),
    TEXT_CSS(new String[]{".css"}, "text/css"),
    TEXT_XML(new String[]{".xml"}, "text/xml"),
    IMAGE_GIF(new String[]{".gif"}, "image/gif"),
    IMAGE_JPEG(new String[]{".jpeg", ".jpg"}, "image/jpeg"),
    IMAGE_PNG(new String[]{".png"}, "image/png"),
    APPLICATION_JS(new String[]{".js"}, "application/x-javascript"),
    APPLICATION_XHTML(new String[]{".xhtml", ".xhtm"}, "application/xhtml+xml"),
    APPLICATION_XML(new String[]{".xml"}, "application/xml"),
    APPLICATION_ATOM_XML(new String[]{".xml"}, "application/atom+xml"),
    APPLICATION_JSON(new String[]{".json"}, "application/json"),
    APPLICATION_PDF(new String[]{".pdf"}, "application/pdf"),
    APPLICATION_WORD(new String[]{".doc", ".docx"}, "application/msword"),
    APPLICATION_XLS(new String[]{".xls"}, "application/vnd.ms-excel"),
    APPLICATION_PPT(new String[]{".ppt", ".pptx"}, "application/vnd.ms-powerpoint"),
    APPLICATION_APK(new String[]{".apk"}, "application/vnd.android.package-archive"),
    APPLICATION_IPA(new String[]{".ipa"}, "application/vnd.iphone"),
    APPLICATION_ICO(new String[]{".ico"}, "application/x-ico"),
    APPLICATION_TORRENT(new String[]{".torrent"}, "application/x-bittorrent"),
    APPLICATION_OCTET_STREAM(new String[]{".*"}, "application/octet-stream"),
    MULTIPART_FORM_DATA(new String[0], "multipart/form-data"),
    VIDEO_AVI(new String[]{".avi"}, "video/avi"),
    VIDEO_MP4(new String[]{".mp4"}, "video/mpeg4"),
    AUDIO_WAV(new String[]{".wav"}, "audio/wav"),
    AUDIO_MID(new String[]{".midi"}, "audio/mid"),
    AUDIO_MP3(new String[]{".mp3"}, "audio/mp3"),
    // todo
    ;

    private final String[] suffixes;

    private final String contentType;

    ContentType(String[] suffixes, String contentType) {
        this.suffixes = suffixes;
        this.contentType = contentType;
    }

    @NotNull
    public String[] getSuffixes() {
        return suffixes;
    }

    @NotNull
    public String getContentType() {
        return contentType;
    }

}