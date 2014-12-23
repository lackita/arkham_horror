(ns arkham-horror.use-cases.azathoth-awakens
  (:require [clojure.test :refer :all]
            [arkham-horror.board :as board]
            [arkham-horror.game :as game]
            [arkham-horror.ancient-one :as ancient-one]))

(deftest awakening-destroys-world-example
  (testing "Primary Course"
    (let [board (board/make {:ancient-one "Azathoth"})]
      (ancient-one/awaken board)
      (is (game/lost? board))))
  (testing "Exceptional Course: Ancient One Defeated"
    (let [board (board/make {:ancient-one "Azathoth"})]
      (ancient-one/defeat board)
      (is (thrown? AssertionError (ancient-one/awaken board))))))
