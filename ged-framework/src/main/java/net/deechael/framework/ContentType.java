package net.deechael.framework;

public enum ContentType {

    TEXT_HTML(".html", "text/html"),
    TEXT_PLAIN("", "text/plain"),
    TEXT_JAVASCRIPT(".js", "text/javascript"),
    TEXT_CSS(".css", "text/css")
    // todo
    ;


    private final String suffix;

    private final String contentType;

    ContentType(String suffix, String contentType) {
        this.suffix = suffix;
        this.contentType = contentType;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getContentType() {
        return contentType;
    }

}