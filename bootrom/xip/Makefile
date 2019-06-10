# RISCV environment variable must be set

CC=$(RISCV)/bin/riscv64-unknown-elf-gcc
OBJCOPY=$(RISCV)/bin/riscv64-unknown-elf-objcopy
CFLAGS=-march=rv32imac -mabi=ilp32 -O2 -std=gnu11 -Wall -I. -nostartfiles -fno-common -g
LFLAGS=-static -nostdlib

dtb := $(BUILD_DIR)/$(CONFIG_PROJECT).$(CONFIG).dtb
$(dtb): $(BUILD_DIR)/$(CONFIG_PROJECT).$(CONFIG).dts
	dtc -I dts -O dtb -o $@ $<

ASM_SRC := xip.S

.PHONY: dtb
dtb: $(dtb)

elf := $(BUILD_DIR)/$(ASM_SRC).elf
$(elf): $(ASM_SRC).S $(dtb)
	$(CC) $(CFLAGS) -DXIP_TARGET_ADDR=0x20400000 -DDEVICE_TREE='"$(dtb)"' $(LFLAGS) -o $@ $<

.PHONY: elf
elf: $(elf)

bin := $(BUILD_DIR)/$(ASM_SRC).bin
$(bin): $(elf)
	$(OBJCOPY) -O binary $< $@

.PHONY: bin
bin: $(bin)

hex := $(BUILD_DIR)/$(ASM_SRC).hex
$(hex): $(bin)
	od -t x4 -An -w4 -v $< > $@

.PHONY: hex
hex: $(hex)

romgen := $(BUILD_DIR)/rom.v
$(romgen): $(hex)
	$(rocketchip_dir)/scripts/vlsi_rom_gen $(ROMCONF) $< > $@

.PHONY: romgen
romgen: $(romgen)

.PHONY: clean
clean::
	rm -rf $(hex) $(elf) 
