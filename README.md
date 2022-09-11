# GedFramework
## A light-weight website framework
In developing

## Import
maven
```xml
<dependency>
    <groupId>net.deechael</groupId>
    <artifactId>ged-framework</artifactId>
    <version>1.00.0</version>
</dependency>
```

gradle
```groovy
dependencies { 
    //...
    implementation 'net.deechael:ged-framework:1.00.0'
}
```

## Quick start
```java
package net.deechael.framework.test;

import io.netty.handler.codec.http.cookie.DefaultCookie;
import net.deechael.framework.*;
import net.deechael.framework.content.FileContent;
import net.deechael.framework.content.StringContent;

@Website(port = 8080)
public class ExampleWebsite {

    public static void main(String[] args) throws InterruptedException {
        GedWebsite website = new GedWebsite(ExampleWebsite.class);
        // Because I don't know how to code an AnnotationProcessor
        // If you could, please help me and make a pull request!
        website.start();
    }

    @RequestMethod(HttpMethod.GET)
    @Path("/test")
    public static void test(Request request, Responder responder) {
        responder.addCookie(new DefaultCookie("ged_token", "114514"));
        responder.setContent(new StringContent("hello, world! your address is " + request.getUserAddress()));
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
