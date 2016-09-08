# Metosin Clojure Training 2016-09-09

## Start the app for development

### Boot

- Cursive: `boot dev` and connect to remote repl
- Fireplace: `boot dev` and connect to remote repl
- Cider: `boot dev` and `M-x cider-connect`

### Lein

- `lein less4j auto`
- `lein figwheel`
- Cursive: Start repl from cursive or `lein repl` and connect to remote repl
- Fireplace: `lein repl` and connect to remote repl
- Cider: `M-x cider-jack-in` to start repl or `lein repl` and `M-x cider-connect`

## File tree

### Common

- Source files are separated per filetype, common practice(?)
- Helpful as some files have different roles
    - Cljs files are input only
    - Clj is input only if AOT compiled, but input+output if no AOT
- `src/clj`
- `src/cljc`
- `src/cljs`
- `src/less`
- `test/clj`
- `test/cljc`
- `test/cljs`

### Boot

- `build.boot` Boot entry file
- `boot.properties` Java properties file, defines Boot version used
    - by the time build.boot is read, Boot is already running - need for separate file
- `src/cljs/js/main.cljs.edn` configuration file for Boot-cljs, will result
in result being written to js/main.js path inside the fileset.

### Lein

- `project.clj` Lein configuration file
- `lein-target` The directory used for files written by Lein stuff, in
development some parts are included in classpath to serve files from

### Cljs macros are defined in Clj files, where would you place such file?

# TODO

- https://github.com/adzerk-oss/boot-cljs/issues/95
- New boot-cljs release for compiler-options precedence change
- Less4clj 1.9.0-alpha12 compat
