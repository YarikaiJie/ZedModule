package com.module.kotlin

import com.module.kotlin.utils.ByteUtil
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testByteArrayToIntWithRange() {
        val testArray = byteArrayOf(0x12, 0x34, 0x56, 0x78, 0x9A, 0xBC)
        
        // 测试范围 0-3
        val result1 = ByteUtil.byteArrayToInt(testArray, 0..3)
        assertEquals(0x12345678, result1)
        
        // 测试范围 1-4
        val result2 = ByteUtil.byteArrayToInt(testArray, 1..4)
        assertEquals(0x3456789A, result2)
        
        // 测试范围 2-5
        val result3 = ByteUtil.byteArrayToInt(testArray, 2..5)
        assertEquals(0x56789ABC, result3)
        
        // 测试单字节范围 0-0
        val result4 = ByteUtil.byteArrayToInt(testArray, 0..0)
        assertEquals(0x12, result4)
        
        // 测试单字节范围 5-5
        val result5 = ByteUtil.byteArrayToInt(testArray, 5..5)
        assertEquals(0xBC, result5)
        
        // 测试范围 0-1
        val result6 = ByteUtil.byteArrayToInt(testArray, 0..1)
        assertEquals(0x1234, result6)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testByteArrayToIntWithRangeInvalidStartIndex() {
        val testArray = byteArrayOf(0x12, 0x34, 0x56, 0x78)
        // 起始下标小于 0
        ByteUtil.byteArrayToInt(testArray, -1..2)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testByteArrayToIntWithRangeInvalidEndIndex() {
        val testArray = byteArrayOf(0x12, 0x34, 0x56, 0x78)
        // 结束下标超过数组长度
        ByteUtil.byteArrayToInt(testArray, 0..4)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testByteArrayToIntWithRangeTooLarge() {
        val testArray = byteArrayOf(0x12, 0x34, 0x56, 0x78, 0x9A)
        // 范围长度超过 4 字节
        ByteUtil.byteArrayToInt(testArray, 0..4)
    }
}