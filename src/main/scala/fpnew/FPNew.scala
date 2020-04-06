package fpnew

import chisel3._
import chisel3.util.Cat

class FPNew(flen: Int) extends MultiIOModule {
  val in1 = IO(Input(UInt(flen.W)))
  val in2 = IO(Input(UInt(flen.W)))
  val in3 = IO(Input(UInt(flen.W)))

  val blackbox = Module(new FPNewBlackbox(flen = flen))
  blackbox.io.clk_i := clock
  blackbox.io.rst_ni := ~reset.asBool()
  blackbox.io.operands_i := Cat(in1, in2, in3)
}

object FPNewMain extends App {
  chisel3.Driver.execute(args, () => new FPNew(flen = 64))
}
