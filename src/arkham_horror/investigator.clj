(ns arkham-horror.investigator
  (:require [arkham-horror.core :as core]))

(defn make [name {speed :speed fight :fight lore :lore}]
  {:pre [(not (@core/ancient-one :awakened)) (@core/game :begun)
         (> speed 0) (< speed 5)
         (> fight 1) (< fight 6)
         (> lore  0) (< lore  5)]}
  (ref {:name name
        :maximum-stamina (if (= (@core/ancient-one :name) "Cthulu") 6 7)
        :maximum-sanity  (if (= (@core/ancient-one :name) "Cthulu") 2 3)
        :speed speed     :sneak (- 4 speed)
        :fight fight     :will  (- 5 fight)
        :lore  lore      :luck  (- 6 lore)
        :focus 2}))

(defn get-focus-amount [deltas]
  (apply + (map #(Math/abs %) (vals deltas))))

(defn focus [investigator {speed-delta :speed-sneak
                           fight-delta :fight-will
                           lore-delta  :lore-luck
                           :as deltas}]
  {:pre [(<= (get-focus-amount deltas) (@investigator :focus))]}
  (dosync (alter investigator #(merge-with + % {:speed (or speed-delta 0)
                                                :sneak (- (or speed-delta 0))
                                                :fight (or fight-delta 0)
                                                :will  (- (or fight-delta 0))
                                                :lore  (or lore-delta 0)
                                                :luck  (- (or lore-delta 0))
                                                :focus (- (get-focus-amount deltas))}))))

(defn attack [investigator]
  (dosync (core/set-status! "Roll: "
                            "Attack"
                            '(accept-roll))))

(defn defend [investigator meter]
  (dosync (core/set-status! (clojure.string/join "\n" ["Monterey Jack:"
                                                       "\tStamina: 5/5"
                                                       "\tSanity:  2/2"
                                                       "\tSpeed:  1 <2> 3  4 "
                                                       "\tSneak:  3 <2> 1  0 "
                                                       "\tFight: <2> 3  4  5 "
                                                       "\tWill:  <3> 2  1  0 "
                                                       "\tLore:   1 <2> 3  4 "
                                                       "\tLuck:   5 <4> 3  2 "])
                            "Upkeep"
                            '(investigator/focus <investigator> {:speed-sneak <speed-delta>
                                                                 :fight-will <fight-delta>
                                                                 :lore-luck <lore-delta>}))))
