(ns arkham-horror.phase)

(defn start [{investigators :investigators :as game}]
  {:pre [(not (nil? investigators))]}
  (assoc (dissoc game :investigators)
    :phase {:processed-investigators []
            :current-investigator (first investigators)
            :remaining-investigators (rest investigators)}))

(defn all-investigators [game]
  (concat (-> game :phase :processed-investigators)
          (if (-> game :phase :current-investigator)
            [(-> game :phase :current-investigator)] [])
          (-> game :phase :remaining-investigators)))

(defn end [game]
  (assoc (dissoc game :phase)
    :investigators (all-investigators game)))

(defn advance [{{processed :processed-investigators
                 current :current-investigator
                 remaining :remaining-investigators} :phase
                :as game}]
  (update-in game [:phase]
             #(merge % {:processed-investigators (conj processed current)
                        :current-investigator (first remaining)
                        :remaining-investigators (rest remaining)})))

(defn investigator [game]
  (-> game :phase :current-investigator))

(defn active? [game]
  (game :phase))
