package dev.mccue.jproject;

import java.io.InputStream;
import java.util.Map;
import java.util.function.Function;

public record ConflictResolution<State>(
        State state,
        Map<String, Write> write
) {
    <NewState> ConflictResolution<NewState> mapState(Function<? super State, ? extends NewState> f) {
        return new ConflictResolution<>(
                f.apply(state),
                write
        );
    }

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
