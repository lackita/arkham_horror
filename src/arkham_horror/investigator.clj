(ns arkham-horror.investigator
  (:require [arkham-horror.board :as board]))

(defn make [name {speed :speed fight :fight lore :lore}]
  {:pre [(not (@board/ancient-one :awakened)) (@board/game :begun)
         (> speed 0) (< speed 5)
         (> fight 1) (< fight 6)
         (> lore  0) (< lore  5)]}
  (let [investigator (ref {:name name
                           :maximum-stamina (if (= (@board/ancient-one :name) "Cthulu") 6 7)
                           :maximum-sanity  (if (= (@board/ancient-one :name) "Cthulu") 2 3)
                           :speed speed     :sneak (- 4 speed)
                           :fight fight     :will  (- 5 fight)
                           :lore  lore      :luck  (- 6 lore)
                           :focus 2})]
    (dosync (ref-set board/investigator investigator))
    investigator))

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
  (dosync (board/set-status! "Roll: "
                               "Attack"
                               '(accept-roll))))

(defn get-status [investigator]
  (clojure.string/join "\n" ["Monterey Jack:"
                             "\tStamina: 5/5"
                             ;; (str "\tStamina: " (@investigator :maximum-stamina)
                             ;;      "/"           (@investigator :maximum-stamina))
                             "\tSanity:  2/2"
                             "\tSpeed:  1 <2> 3  4 "
                             "\tSneak:  3 <2> 1  0 "
                             "\tFight: <2> 3  4  5 "
                             "\tWill:  <3> 2  1  0 "
                             "\tLore:   1 <2> 3  4 "
                             "\tLuck:   5 <4> 3  2 "]))

(defn defend [investigator meter]
  (dosync (board/set-status! (get-status investigator)
                               "Upkeep"
                               '(investigator/focus <investigator> {:speed-sneak <speed-delta>
                                                                    :fight-will <fight-delta>
                                                                    :lore-luck <lore-delta>}))))
