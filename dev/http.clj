(ns http)
;; move this atom to its own name-space
;; so that it won't get redefined when user or kaleidocs.core reloaded
(def http-app (atom nil))
