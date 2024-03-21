package com.baz1s.cryptorrsacompose

class BinaryConvert {
    public fun pow(number: Long, pow: Int): Long {
        var _number: Long = number
        if (pow == 0){
            _number = 1
        }
        else{
            for (i in 0..<pow - 1){
                _number *= number
            }
        }

        return _number
    }

    private fun convertToTen(numberBase: Long, number: Long): Long {
        var finalSumm: Long = 0
        var tempIndex = 0
        var tempPow = 0
        for (i in 0..<number.toString().length) {
            finalSumm += number.toString()[i].digitToInt() * pow(numberBase, number.toString().length - 1 - i)
        }
        return finalSumm
    }

    private fun convertToAny(number: Long, finalNumberBase: Long): String {
        var _number = number
        var tempInt = 0
        var numberStringOut = ""
        var numberArrayList = ArrayList<Int>()

        while (_number.toInt() != 0){
            numberArrayList.add((_number % finalNumberBase).toInt())
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

    public fun convert(numberBase: Long, number: Long, finalNumberBase: Long): String {
        var tempInt: Long = 0
        if (numberBase.toInt() == 10){
            return convertToAny(number, finalNumberBase)
        }
        else {
            tempInt = convertToTen(numberBase, number)
            return convertToAny(tempInt, finalNumberBase)
        }
    }
}