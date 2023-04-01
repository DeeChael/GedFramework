package net.deechael.framework;

import net.deechael.framework.response.ResponseListener;
import net.deechael.framework.ssl.SSLProvider;
import net.deechael.useless.objs.DuObj;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiConsumer;

public class WebsiteBuilder {

    private final int port;
    private File favicon;
    private SSLProvider sslProvider;

    private final List<ResponseListener> responseListeners = new ArrayList<>();

    private final Map<Path, Invoker> paths = new HashMap<>();
    private final Map<Path, Invoker> gets = new HashMap<>();
    private final Map<Path, Invoker> posts = new HashMap<>();
    private final Map<Path, Invoker> heads = new HashMap<>();
    private final Map<Path, Invoker> options = new HashMap<>();
    private final Map<Path, Invoker> patches = new HashMap<>();
    private final Map<Path, Invoker> deletes = new HashMap<>();
    private final Map<Path, Invoker> traces = new HashMap<>();
    private final Map<Path, Invoker> connects = new HashMap<>();
    private Invoker unknown = null;

    private WebsiteBuilder(int port) {
        this.port = port;
    }

    public int port() {
        return port;
    }

    public DuObj<Method, ?> unknown() {
        return new DuObj<>(Invoker.METHOD, this.unknown);
    }

    public DuObj<Method, Map<HttpMethod, Map<Path, ?>>> paths() {
        Map<HttpMethod, Map<Path, ?>> map = new HashMap<>();
        map.put(HttpMethod.GET, gets);
        map.put(HttpMethod.POST, posts);
        map.put(HttpMethod.HEAD, heads);
        map.put(HttpMethod.OPTIONS, options);
        map.put(HttpMethod.PATCH, patches);
        map.put(HttpMethod.DELETE, deletes);
        map.put(HttpMethod.TRACE, traces);
        map.put(HttpMethod.CONNECT, connects);
        return new DuObj<>(Invoker.METHOD, map);
    }


    public Map<Path, ?> ignoredPaths() {
        return this.paths;
    }

    public Collection<? extends ResponseListener> responseListeners() {
        return this.responseListeners;
    }

    public SSLProvider sslProvider() {
        return this.sslProvider;
    }

    public File favicon() {
        if (this.favicon != null && this.favicon.exists() && this.favicon.isFile())
            return this.favicon;
        return null;
    }

    public WebsiteBuilder unknown(BiConsumer<Request, Responder> consumer) {
        this.unknown = new Invoker(consumer);
        return this;
    }

    public WebsiteBuilder head(String path, boolean ignoreCaps, boolean regex, BiConsumer<Request, Responder> consumer) {
        this.heads.put(
                buildPath(path, ignoreCaps, regex, consumer),
                new Invoker(consumer)
        );
        return this;
    }

    public WebsiteBuilder options(String path, boolean ignoreCaps, boolean regex, BiConsumer<Request, Responder> consumer) {
        this.options.put(
                buildPath(path, ignoreCaps, regex, consumer),
                new Invoker(consumer)
        );
        return this;
    }

    public WebsiteBuilder delete(String path, boolean ignoreCaps, boolean regex, BiConsumer<Request, Responder> consumer) {
        this.deletes.put(
                buildPath(path, ignoreCaps, regex, consumer),
                new Invoker(consumer)
        );
        return this;
    }

    public WebsiteBuilder trace(String path, boolean ignoreCaps, boolean regex, BiConsumer<Request, Responder> consumer) {
        this.traces.put(
                buildPath(path, ignoreCaps, regex, consumer),
                new Invoker(consumer)
        );
        return this;
    }

    public WebsiteBuilder connect(String path, boolean ignoreCaps, boolean regex, BiConsumer<Request, Responder> consumer) {
        this.connects.put(
                buildPath(path, ignoreCaps, regex, consumer),
                new Invoker(consumer)
        );
        return this;
    }

    public WebsiteBuilder patch(String path, boolean ignoreCaps, boolean regex, BiConsumer<Request, Responder> consumer) {
        this.patches.put(
                buildPath(path, ignoreCaps, regex, consumer),
                new Invoker(consumer)
        );
        return this;
    }

    public WebsiteBuilder post(String path, boolean ignoreCaps, boolean regex, BiConsumer<Request, Responder> consumer) {
        this.posts.put(
                buildPath(path, ignoreCaps, regex, consumer),
                new Invoker(consumer)
        );
        return this;
    }

    public WebsiteBuilder get(String path, boolean ignoreCaps, boolean regex, BiConsumer<Request, Responder> consumer) {
        this.gets.put(
                buildPath(path, ignoreCaps, regex, consumer),
                new Invoker(consumer)
        );
        return this;
    }

    public WebsiteBuilder path(String path, boolean ignoreCaps, boolean regex, BiConsumer<Request, Responder> consumer) {
        this.paths.put(
                buildPath(path, ignoreCaps, regex, consumer),
                new Invoker(consumer)
        );
        return this;
    }

    public WebsiteBuilder head(String path, BiConsumer<Request, Responder> consumer) {
        return this.head(path, false, false, consumer);
    }

    public WebsiteBuilder options(String path, BiConsumer<Request, Responder> consumer) {
        return this.options(path, false, false, consumer);
    }

    public WebsiteBuilder delete(String path, BiConsumer<Request, Responder> consumer) {
        return this.delete(path, false, false, consumer);
    }

    public WebsiteBuilder trace(String path, BiConsumer<Request, Responder> consumer) {
        return this.trace(path, false, false, consumer);
    }

    public WebsiteBuilder connect(String path, BiConsumer<Request, Responder> consumer) {
        return this.connect(path, false, false, consumer);
    }

    public WebsiteBuilder patch(String path, BiConsumer<Request, Responder> consumer) {
        return this.patch(path, false, false, consumer);
    }

    public WebsiteBuilder post(String path, BiConsumer<Request, Responder> consumer) {
        return this.post(path, false, false, consumer);
    }

    public WebsiteBuilder get(String path, BiConsumer<Request, Responder> consumer) {
        return this.get(path, false, false, consumer);
    }

    public WebsiteBuilder path(String path, BiConsumer<Request, Responder> consumer) {
        return this.path(path, false, false, consumer);
    }

    public WebsiteBuilder responseListener(ResponseListener listener) {
        this.responseListeners.add(listener);
        return this;
    }

    public WebsiteBuilder sslProvider(SSLProvider sslProvider) {
        this.sslProvider = sslProvider;
        return this;
    }

    public WebsiteBuilder favicon(File favicon) {
        this.favicon = favicon;
        return this;
    }

    public WebsiteBuilder favicon(String favicon) {
        this.favicon = new File(favicon);
        return this;
    }

    public GedWebsite build() {
        return new GedWebsite(this);
    }

    public static WebsiteBuilder of(int port) {
        return new WebsiteBuilder(port);
    }

    private Path buildPath(String path, boolean ignoreCaps, boolean regex, BiConsumer<Request, Responder> consumer) {
        return new Path() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return Path.class;
            }

            @Override
            public @NotNull String value() {
                return path;
            }

            @Override
            public boolean ignoreCaps() {
                return ignoreCaps;
            }

            @Override
            public boolean regex() {
                return regex;
            }
        };
    }

    private static class Invoker {

        private final BiConsumer<Request, Responder> consumer;

        public Invoker(BiConsumer<Request, Responder> consumer) {
            this.consumer = consumer;
        }

        public void invoke(Request request, Responder responder) {
            this.consumer.accept(request, responder);
        }

        private final static Method METHOD;

        static {
            try {
                METHOD = Invoker.class.getMethod("invoke", Request.class, Responder.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

    }

}
