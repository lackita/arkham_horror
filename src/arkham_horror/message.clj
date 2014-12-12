(ns arkham-horror.message
  (:refer-clojure :exclude [get set]))

(defn get [game]
  (game :message))

(defn set [game message]
  (assoc game :message message))
