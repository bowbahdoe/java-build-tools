package dev.mccue.jproject;

public final class JarOptions {
    /*
      :class-dir - required, dir to include in jar
  :jar-file - required, jar to write
  :main - main class symbol
  :manifest - map of manifest attributes, merged last over defaults+:main
     */
    final String main;
    // final Object manifest;

    private JarOptions(Builder builder) {
        this.main = builder.main;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String main;

        private Builder() {}

        public Builder main(String main) {
            this.main = main;
            return this;
        }

        public JarOptions build() {
            return new JarOptions(this);
        }
    }
}
