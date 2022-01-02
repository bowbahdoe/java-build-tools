package dev.mccue.jproject;

import clojure.java.api.Clojure;
import clojure.lang.IFn;
import clojure.lang.ISeq;
import clojure.lang.Obj;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public final class BuildTools {
    private BuildTools() {}

    private static final BuildTools INSTANCE = new BuildTools();

    private static final IFn DEREF;
    private static final IFn SEQ;

    private static final IFn INTO;
    private static final IFn ASSOC;

    private static final IFn KEYWORD;
    private static final IFn SYMBOL;
    private static final IFn HASH_MAP;
    private static final IFn VECTOR;

    private static final IFn API_PROJECT_ROOT;
    private static final IFn API_COPY_DIR;
    private static final IFn API_COPY_FILE;
    private static final IFn API_CREATE_BASIS;
    private static final IFn API_DELETE;
    private static final IFn API_GIT_COUNT_REVS;

    private static final IFn API_JAR;

    private static final IFn API_JAVAC;

    private static final IFn API_SET_PROJECT_ROOT;

    private static final IFn API_ZIP;

    static {
        DEREF = Clojure.var("clojure.core","deref");
        SEQ = Clojure.var("clojure.core","seq");
        INTO = Clojure.var("clojure.core","into");
        ASSOC = Clojure.var("clojure.core","assoc");

        KEYWORD = Clojure.var("clojure.core", "keyword");
        SYMBOL = Clojure.var("clojure.core", "symbol");
        HASH_MAP = Clojure.var("clojure.core", "hash-map");
        VECTOR = Clojure.var("clojure.core", "vector");

        IFn REQUIRE = Clojure.var("clojure.core", "require");
        String api = "clojure.tools.build.api";
        REQUIRE.invoke(Clojure.read(api));
        API_PROJECT_ROOT = Clojure.var(api, "*project-root*");
        API_COPY_DIR = Clojure.var(api, "copy-dir");
        API_COPY_FILE = Clojure.var(api, "copy-file");
        API_CREATE_BASIS = Clojure.var(api, "create-basis");
        API_DELETE = Clojure.var(api, "delete");
        API_GIT_COUNT_REVS = Clojure.var(api, "git-count-revs");
        // git-process
        // install
        API_JAR = Clojure.var(api, "jar");
        // java-command
        API_JAVAC = Clojure.var(api, "javac");
        // pom-path
        // process
        // resolve-path
        API_SET_PROJECT_ROOT = Clojure.var(api, "set-project-root!");
        // uber
        // unzip
        // write-file
        // write-pom
        API_ZIP = Clojure.var(api, "zip");
    }

    /**
     * Project root path, defaults to current directory.
     * Use `resolve-path` to resolve relative paths in terms of the *project-root*.
     * Use BuildTools#setProjectRoot to override the default for all tasks.
     */
    public String projectRoot() {
        return (String) DEREF.invoke(API_PROJECT_ROOT);
    }

    /**
     * Copy the contents of the src-dirs to the target-dir, optionally do text replacement.
     * @param targetDir dir to write files, will be created if it doesn't exist
     * @param srcDirs coll of dirs to copy from
     */
    public void copyDir(String targetDir, List<String> srcDirs) {
        copyDir(targetDir, srcDirs, CopyDirOptions.builder().build());
    }

    /**
     * Copy the contents of the src-dirs to the target-dir, optionally do text replacement.
     * @param targetDir dir to write files, will be created if it doesn't exist
     * @param srcDirs coll of dirs to copy from
     * @param options options
     */
    public void copyDir(String targetDir, List<String> srcDirs, CopyDirOptions options) {
        Objects.requireNonNull(targetDir);
        Objects.requireNonNull(srcDirs);

        var args = HASH_MAP.invoke(
                Clojure.read(":target-dir"), targetDir,
                Clojure.read(":src-dirs"), SEQ.invoke(srcDirs)
        );
        if (options.include != null) {
            args = ASSOC.invoke(args, Clojure.read(":include"), options.include);
        }
        if (options.ignores != null) {
            args = ASSOC.invoke(args, Clojure.read(":ignores"), SEQ.invoke(options.ignores));
        }
        if (options.replace != null) {
            args = ASSOC.invoke(args, Clojure.read(":replace"), INTO.invoke(Clojure.read("{}"), options.replace));
        }
        if (options.nonReplacedExts != null) {
            args = ASSOC.invoke(args, Clojure.read(":non-replaced-exts"), SEQ.invoke(options.nonReplacedExts));
        }
        API_COPY_DIR.invoke(args);
    }

    /**
     * Copy one file from source to target, creating target dirs if needed.
     * @param src Source path
     * @param target Target path
     */
    public void copyFile(String src, String target) {
        API_COPY_FILE.invoke(src, target);
    }

    public Basis createBasis() {
        return new Basis(API_CREATE_BASIS.invoke());
    }

    public Basis createBasis(CreateBasisOptions options) {
        Objects.requireNonNull(options);

        Function<DepSource, Object> toClojure = depSource ->
            switch (depSource) {
                case DepSource.Standard standard -> Clojure.read(":standard");
                case DepSource.Path path -> path.path();
            };

        var args = Clojure.read("{}");
        if (options.root != null) {
            args = ASSOC.invoke(args, Clojure.read(":include"), toClojure.apply(options.root));
        }
        if (options.user != null) {
            args = ASSOC.invoke(args, Clojure.read(":user"), toClojure.apply(options.user));
        }
        if (options.project != null) {
            args = ASSOC.invoke(args, Clojure.read(":project"), toClojure.apply(options.project));
        }
        if (options.extra != null) {
            args = ASSOC.invoke(args, Clojure.read(":extra"), toClojure.apply(options.extra));
        }
        return new Basis(API_CREATE_BASIS.invoke(args));
    }

    /**
     * Delete file or directory recursively, if it exists.
     */
    public void delete(String path) {
        API_DELETE.invoke(path);
    }

    /**
     * Shells out to git and returns count of commits on this branch:
     *   git rev-list HEAD --count
     */
    public Long gitCountRevs(GitCountRevsOptions options) {
        Object args = Clojure.read("{}");
        if (options.dir != null) {
            args = ASSOC.invoke(args, Clojure.read(":include"), options.dir);
        }
        if (options.gitCommand != null) {
            args = ASSOC.invoke(args, Clojure.read(":include"), options.gitCommand);
        }
        if (options.path != null) {
            args = ASSOC.invoke(args, Clojure.read(":include"), options.path);
        }

        Object revs = API_GIT_COUNT_REVS.invoke(args);
        if (revs == null) {
            return null;
        }
        else if (revs instanceof Long l) {
            return l;
        }
        else {
            return Long.parseLong((String) revs);
        }
    }

    public void jar(String classDir, String jarFile) {
        jar(classDir, jarFile, JarOptions.builder().build());
    }

    public void jar(String classDir, String jarFile, JarOptions options) {
        Objects.requireNonNull(classDir);
        Objects.requireNonNull(jarFile);
        Objects.requireNonNull(options);

        Object args = HASH_MAP.invoke(
                Clojure.read(":class-dir"), classDir,
                Clojure.read(":jar-file"), jarFile
        );
        if (options.main != null) {
            args = ASSOC.invoke(args, Clojure.read(":main"), SYMBOL.invoke(options.main));
        }

        API_JAR.invoke(args);
    }

    public void javac(
            List<String> srcDirs,
            String classDir,
            List<String> javacOpts,
            Basis basis
    ) {
        Objects.requireNonNull(srcDirs);
        Objects.requireNonNull(classDir);

        var args = HASH_MAP.invoke(
                Clojure.read(":src-dirs"), SEQ.invoke(srcDirs),
                Clojure.read(":class-dir"), classDir
        );
        if (basis != null) {
            args = ASSOC.invoke(args, Clojure.read(":basis"), basis.rawBasisObject);
        }
        if (javacOpts != null) {
            args = ASSOC.invoke(args, Clojure.read(":javac-opts"), SEQ.invoke(javacOpts));
        }

        API_JAVAC.invoke(args);
    }

    public void setProjectRoot(String newProjectRoot) {
        API_SET_PROJECT_ROOT.invoke(newProjectRoot);
    }

    /**
     * Create zip file containing contents of src dirs.
     *
     * @param srcDirs coll of source directories to include in zip
     * @param zipFile zip file to create
     */
    public void zip(List<String> srcDirs, String zipFile) {
        API_ZIP.invoke(HASH_MAP.invoke(
                Clojure.read(":src-dirs"), SEQ.invoke(srcDirs),
                Clojure.read(":zip-file"), zipFile
        ));
    }

    public static BuildTools getInstance() {
        return INSTANCE;
    }

    public static void main(String[] args) {
        // ~/Library/Java/JavaVirtualMachines/openjdk-17/Contents/Home/bin/javac --enable-preview --source 17 -cp $(clj -Spath) src/**/*.java -d target && ~/Library/Java/JavaVirtualMachines/openjdk-17/Contents/Home/bin/java --enable-preview  -cp $(clj -Spath) dev/mccue/jproject/BuildTools
        /* BuildTools.getInstance().zip(
                List.of("src"),
                "out.zip"
        ); */



        /*BuildTools.getInstance().copyDir(

                "aa",
                List.of("src", ".idea"),
                CopyDirOptions.builder()
                        .replace(Map.of(
                                "BuildTools", "look in file to see this worked"
                        )).build()
        );/*

        /* var buildTools = BuildTools.getInstance();
        buildTools.javac(
                List.of("src"),
                "target",
                List.of(),
                buildTools.createBasis()
        ); */

        /*System.out.println(BuildTools.getInstance().gitCountRevs(
                GitCountRevsOptions.builder().build()
        ));*/

        /*
        System.out.println("EXECUTING");
        BuildTools.getInstance().jar(
                "target",
                "out.jar",
                JarOptions.builder().main("dev.mccue.jproject.BuildTools").build()
        );
         ~/Library/Java/JavaVirtualMachines/openjdk-17/Contents/Home/bin/java --enable-preview -jar out.jar  -cp $(clj -Spath)
         */
        // ~/Library/Java/JavaVirtualMachines/openjdk-17/Contents/Home/bin/javac --enable-preview --source 17 -cp $(clj -Spath) src/**/*.java -d target && ~/Library/Java/JavaVirtualMachines/openjdk-17/Contents/Home/bin/java --enable-preview  -cp $(clj -Spath) dev/mccue/jproject/BuildTools
    }

}
