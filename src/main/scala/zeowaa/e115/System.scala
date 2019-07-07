package sifive.freedom.zeowaa.e115

import chisel3._
import devices.intel.{HasAltmemphyDDR2, HasAltmemphyDDR2Imp}
import freechips.rocketchip.config.Parameters
import freechips.rocketchip.devices.debug.{HasPeripheryDebug, HasPeripheryDebugModuleImp}
import freechips.rocketchip.devices.tilelink.{HasPeripheryMaskROMSlave, PeripheryMaskROMKey}
import freechips.rocketchip.diplomacy.{FixedClockResource, Resource, ResourceAddress, ResourceBinding}
import freechips.rocketchip.subsystem.{RocketSubsystem, RocketSubsystemModuleImp}
import sifive.blocks.devices.gpio.{HasPeripheryGPIO, HasPeripheryGPIOModuleImp}
import sifive.blocks.devices.spi.{HasPeripherySPI, HasPeripherySPIFlashModuleImp, HasPeripherySPIModuleImp, MMCDevice}
import sifive.blocks.devices.uart.{HasPeripheryUART, HasPeripheryUARTModuleImp}
import sifive.freedom.unleashed.DevKitFPGAFrequencyKey

class System(implicit p: Parameters) extends RocketSubsystem
  with HasPeripheryMaskROMSlave
  with HasPeripheryDebug
  with HasPeripherySPI
  with HasPeripheryUART
  with HasPeripheryGPIO
  with HasAltmemphyDDR2
{
  val tlclock = new FixedClockResource("tlclk", p(DevKitFPGAFrequencyKey))

  override lazy val module = new SystemModule(this)
}

class SystemModule[+L <: System](_outer: L)
  extends RocketSubsystemModuleImp(_outer)
    with HasPeripheryDebugModuleImp
    with HasPeripherySPIModuleImp
    with HasPeripheryUARTModuleImp
    with HasPeripheryGPIOModuleImp
    with HasAltmemphyDDR2Imp
{
  // Reset vector is set to the location of the mask rom
  val maskROMParams = p(PeripheryMaskROMKey)
  global_reset_vector := maskROMParams(0).address.U

  // Timer

  val rtcDivider = RegInit(0.asUInt(16.W)) // just in case, support up to 16 GHz :)
  val mhzInt = p(DevKitFPGAFrequencyKey).toInt
  // suppose, frequency in MHz is integral...
  rtcDivider := Mux(rtcDivider === (mhzInt - 1).U, 0.U, rtcDivider + 1.U)
  outer.clintOpt.foreach { clint =>
    clint.module.io.rtcTick := rtcDivider === 0.U
  }
}
