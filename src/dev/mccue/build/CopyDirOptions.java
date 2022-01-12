package dev.mccue.build;

import java.util.List;
import java.util.Map;

public final class CopyDirOptions {
    final String include;
    final List<String> ignores;
    final Map<String, String> replace;
    final List<String> nonReplacedExts;

    private CopyDirOptions(Builder builder) {
        this.include = builder.include;
        this.ignores = builder.ignores;
        this.replace = builder.replace;
        this.nonReplacedExts = builder.nonReplacedExts;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String include;
        private List<String> ignores;
        private Map<String, String> replace;
        private List<String> nonReplacedExts;

        private Builder() {}

        /**
         * glob of files to include, default = "**"
         */
        public Builder include(String include) {
            this.include = include;
            return this;
        }

        /**
         * collection of ignore regex patterns (applied only to file names),
         *
         * default = [".*~$" "^#.*#$" "^\\.#.*" "^.DS_Store$"]
         */
        public Builder ignores(List<String> ignores) {
            this.ignores = ignores;
            return this;
        }

        /**
         * map of source to replacement string in files
         */
        public Builder replace(Map<String, String> replace) {
            this.replace = replace;
            return this;
        }

        /**
         * coll of extensions to skip when replacing (still copied)
         * default = ["jpg" "jpeg" "png" "gif" "bmp"]
         */
        public Builder nonReplacedExts(List<String> nonReplacedExts) {
            this.nonReplacedExts = nonReplacedExts;
            return this;
        }

        public CopyDirOptions build() {
            return new CopyDirOptions(this);
        }
    }
}

