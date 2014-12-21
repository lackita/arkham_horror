(ns arkham-horror.use-cases.azathoth-awakens
  (:require [clojure.test :refer :all]
            [arkham-horror.board :as board]
            [arkham-horror.game :as game]
            [arkham-horror.ancient-one :as ancient-one]))

(deftest destroy-world-example
  (testing "Primary Course"
    (let [board (board/make {:ancient-one "Azathoth"})]
      (ancient-one/awaken board)
      (is (game/lost? board)))))