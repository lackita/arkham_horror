(ns arkham-horror.board)

(defn make [config]
  (dosync (when (= (config :ancient-one) "Cthulu")
            (doseq [investigator (config :investigators)]
              (alter investigator update-in [:sanity :maximum] dec)))
          {:ancient-one (ref {})}))
