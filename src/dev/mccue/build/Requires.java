package dev.mccue.build;

import clojure.java.api.Clojure;
import clojure.lang.IFn;

/**
 * All the clojure vars required by this library.
 */
final class Requires {
    static final IFn DEREF;
    static final IFn SEQ;
    static final IFn VEC;

    static final IFn INTO;
    static final IFn ASSOC;

    static final IFn KEYWORD;
    static final IFn SYMBOL;
    static final IFn HASH_MAP;
    static final IFn VECTOR;
    static final IFn LIST;
    static final IFn GENSYM;
    static final IFn EVAL;

    static final IFn API_PROJECT_ROOT;
    static final IFn API_COPY_DIR;
    static final IFn API_COPY_FILE;
    static final IFn API_CREATE_BASIS;
    static final IFn API_DELETE;
    static final IFn API_GIT_COUNT_REVS;
    static final IFn API_GIT_PROCESS;
    static final IFn API_INSTALL;
    static final IFn API_JAR;
    static final IFn API_JAVA_COMMAND;
    static final IFn API_JAVAC;
    static final IFn API_POM_PATH;
    static final IFn API_PROCESS;
    static final IFn API_RESOLVE_PATH;
    static final IFn API_SET_PROJECT_ROOT;
    static final IFn API_UBER;
    static final IFn API_UNZIP;
    static final IFn API_WRITE_FILE;
    static final IFn API_WRITE_POM;
    static final IFn API_ZIP;

    static {
        DEREF = Clojure.var("clojure.core","deref");
        SEQ = Clojure.var("clojure.core","seq");
        VEC = Clojure.var("clojure.core", "vec");
        INTO = Clojure.var("clojure.core","into");
        ASSOC = Clojure.var("clojure.core","assoc");

        KEYWORD = Clojure.var("clojure.core", "keyword");
        SYMBOL = Clojure.var("clojure.core", "symbol");
        HASH_MAP = Clojure.var("clojure.core", "hash-map");
        VECTOR = Clojure.var("clojure.core", "vector");
        LIST = Clojure.var("clojure.core", "list");
        GENSYM = Clojure.var("clojure.core", "gensym");
        EVAL = Clojure.var("clojure.core", "eval");

        IFn REQUIRE = Clojure.var("clojure.core", "require");
        String api = "clojure.tools.build.api";
        REQUIRE.invoke(Clojure.read(api));
        API_PROJECT_ROOT = Clojure.var(api, "*project-root*");
        API_COPY_DIR = Clojure.var(api, "copy-dir");
        API_COPY_FILE = Clojure.var(api, "copy-file");
        API_CREATE_BASIS = Clojure.var(api, "create-basis");
        API_DELETE = Clojure.var(api, "delete");
        API_GIT_COUNT_REVS = Clojure.var(api, "git-count-revs");
        API_GIT_PROCESS = Clojure.var(api, "git-process");
        API_INSTALL = Clojure.var(api, "install");
        API_JAR = Clojure.var(api, "jar");
        API_JAVA_COMMAND = Clojure.var(api, "java-command");
        API_JAVAC = Clojure.var(api, "javac");
        API_POM_PATH = Clojure.var(api, "pom-path");
        API_PROCESS = Clojure.var(api, "process");
        API_RESOLVE_PATH = Clojure.var(api, "resolve-path");
        API_SET_PROJECT_ROOT = Clojure.var(api, "set-project-root!");
        API_UBER = Clojure.var(api, "uber");
        API_UNZIP = Clojure.var(api, "unzip");
        API_WRITE_FILE = Clojure.var(api, "write-file");
        API_WRITE_POM = Clojure.var(api, "write-pom");
        API_ZIP = Clojure.var(api, "zip");
    }

    private Requires() {}
}
