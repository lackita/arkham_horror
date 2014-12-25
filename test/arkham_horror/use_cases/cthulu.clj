(ns arkham-horror.use-cases.cthulu
  (:require [clojure.test :refer :all]
            [clojure.test.check.clojure-test :refer :all]
            [arkham-horror.generators :as gen]
            [arkham-horror.game :as game]
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
  (checking "Primary Course: Sanity Reduced" [investigator gen/investigator]
    (dosync
     (let [board (board/make {:ancient-one "Cthulu" :investigators [investigator]})]
       (ancient-one/awaken (board :ancient-one))
       (ancient-one/attack (board :ancient-one) (board :investigators))
       (is (investigator/pending-decision investigator))
       (let [old-maximum-sanity (investigator/maximum-sanity investigator)
             old-maximum-stamina (investigator/maximum-stamina investigator)]
         (investigator/make-decision investigator :sanity)
         (is (= (investigator/maximum-sanity investigator) (dec old-maximum-sanity)))
         (is (= (investigator/maximum-stamina investigator) old-maximum-stamina)))
       (ancient-one/attack (board :ancient-one) (board :investigators))
       (let [old-maximum-sanity (investigator/maximum-sanity investigator)
             old-maximum-stamina (investigator/maximum-stamina investigator)]
         (investigator/make-decision investigator :stamina)
         (is (= (investigator/maximum-sanity investigator) old-maximum-sanity))
         (is (= (investigator/maximum-stamina investigator) (dec old-maximum-stamina))))
       (dotimes [n (investigator/maximum-sanity investigator)]
         (ancient-one/attack (board :ancient-one) (board :investigators))
         (investigator/make-decision investigator :sanity))
       (is (game/lost? board)))))
  (checking "Primary Course: Stamina Reduced" [investigator gen/investigator]
    (dosync
     (let [board (board/make {:ancient-one "Cthulu" :investigators [investigator]})]
       (ancient-one/awaken (board :ancient-one))
       (dotimes [n (investigator/maximum-stamina investigator)]
         (ancient-one/attack (board :ancient-one) (board :investigators))
         (investigator/make-decision investigator :stamina))
       (is (game/lost? board))))))
