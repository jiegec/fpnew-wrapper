package fpnew

import chisel3._
import chisel3.util.HasBlackBoxResource
import chisel3.experimental._

class FPNewBlackbox(
    flen: Int = 64,
    enableVectors: Boolean = true,
    enableNanBox: Boolean = true,
    enableFP32: Boolean = true,
    enableFP64: Boolean = true,
    enableFP16: Boolean = true,
    enableFP8: Boolean = true,
    enableFP16Alt: Boolean = true,
    enableInt8: Boolean = true,
    enableInt16: Boolean = true,
    enableInt32: Boolean = true,
    enableInt64: Boolean = true,
    tagWidth: Int = 0
) extends BlackBox(
      Map(
        "FLEN" -> IntParam(flen),
        "ENABLE_VECTORS" -> IntParam(enableVectors.compare(false))
      )
    )
    with HasBlackBoxResource {
  val io = IO(new Bundle {
    val clk_i = Input(Clock())
    val rst_ni = Input(Bool())
    val operands_i = Input(UInt((flen * 3).W))
    val rnd_mode_i = Input(UInt(3.W))
    val op_i = Input(UInt(4.W))
    val op_mod_i = Input(Bool())
    val src_fmt_i = Input(UInt(3.W))
    val dst_fmt_i = Input(UInt(3.W))
    val int_fmt_i = Input(UInt(2.W))
    val vectorial_op_i = Input(Bool())
    val tag_i = Input(UInt(tagWidth.W))
    val in_valid_i = Input(Bool())
    val in_ready_o = Output(Bool())
    val flush_i = Input(Bool())
    val result_o = Output(UInt(flen.W))
    val status_o = Output(UInt(5.W))
    val tag_o = Output(UInt(tagWidth.W))
    val out_valid_o = Output(Bool())
    val out_ready_i = Input(Bool())
    val busy_o = Output(Bool())
  })

  addResource("/vsrc/FPNewBlackbox.preprocessed.sv")
}
