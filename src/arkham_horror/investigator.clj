(ns arkham-horror.investigator
  (:refer-clojure :exclude [name])
  (:require [arkham-horror.card :as card]))

(defn make [name stats]
  stats)

(defn name [investigator]
  "Monterey Jack")

(defn sanity [investigator]
  3)

(defn stamina [investigator]
  7)

(defn speed [investigator]
  2)

(defn sneak [investigator]
  2)

(defn fight [investigator]
  2)

(defn will [investigator]
  3)

(defn lore [investigator]
  2)

(defn luck [investigator]
  4)

(defn money [investigator]
  7)

(defn clue-tokens [investigator]
  1)

(defn cards [investigator]
  [(card/make :skill  "Fake")
   (card/make :unique "Fake")
   (card/make :unique "Fake")
   (card/make :common "Bullwhip")
   (card/make :common ".38 Revolver")])

(defn cards-from-deck [investigator deck]
  (filter #(= (card/deck %) deck) (cards investigator)))

(defn unique-items [investigator]
  (cards-from-deck investigator :unique))

(defn common-items [investigator]
  (cards-from-deck investigator :common))

(defn skills [investigator]
  (cards-from-deck investigator :skill))
