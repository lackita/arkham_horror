(ns arkham-horror.use-cases.cthulu
  (:require [clojure.test :refer :all]
            [clojure.test.check.clojure-test :refer :all]
            [arkham-horror.generators :as gen]
            [arkham-horror.board :as board]
            [arkham-horror.investigator :as investigator]))

(deftest investigators-max-sanity-and-stamina-reduced
  (checking "Primary Course" [investigator gen/investigator]
    (board/make {:ancient-one "Cthulu" :investigators [investigator]})
    (is (= (investigator/maximum-sanity investigator)
           (dec (investigator/initial-maximum-sanity (investigator/name investigator)))))))
