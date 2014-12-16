(ns arkham-horror.phase-test
  (:require [clojure.test :refer :all]
            [arkham-horror.game :as game]
            [arkham-horror.phase :as phase]
            [arkham-horror.investigator :as investigator]
            [arkham-horror.help :as help]
            [arkham-horror.ancient-one :as ancient-one]))

(def single-investigator (game/make {:investigators ["Monterey Jack"]}))
(def single-started (phase/start-init single-investigator))
(def two-investigators (phase/start-init (game/make {:investigators (repeat 2 "Monterey Jack")})))

(deftest end-test
  (is (= (:investigators (phase/end single-started))
         [(investigator/make "Monterey Jack")]))
  (is (= (:investigators (phase/end (phase/update single-started phase/advance)))
         [(investigator/make "Monterey Jack")]))
  (is (= (:investigators (phase/end two-investigators))
         (map investigator/make (repeat 2 "Monterey Jack"))))
  (is (= (:investigators (phase/end (phase/update two-investigators phase/advance)))
         (map investigator/make (repeat 2 "Monterey Jack"))))
  (is (= (help/get-message (phase/end-init single-started)) "Investigators initialized")))

(deftest advance-test
  (is (nil? (-> (phase/update single-started phase/advance)
                phase/get :current-investigator)))
  (is (= (-> (phase/update single-started phase/advance) phase/get :processed-investigators)
         [(investigator/make "Monterey Jack")])))

(deftest init-test
  (is (= (investigator/get (phase/init-investigator
                            (phase/make [(investigator/make "Monterey Jack")])
                            {:speed 2 :fight 3 :lore 4}))
         (investigator/init (investigator/make "Monterey Jack")
                            {:speed 2 :fight 3 :lore 4}))))

(def initialized-investigator (investigator/init (investigator/make "Monterey Jack")
                                                 {:speed 2 :fight 2 :lore 2}))
(deftest focus-test
  (is (= (investigator/get (phase/focus-investigator (phase/make [initialized-investigator])
                                                     {:fight-will 2}))
         (investigator/focus initialized-investigator {:fight-will 2})))
  (is (= (phase/get (phase/start-upkeep single-investigator))
         (phase/get (phase/start single-investigator 'end-upkeep))))
  (is (= (phase/get (phase/end-upkeep (phase/start-upkeep single-investigator)))
         (phase/get (phase/end (phase/start-upkeep single-investigator))))))
