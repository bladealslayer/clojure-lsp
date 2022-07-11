(ns clojure-lsp.nrepl
  (:require
   [borkdude.dynaload :refer [dynaload]]
   [clojure-lsp.db :as db]
   [lsp4clj.protocols.logger :as logger]))

(set! *warn-on-reflection* true)

(def start-server (dynaload 'nrepl.server/start-server))

(def cider-nrepl-handler (dynaload 'cider.nrepl/cider-nrepl-handler))

(defn ^:private repl-port []
  (:port (start-server :handler cider-nrepl-handler)))

(defn setup-nrepl [db*]
  (try
    (when-let [port (repl-port)]
      (logger/info "====== LSP nrepl server started on port" port)
      ;; components/components* only available in dev. Don't use it otherwise.
      (reset! db/db* db*)
      (swap! db/db* assoc :port port))
    (catch Throwable _
      (logger/debug "nrepl not found, skipping nrepl server start..."))))
