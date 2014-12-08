(ns arkham-horror.phase-test
  (:require [clojure.test :refer :all]
            [arkham-horror.game :as game]
            [arkham-horror.phase :as phase]
            [arkham-horror.investigator :as investigator]))

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
         (map investigator/make (repeat 2 "Monterey Jack")))))

(deftest advance-test
  (is (nil? (-> (phase/update single-investigator phase/advance)
                phase/get :current-investigator)))
  (is (= (-> (phase/update single-investigator phase/advance) phase/get :processed-investigators)
         [(investigator/make "Monterey Jack")])))
