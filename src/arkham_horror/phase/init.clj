(ns arkham-horror.phase.init
  (:require [arkham-horror.phase :as phase]
            [arkham-horror.investigator :as investigator]))

(defn start [game]
  (phase/start game))

(defn stats [phase config]
  (investigator/update phase #(investigator/init % config)))
