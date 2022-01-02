package dev.mccue.jproject;

public sealed interface DepSource {
    record Standard() implements DepSource {}
    record Path(String path) implements DepSource {}
}
