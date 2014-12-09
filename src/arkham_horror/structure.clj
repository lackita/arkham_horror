(ns arkham-horror.structure)

(defmacro get-path [item path]
  (if (seq path)
    `(when (and ~item ~(not (empty? path)))
       (get-path (~(symbol (str (first path) "/get")) ~item)
                 ~(rest path)))
    item))
