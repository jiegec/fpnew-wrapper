package fpnew

import chisel3._
import chisel3.util.Cat
import chisel3.util.Decoupled
import chisel3.experimental.ChiselEnum

class FPConfig(val flen: Int = 64, val tagWidth: Int = 1)

object FPFloatFormat extends ChiselEnum {
  val Fp32, Fp64, Fp16, Fp8, Fp16Alt = Value
}

object FPIntFormat extends ChiselEnum {
  val Int8, Int16, Int32, Int64 = Value
}

object FPOp extends ChiselEnum {
  val FMADD, FNMSUB, ADD, MUL, DIV, SQRT, SGNJ, MINMAX, CMP, CLASSIFY, F2F, F2I,
      I2F, CPKAB, CPKCD = Value
}

object FPRoundMode extends ChiselEnum {
  val RNE, RTZ, RDN, RUP, RMM, DYN = Value
}

class FPInput(val config: FPConfig) extends Bundle {
  val in1 = UInt(config.flen.W)
  val in2 = UInt(config.flen.W)
  val in3 = UInt(config.flen.W)
  val roundMode = FPRoundMode()
  val op = FPOp()
  val opModifier = Bool()
  val srcFormat = FPFloatFormat()
  val dstFormat = FPFloatFormat()
  val intFormat = FPIntFormat()
  val vectorial = Bool()
  val tag = UInt(config.tagWidth.W)
}

class FPOutput(val config: FPConfig) extends Bundle {
  val out = UInt(config.flen.W)
  val status = new Bundle {
    val nz = Bool() // Invalid
    val dz = Bool() // Divide by zero
    val of = Bool() // Overflow
    val uf = Bool() // Underflow
    val nx = Bool() // Inexact
  }
  val tag = UInt(config.tagWidth.W)
}

class FPNew(config: FPConfig) extends MultiIOModule {
  val in = IO(Flipped(Decoupled(new FPInput(config))))
  val out = IO(Decoupled(new FPOutput(config)))
  val flush = IO(Input(Bool()))
  val busy = IO(Output(Bool()))

  val blackbox = Module(new FPNewBlackbox(flen = config.flen))
  blackbox.io.clk_i := clock
  blackbox.io.rst_ni := ~reset.asBool()
  blackbox.io.operands_i := Cat(in.bits.in3, in.bits.in2, in.bits.in1)
  blackbox.io.rnd_mode_i := in.bits.roundMode.asUInt()
  blackbox.io.op_i := in.bits.op.asUInt()
  blackbox.io.op_mod_i := in.bits.opModifier
  blackbox.io.src_fmt_i := in.bits.srcFormat.asUInt()
  blackbox.io.dst_fmt_i := in.bits.dstFormat.asUInt()
  blackbox.io.int_fmt_i := in.bits.intFormat.asUInt()
  blackbox.io.vectorial_op_i := in.bits.vectorial
  blackbox.io.tag_i := in.bits.tag
  blackbox.io.in_valid_i := in.valid
  in.ready := blackbox.io.in_ready_o
  blackbox.io.flush_i := flush
  out.bits.out := blackbox.io.result_o
  out.bits.status := blackbox.io.status_o.asTypeOf(out.bits.status)
  out.bits.tag := blackbox.io.tag_o
  out.valid := blackbox.io.out_valid_o
  blackbox.io.out_ready_i := out.ready
  busy := blackbox.io.busy_o
}

object FPNewMain extends App {
  chisel3.Driver.execute(args, () => new FPNew(new FPConfig(flen = 64)))
}
