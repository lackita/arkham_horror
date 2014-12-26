(ns arkham-horror.investigator
  (:refer-clojure :exclude [name])
  (:require [arkham-horror.card :as card]))

(defn initial-maximum-sanity [_]
  3)

(defn initial-maximum-stamina [_]
  7)

(defn make-card-decision [cards]
  (fn [investigator decision]
    {:pre [(every? (set (range (count cards))) decision)
           (= (count decision) 2)]}
    (alter investigator update-in [:cards] #(concat % (replace cards decision)))))

(defn maximum-speed [investigator]
  4)

(defn minimum-speed [investigator]
  1)

(defn maximum-fight [investigator]
  5)

(defn minimum-fight [investigator]
  2)

(defn maximum-lore [investigator]
  4)

(defn minimum-lore [investigator]
  1)

(defn make [name stats]
  {:pre [(>= (stats :speed) (minimum-speed name))
         (<= (stats :speed) (maximum-speed name))
         (>= (stats :fight) (minimum-fight name))
         (<= (stats :fight) (maximum-fight name))
         (>= (stats :lore)  (minimum-lore  name))
         (<= (stats :lore)  (maximum-lore  name))]}
  (ref (merge stats {:decision (make-card-decision [(card/make :unique "Fake")
                                                    (card/make :unique "Fake")
                                                    (card/make :unique "Fake")])
                     :cards [(card/make :skill  "Fake")
                             (card/make :common "Bullwhip")
                             (card/make :common ".38 Revolver")]
                     :sanity {:value (initial-maximum-sanity name)
                              :maximum (initial-maximum-sanity name)}
                     :stamina {:value (initial-maximum-stamina name)
                               :maximum (initial-maximum-stamina name)}})))

(defn name [investigator]
  "Monterey Jack")

(defn maximum-sanity [investigator]
  (-> @investigator :sanity :maximum))

(defn decrement-maximum-sanity [investigator]
  (when (> (maximum-sanity investigator) 0)
    (alter investigator update-in [:sanity :maximum] dec)
    (alter investigator update-in [:sanity :value] #(min % (maximum-sanity investigator)))))

(defn sanity [investigator]
  (-> @investigator :sanity :value))

(defn move-sanity [investigator delta]
  (alter investigator update-in [:sanity :value] #(-> (+ % delta)
                                                      (min (maximum-sanity investigator))
                                                      (max 0))))

(defn maximum-stamina [investigator]
  (-> @investigator :stamina :maximum))

(defn decrement-maximum-stamina [investigator]
  (when (> (maximum-stamina investigator) 0)
    (alter investigator update-in [:stamina :maximum] dec)
    (alter investigator update-in [:stamina :value] #(min % (maximum-stamina investigator)))))

(defn stamina [investigator]
  (-> @investigator :stamina :value))

(defn move-stamina [investigator delta]
  (alter investigator update-in [:stamina :value] #(-> (+ % delta)
                                                       (min (maximum-stamina investigator))
                                                       (max 0))))

(defn defeated? [investigator]
  (or (zero? (maximum-sanity investigator))
      (zero? (maximum-stamina investigator))))

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

(defn focus [investigator deltas]
  (alter investigator update-in [:speed] #(-> (+ % (or (deltas :speed-sneak) 0))
                                              (min (maximum-speed investigator))
                                              (max (minimum-speed investigator))))
  (alter investigator update-in [:fight] #(+ % (or (deltas :fight-will)  0)))
  (alter investigator update-in [:lore]  #(+ % (or (deltas :lore-luck)   0))))

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
  ((pending-decision investigator) investigator decision)
  (alter investigator assoc :decision nil))
