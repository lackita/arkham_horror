(ns arkham-horror.investigator
  (:refer-clojure :exclude [name])
  (:require [arkham-horror.card :as card]))

(defn initial-maximum-sanity [_]
  3)

(defn initial-maximum-stamina [_]
  7)

(defn make [name stats]
  {:pre [(> (stats :speed) 0) (< (stats :speed) 5)
         (> (stats :fight) 1) (< (stats :fight) 6)
         (> (stats :lore)  0) (< (stats :lore)  5)]}
  (ref (merge stats {:decision [(card/make :unique "Fake")
                                (card/make :unique "Fake")
                                (card/make :unique "Fake")]
                     :cards [(card/make :skill  "Fake")
                             (card/make :common "Bullwhip")
                             (card/make :common ".38 Revolver")]
                     :sanity {:value (initial-maximum-sanity name)
                              :maximum (initial-maximum-sanity name)}
                     :stamina {:value (initial-maximum-stamina name)
                               :maximum (initial-maximum-stamina name)}})))

(defn name [investigator]
  "Monterey Jack")

(defn sanity [investigator]
  3)

(defn maximum-sanity [investigator]
  (-> @investigator :sanity :maximum))

(defn maximum-stamina [investigator]
  (-> @investigator :stamina :maximum))

(defn stamina [investigator]
  7)

(defn speed [investigator]
  (@investigator :speed))

(defn sneak [investigator]
  (- 4 (speed investigator)))

(defn fight [investigator]
  (@investigator :fight))

(defn will [investigator]
  (- 5 (fight investigator)))

(defn lore [investigator]
  (@investigator :lore))

(defn luck [investigator]
  (- 6 (lore investigator)))

(defn money [investigator]
  7)

(defn clue-tokens [investigator]
  1)

(defn cards [investigator]
  (@investigator :cards))

(defn cards-from-deck [investigator deck]
  (filter #(= (card/deck %) deck) (cards investigator)))

(defn unique-items [investigator]
  (cards-from-deck investigator :unique))

(defn common-items [investigator]
  (cards-from-deck investigator :common))

(defn skills [investigator]
  (cards-from-deck investigator :skill))

(defn pending-decision [investigator]
  (@investigator :decision))

(defn make-decision [investigator decision]
  {:pre [(every? #(and (< % (count (pending-decision investigator))) (>= % 0)) decision)
         (= (count decision) 2)]}
  (dosync (alter investigator update-in [:cards]
                 #(concat % (replace (pending-decision investigator) decision)))
          (alter investigator assoc :decision [])))
