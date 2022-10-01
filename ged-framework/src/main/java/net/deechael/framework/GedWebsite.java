package net.deechael.framework;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import net.deechael.framework.item.Item;
import net.deechael.framework.item.ItemArgument;
import net.deechael.framework.item.ItemArgumentType;
import net.deechael.framework.item.ItemConstructor;
import net.deechael.framework.response.PreparedResponse;
import net.deechael.framework.response.ResponseListener;
import net.deechael.framework.ssl.SSLProvider;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.lang.reflect.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static io.netty.handler.codec.http.HttpHeaderNames.*;

public class GedWebsite {

    private final Logger logger;
    private final Class<?> pageHandler;

    private boolean started = false;
    private final Map<Method, Path[]> method_options = new HashMap<>();
    private final Map<Method, Path[]> method_post = new HashMap<>();
    private final Map<Method, Path[]> method_head = new HashMap<>();
    private final Map<Method, Path[]> method_get = new HashMap<>();
    private final Map<Method, Path[]> method_put = new HashMap<>();
    private final Map<Method, Path[]> method_patch = new HashMap<>();
    private final Map<Method, Path[]> method_delete = new HashMap<>();
    private final Map<Method, Path[]> method_trace = new HashMap<>();
    private final Map<Method, Path[]> method_connect = new HashMap<>();
    private final Map<Method, Path[]> method_ignore = new HashMap<>();

    private Method unknownEntrance = null;

    private final Map<Method, Host[]> unknowns = new HashMap<>();

    private final List<ResponseListener> responseListeners = new ArrayList<>();

    private final Website[] websites;

    public <T> GedWebsite(Class<T> pageHandler) {
        this.logger = LoggerFactory.getLogger(pageHandler);
        this.initLogger();
        Websites websites = pageHandler.getAnnotation(Websites.class);
        Website website = pageHandler.getAnnotation(Website.class);
        if (websites == null && website == null) {
            logger.error("Page handler must be annotated with Website.class", new RuntimeException("Website annotation was missed"));
        }
        List<Website> websiteList = new ArrayList<>();
        websiteList.addAll(websites != null ? Arrays.asList(websites.value()) : Collections.singleton(website));
        if (websiteList.size() == 0)
            logger.error("No website!", new RuntimeException("There is no website found!"));
        for (Website web : websiteList) {
            if (web.ssl()) {
                if (web.sslProvider() == SSLProvider.class) {
                    websiteList.remove(web);
                } else {
                    try {
                        web.sslProvider().getDeclaredConstructor();
                    } catch (NoSuchMethodException ignored) {
                        websiteList.remove(web);
                    }
                }
            }
        }
        this.websites = websiteList.toArray(new Website[0]);
        this.pageHandler = pageHandler;
        for (Method method : pageHandler.getDeclaredMethods()) {
            if (!Modifier.isStatic(method.getModifiers()))
                continue;
            method.setAccessible(true);
            Paths paths = method.getAnnotation(Paths.class);
            Parameter[] parameters = method.getParameters();
            if (parameters.length < 2)
                continue;
            if (parameters[0].getType() != Request.class)
                continue;
            if (parameters[1].getType() != Responder.class)
                continue;
            List<Path> pathList = new ArrayList<>();
            if (paths != null) {
                if (method.getAnnotation(UnknownPath.class) != null) {
                    if (this.unknownEntrance == null) {
                        if (parameters.length == 2) {
                            if (parameters[0].getType() == Request.class) {
                                if (parameters[1].getType() == Responder.class) {
                                    this.unknownEntrance = method;
                                }
                            }
                        }
                    } else {
                        Hosts hostsAnnotation = method.getAnnotation(Hosts.class);
                        Host hostAnnotation = method.getAnnotation(Host.class);
                        Host[] hosts = hostsAnnotation != null ? hostsAnnotation.value() : hostAnnotation != null ? new Host[]{hostAnnotation} : new Host[0];
                        this.unknowns.put(method, hosts);
                    }
                }
                pathList.addAll(Arrays.asList(paths.value()));
            } else {
                Path path = method.getAnnotation(Path.class);
                if (path == null) {
                    if (method.getAnnotation(UnknownPath.class) == null)
                        continue;
                    if (parameters.length != 2)
                        continue;
                    if (parameters[0].getType() != Request.class)
                        continue;
                    if (parameters[1].getType() != Responder.class)
                        continue;
                    if (this.unknownEntrance != null) {
                        Hosts hostsAnnotation = method.getAnnotation(Hosts.class);
                        Host hostAnnotation = method.getAnnotation(Host.class);
                        Host[] hosts = hostsAnnotation != null ? hostsAnnotation.value() : hostAnnotation != null ? new Host[]{hostAnnotation} : new Host[0];
                        if (hosts.length != 0) {
                            this.unknowns.put(method, hosts);
                        }
                    } else {
                        this.unknownEntrance = method;
                    }
                    continue;
                } else {
                    if (method.getAnnotation(UnknownPath.class) != null) {
                        if (this.unknownEntrance == null) {
                            if (parameters.length == 2) {
                                if (parameters[0].getType() == Request.class) {
                                    if (parameters[1].getType() == Responder.class) {
                                        this.unknownEntrance = method;
                                    }
                                }
                            }
                        } else {
                            Hosts hostsAnnotation = method.getAnnotation(Hosts.class);
                            Host hostAnnotation = method.getAnnotation(Host.class);
                            Host[] hosts = hostsAnnotation != null ? hostsAnnotation.value() : hostAnnotation != null ? new Host[]{hostAnnotation} : new Host[0];
                            if (hosts.length != 0) {
                                this.unknowns.put(method, hosts);
                            }
                        }
                    }
                    boolean shouldContinue = false;
                    for (int i = 2; i < parameters.length; i++) {
                        Argument arg = parameters[i].getAnnotation(Argument.class);
                        Header head = parameters[i].getAnnotation(Header.class);
                        if (arg == null && head == null) {
                            shouldContinue = true;
                            break;
                        }
                        if (arg != null && head != null) {
                            shouldContinue = true;
                            break;
                        }
                        if (arg != null) {
                            if (arg.type() == ArgumentType.ITEM) {
                                Class<?> itemType = parameters[i].getType();
                                if (itemType.getAnnotation(Item.class) == null) {
                                    shouldContinue = true;
                                    break;
                                }
                                shouldContinue = true;
                                for (Constructor<?> constructor : itemType.getDeclaredConstructors()) {
                                    if (constructor.getAnnotation(ItemConstructor.class) == null)
                                        continue;
                                    boolean oughtToContinue = false;
                                    for (Parameter constructorParameter : constructor.getParameters()) {
                                        ItemArgument itemArgument = constructorParameter.getAnnotation(ItemArgument.class);
                                        if (itemArgument == null) {
                                            oughtToContinue = true;
                                            break;
                                        }
                                        if (itemArgument.type().getTypeClass() != constructorParameter.getType()) {
                                            oughtToContinue = true;
                                            break;
                                        }
                                    }
                                    if (oughtToContinue)
                                        continue;
                                    shouldContinue = false;
                                    break;
                                }
                                if (shouldContinue)
                                    break;
                            } else {
                                if (parameters[i].getType() != arg.type().getTypeClass()) {
                                    shouldContinue = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (shouldContinue)
                        continue;
                }
                pathList.add(path);
            }
            if (pathList.isEmpty())
                continue;
            RequestMethod requestMethod = method.getAnnotation(RequestMethod.class);
            if (requestMethod != null) {
                for (HttpMethod mt : requestMethod.value()) {
                    getMethodsWithoutIgnore(mt).put(method, pathList.toArray(new Path[0]));
                }
            } else {
                method_ignore.put(method, pathList.toArray(new Path[0]));
            }
        }
    }

    public void addLastListener(ResponseListener listener) {
        this.responseListeners.add(listener);
    }

    public final void start() throws InterruptedException {
        if (!started) {
            started = true;
            for (Website website : websites) {
                if (website.ssl()) {
                    try {
                        SSLProvider provider = website.sslProvider().newInstance();
                        runInNewThread(() -> {
                            try {
                                startHttps(provider.generate(), website.port());
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    runInNewThread(() -> {
                        try {
                            startHttp(website.port());
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            }
        }
    }

    private void runInNewThread(Runnable runnable) {
        new Thread(runnable).start();
    }

    private void startHttp(int port) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(@NotNull SocketChannel ch) {
                            ch.pipeline().addLast("http-decoder", new HttpRequestDecoder());
                            ch.pipeline().addLast("http-aggregator",
                                    new HttpObjectAggregator(65536));
                            ch.pipeline()
                                    .addLast("http-encoder", new HttpResponseEncoder());
                            ch.pipeline()
                                    .addLast("http-chunked", new ChunkedWriteHandler());
                            ch.pipeline().addLast(
                                    new Listener(GedWebsite.this));
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture f = b.bind(port).sync();

            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    private void startHttps(SSLContext context, int port) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(@NotNull SocketChannel ch) {
                            SSLEngine sslEngine = context.createSSLEngine();
                            sslEngine.setUseClientMode(false);
                            ch.pipeline().addLast(new SslHandler(sslEngine));
                            ch.pipeline().addLast("http-decoder", new HttpRequestDecoder());
                            ch.pipeline().addLast("http-aggregator",
                                    new HttpObjectAggregator(65536));
                            ch.pipeline()
                                    .addLast("http-encoder", new HttpResponseEncoder());
                            ch.pipeline()
                                    .addLast("http-chunked", new ChunkedWriteHandler());
                            ch.pipeline().addLast(
                                    new Listener(GedWebsite.this));
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture f = b.bind(port).sync();

            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    private class Listener extends ChannelInboundHandlerAdapter {

        private final GedWebsite website;

        public Listener(GedWebsite website) {
            this.website = website;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
            if (msg instanceof FullHttpRequest) {
                FullHttpRequest request = (FullHttpRequest) msg;
                request.headers().get(HOST);
                String urlRaw = request.uri();
                String url = urlRaw;
                if (url.equalsIgnoreCase("favicon.ico")) {
                    ctx.close();
                    return;
                }
                if (url.equalsIgnoreCase("/favicon.ico")) {
                    ctx.close();
                    return;
                }
                String host = request.headers().get(HOST);
                int port = -1;
                if (host.contains(":")) {
                    String[] split = host.split(":");
                    host = split[0];
                    port = Integer.parseInt(split[1]);
                }
                String[] paths = new String[]{};
                Map<String, String> args = new HashMap<>();
                if (url.contains("?")) {
                    String[] split = url.split("\\?");
                    url = split[0];
                    String arg = split[1];
                    if (arg.contains("&")) {
                        for (String aaaaa : arg.split("&")) {
                            if (aaaaa.contains("=")) {
                                args.put(aaaaa.split("=")[0], aaaaa.split("=")[1]);
                            }
                        }
                    } else if (arg.contains("=")) {
                        args.put(arg.split("=")[0], arg.split("=")[1]);
                    }
                }
                if (!url.equals("/")) {
                    if (url.startsWith("/")) url = url.substring(1);
                    if (url.endsWith("/")) url = url.substring(0, url.length() - 1);
                    if (url.contains("/")) {
                        paths = url.split("/");
                    } else {
                        paths = new String[]{url};
                    }
                }
                Map<String, String> headers = headerMap(request.headers());
                List<Cookie> cookies = new ArrayList<>();
                if (request.headers().contains(COOKIE)) {
                    cookies = ServerCookieDecoder.STRICT.decodeAll(request.headers().get(COOKIE));
                }
                HttpMethod httpMethod = HttpMethod.valueOf(request.method().name());

                Method result = null;
                for (Map.Entry<Method, Path[]> entry : website.methoder(httpMethod).entrySet()) {
                    Method method = entry.getKey();
                    Hosts hostsAnnotation = method.getAnnotation(Hosts.class);
                    Host hostAnnotation = method.getAnnotation(Host.class);
                    Host[] hosts;
                    if (hostsAnnotation != null) {
                        hosts = hostsAnnotation.value();
                    } else if (hostAnnotation != null) {
                        hosts = new Host[]{};
                    } else {
                        hosts = new Host[0];
                    }
                    boolean shouldContinue = false;
                    if (hosts.length > 0) {
                        shouldContinue = true;
                        for (Host hst : hosts) {
                            if (hst.regex()) {
                                if (Pattern.matches(hst.value(), host)) {
                                    if (hst.port() != -1 && port != -1) {
                                        if (hst.port() != port)
                                            continue;
                                    }
                                    shouldContinue = false;
                                    break;
                                }
                            } else {
                                if (hst.value().equals(host)) {
                                    if (hst.port() != -1 && port != -1) {
                                        if (hst.port() != port)
                                            continue;
                                    }
                                    shouldContinue = false;
                                    break;
                                }
                            }
                        }
                    }
                    if (shouldContinue)
                        continue;
                    shouldContinue = true;
                    for (Path ptAnno : entry.getValue()) {
                        String pth = ptAnno.value();

                        if (ptAnno.regex()) {
                            if (Pattern.matches(ptAnno.value(), urlRaw))
                                shouldContinue = false;
                        } else {
                            String[] pths = new String[]{};
                            if (!pth.equals("/")) {
                                if (pth.startsWith("/"))
                                    pth = pth.substring(1);
                                if (pth.endsWith("/"))
                                    pth = pth.substring(0, pth.length() - 1);
                                pths = pth.contains("/") ? pth.split("/") : new String[]{pth};
                            }
                            if (pths.length != paths.length)
                                continue;
                            boolean shouldBreak = true;
                            for (int i = 0; i < paths.length; i++) {
                                String serverPath = pths[i];
                                String clientPath = paths[i];
                                if (serverPath.equals("%s"))
                                    continue;
                                if (serverPath.equals("%i") && Pattern.matches("-?\\d+", clientPath))
                                    continue;
                                if (serverPath.equals("%d") && Pattern.matches("-?\\d+(\\.?\\d+)?", clientPath))
                                    continue;
                                if (ptAnno.ignoreCaps() && serverPath.equalsIgnoreCase(clientPath))
                                    break;
                                if (clientPath.equals(serverPath))
                                    break;
                                shouldBreak = false;
                            }
                            if (shouldBreak) {
                                shouldContinue = false;
                                break;
                            }
                        }
                    }
                    if (shouldContinue)
                        continue;
                    result = method;
                    break;
                }

                if (result == null)
                    result = findUnknown(host, port);

                if (result == null) {
                    ctx.close();
                    return;
                }

                byte[] bodyBytes;
                ByteBuf bodyContent = request.content();
                bodyBytes = bodyContent.isReadable() ? bodyContent.copy().array() : new byte[0];
                Request req = new Request(paths, args, httpMethod, ctx.channel().remoteAddress().toString(), request.headers().get(HOST) + request.uri(), host, port, headers, cookies, bodyBytes);
                Responder responder = new Responder();
                List<Object> arguments = new ArrayList<>();
                arguments.add(req);
                arguments.add(responder);
                Parameter[] parameters = result.getParameters();
                int doWhat = 0;
                for (int i = 2; i < parameters.length; i++) {
                    Argument arg = parameters[i].getAnnotation(Argument.class);
                    if (arg != null) {
                        if (arg.type() == ArgumentType.ITEM) {
                            Class<?> itemClass = parameters[i].getType();
                            List<Map.Entry<Constructor<?>, ItemConstructor>> itemConstructors = new ArrayList<>();
                            for (Constructor<?> itemConstructor : itemClass.getDeclaredConstructors()) {
                                ItemConstructor itemConstructorAnno = itemConstructor.getAnnotation(ItemConstructor.class);
                                if (itemConstructorAnno != null)
                                    itemConstructors.add(new AbstractMap.SimpleEntry<>(itemConstructor, itemConstructorAnno));
                            }
                            itemConstructors.sort((a, b) -> Integer.compare(b.getValue().priority(), a.getValue().priority()));
                            Constructor<?> itemConstructor = null;
                            for (Constructor<?> constructor : itemConstructors.stream().map(Map.Entry::getKey).collect(Collectors.toList())) {
                                boolean constructorBreak = true;
                                for (Parameter itemParameter : constructor.getParameters()) {
                                    ItemArgument itemArgument = itemParameter.getAnnotation(ItemArgument.class);
                                    if (itemArgument.require()) {
                                        if (itemArgument.type() == ItemArgumentType.HEADER) {
                                            if (!headers.containsKey(itemArgument.name())) {
                                                constructorBreak = false;
                                                break;
                                            }
                                        } else {
                                            if (!args.containsKey(itemArgument.name())) {
                                                constructorBreak = false;
                                                break;
                                            }
                                        }
                                    }
                                }
                                if (constructorBreak) {
                                    itemConstructor = constructor;
                                    break;
                                }
                            }
                            if (itemConstructor == null) {
                                if (arg.requirement() == DataRequirement.REQUIRED_CLOSE)
                                    doWhat = 1;
                                if (arg.requirement() == DataRequirement.REQUIRED_GO_TO_UNKNOWN_PATH)
                                    doWhat = 2;
                                if (doWhat != 0)
                                    break;
                                arguments.add(arg.type().getEmptyValue());
                                continue;
                            }
                            itemConstructor.setAccessible(true);
                            List<Object> itemParameters = new ArrayList<>();
                            for (Parameter itemParameter : itemConstructor.getParameters()) {
                                ItemArgument itemArgument = itemParameter.getAnnotation(ItemArgument.class);
                                if (itemArgument.type() == ItemArgumentType.HEADER) {
                                    itemParameters.add(headers.get(itemArgument.name()));
                                } else {
                                    if (!args.containsKey(itemArgument.name())) {
                                        itemParameters.add(itemArgument.type().getEmptyValue());
                                    } else {
                                        itemParameters.add(itemArgument.type().parse(args.get(itemArgument.name())));
                                    }
                                }
                            }
                            arguments.add(itemConstructor.newInstance(itemParameters.toArray(new Object[0])));
                        } else {
                            if (args.containsKey(arg.value())) {
                                arguments.add(arg.type().parse(args.get(arg.value())));
                            } else {
                                if (arg.requirement() == DataRequirement.REQUIRED_CLOSE)
                                    doWhat = 1;
                                if (arg.requirement() == DataRequirement.REQUIRED_GO_TO_UNKNOWN_PATH)
                                    doWhat = 2;
                                if (doWhat != 0)
                                    break;
                                arguments.add(arg.type().getEmptyValue());
                            }
                        }
                    } else {
                        Header head = parameters[i].getAnnotation(Header.class);
                        String headerValue = headers.get(head.value().toLowerCase());
                        if (headerValue != null) {
                            arguments.add(headerValue);
                        } else {
                            if (head.requirement() == DataRequirement.REQUIRED_CLOSE)
                                doWhat = 1;
                            if (head.requirement() == DataRequirement.REQUIRED_GO_TO_UNKNOWN_PATH)
                                doWhat = 2;
                            if (doWhat != 0)
                                break;
                            arguments.add(null);
                        }
                    }
                }
                if (doWhat == 1) {
                    ctx.close();
                    return;
                } else if (doWhat == 2) {
                    result = findUnknown(host, port);
                    result.invoke(request, responder);
                } else {
                    result.invoke(null, arguments.toArray());
                    PreparedResponse preparedResponse = new PreparedResponse(website.pageHandler, result, req, responder);
                    for (ResponseListener responseListener : website.responseListeners) {
                        if (!responseListener.ignoreCancelled() && preparedResponse.isCancelled())
                            continue;
                        responseListener.onResponse(preparedResponse);
                    }
                }

                if (responder.getContent() == null) {
                    ctx.close();
                    return;
                }

                FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(responder.getStatus().getCode()), Unpooled.copiedBuffer(responder.getContent().getBytes()));
                response.headers().set(SET_COOKIE, ServerCookieEncoder.STRICT.encode(responder.getCookies()));
                response.headers().set(CONTENT_TYPE, responder.getContentType().getContentType());
                response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
                response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                ctx.writeAndFlush(response);
            }
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) {
            ctx.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            ctx.close();
        }

        private Method findUnknown(String host, int port) {
            for (Map.Entry<Method, Host[]> entry : website.unknowns.entrySet()) {
                boolean shouldContinue = true;
                for (Host hst : entry.getValue()) {
                    if (hst.regex()) {
                        if (Pattern.matches(hst.value(), host)) {
                            if (hst.port() != -1 && port != -1) {
                                if (hst.port() != port)
                                    continue;
                            }
                            shouldContinue = false;
                            break;
                        }
                    } else {
                        if (hst.value().equals(host)) {
                            if (hst.port() != -1 && port != -1) {
                                if (hst.port() != port)
                                    continue;
                            }
                            shouldContinue = false;
                            break;
                        }
                    }
                }
                if (shouldContinue)
                    continue;
                return entry.getKey();
            }
            return unknownEntrance;
        }

    }

    private boolean isInt(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException ignore) {
            return false;
        }
    }

    private boolean in(HttpMethod[] methods, HttpMethod method) {
        return Arrays.asList(methods).contains(method);
    }

    private void initLogger() {
        ch.qos.logback.classic.Logger lgr = (ch.qos.logback.classic.Logger) logger;
        LoggerContext loggerContext = lgr.getLoggerContext();
        // we are not interested in auto-configuration
        loggerContext.reset();

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(loggerContext);
        encoder.setPattern("[%date] [%logger{32}] [%thread] [%level] %message%n");
        encoder.start();

        ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<>();
        appender.setContext(loggerContext);
        appender.setEncoder(encoder);
        appender.start();

        lgr.addAppender(appender);
        lgr.setLevel(Level.INFO);
    }

    private Map<String, String> headerMap(HttpHeaders headers) {
        Map<String, String> mapHeaders = new HashMap<>();
        for (Map.Entry<String, String> entry : headers.entries()) {
            mapHeaders.put(entry.getKey().toLowerCase(), entry.getValue());
        }
        return mapHeaders;
    }

    private <T> boolean in(T[] ts, T t) {
        return Arrays.asList(ts).contains(t);
    }

    private Map<Method, Path[]> getMethodsWithoutIgnore(HttpMethod method) {
        switch (method) {
            case OPTIONS:
                return method_options;
            case POST:
                return method_post;
            case HEAD:
                return method_head;
            case GET:
                return method_get;
            case PUT:
                return method_put;
            case PATCH:
                return method_patch;
            case DELETE:
                return method_delete;
            case TRACE:
                return method_trace;
            case CONNECT:
                return method_connect;
        }
        throw new RuntimeException("Unknown method");
    }

    private Map<Method, Path[]> getMethodsWithIgnore(HttpMethod method) {
        switch (method) {
            case OPTIONS:
                return method_options;
            case POST:
                return method_post;
            case HEAD:
                return method_head;
            case GET:
                return method_get;
            case PUT:
                return method_put;
            case PATCH:
                return method_patch;
            case DELETE:
                return method_delete;
            case TRACE:
                return method_trace;
            case CONNECT:
                return method_connect;
        }
        return method_ignore;
    }

    private Map<Method, Path[]> methoder(HttpMethod method) {
        Map<Method, Path[]> entries = new HashMap<>();
        entries.putAll(getMethodsWithoutIgnore(method));
        entries.putAll(method_ignore);
        return entries;
    }

}
