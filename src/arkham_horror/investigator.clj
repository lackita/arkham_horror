(ns arkham-horror.investigator)

(defn get-smaller-stat [{sanity :maximum-sanity
                         stamina :maximum-stamina}]
  (if (> sanity stamina)
    :maximum-sanity
    :maximum-stamina))

(defn devoured? [investigator]
  (->> [:maximum-sanity :maximum-stamina]
       (map investigator)
       (not-any? zero?)))

(defn reduce-sanity-or-stamina [investigator]
  (update-in investigator [(get-smaller-stat investigator)] dec))

(defn make [config]
  config)

(defn speed [investigator]
  (investigator :speed))

(defn sneak [investigator]
  (investigator :sneak))

(defn fight [investigator]
  (investigator :fight))

(defn speed-sneak-slider [investigator delta]
  (merge-with + investigator {:speed delta
                              :sneak (- delta)}))

(defn fight-will-slider [investigator delta]
  (merge-with + investigator {:fight delta}))
