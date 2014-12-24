(ns arkham-horror.board
  (:require [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.investigator :as investigator]))

(defn make [config]
  {:ancient-one (ancient-one/make (config :ancient-one) (config :investigators))})
