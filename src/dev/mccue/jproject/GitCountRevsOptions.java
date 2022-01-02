package dev.mccue.jproject;

import java.util.List;

public final class GitCountRevsOptions {
    final String dir;
    final String gitCommand;
    final String path;

    private GitCountRevsOptions(GitCountRevsOptions.Builder builder) {
        this.dir = builder.dir;
        this.gitCommand = builder.gitCommand;
        this.path = builder.path;
    }

    public static GitCountRevsOptions.Builder builder() {
        return new GitCountRevsOptions.Builder();
    }

    public static final class Builder {
        private String dir;
        private String gitCommand;
        private String path;

        private Builder() {}


        public GitCountRevsOptions.Builder dir(String dir) {
            this.dir = dir;
            return this;
        }

        public GitCountRevsOptions.Builder gitCommand(String gitCommand) {
            this.gitCommand = gitCommand;
            return this;
        }

        public GitCountRevsOptions.Builder path(String path) {
            this.path = path;
            return this;
        }


        public GitCountRevsOptions build() {
            return new GitCountRevsOptions(this);
        }
    }
}
