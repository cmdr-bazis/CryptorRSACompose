package com.baz1s.cryptorrsacompose

import java.nio.file.Files
import java.nio.file.Paths
import java.math.BigInteger

abstract class Cryptor {
    protected abstract var PRS: RSAkeygen
    protected abstract var convert: BinaryConvert
    protected abstract var numN: BigInteger
    protected abstract var numD: BigInteger
    protected abstract var numE: BigInteger
    protected abstract var messageCryptedAndConverted: String


    protected fun fillLeft(string: String, number: Int): String {
        var stringOut = ""
        for (i in 0..<number - string.length){
            stringOut += "0"
        }
        stringOut += string
        return stringOut
    }


    protected abstract fun convert()
    protected abstract fun cryption()

    protected abstract fun convertCrypted()

    public abstract fun setMessage(message: String, keyString: String)

    public abstract fun getFinalMessage(): String

    public abstract fun getConvertedMessage(): String

    public abstract fun getCryptedMessage(): String

    protected fun String.getCharFromASCIIString(): Char {
        var intOut = 0
        var tempMultiply = "1"
        for (i in 0..<this.length){
            intOut += this[this.length - i - 1].digitToInt() * tempMultiply.toInt()
            tempMultiply += "0"
        }
        return intOut.toChar()
//        return (this[0].digitToInt() * 1000 + this[1].digitToInt() * 100 + this[2].digitToInt() * 10 + this[3].digitToInt()).toChar()
    }

    public fun getGamma(): String {
        return this.PRS.getPRS()
    }

    public fun keyCheck(keyString: String): Boolean {
        val keyStringArray = keyString.split(" ")

        if ((keyStringArray.size == 2)) return true
        else return false
    }
}