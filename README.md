# GedFramework
## A light-weight website framework
In developing

## Import
maven
```xml
<dependency>
    <groupId>net.deechael</groupId>
    <artifactId>ged-framework</artifactId>
    <version>1.05.0</version>
</dependency>
```

gradle
```groovy
dependencies { 
    //...
    implementation 'net.deechael:ged-framework:1.05.0'
}
```

## Quick start
Lightweight
```java
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

```
Advanced
```java
package net.deechael.framework.test;

import net.deechael.framework.*;
import net.deechael.framework.content.FileContent;
import net.deechael.framework.content.StringContent;
import net.deechael.framework.item.Item;
import net.deechael.framework.item.ItemArgument;
import net.deechael.framework.item.ItemArgumentType;
import net.deechael.framework.item.ItemConstructor;

@Websites({
        @Website(port = 8080),
        @Website(port = 4430, ssl = true, sslProvider = PemSSLProvider.class)
})
public class ExampleWebsite {

    public static void main(String[] args) throws InterruptedException {
        try {
            GedWebsite website = new GedWebsite(ExampleWebsite.class);
            new JksSSLProvider();
            website.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Item
    public static class User {

        private String userAgent;

        @ItemConstructor
        public User(@ItemArgument(type = ItemArgumentType.HEADER, name = "user-agent") String userAgent) {
            this.userAgent = userAgent;
        }

        public String getUserAgent() {
            return userAgent;
        }

    }

    @Path("/itemtest")
    public static void itemTest(Request request,
                                Responder responder,
                                @Argument(value = "", type = ArgumentType.ITEM) User user) {
        responder.setContent(new StringContent("Welcome!\n" +
                "Your user agent is:\n" +
                user.getUserAgent()));
    }

    @RequestMethod(HttpMethod.GET)
    @Path("/header-testing")
    public static void headerTest(Request request, Responder responder, @Header("User-Agent") String contentType) {
        responder.setContent(new StringContent("Your content type is " + contentType));
    }

    @RequestMethod(HttpMethod.GET)
    @Path("/test")
    public static void test(Request request, Responder responder) {
        responder.setContent(new StringContent("hello, world! your address is " + request.getUserAddress()));
    }

    @RequestMethod(HttpMethod.GET)
    @Paths({
            @Path("regexer"),
            @Path(value = "/?regex/(test|fuck|blabla)", regex = true)
    })
    public static void regexTest(Request request, Responder responder) {
        responder.setContent(new StringContent("Regex test success!"));
    }

    @RequestMethod(HttpMethod.GET)
    @Path(value = "/list", ignoreCaps = true)
    public static void list(Request request, Responder responder,
                            @Argument(value = "username", type = ArgumentType.STRING) String username,
                            @Argument(value = "password", type = ArgumentType.STRING) String password) {
        responder.setContent(new StringContent("hello, world!\nfull url: " + request.getFullUrl() + "\nusername: " + username + "\npassword: " + password));
    }

    @Path("/")
    public static void main(Request request, Responder responder) {
        responder.setContentType(ContentType.TEXT_HTML);
        responder.setContent(new FileContent("index.html"));
    }

    @Path("/js/%s")
    public static void js(Request request, Responder responder) {
        responder.setContentType(ContentType.TEXT_JAVASCRIPT);
        responder.setContent(new FileContent("js/" + request.getPaths()[1]));
    }

    @Path("/css/%s")
    public static void css(Request request, Responder responder) {
        responder.setContentType(ContentType.TEXT_CSS);
        responder.setContent(new FileContent("css/" + request.getPaths()[1]));
    }

    @UnknownPath
    public static void unknown(Request request, Responder responder) {
        responder.setContent(new StringContent("All will redirect to me!"));
    }


}

```
