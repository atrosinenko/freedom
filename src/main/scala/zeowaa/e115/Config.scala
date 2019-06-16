package sifive.freedom.zeowaa.e115

import freechips.rocketchip.config.Config
import freechips.rocketchip.devices.debug.{JtagDTMConfig, JtagDTMKey}
import freechips.rocketchip.devices.tilelink.{MaskROMParams, PeripheryMaskROMKey}
import freechips.rocketchip.subsystem._
import freechips.rocketchip.system.{BaseConfig, TinyConfig}
import sifive.blocks.devices.gpio.{GPIOParams, PeripheryGPIOKey}

object Config {
  val defaultJTAGConfig = new JtagDTMConfig (
    idcodeVersion = 2,
    idcodePartNum = 0xe31,
    idcodeManufId = 0x489,
    debugIdleCycles = 5)
}

class TinyZeowaaConfig extends Config (
  new WithNBreakpoints(2)        ++
    new WithNExtTopInterrupts(0)   ++
    new WithJtagDTM                ++
    new TinyConfig
)

class BigZeowaaConfig extends Config (
  new WithNBreakpoints(2) ++
    new WithNExtTopInterrupts(0) ++
    new WithExtMemSize(1l << 30) ++
    new WithNMemoryChannels(1) ++
    new WithCacheBlockBytes(16) ++
    new WithNSmallCores(1) ++
    new WithJtagDTM ++
    new BaseConfig
)

class TinyPeripherals extends Config((site, here, up) => {
  case PeripheryGPIOKey => List(
    GPIOParams(address = BigInt(0x64002000L), width = 6)
  )
  case PeripheryMaskROMKey => List(
    MaskROMParams(address = 0x10000, name = "BootROM")
  )
})

class BigPeripherals extends Config((site, here, up) => {
  case PeripheryGPIOKey => List(
    GPIOParams(address = BigInt(0x64002000L), width = 6)
  )
  case PeripheryMaskROMKey => List(
    MaskROMParams(address = 0x10000, name = "BootROM")
  )
  case PeripheryBusKey =>
    up(PeripheryBusKey, site).copy(frequency = 25 * 1000000, errorDevice = None)
})

class DefaultTinyZeowaaConfig extends Config(
  new TinyPeripherals    ++
    new TinyZeowaaConfig().alter((site, here, up) => {
      case JtagDTMKey => Config.defaultJTAGConfig
    })
)

class DefaultZeowaaConfig extends Config(
  new BigPeripherals    ++
    new BigZeowaaConfig().alter((site, here, up) => {
      case JtagDTMKey => Config.defaultJTAGConfig
    })
)
