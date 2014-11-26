(ns arkham-horror.setup
  (:require [arkham-horror.game :as game]
            [arkham-horror.ancient-one :as ancient-one]))

(defn begin []
  (ancient-one/awaken (game/make {:ancient-one (ancient-one/random)})))
