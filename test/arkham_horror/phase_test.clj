(ns arkham-horror.phase-test
  (:require [clojure.test :refer :all]
            [arkham-horror.game :as game]
            [arkham-horror.phase :as phase]
            [arkham-horror.investigator :as investigator]
            [arkham-horror.message :as message]))

(def single-investigator (phase/start (game/make {:investigators ["Monterey Jack"]})))
(def two-investigators (phase/start (game/make {:investigators ["Monterey Jack"
                                                                "Monterey Jack"]})))

(deftest end-test
  (is (= (:investigators (phase/end single-investigator))
         [(investigator/make "Monterey Jack")]))
  (is (= (:investigators (phase/end (phase/update single-investigator phase/advance)))
         [(investigator/make "Monterey Jack")]))
  (is (= (:investigators (phase/end two-investigators))
         (map investigator/make (repeat 2 "Monterey Jack"))))
  (is (= (:investigators (phase/end (phase/update two-investigators phase/advance)))
         (map investigator/make (repeat 2 "Monterey Jack"))))
  (is (= (message/get (phase/end-init single-investigator)) "Investigators initialized")))

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
