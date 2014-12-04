(ns arkham-horror.phase)

(defn start [game]
  (assoc game :phase {:investigator 0}))

(defn end [game]
  (dissoc game :phase))

(defn advance [game]
  (update-in game [:phase :investigator] inc))

(defn investigator [game]
  (let [position (-> game :phase :investigator)
        investigators (game :investigators)]
    (when (and position (< position (count investigators)))
      (nth investigators position))))
