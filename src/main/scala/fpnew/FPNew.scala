package fpnew

import chisel3._
import chisel3.util.Decoupled
import chisel3.experimental.ChiselEnum

class FPConfig(
    val fLen: Int = 64,
    val tagWidth: Int = 1,
    val pipelineStages: Int = 0
)

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

object FPRoundingMode extends ChiselEnum {
  val RNE, RTZ, RDN, RUP, RMM, DYN = Value
}

// For meanings of these fields, visit https://github.com/pulp-platform/fpnew/blob/develop/docs/README.md
class FPRequest(implicit val config: FPConfig) extends Bundle {
  val operands = Vec(3, UInt(config.fLen.W))
  val roundingMode = FPRoundingMode()
  val op = FPOp()
  val opModifier = Bool()
  val srcFormat = FPFloatFormat()
  val dstFormat = FPFloatFormat()
  val intFormat = FPIntFormat()
  val vectorialOp = Bool()
  val tag = UInt(config.tagWidth.W)
}

class FPResponse(implicit val config: FPConfig) extends Bundle {
  val result = UInt(config.fLen.W)
  val status = new Bundle {
    val nz = Bool() // Invalid
    val dz = Bool() // Divide by zero
    val of = Bool() // Overflow
    val uf = Bool() // Underflow
    val nx = Bool() // Inexact
  }
  val tag = UInt(config.tagWidth.W)
}

class FPIO(implicit val config: FPConfig) extends Bundle {
  val req = Flipped(Decoupled(new FPRequest()))
  val resp = Decoupled(new FPResponse())
  val flush = Input(Bool())
  val busy = Output(Bool())
}

class FPNew(implicit val config: FPConfig) extends Module {

  val io = IO(new FPIO())

  private val blackbox = Module(
    new FPNewBlackbox(
      flen = config.fLen,
      tagWidth = config.tagWidth,
      pipelineStages = config.pipelineStages
    )
  )

  // clock & reset
  blackbox.io.clk_i := clock
  blackbox.io.rst_ni := ~reset.asBool()
  // request
  blackbox.io.operands_i := io.req.bits.operands.asUInt()
  blackbox.io.rnd_mode_i := io.req.bits.roundingMode.asUInt()
  blackbox.io.op_i := io.req.bits.op.asUInt()
  blackbox.io.op_mod_i := io.req.bits.opModifier
  blackbox.io.src_fmt_i := io.req.bits.srcFormat.asUInt()
  blackbox.io.dst_fmt_i := io.req.bits.dstFormat.asUInt()
  blackbox.io.int_fmt_i := io.req.bits.intFormat.asUInt()
  blackbox.io.vectorial_op_i := io.req.bits.vectorialOp
  blackbox.io.tag_i := io.req.bits.tag
  blackbox.io.in_valid_i := io.req.valid
  io.req.ready := blackbox.io.in_ready_o
  // response
  io.resp.bits.result := blackbox.io.result_o
  io.resp.bits.status := blackbox.io.status_o.asTypeOf(io.resp.bits.status)
  io.resp.bits.tag := blackbox.io.tag_o
  io.resp.valid := blackbox.io.out_valid_o
  blackbox.io.out_ready_i := io.resp.ready
  // flush & flush
  blackbox.io.flush_i := io.flush
  io.busy := blackbox.io.busy_o
}

object FPNewMain extends App {
  implicit private val config = new FPConfig(fLen = 64, pipelineStages = 2)
  chisel3.Driver.execute(args, () => new FPNew())
}
