(ns arkham-horror.status)

(def full-message (ref ""))

(defn get-message []
  @full-message)

(defn set-message! [message phase & commands]
  (ref-set full-message
           (clojure.string/join "\n" `[~message
                                       ~(str "Phase: " phase)
                                       "Commands:"
                                       ~@(map #(str "\t" %) commands)])))
