package sifive.freedom.zeowaa.e115

import freechips.rocketchip.config.Config
import freechips.rocketchip.devices.debug.{IncludeJtagDTM, JtagDTMConfig, JtagDTMKey}
import freechips.rocketchip.devices.tilelink.{DevNullParams, MaskROMParams, PeripheryMaskROMKey}
import freechips.rocketchip.diplomacy.{AddressSet, RegionType}
import freechips.rocketchip.subsystem.{PeripheryBusKey, SystemBusKey, With1TinyCore, WithDefaultMemPort, WithJtagDTM, WithNBanks, WithNBigCores, WithNBreakpoints, WithNExtTopInterrupts, WithNMemoryChannels, WithNoMMIOPort, WithNoMemPort, WithNoSlavePort, WithTimebase}
import freechips.rocketchip.system.{BaseConfig, MemPortOnlyConfig, TinyConfig}
import freechips.rocketchip.tile.XLen
import sifive.blocks.devices.gpio.{GPIOParams, PeripheryGPIOKey}
import sifive.blocks.devices.spi.{PeripherySPIKey, SPIParams}
import sifive.blocks.devices.uart.{PeripheryUARTKey, UARTParams}
import sifive.freedom.unleashed.DevKitFPGAFrequencyKey

class DefaultZeowaaConfig extends Config (
  new WithNBreakpoints(2)        ++
    new WithNExtTopInterrupts(0)   ++
    new WithJtagDTM                ++
    new TinyConfig
)

class Peripherals extends Config((site, here, up) => {
  case PeripheryGPIOKey => List(
    GPIOParams(address = BigInt(0x64002000L), width = 6)
  )
  case PeripheryMaskROMKey => List(
    MaskROMParams(address = 0x10000, name = "BootROM"))
})

class ZeowaaConfig extends Config(
  new Peripherals    ++
    new DefaultZeowaaConfig().alter((site, here, up) => {
      case JtagDTMKey => new JtagDTMConfig (
        idcodeVersion = 2,
        idcodePartNum = 0xe31,
        idcodeManufId = 0x489,
        debugIdleCycles = 5)
    })
)
