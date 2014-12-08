(ns arkham-horror.phase
  (:refer-clojure :exclude [get set]))

(defn get [game]
  (game :phase))

(defn update [game function]
  (update-in game [:phase] function))

(defn set [game phase]
  (assoc game :phase phase))

(defn make [investigators]
  {:processed-investigators []
   :current-investigator (first investigators)
   :remaining-investigators (rest investigators)})

(defn start [{investigators :investigators :as game}]
  {:pre [(not (nil? investigators))]}
  (set (dissoc game :investigators) (make investigators)))

(defn all-investigators [phase]
  (concat (phase :processed-investigators)
          (if (phase :current-investigator) [(phase :current-investigator)] [])
          (phase :remaining-investigators)))

(defn end [game]
  (assoc (dissoc game :phase)
    :investigators (all-investigators (get game))))

(defn advance [{processed :processed-investigators
                current :current-investigator
                remaining :remaining-investigators
                :as phase}]
  (merge phase {:processed-investigators (conj processed current)
                :current-investigator (first remaining)
                :remaining-investigators (rest remaining)}))

(defn investigator [game]
  (-> game :phase :current-investigator))

(defn active? [game]
  (game :phase))
