(ns arkham-horror.phase-test
  (:require [clojure.test :refer :all]
            [arkham-horror.game :as game]
            [arkham-horror.phase :as phase]
            [arkham-horror.investigator :as investigator]
            [arkham-horror.help :as help]))

(def single-investigator (phase/start-init (game/make {:investigators ["Monterey Jack"]})))
(def two-investigators (phase/start-init (game/make {:investigators (repeat 2 "Monterey Jack")})))

(deftest end-test
  (is (= (:investigators (phase/end single-investigator))
         [(investigator/make "Monterey Jack")]))
  (is (= (:investigators (phase/end (phase/update single-investigator phase/advance)))
         [(investigator/make "Monterey Jack")]))
  (is (= (:investigators (phase/end two-investigators))
         (map investigator/make (repeat 2 "Monterey Jack"))))
  (is (= (:investigators (phase/end (phase/update two-investigators phase/advance)))
         (map investigator/make (repeat 2 "Monterey Jack"))))
  (is (= (help/get-message (phase/end-init single-investigator)) "Investigators initialized")))

(deftest advance-test
  (is (nil? (-> (phase/update single-investigator phase/advance)
                phase/get :current-investigator)))
  (is (= (-> (phase/update single-investigator phase/advance) phase/get :processed-investigators)
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
         (investigator/focus initialized-investigator {:fight-will 2}))))
