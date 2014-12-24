(ns arkham-horror.use-cases.azathoth
  (:require [clojure.test :refer :all]
            [clojure.test.check.clojure-test :refer :all]
            [clojure.test.check.generators :as gen']
            [arkham-horror.generators :as gen]
            [arkham-horror.board :as board]
            [arkham-horror.game :as game]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.investigator :as investigator]))

(deftest awaken
  (testing "Primary Course"
    (dosync
     (let [board (board/make {:ancient-one "Azathoth"})]
       (is (not (game/lost? board)))
       (ancient-one/awaken (board :ancient-one))
       (is (game/lost? board)))))
  (testing "Exceptional Course: Ancient One Not Azathoth"
    (dosync
     (let [board (board/make {:ancient-one "Cthulu"})]
       (ancient-one/awaken (board :ancient-one))
       (is (not (game/lost? board)))))))
