(ns arkham-horror.use-cases.cthulu
  (:require [clojure.test :refer :all]
            [clojure.test.check.clojure-test :refer :all]
            [arkham-horror.generators :as gen]
            [arkham-horror.board :as board]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.investigator :as investigator]))

(deftest investigators-max-sanity-and-stamina-reduced
  (checking "Primary Course" [investigator gen/investigator]
    (dosync
     (board/make {:ancient-one "Cthulu" :investigators [investigator]})
     (is (= (investigator/maximum-sanity investigator)
            (dec (investigator/initial-maximum-sanity (investigator/name investigator)))))
     (is (= (investigator/maximum-stamina investigator)
            (dec (investigator/initial-maximum-stamina (investigator/name investigator)))))))
  (checking "Exceptional Course: Ancient One Not Cthulu" [investigator gen/investigator]
    (dosync
     (board/make {:ancient-one "Azathoth" :investigators [investigator]})
     (is (= (investigator/maximum-sanity investigator)
            (investigator/initial-maximum-sanity (investigator/name investigator)))))))

(deftest cthulu-wins
  (checking "Primary Course" [investigator gen/investigator]
    (dosync
     (let [board (board/make {:ancient-one "Cthulu" :investigators [investigator]})]
       (ancient-one/awaken (board :ancient-one))
       (ancient-one/attack (board :ancient-one) (board :investigators))
       (is (not (empty? (investigator/pending-decision investigator))))
       (let [old-maximum-sanity (investigator/maximum-sanity investigator)]
         (investigator/make-decision investigator :sanity)
         )))))
