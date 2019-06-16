// See LICENSE for license details.
// Execute in place
// Jump directly to XIP_TARGET_ADDR

  .section .text.init
  .option norvc
  .globl _start
_start:
// fill first 2^24 bytes of RAM
    li  a5,1
    slli    a5,a5,31
    li  a3,129
    slli    a3,a3,24
.L2:
    andi    a4,a5,0xff
    sb  a4,0(a5)
    addi    a5,a5,1
    bne a5,a3,.L2

  li a5, 0x64002000
  li a1, 0x0F
  li a2, 0x01
  li a3, 0x30

  li a6, 0x10
  li a7, 0x20

  sw zero, 0x38(a5) // iof_en
  sw a3,   0x04(a5) // input_en
  sw a1,   0x14(a5) // drive

// a0 <- timer
// a1 <- 0x0F
// a2 <- [state]
// a3 <- 0x30
// a4 <- [buttons]
// a5 <- [addr]
// a6 <- 0x10
// a7 <- 0x20

loop:
  li a4, 0x1000
  add a0, a0, a4
  bgtu a0, zero, loop

  lw a4,   0x00(a5) // value
  beq a4, a6, plus
  beq a4, a7, minus
  j store
plus:
  srai a2, a2, 1
  beq a2, zero, pzero
  j store
pzero:
  li a2, 0x08
  j store
minus:
  slli a2, a2, 1
  beq a2, a6, mzero
  j store
mzero:
  li a2, 0x01
store:
  sw a1,  0x08(a5) // output_en
  sw a2,  0x0c(a5) // port
  j loop

