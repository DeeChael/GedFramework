package net.deechael.framework.test;

import io.netty.handler.codec.http.cookie.DefaultCookie;
import net.deechael.framework.*;
import net.deechael.framework.content.FileContent;
import net.deechael.framework.content.StringContent;

@Website(port = 8080)
public class ExampleWebsite {

    public static void main(String[] args) throws InterruptedException {
        GedWebsite website = new GedWebsite(ExampleWebsite.class);
        website.start();
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
