package sifive.freedom.zeowaa.e115

import chisel3._
import devices.intel.{HasAltmemphyDDR2, HasAltmemphyDDR2Imp}
import freechips.rocketchip.config.Parameters
import freechips.rocketchip.devices.debug.{HasPeripheryDebug, HasPeripheryDebugModuleImp}
import freechips.rocketchip.devices.tilelink.{HasPeripheryMaskROMSlave, PeripheryMaskROMKey}
import freechips.rocketchip.subsystem.{RocketSubsystem, RocketSubsystemModuleImp}
import sifive.blocks.devices.gpio.{HasPeripheryGPIO, HasPeripheryGPIOModuleImp}

class System(implicit p: Parameters) extends RocketSubsystem
  with HasPeripheryMaskROMSlave
  with HasPeripheryDebug
  with HasPeripheryGPIO
  with HasAltmemphyDDR2
{
  override lazy val module = new SystemModule(this)
}

class SystemModule[+L <: System](_outer: L)
  extends RocketSubsystemModuleImp(_outer)
    with HasPeripheryDebugModuleImp
    with HasPeripheryGPIOModuleImp
    with HasAltmemphyDDR2Imp
{
  // Reset vector is set to the location of the mask rom
  val maskROMParams = p(PeripheryMaskROMKey)
  global_reset_vector := maskROMParams(0).address.U
}
