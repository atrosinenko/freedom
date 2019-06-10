package sifive.freedom.zeowaa.e115

import chisel3._
import chisel3.core.withClockAndReset
import freechips.rocketchip.config.Parameters
import ip.intel.IOBUF
import shell.intel.ZeowaaShell

class FPGAChip(override implicit val p: Parameters) extends ZeowaaShell {
  withClockAndReset(cpu_clock, cpu_rst) {
    val dut = Module(new Platform)

    dut.io.jtag.TCK := jtag_tck
    dut.io.jtag.TDI := jtag_tdi
    IOBUF(jtag_tdo, dut.io.jtag.TDO)
    dut.io.jtag.TMS := jtag_tms
    dut.io.jtag_reset := jtag_rst

    Seq(led_0, led_1, led_2, led_3) zip dut.io.gpio.pins foreach {
      case (led, pin) =>
        led := ~Mux(pin.o.oe, pin.o.oval, false.B)
    }

    dut.io.gpio.pins.foreach(_.i.ival := false.B)
    dut.io.gpio.pins(4).i.ival := key1
    dut.io.gpio.pins(5).i.ival := key2
  }
}
