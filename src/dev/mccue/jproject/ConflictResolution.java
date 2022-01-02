package dev.mccue.jproject;

import java.io.InputStream;
import java.util.Map;

public record ConflictResolution<State>(
        State state,
        Map<String, Write> write
) {
    public record Write(
            Source source,
            boolean append
    ) {
        public Write(Source source) {
            this(source, false);
        }
    }

    public sealed interface Source {}

    public record StringSource(String value) implements Source {
    }

    public record InputStreamSource(InputStream inputStream) implements Source {
    }

}
