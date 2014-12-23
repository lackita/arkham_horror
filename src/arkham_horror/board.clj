(ns arkham-horror.board)

(defn make [config]
  (dosync (when (= (config :ancient-one) "Cthulu")
            (doseq [investigator (config :investigators) meter [:sanity :stamina]]
              (alter investigator update-in [meter :maximum] dec)))
          {:ancient-one (ref {})}))
