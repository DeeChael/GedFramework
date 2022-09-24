package net.deechael.framework.response;

import lombok.Getter;
import lombok.Setter;
import net.deechael.framework.Request;
import net.deechael.framework.Responder;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

public final class PreparedResponse {

    @Getter
    @NotNull
    private final Class<?> pageHandler;
    @Getter
    @NotNull
    private final Method invoker;
    @Getter
    @NotNull
    private final Request request;
    @Getter
    @NotNull
    private final Responder responder;

    @Getter
    @Setter
    private boolean cancelled;

    public PreparedResponse(@NotNull Class<?> pageHandler, @NotNull Method invoker, @NotNull Request request, @NotNull Responder responder) {
        this.pageHandler = pageHandler;
        this.invoker = invoker;
        this.request = request;
        this.responder = responder;
    }

}
