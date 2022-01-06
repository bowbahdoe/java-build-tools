package dev.mccue.jproject;

import clojure.java.api.Clojure;
import clojure.lang.IFn;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.function.Function;
import static dev.mccue.jproject.Requires.*;

public record ConflictInformation<State>(
        String path,
        InputStream in,
        Path existing,
        String lib,
        State state // need to put in and pull out of map
) {
    <NewState> ConflictInformation<NewState> mapState(
            Function<? super State, ? extends NewState> f
    ) {
        return new ConflictInformation<>(
                path,
                in,
                existing,
                lib,
                f.apply(state)
        );
    }

    @SuppressWarnings("unchecked")
    static <State> ConflictInformation<State> fromClojure(Object o) {
        var map = (IFn) o;
        var path = (String) map.invoke(Clojure.read(":path"));

        var in = (InputStream) map.invoke(Clojure.read(":in"));

        var existing = ((File) map.invoke(Clojure.read(":existing"))).toPath();

        var lib = (String) map.invoke(Clojure.read(":lib"));

        var state = (State) ((IFn) map.invoke(Clojure.read(":state"))).invoke(KEYWORD.invoke(
                "dev.mccue.jproject",
                "state-key"
        ));

        return new ConflictInformation<>(
                path,
                in,
                existing,
                lib,
                state
        );
    }
}
