package dev.mccue.build;

public sealed interface DepSource {
    record Standard() implements DepSource {}
    record Path(String path) implements DepSource {}
}
