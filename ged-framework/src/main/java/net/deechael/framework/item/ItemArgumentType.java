package net.deechael.framework.item;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public enum ItemArgumentType {

    STRING(String.class, String::valueOf, null),
    INT(int.class, Integer::parseInt, 0),
    DOUBLE(double.class, Double::parseDouble, 0.0),
    BOOL(boolean.class, Boolean::parseBoolean, false),
    HEADER(String.class, String::valueOf, null);

    private final Class<?> typeClass;
    private final Function<String, ?> parser;

    private final Object emptyValue;

    <T> ItemArgumentType(Class<T> typeClass, Function<String, T> function, T emptyValue) {
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
