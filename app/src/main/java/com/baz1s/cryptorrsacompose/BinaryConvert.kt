package com.baz1s.cryptorrsacompose

import java.math.BigInteger
import kotlin.math.pow

class BinaryConvert {

    private fun convertToTen(numberBase: Int, number: Int): Int {
        var finalSumm = 0

        for (i in 0..<number.toString().length) {
            finalSumm += (number.toString()[i].digitToInt() * numberBase.toDouble().pow(number.toString().length - 1 - i)).toInt()
        }
        return finalSumm
    }

    private fun convertToAny(number: Int, finalNumberBase: Int): String {
        var _number = number
        var tempInt = 0
        var numberStringOut = ""
        var numberArrayList = ArrayList<Int>()

        while (_number != 0){
            numberArrayList.add((_number % finalNumberBase))
            _number /= finalNumberBase
        }

        for (i in 0..<numberArrayList.size / 2){
            tempInt = numberArrayList[numberArrayList.size - i - 1]
            numberArrayList[numberArrayList.size - i - 1] = numberArrayList[i]
            numberArrayList[i] = tempInt
        }

        for (i in 0..<numberArrayList.size){
            numberStringOut += numberArrayList[i].toString()
        }

        return numberStringOut
    }

    public fun convert(numberBase: Int, number: Int, finalNumberBase: Int): String {
        var tempInt: Int = 0
        if (numberBase == 10){
            return convertToAny(number, finalNumberBase)
        }
        else {
            tempInt = convertToTen(numberBase, number)
            return convertToAny(tempInt, finalNumberBase)
        }
    }
}