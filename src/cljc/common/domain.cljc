(ns common.domain)

(defn valid-todo? [{:keys [title id done]}]
  (and (string? title) (seq title)
       id
       (instance? Boolean done)))

(defn complete-todo [todo]
  (assoc todo :done true))

(defn parse-number [s]
  #?(:clj (try (Integer/parseInt s)
               (catch Exception e
                 nil))
     :cljs (js/parseInt s 10)))
