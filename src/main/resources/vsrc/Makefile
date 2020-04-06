VSRCS = fpnew/src/common_cells/src/rr_arb_tree.sv \
		fpnew/src/common_cells/src/lzc.sv \
		fpnew/src/fpu_div_sqrt_mvp/hdl/defs_div_sqrt_mvp.sv \
		fpnew/src/fpu_div_sqrt_mvp/hdl/control_mvp.sv \
		fpnew/src/fpu_div_sqrt_mvp/hdl/div_sqrt_top_mvp.sv \
		fpnew/src/fpu_div_sqrt_mvp/hdl/iteration_div_sqrt_mvp.sv \
		fpnew/src/fpu_div_sqrt_mvp/hdl/norm_div_sqrt_mvp.sv \
		fpnew/src/fpu_div_sqrt_mvp/hdl/nrbd_nrsc_mvp.sv \
		fpnew/src/fpu_div_sqrt_mvp/hdl/preprocess_mvp.sv
VSRCS += fpnew/src/fpnew_pkg.sv \
		fpnew/src/fpnew_cast_multi.sv \
		fpnew/src/fpnew_classifier.sv \
		fpnew/src/fpnew_divsqrt_multi.sv \
		fpnew/src/fpnew_fma.sv \
		fpnew/src/fpnew_fma_multi.sv \
		fpnew/src/fpnew_noncomp.sv \
		fpnew/src/fpnew_opgroup_block.sv \
		fpnew/src/fpnew_opgroup_fmt_slice.sv \
		fpnew/src/fpnew_opgroup_multifmt_slice.sv \
		fpnew/src/fpnew_rounding.sv \
		fpnew/src/fpnew_top.sv
VSRCS += FPNewBlackbox.sv

VERILATOR_OPTS = -E +incdir+./fpnew/src/common_cells/include

FPNewBlackbox.preprocessed.sv: Makefile $(VSRCS)
	cat $(VSRCS) > cat.sv
	verilator --cc --exe $(VERILATOR_OPTS) cat.sv --top-module FPNewBlackbox > $@
	sed -i '/^`line/d' $@
	rm cat.sv
