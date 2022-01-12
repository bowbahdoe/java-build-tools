package dev.mccue.build;

import java.util.Objects;

public final class InstallOptions {
    final Basis basis;
    final String lib;
    final String classifier;
    final String version;
    final String jarFile;
    final String classDir;

    private InstallOptions(Builder builder) {
        this.basis = Objects.requireNonNull(builder.basis);
        this.lib = Objects.requireNonNull(builder.lib);
        this.classifier = builder.classifier;
        this.version = Objects.requireNonNull(builder.version);
        this.jarFile = Objects.requireNonNull(builder.jarFile);
        this.classDir = Objects.requireNonNull(builder.classDir);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Basis basis;
        private String lib;
        private String classifier;
        private String version;
        private String jarFile;
        private String classDir;

        private Builder() {}


    }


}
