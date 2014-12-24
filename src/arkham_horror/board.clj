(ns arkham-horror.board
  (:require [arkham-horror.investigator :as investigator]))

(defn make [config]
  (when (= (config :ancient-one) "Cthulu")
    (doseq [investigator (config :investigators)]
      (investigator/decrement-maximum-sanity investigator)
      (investigator/decrement-maximum-stamina investigator)))
  {:ancient-one (ref {})})
