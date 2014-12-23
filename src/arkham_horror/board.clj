(ns arkham-horror.board)

(defn make [config]
  (dosync (doseq [investigator (config :investigators)]
            (alter investigator update-in [:sanity :maximum] dec))
          {:ancient-one (ref {})}))
