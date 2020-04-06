package fpnew

import chisel3.iotesters._

class FPNewTester(c: FPNew) extends PeekPokeTester(c) {

}

class FPNewUnitTest extends ChiselFlatSpec {
  "Basic test using Driver.execute" should "work" in {
    Driver.execute(
      Array(
        "--generate-vcd-output",
        "on",
        "--backend-name",
        "verilator"
      ),
      () => new FPNew(new FPConfig(flen = 64))
    ) { c =>
      new FPNewTester(c)
    } should be(true)
  }
}
