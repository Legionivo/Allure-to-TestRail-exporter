package io.github.legionivo.plugin.api;

class TestRailException extends RuntimeException {

    TestRailException(String message) {
        super(message);
    }

    TestRailException(String message, Throwable e) {
        super(message, e);
    }


}
