(ns arkham-horror.use-cases.azathoth
  (:require [clojure.test :refer :all]
            [clojure.test.check.clojure-test :refer :all]
            [arkham-horror.generators :as gen]
            [arkham-horror.board :as board]
            [arkham-horror.game :as game]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.investigator :as investigator]))

(deftest awaken
  (checking "Primary Course" [investigator gen/investigator]
    (dosync
     (let [board (board/make {:ancient-one "Azathoth" :investigators [investigator]})]
       (is (not (game/lost? board)))
       (ancient-one/awaken (board :ancient-one))
       (is (game/lost? board)))))
  (checking "Exceptional Course: Ancient One Not Azathoth" [investigator gen/investigator]
    (dosync
     (let [board (board/make {:ancient-one "Cthulu" :investigators [investigator]})]
       (ancient-one/awaken (board :ancient-one))
       (is (not (game/lost? board)))))))
