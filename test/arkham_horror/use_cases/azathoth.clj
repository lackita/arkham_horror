(ns arkham-horror.use-cases.azathoth
  (:require [clojure.test :refer :all]
            [clojure.test.check.clojure-test :refer :all]
            [arkham-horror.generators :as gen]
            [arkham-horror.board :as board]
            [arkham-horror.game :as game]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.investigator :as investigator]))

(deftest awaken
  (testing "Primary Course"
    (let [board (board/make {:ancient-one "Azathoth"})]
      (ancient-one/awaken (board :ancient-one))
      (is (game/lost? board))))
  (testing "Exceptional Course: Ancient One Defeated"
    (let [board (board/make {:ancient-one "Azathoth"})]
      (ancient-one/defeat board)
      (is (thrown? AssertionError (ancient-one/awaken (board :ancient-one)))))))

(deftest investigators-maximum-sanity-and-stamina-constant
  (checking "Primary Course" [investigator gen/investigator]
    (board/make {:ancient-one "Azathoth" :investigators [investigator]})
    (is (= (investigator/maximum-sanity investigator)
           (investigator/initial-maximum-sanity (investigator/name investigator))))))
