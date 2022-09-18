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
import io.netty.handler.stream.ChunkedWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Pattern;

import static io.netty.handler.codec.http.HttpHeaderNames.*;

public class GedWebsite {

    private final Logger logger;
    private final int port;
    private final Class<?> pageHandler;

    private boolean started = false;

    private final Map<Method, Path[]> methods = new HashMap<>();

    private Method unknownEntrance = null;

    public <T> GedWebsite(Class<T> pageHandler) {
        this.logger = LoggerFactory.getLogger(pageHandler);
        this.initLogger();
        Website website = pageHandler.getAnnotation(Website.class);
        if (website == null) {
            logger.error("Page handler must be annotated with Website.class", new RuntimeException("Website annotation was missed"));
        }
        this.port = website.port();
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
                    this.unknownEntrance = method;
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
                        }
                    }
                    boolean shouldContinue = false;
                    for (int i = 2; i < parameters.length; i++) {
                        Argument arg = parameters[i].getAnnotation(Argument.class);
                        if (arg == null) {
                            shouldContinue = true;
                            break;
                        }
                        if (parameters[i].getType() != arg.type().getTypeClass()) {
                            shouldContinue = true;
                            break;
                        }
                    }
                    if (shouldContinue)
                        continue;
                }
                pathList.add(path);
            }
            if (pathList.isEmpty())
                continue;
            methods.put(method, pathList.toArray(new Path[0]));
        }
    }

    public final int getPort() {
        return port;
    }

    public final void start() throws InterruptedException {
        if (!started) {
            started = true;
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap b = new ServerBootstrap();
                b.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(SocketChannel ch) {
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
                ChannelFuture f = b.bind(getPort()).sync();

                f.channel().closeFuture().sync();
            } finally {
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            }
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
                if (host.contains(":"))
                    host = host.split(":")[0];
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
                List<Cookie> cookies = new ArrayList<>();
                if (request.headers().contains(COOKIE)) {
                    cookies = ServerCookieDecoder.STRICT.decodeAll(request.headers().get(COOKIE));
                }
                HttpMethod httpMethod = HttpMethod.valueOf(request.method().name());

                Method result = null;
                for (Map.Entry<Method, Path[]> entry : website.methods.entrySet()) {
                    Method method = entry.getKey();
                    Host hostAnnotation = method.getAnnotation(Host.class);
                    if (hostAnnotation != null)
                        if (!in(hostAnnotation.value(), host))
                            continue;
                    RequestMethod requestMethod = method.getAnnotation(RequestMethod.class);
                    if (requestMethod != null)
                        if (!in(requestMethod.value(), httpMethod))
                            continue;
                    boolean shouldContinue = true;
                    for (Path ptAnno : entry.getValue()) {
                        String pth = ptAnno.value();
                        if (ptAnno.regex()) {
                            System.out.println(ptAnno.value());
                            System.out.println(urlRaw);
                            if (Pattern.matches(ptAnno.value(), urlRaw))
                                shouldContinue = false;
                        } else {
                            String[] pths = new String[]{};
                            if (!pth.equals("/")) {
                                if (pth.startsWith("/")) pth = pth.substring(1);
                                if (pth.endsWith("/")) pth = pth.substring(0, pth.length() - 1);
                                if (pth.contains("/")) {
                                    pths = pth.split("/");
                                } else {
                                    pths = new String[]{pth};
                                }
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
                                if (clientPath.equals(serverPath)) {
                                    break;
                                }
                                if (ptAnno.ignoreCaps() && serverPath.equalsIgnoreCase(clientPath)) {
                                    break;
                                }
                                shouldBreak = false;
                            }
                            if (shouldBreak) {
                                shouldContinue = false;
                                break;
                            }
                        }
                    }
                    if (shouldContinue) {
                        continue;
                    }
                    result = method;
                    break;
                }

                if (result == null) {
                    result = unknownEntrance;
                }

                if (result == null) {
                    ctx.close();
                    return;
                }
                byte[] bodyBytes;
                ByteBuf bodyContent = request.content();
                if (bodyContent.isReadable()) {
                    bodyBytes = bodyContent.copy().array();
                } else {
                    bodyBytes = new byte[0];
                }
                Request req = new Request(paths, args, httpMethod, ctx.channel().remoteAddress().toString(), request.headers().get(HOST) + request.uri(), host, map(request.headers()), cookies, bodyBytes);
                Responder responder = new Responder();
                List<Object> arguments = new ArrayList<>();
                arguments.add(req);
                arguments.add(responder);
                Parameter[] parameters = result.getParameters();
                for (int i = 2; i < parameters.length; i++) {
                    Argument arg = parameters[i].getAnnotation(Argument.class);
                    if (args.containsKey(arg.value())) {
                        arguments.add(arg.type().parse(args.get(arg.value())));
                    } else {
                        arguments.add(arg.type().getEmptyValue());
                    }
                }
                result.invoke(null, arguments.toArray());

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

    private Map<String, String> map(HttpHeaders headers) {
        Map<String, String> mapHeaders = new HashMap<>();
        for (Map.Entry<String, String> entry : headers.entries()) {
            mapHeaders.put(entry.getKey(), entry.getValue());
        }
        return mapHeaders;
    }

    private <T> boolean in(T[] ts, T t) {
        return Arrays.asList(ts).contains(t);
    }

}
