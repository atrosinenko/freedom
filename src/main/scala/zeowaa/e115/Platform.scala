package sifive.freedom.zeowaa.e115

import chisel3._
import freechips.rocketchip.config.Parameters
import freechips.rocketchip.devices.debug.JtagDTMKey
import freechips.rocketchip.diplomacy.{FixedClockResource, LazyModule}
import freechips.rocketchip.jtag.JTAGIO
import freechips.rocketchip.util.SyncResetSynchronizerShiftReg
import shell.intel.MemIfBundle
import sifive.blocks.devices.gpio.{GPIOPins, PeripheryGPIOKey}
import sifive.blocks.devices.pinctrl.BasePin
import sifive.freedom.unleashed.DevKitFPGAFrequencyKey

class PlatformIO(implicit val p: Parameters) extends Bundle {
  val jtag = Flipped(new JTAGIO(hasTRSTn = false))
  val jtag_reset = Input(Bool())
  val mem_if = new MemIfBundle
  val gpio = new GPIOPins(() => new BasePin(), p(PeripheryGPIOKey)(0))

  val uart_rx = Input(Bool())
  val uart_tx = Output(Bool())

  val sd_cs = Output(Bool())
  val sd_sck = Output(Bool())
  val sd_mosi = Output(Bool())
  val sd_miso = Input(Bool())
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

  // UART
  io.uart_tx := sys.uart(0).txd
  sys.uart(0).rxd := RegNext(RegNext(io.uart_rx))

  // SD card
  io.sd_cs := sys.spi(0).cs(0)
  io.sd_sck := sys.spi(0).sck
  io.sd_mosi := sys.spi(0).dq(0).o
  sys.spi(0).dq(0).i := false.B
  sys.spi(0).dq(1).i := RegNext(RegNext(io.sd_miso))
  sys.spi(0).dq(2).i := false.B
  sys.spi(0).dq(3).i := false.B
}
