package net.deechael.framework;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public enum ArgumentType {

    STRING(String.class, String::valueOf, null),
    INT(int.class, Integer::parseInt, 0),
    DOUBLE(double.class, Double::parseDouble, 0.0),
    BOOL(boolean.class, Boolean::parseBoolean, false);

    private final Class<?> typeClass;
    private final Function<String, ?> parser;

    private final Object emptyValue;

    <T> ArgumentType(Class<T> typeClass, Function<String, T> function, T emptyValue) {
        this.typeClass = typeClass;
        this.parser = function;
        this.emptyValue = emptyValue;
    }

    @Nullable
    public Object getEmptyValue() {
        return emptyValue;
    }

    @NotNull
    public Object parse(String string) {
        return parser.apply(string);
    }

    @NotNull
    public Class<?> getTypeClass() {
        return typeClass;
    }

}
