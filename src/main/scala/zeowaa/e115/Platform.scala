package sifive.freedom.zeowaa.e115

import chisel3._
import freechips.rocketchip.config.Parameters
import freechips.rocketchip.devices.debug.JtagDTMKey
import freechips.rocketchip.diplomacy.LazyModule
import freechips.rocketchip.jtag.JTAGIO
import shell.intel.MemIfBundle
import sifive.blocks.devices.gpio.{GPIOPins, PeripheryGPIOKey}
import sifive.blocks.devices.pinctrl.BasePin

class PlatformIO(implicit val p: Parameters) extends Bundle {
  val jtag = Flipped(new JTAGIO(hasTRSTn = false))
  val jtag_reset = Input(Bool())
  val mem_if = new MemIfBundle
  val gpio = new GPIOPins(() => new BasePin(), p(PeripheryGPIOKey)(0))
}

class Platform(implicit p: Parameters) extends Module {
  val sys = Module(LazyModule(new System).module)
  override val io = IO(new PlatformIO)
  io.mem_if <> sys.mem_if

  val sjtag = sys.debug.systemjtag.get
  sjtag.reset := io.jtag_reset
  sjtag.mfr_id := p(JtagDTMKey).idcodeManufId.U(11.W)
  sjtag.jtag <> io.jtag
  io.gpio <> sys.gpio.head
}
