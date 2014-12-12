(ns arkham-horror.phase
  (:refer-clojure :exclude [get set])
  (:require [arkham-horror.investigator :as investigator]))

(defn get [game]
  (game :phase))

(defn update [game function]
  (update-in game [:phase] function))

(defn set [game phase]
  (assoc game :phase phase))

(defn make
  ([processed current remaining] {:processed-investigators processed
                                  :current-investigator current
                                  :remaining-investigators remaining})
  ([investigators] (make [] (first investigators) (rest investigators))))

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
  (merge phase (make (conj processed current) (first remaining) (rest remaining))))

(defn over? [phase]
  (nil? (phase :current-investigator)))

(defn init-investigator [phase stats]
  (investigator/update phase #(investigator/init % stats)))
