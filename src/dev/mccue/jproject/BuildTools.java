package dev.mccue.jproject;

import clojure.java.api.Clojure;
import clojure.lang.IFn;
import clojure.lang.ISeq;
import clojure.lang.Obj;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import static dev.mccue.jproject.Requires.*;

public final class BuildTools {
    private BuildTools() {}

    private static final BuildTools INSTANCE = new BuildTools();


    /**
     * Project root path, defaults to current directory.
     * Use `resolve-path` to resolve relative paths in terms of the *project-root*.
     * Use BuildTools#setProjectRoot to override the default for all tasks.
     */
    public String projectRoot() {
        return (String) DEREF.invoke(API_PROJECT_ROOT);
    }

    /**
     * Sets the project root in the given scope.
     */
    @SuppressWarnings("unchecked")
    public <T> T withProjectRoot(String projectRoot, Supplier<T> callable) {
        var sym = GENSYM.invoke();
        var code = LIST.invoke(
                SYMBOL.invoke("fn"),
                VECTOR.invoke(sym),
                LIST.invoke(
                    SYMBOL.invoke("clojure.core", "binding"),
                    VECTOR.invoke(
                            SYMBOL.invoke("clojure.tools.build.api", "*project-root*"),
                            projectRoot
                    ),
                        LIST.invoke(SYMBOL.invoke(".get"), sym)
                )
        );

        return (T) ((IFn) EVAL.invoke(code)).invoke(callable);
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
                case DepSource.Standard __ -> Clojure.read(":standard");
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
        if (options.aliases != null) {
            args = ASSOC.invoke(args, Clojure.read(":aliases"), VEC.invoke(options.aliases));
        }
        return new Basis(API_CREATE_BASIS.invoke(args));
    }

    /**
     * Delete file or directory recursively, if it exists.
     */
    public void delete(String path) {
        API_DELETE.invoke(HASH_MAP.invoke(Clojure.read(":path"), path));
    }

    /**
     * Shells out to git and returns count of commits on this branch:
     *   git rev-list HEAD --count
     */
    public Long gitCountRevs(GitCountRevsOptions options) {
        Objects.requireNonNull(options);

        Object args = Clojure.read("{}");
        if (options.dir != null) {
            args = ASSOC.invoke(args, Clojure.read(":include"), options.dir);
        }
        if (options.gitCommand != null) {
            args = ASSOC.invoke(args, Clojure.read(":git-command"), options.gitCommand);
        }
        if (options.path != null) {
            args = ASSOC.invoke(args, Clojure.read(":path"), options.path);
        }

        Object revs = API_GIT_COUNT_REVS.invoke(args);
        if (revs == null) {
            return null;
        }
        else if (revs instanceof Number n) {
            return n.longValue();
        }
        else {
            return Long.parseLong((String) revs);
        }
    }

    public Long gitCountRevs() {
        return gitCountRevs(GitCountRevsOptions.builder().build());
    }

    public String gitProcess(List<String> gitArgs) {
        return gitProcess(gitArgs, GitProcessOptions.builder().build());
    }

    /**
     * Run git process in the specified dir using git-command with git-args (which should not
     * start with "git"). By default, stdout is captured, trimmed, and returned.
     */
    public String gitProcess(List<String> gitArgs, GitProcessOptions options) {
        Object args = HASH_MAP.invoke(
                Clojure.read(":git-args"), VEC.invoke(gitArgs)
        );
        if (options.capture != null) {

            args = ASSOC.invoke(args, Clojure.read(":capture"), switch (options.capture) {
                case OUT -> Clojure.read(":out");
                case ERR -> Clojure.read(":err");
                case NOTHING -> null;
            });
        }
        if (options.dir != null) {
            args = ASSOC.invoke(args, Clojure.read(":dir"), options.dir);
        }
        if (options.gitCommand != null) {
            args = ASSOC.invoke(args, Clojure.read(":git-command"), options.gitCommand);
        }

        return (String) API_GIT_PROCESS.invoke(args);
    }

    public void install(InstallOptions options) {

        API_INSTALL.invoke();
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

    public <ConflictHandlerState> void uber(
            UberOptions<ConflictHandlerState> options
    ) {
        API_UBER.invoke(options.toClojure());
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
        /*var buildTools = BuildTools.getInstance();

        buildTools.javac(
                List.of("src"),
                "target/classes",
                List.of("-source", "17", "--enable-preview"),
                buildTools.createBasis()
        );*/
        // ~/Library/Java/JavaVirtualMachines/openjdk-17/Contents/Home/bin/javac --enable-preview --source 17 -cp $(clj -Spath) src/**/*.java -d target && ~/Library/Java/JavaVirtualMachines/openjdk-17/Contents/Home/bin/java --enable-preview  -cp $(clj -Spath) dev/mccue/jproject/BuildTools
        /* BuildTools.getInstance().zip(
                List.of("src"),
                "out.zip"
        ); */

        /*
        var buildTools = BuildTools.getInstance();
        System.out.println(buildTools.withProjectRoot(
                "a/d",
                buildTools::projectRoot
        ));
         */



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

        /* System.out.println(BuildTools.getInstance().gitProcess(List.of(
                "branch",
                "--show-current"
        )).getClass());
        */
        /*
        System.out.println(BuildTools.getInstance().gitProcess(List.of(
                "branch",
                "--show-current"
        )));

        System.out.println(
                BuildTools.getInstance().gitProcess(List.of(
                "branch",
                "--show-current"),
                GitProcessOptions.builder()
                        .capture(GitProcessOptions.Capture.NOTHING).build()));
        */

        /*
        System.out.println(Clojure.var("clojure.pprint", "pprint").invoke(
                BuildTools.getInstance().createBasis().rawBasisObject
        ));

        BuildTools.getInstance().uber(
                UberOptions
                        .builder()
                        .conflictHandlers(Map.of("something", new ConflictHandler.Append<>()))
                        .build()
        );

         */

        // ~/Library/Java/JavaVirtualMachines/openjdk-17/Contents/Home/bin/javac --enable-preview --source 17 -cp $(clj -Spath) src/**/*.java -d target && ~/Library/Java/JavaVirtualMachines/openjdk-17/Contents/Home/bin/java --enable-preview  -cp $(clj -Spath) dev/mccue/jproject/BuildTools
    }

}
