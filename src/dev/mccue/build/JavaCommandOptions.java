package dev.mccue.build;

import clojure.java.api.Clojure;
import java.util.List;
import java.util.Objects;

import static dev.mccue.build.Requires.ASSOC;
import static dev.mccue.build.Requires.HASH_MAP;
import static dev.mccue.build.Requires.SYMBOL;
import static dev.mccue.build.Requires.VEC;

public final class JavaCommandOptions {
    /*
    java-command
  :java-cmd - Java command, default = "java"
  :cp - coll of string classpath entries, used first (if provided)
  :basis - runtime basis used for classpath, used last (if provided)
  :java-opts - coll of string jvm opts
  :main - required, main class symbol
  :main-args - coll of main class args
  :use-cp-file - one of:
                   :auto (default) - use only if os=windows && Java >= 9 && command length >= 8k
                   :always - always write classpath to temp file and include
                   :never - never write classpath to temp file (pass on command line)

     */

    final String javaCmd;
    final List<String> cp;
    final Basis basis;
    final List<String> javaOpts;
    final List<String> mainArgs;
    final UseCpFile useCpFile;

    enum UseCpFile {
        AUTO,
        ALWAYS,
        NEVER;

        Object toClojure() {
            return switch (this) {
                case AUTO -> Clojure.read(":auto");
                case ALWAYS -> Clojure.read(":always");
                case NEVER -> Clojure.read(":never");
            };
        }
    }

    private JavaCommandOptions(Builder builder) {
        this.javaCmd = builder.javaCmd;
        this.cp = builder.cp;
        this.basis = builder.basis;
        this.javaOpts = builder.javaOpts;
        this.mainArgs = builder.mainArgs;
        this.useCpFile = builder.useCpFile;
    }

    Object toClojure() {
        Object o = null;

        if (this.javaCmd != null) {
            o = ASSOC.invoke(o, Clojure.read(":java-cmd"), this.javaCmd);
        }
        if (this.cp != null) {
            o = ASSOC.invoke(o, Clojure.read(":cp"), VEC.invoke(this.cp));
        }
        if (this.basis != null) {
            o = ASSOC.invoke(o, Clojure.read(":basis"), this.basis.rawBasisObject);
        }
        if (this.javaOpts != null) {
            o = ASSOC.invoke(o, Clojure.read(":java-opts"), VEC.invoke(this.javaOpts));
        }
        if (this.mainArgs != null) {
            o = ASSOC.invoke(o, Clojure.read(":main-args"), VEC.invoke(this.mainArgs));
        }
        if (this.useCpFile != null) {
            o = ASSOC.invoke(o, Clojure.read(":use-cp-file"), this.useCpFile.toClojure());
        }
        return o;
    }
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String javaCmd;
        private List<String> cp;
        private Basis basis;
        private List<String> javaOpts;
        private List<String> mainArgs;
        private UseCpFile useCpFile;

        private Builder() {}

        public Builder javaCmd(String javaCmd) {
            this.javaCmd = javaCmd;
            return this;
        }

        public Builder cp(List<String> cp) {
            this.cp = cp;
            return this;
        }

        public Builder basis(Basis basis) {
            this.basis = basis;
            return this;
        }

        public Builder javaOpts(List<String> javaOpts) {
            this.javaOpts = javaOpts;
            return this;
        }

        public Builder mainArgs(List<String> mainArgs) {
            this.mainArgs = mainArgs;
            return this;
        }

        public Builder useCpFile(UseCpFile useCpFile) {
            this.useCpFile = useCpFile;
            return this;
        }

        public JavaCommandOptions build() {
            return new JavaCommandOptions(this);
        }
    }
}
