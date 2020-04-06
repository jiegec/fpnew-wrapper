package fpnew

import chisel3._
import chisel3.util.Cat
import chisel3.util.Decoupled

class FPInput(val flen: Int) extends Bundle {
  val in1 = UInt(flen.W)
  val in2 = UInt(flen.W)
  val in3 = UInt(flen.W)
}

class FPOutput(val flen: Int) extends Bundle {
  val out = UInt(flen.W)
}

class FPNew(flen: Int) extends MultiIOModule {
  val in = IO(Flipped(Decoupled(new FPInput(flen))))
  val out = IO(Decoupled(new FPOutput(flen)))

  val blackbox = Module(new FPNewBlackbox(flen = flen))
  blackbox.io.clk_i := clock
  blackbox.io.rst_ni := ~reset.asBool()
  blackbox.io.operands_i := Cat(in.bits.in1, in.bits.in2, in.bits.in3)
  blackbox.io.in_valid_i := in.valid
  in.ready := blackbox.io.in_ready_o
  out.bits.out := blackbox.io.result_o
  out.valid := blackbox.io.out_valid_o
  blackbox.io.out_ready_i := out.ready
}

object FPNewMain extends App {
  chisel3.Driver.execute(args, () => new FPNew(flen = 64))
}
