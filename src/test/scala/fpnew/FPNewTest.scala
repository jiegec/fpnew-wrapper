package fpnew

import chisel3._
import chisel3.iotesters._

class FPNewTester(c: FPNew) extends PeekPokeTester(c) {
  // 2 * 3 + 4 = 10
  // NaN-boxed
  // 2.0
  poke(c.in.bits.in1, "hFFFFFFFF40000000".U)
  // 3.0
  poke(c.in.bits.in2, "hFFFFFFFF40400000".U)
  // 4.0
  poke(c.in.bits.in3, "hFFFFFFFF40800000".U)
  poke(c.in.bits.op, FPOp.FMADD)
  poke(c.in.bits.srcFormat, FPFloatFormat.Fp32)
  poke(c.in.bits.dstFormat, FPFloatFormat.Fp32)
  poke(c.in.bits.tag, 1)
  poke(c.in.valid, true)
  poke(c.out.ready, true)
  while (peek(c.in.ready) == 0) {
    step(1)
  }
  poke(c.in.valid, 0)
  while (peek(c.out.valid) == 0) {
    step(1)
  }
  // 10.0
  expect(c.out.bits.out, "hFFFFFFFF41200000".U)
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
        "test_run_dir"
      ),
      () => new FPNew(new FPConfig(flen = 64))
    ) { c =>
      new FPNewTester(c)
    } should be(true)
  }
}
