package dev.mccue.build;

import java.util.Objects;

public final class GitProcessOptions {
    final String dir;
    final String gitCommand;
    final Capture capture;

    public enum Capture {
        OUT,
        ERR,
        NOTHING
    }

    private GitProcessOptions(Builder builder) {
        this.dir = builder.dir;
        this.gitCommand = builder.gitCommand;
        this.capture = builder.capture;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String dir;
        private String gitCommand;
        private Capture capture;

        private Builder() {}

        /**
         * dir to invoke this command from, default = current directory
         */
        public Builder dir(String dir) {
            Objects.requireNonNull(dir);
            this.dir = dir;
            return this;
        }

        /**
         * git command to use, default = "git"
         */
        public Builder gitCommand(String gitCommand) {
            Objects.requireNonNull(gitCommand);
            this.gitCommand = gitCommand;
            return this;
        }

        public Builder capture(Capture capture) {
            Objects.requireNonNull(capture);
            this.capture = capture;
            return this;
        }

        public GitProcessOptions build() {
            return new GitProcessOptions(this);
        }
    }
}
