package fpnew

import chisel3._
import chisel3.iotesters._

class FPNewTester(c: FPNew) extends PeekPokeTester(c) {
  def waitAndExpect(num: String) {
    poke(c.io.req.valid, true)
    poke(c.io.resp.ready, true)
    step(1)
    while (peek(c.io.req.ready) == 0) {
      step(1)
    }
    poke(c.io.req.valid, false)
    while (peek(c.io.resp.valid) == 0) {
      step(1)
    }
    expect(c.io.resp.bits.result, num.U)
    poke(c.io.resp.ready, false)
    poke(c.io.req.valid, false)
  }

  // 2 * 3 + 4 = 10
  // NaN-boxed
  // 2.0
  poke(c.io.req.bits.operands(0), "hFFFFFFFF40000000".U)
  // 3.0
  poke(c.io.req.bits.operands(1), "hFFFFFFFF40400000".U)
  // 4.0
  poke(c.io.req.bits.operands(2), "hFFFFFFFF40800000".U)
  poke(c.io.req.bits.op, FPOp.FMADD)
  poke(c.io.req.bits.srcFormat, FPFloatFormat.Fp32)
  poke(c.io.req.bits.dstFormat, FPFloatFormat.Fp32)
  poke(c.io.req.bits.tag, 1)
  // 10.0
  waitAndExpect("hFFFFFFFF41200000")

  // packed 2 fp32
  // 2.0, 3.0
  poke(c.io.req.bits.operands(0), "h4000000040400000".U)
  // 4.0, 5.0
  poke(c.io.req.bits.operands(1), "h4080000040A00000".U)
  // 6.0, 7.0
  poke(c.io.req.bits.operands(2), "h40C0000040E00000".U)
  poke(c.io.req.bits.op, FPOp.FMADD)
  poke(c.io.req.bits.vectorialOp, true)
  poke(c.io.req.bits.srcFormat, FPFloatFormat.Fp32)
  poke(c.io.req.bits.dstFormat, FPFloatFormat.Fp32)
  // 14.0, 22.0
  waitAndExpect("h4160000041B00000")

  // convert float to int
  poke(c.io.req.bits.op, FPOp.F2I)
  poke(c.io.req.bits.intFormat, FPIntFormat.Int32)
  waitAndExpect("h0000000200000003")
}

class FPNewUnitTest extends ChiselFlatSpec {
  "Basic test using Driver.execute" should "work" in {
    iotesters.Driver.execute(
      Array(
        "--generate-vcd-output",
        "on",
        "--backend-name",
        "verilator",
        "--top-name",
        "FPNew",
        "--target-dir",
        "test_run_dir",
        "--more-vcs-flags", // passed to verilator in fact
        "-Wno-BLKANDNBLK" // required when pipelineStages > 0
      ),
      () => new FPNew()(new FPConfig(fLen = 64, pipelineStages = 2))
    ) { c =>
      new FPNewTester(c)
    } should be(true)
  }
}
