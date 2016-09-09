# Tooling & workflow for Clj and Cljs

- Single example project for both of these topics.
- Todo mvc!
- Existing backend and frontend
- Both Lein and Boot setup.

## Tooling

Basic idea of Clojure build tools is to manage the classpath.

### Lein

- Separate classpaths using multiple JVM runtimes
- User needs to configure output (and maybe) input tasks for each plugin
- DRAW: Picture of Lein classpath and (plugin) project classpath
- SHOW: Less4clj Lein plugin

### Boot

- DISCLAIMER: Windows 10 support maybe - probably many tasks broken, including tasks written by me
    - You are welcome to help if you use Windows, the problem are just a few badly coded path manipulations
    - I have already setup Windows CI but fixes are still on my todo list...

- Pods - separate classpaths inside single JVM
- Fileset
    - Introduces some additional overhead - so Boot will be slightly slower
- DEMO: Run code with two different Clojure versions in one session
- HANDS ON: Create a Boot task
    - Implement zip task which packages some files

## Workflow

### Clojure

- lein run
- lein ring server ?
- repl + manual reload using editor
- repl + automatic reload using c.t.n or such
- **repl + component/mount etc.**
    - restartable
- remote nrepl - to prod!!

- Debugging: CURSIVE!

- Profiling
    - Java VisualVM: Can be SLOW if the classpath has nrepl + middlewares (cider-nrepl)
    - TENS OF THOUSANDS CLASSES - Cljs compile -> 10 minutes
    - YourKit

### Cljs

- Live reloads (Figwheel, Boot-reload)
- Browser repl (Nrepl + Websocket)
- (emacs) Inferior lisp mode repl
- "David's workflow, maybe" Cljs.jar + bash build script + manual reload

## Cljs build tooling

### Figwheel

- Live reload (JS, CSS...)
- HUD - Displays e.g. Cljs warnings and errors
- Built-in Cljs REPL
- Multiple clients

### Boot-cljs & co.

- Provides most of the same feature as Figwheel
- Three separate tasks: boot-cljs, boot-reload and boot-cljs-repl

### Alternatives

- Don't want Boot but Lein has too many processes - Fix it yourself:
- Use Figwheel library from `lein repl` (or boot): https://github.com/bhauman/lein-figwheel#scripting-figwheel
    - One JVM process less, has side-effects!
- Similarly you could run Less4clj without Lein plugin
- https://github.com/clojure/clojurescript/wiki/Quick-Start#auto-building
- https://github.com/clojure/clojurescript/blob/master/src/main/clojure/cljs/build/api.clj

### Testing

- Run tests in editor
- https://github.com/jakemcc/lein-test-refresh
    - Run tests in changed namespaces (including dependets)
    - Run all tests with Enter or such
    - Similar Boot task: https://github.com/metosin/boot-alt-test
- https://github.com/bensu/doo
    - Cljs test runner
    - Run on Chrome, FF, Phantomn, Node etc.
    - Karma runner
    - Boot task: https://github.com/crisptrutski/boot-cljs-test

### Working with multiple projects

- Developing a library on which the application depends on
- Lein & Boot checkouts: Same objective, different implementation
- Lein checkouts: Add the libraries to `checkouts` directory in working dir
    - You can use symlinks (Unix)
    - Lein checks the project.clj on libraries and adds necessary source-paths to app project
    - Change a file in library project -> the edited file is available in app classpath
    - Might need some manual Cljs configuration in App project
- Boot checkouts: Add dependency vectors to `:checkouts` key in project env
    - Boot will watch the local maven repository (`~/.m2`) for new jar's for that dependency
    - JAR contents are added to Boot tmp-dirs (classpath)
    - Boot-cljs and everything automatically sees the changed files
    - Works with both Boot and Lein library projects
    - Library projects needs it's own development process to build the JAR file after changes
    - Easy with Boot: `boot watch pom jar install` (install a jar to local maven repo after everychange)
    - Slow with Lein

## Initializing a project

- Templates (`lein new`, `boot new`)
- Example project
- Copy the previous project
- From scratch?

## Deploying a project

- Uberjar?
- SystemD service
- Can get quite big if lots of dependencies
- There are couple of large offender packages - like ring-middleware-format which requires ibm/icu4j
- Make sure development deps are not included: Cljs compiler & Cljs libs
    - Use Maven scope with Boot and Lein profiles
- *Solution 1*: Don't use the largest offending packages - use alternatives or fix them
- *Solution 2*: Only send changed files as not all deps inside uberjar change everytime
    - Unpack JAR (Zip) and send contents over Rsync - Repackage on target
    - Rsync can take of deleted files etc.
- Docker? I have not used yet
    - Unpack JAR and separate app code and libraries in different layers

[]: vim: set nospell :
