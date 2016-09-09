# Architecture considerations for full-stack Clojure apps

This is not the truth. These are some things we have learned and use.

## Application namespace hierachy

- `common`, `backend`, `frontend`
- We have found no reason to include package name in namespaces
    - `project-a.backend.main` vs. `backend.main`
    - Applications won't be included in classpath of other apps -> no clashes
- Initializing project: copy the previous project...
    - Works surprisingly good
    - Alternatives: Template, separate example project
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
- Transit+json
- HTTP vs. WebSocket
- HANDS ON: Sente example - connect existing backend app and frontend

- "Focusing on the essence" - hide the transfer mechanic?
    - Same dispatch mechanism on back and front
    - multimethod

## Designing reusable components

- We use Reagent, but these same ideas hold for Om, Rum etc.
- Pass only PURE DATA to the components!
- No atoms, instead value & on-change callback etc.
- Parametrize class of every element?
- HANDS ON: Reagent, build autocomplete component

# Random ideas worth mentioning

- Re-frame
- Routing
- How to trigger queries to the backend
- Performant Cljs code: https://github.com/funcool/bide write critical parts in JS

[]: vim: set nospell :
