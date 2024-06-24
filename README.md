# Deferred JSON Builder

[Git Hub Packages](https://github.com/tpasipanodya/deferred-json-builder/packages/2182759)

Deferred JSON Builder is a tool for building JSON objects concurrently by assembling their fields via coroutines.
Unlike [aPureBase's original implementation](https://github.com/aPureBase/DeferredJsonBuilder), this fork allows
you to propagate select Coroutine context elements across all Coroutines. An example use-case for this
is propagating `ThreadLocalContextElement` that are required for use in data fetching operations.
