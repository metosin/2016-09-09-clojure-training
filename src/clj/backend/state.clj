(ns backend.state
  "A database!")

(defonce todos (atom (sorted-map)))
