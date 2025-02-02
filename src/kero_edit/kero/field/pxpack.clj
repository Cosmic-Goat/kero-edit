(ns kero-edit.kero.field.pxpack
  (:require [clojure.spec.alpha :as spec]
            [org.clojars.smee.binary.core :as bin]
            [kero-edit.kero.field.head :as head]
            [kero-edit.kero.field.tile-layer :as tile-layer]
            [kero-edit.kero.field.unit :as unit]
            [kero-edit.kero.util :as util]))

(def max-unit-count
  "The maximum number of units in a PxPack field. Equivalent to the maximum of an unsigned short."
  0xFFFF)

(spec/def ::head ::head/head)
(spec/def ::tile-layers (spec/and
                         (spec/map-of (constantly true) ::tile-layer/tile-layer)
                         (spec/keys :req [::tile-layer/foreground ::tile-layer/middleground ::tile-layer/background])))
(spec/def ::units (spec/coll-of ::unit/unit :max-count max-unit-count))
(spec/def ::pxpack (spec/keys :req [::head ::tile-layers ::units]))

(def pxpack-codec
  "Codec for a PxPack file."
  (bin/ordered-map
   ::head head/head-codec
   ::tile-layers (apply bin/ordered-map (interleave tile-layer/layers (repeat tile-layer/tile-layer-codec)))
   ::units (bin/repeated unit/unit-codec :prefix :ushort-le)))
