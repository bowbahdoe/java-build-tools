package dev.mccue.jproject;

public final class CreateBasisOptions {
    final DepSource root;
    final DepSource user;
    final DepSource project;
    final DepSource extra;
    // final Object aliases;

    private CreateBasisOptions(Builder builder) {
        this.root = builder.root;
        this.user = builder.user;
        this.project = builder.project;
        this.extra = builder.extra;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private DepSource root;
        private DepSource user;
        private DepSource project;
        private DepSource extra;

        private Builder() {}

        public Builder root(DepSource root) {
            this.root = root;
            return this;
        }

        public Builder user(DepSource user) {
            this.user = user;
            return this;
        }

        public Builder project(DepSource project) {
            this.project = project;
            return this;
        }

        public Builder extra(DepSource extra) {
            this.extra = extra;
            return this;
        }

        public CreateBasisOptions build() {
            return new CreateBasisOptions(this);
        }
    }
}
