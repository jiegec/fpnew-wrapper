# FPnew Wrapper

A chisel3 wrapper for [pulp-platform/fpnew](https://github.com/pulp-platform/fpnew). Use the same method how ucb-bar/ariane-wrapper wraps Ariane.

This project uses verilator to preprocess fpnew sources to get rid of compiler derivatives to create a self contained verilog file.

## Installation

You can either:

1. Add this project to subdirectory and update your `build.sbt`.
2. Run `sbt publishLocal` and use `"jia.je" %% "fpnew" % "1.0-SNAPSHOT"` in libraryDependencies.

## Caveats

1. When pipelineStages is configured other than zero and verilator is used for simulation, `-Wno-BLKANDNBLK` must be passed to verilator. See `src/test/scala/fpnew/FPNewTest.scala` for usage with chisel-iotesters.

## License

See `LICENSE`. This project wraps code from [pulp-platform/fpnew](https://github.com/pulp-platform/fpnew) which is licensed under SolderPad Hardware License.
