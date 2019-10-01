package io.github.legionivo.plugin;

import java.util.function.Supplier;

public class PluginException extends RuntimeException {

    private PluginException(final String message) {
        super(message);
    }

    public PluginException(final String message, final Throwable e) {
        super(message, e);
    }

    public static Supplier<PluginException> notFound(final String message) {
        return () -> new PluginException(String.format("%s not found", message));
    }
}
