package dev.mccue.jproject;

import clojure.java.api.Clojure;
import clojure.lang.AFn;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static dev.mccue.jproject.Requires.*;

public final class UberOptions<ConflictHandlerState> {
    /*
      :uber-file - required, uber jar file to create
  :class-dir - required, local class dir to include
  :basis - used to pull dep jars
  :main - main class symbol
  :manifest - map of manifest attributes, merged last over defaults + :main
  :exclude - coll of string patterns (regex) to exclude from deps
  :conflict-handlers - map of string pattern (regex) to built-in handlers,
                       symbols to eval, or function instances

     */
    final String uberFile;
    final String classDir;
    final Basis basis;
    final String main;
    final List<String> exclude;
    final Map<Object, ConflictHandler<ConflictHandlerState>> conflictHandlers;

    private UberOptions(Builder<ConflictHandlerState> builder) {
        this.uberFile = Objects.requireNonNull(builder.uberFile);
        this.classDir = Objects.requireNonNull(builder.classDir);
        this.basis = builder.basis;
        this.main = builder.main;
        this.exclude = builder.exclude;
        this.conflictHandlers = builder.conflictHandlers;
    }

    Object toClojure() {
        var map = HASH_MAP.invoke(
                Clojure.read(":uber-file"), this.uberFile,
                Clojure.read(":class-dir"), this.classDir
        );
        if (this.basis != null) {
            map = ASSOC.invoke(map, Clojure.read(":basis"), this.basis.rawBasisObject);
        }
        if (this.main != null) {
            map = ASSOC.invoke(map, Clojure.read(":main"), SYMBOL.invoke(this.main));
        }
        if (this.exclude != null) {
            map = ASSOC.invoke(map, Clojure.read(":exclude"), VEC.invoke(this.exclude));
        }
        if (this.conflictHandlers != null) {
            for (var entry : this.conflictHandlers.entrySet()) {
                var value = switch (entry.getValue()) {
                    case ConflictHandler.Ignore __ -> Clojure.read(":ignore");
                    case ConflictHandler.Overwrite __ -> Clojure.read(":overwrite");
                    case ConflictHandler.Append __ -> Clojure.read(":append");
                    case ConflictHandler.AppendDedupe __ -> Clojure.read(":append-dedupe");
                    case ConflictHandler.Warn __ -> Clojure.read(":warn");
                    case ConflictHandler.Error __ -> Clojure.read(":error");
                    case ConflictHandler.Default __ -> Clojure.read(":default");
                    case ConflictHandler.UserDefined<ConflictHandlerState> udf -> new AFn() {
                        @Override
                        public Object invoke(Object arg) {
                            return udf.resolve(ConflictInformation.fromClojure(arg));
                        }
                    };
                };
                map = ASSOC.invoke(map, entry.getKey(), value);
            }
        }
        return map;
    }
    public static <ConflictHandlerState> Builder<ConflictHandlerState> builder() {
        return new Builder<>();
    }

    public static final class Builder<ConflictHandlerState> {
        private String uberFile;
        private String classDir;
        private Basis basis;
        private String main;
        private List<String> exclude;
        private Map<Object, ConflictHandler<ConflictHandlerState>> conflictHandlers;

        private Builder() {
        }

        public Builder<ConflictHandlerState> uberFile(String uberFile) {
            this.uberFile = uberFile;
            return this;
        }

        public Builder<ConflictHandlerState> classDir(String classDir) {
            this.classDir = classDir;
            return this;
        }

        public Builder<ConflictHandlerState> basis(Basis basis) {
            this.basis = basis;
            return this;
        }

        public Builder<ConflictHandlerState> main(String main) {
            this.main = main;
            return this;
        }

        public Builder<ConflictHandlerState> exclude(List<String> exclude) {
            this.exclude = exclude;
            return this;
        }

        public Builder<ConflictHandlerState> conflictHandler(String pattern, ConflictHandler<ConflictHandlerState> conflictHandler) {
            if (this.conflictHandlers == null) {
                this.conflictHandlers = new HashMap<>();
            }
            this.conflictHandlers.put(pattern, conflictHandler);
            return this;
        }

        public Builder<ConflictHandlerState> defaultConflictHandler( ConflictHandler<ConflictHandlerState> conflictHandler) {
            if (this.conflictHandlers == null) {
                this.conflictHandlers = new HashMap<>();
            }
            this.conflictHandlers.put(Clojure.read(":default"), conflictHandler);
            return this;
        }

        public UberOptions<ConflictHandlerState> build() {
            return new UberOptions<>(this);
        }
    }

}
