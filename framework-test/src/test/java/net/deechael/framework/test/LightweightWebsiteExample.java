package net.deechael.framework.test;

import net.deechael.framework.WebsiteBuilder;
import net.deechael.framework.content.Content;

public class LightweightWebsiteExample {

    public static void main(String[] args) throws InterruptedException {
        WebsiteBuilder.of(8080)
                .unknown(((request, responder) -> {
                    responder.setContent(Content.text("Nothing there"));
                }))
                .path("/", false, false, ((request, responder) -> {
                    responder.setContent(Content.text("Hello, world!"));
                }))
                .build()
                .start();
    }

}
