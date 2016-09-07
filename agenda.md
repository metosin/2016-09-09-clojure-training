- Single example project for both of these topics.
- TODO APP!
- Existing backend and partial frontend
- Both Lein and Boot setup.

# Tooling & workflow for Clj and Cljs (1.5h)

## Tooling

Basic idea of Clojure build tools is to manage the classpath.

### Lein

- Separate classpaths using multiple JVM runtimes
- User needs to configure output (and maybe) input tasks for each plugin
- DRAW: Picture of Lein classpath and (plugin) project classpath
- SHOW: Less4clj Lein plugin

### Boot

- Pods - separate classpaths inside single JVM
- Fileset
- DEMO: Run code with two different Clojure versions in one session
- HANDS ON: Create a Boot task
    - IDEA 1: Create a Clojure namespace which defines a var with current git sha
        - Doesn't read any input files -> not the best example

## Cljs build tooling

### Figwheel

- Live reload (JS, CSS...)
- HUD - Displays e.g. Cljs warnings and errors
- Built-in Cljs REPL
- Multiple clients

### Boot-cljs & co.

- Provides most of the same feature as Figwheel
- Three separate tasks: boot-cljs, boot-reload and boot-cljs-repl

## Workflow

### Clojure

- lein run
- lein ring server ?
- repl + manual reload using editor
- repl + automatic reload using c.t.n or such
- repl + component/mount etc.
    - restartable
- remote nrepl - to prod!!

- Debugging: CURSIVE!
    - DEMO: ClojureScript compiler add breakpoint (http://dev.clojure.org/jira/browse/CLJS-1762)

- Profiling
    - Java VisualVM: Can be SLOW if the classpath has nrepl + middlewares (cider-nrepl)
    - TENS OF THOUSANDS CLASSES - Cljs compile -> 10 minutes
    - YourKit

### Cljs

- "David's workflow, maybe" Cljs.jar + bash build script + manual reload
- (emacs) Inferior lisp mode repl
- Browser repl (Nrepl + Websocket)
- Live reloads (Figwheel, Boot-reload)

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

- ASK: How do you do currently?
- Templates
- Example projects?
- Use the previous project?
- Future: Arachne?

## Deploying a project

- Uberjar?
- Can be very large - 50-100M
- Can be slow to deploy to server (shouldn't be problem if you run CI and CI has fast connection to server)
- We haven't yet solved this - not a huge problem but can be annonying
- There are couple of offender packages - like ring-middleware-format which requires ibm/icu4j
- Solution 1: Don't use the largest offending packages - use alternatives or fix them
- Solution 2: Only send changed files as not all deps inside uberjar change everytime
    - Unpack JAR (Zip) and send contents over Rsync - Repackage on target
    - Rsync can take of deleted files etc.
- Docker? Not in use yet
    - Unpack JAR and separate app code and libraries in different layers





# Architecture considerations of full-stack Clojure apps (2h)

This is not the truth. These are some things we have learned and use.

## Application namespace hierachy

- `common`, `backend`, `frontend`
- We have found no reason to include package name in namespaces
    - `project-a.backend.main` vs. `backend.main`
    - Applications won't be included in classpath of other apps -> no clashes
- Names are the same between two projects
    - New project -> Copy old one and keep many of basic namespaces
    - Easy to diff files between projects
    - General namespaces are moved to company wide common lib: https://github.com/metosin/metosin-common/
    - Later, move the best parts to separate libs
    - But there are cases where namespaces are still included in projects
    - Lazyness, bad design, practicality

## Communications

- JSON
- EDN
- Transit
- HTTP vs. WebSocket
- HANDS ON: Sente example - connect existing backend app and frontend

- "Focusing on the essence" - hide the transfer mechanic
    - Same dispatch mecanism on back and front
    - Example from this?

## Designing reusable components

- We use Reagent, but these same ideas hold for Om, Rum etc.
- Pass only PURE DATA to the components!
- No atoms, instead value & on-change callback etc.
- Parametrize class of every element?
- HANDS ON: Reagent, build x component

# Random ideas worth mentioning

- Performant Cljs code: https://github.com/funcool/bide write critical parts in JS

[]: vim: set nospell :
