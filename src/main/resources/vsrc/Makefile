VSRCS = $(shell find fpnew/. -iname "*.sv" 2> /dev/null)
VSRCS += FPNewBlackbox.sv

VERILATOR_OPTS = -E +incdir+./fpnew/src/common_cells/include

FPNewBlackbox.preprocessed.sv: Makefile $(VSRCS)
	cat $(VSRCS) > cat.sv
	verilator --cc --exe $(VERILATOR_OPTS) cat.sv --top-module FPNewBlackbox > $@
	sed -i '/^`line/d' $@
	rm cat.sv
