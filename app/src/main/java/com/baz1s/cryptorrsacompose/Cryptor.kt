package com.baz1s.cryptorrsacompose

import java.nio.file.Files
import java.nio.file.Paths
import java.math.BigInteger

abstract class Cryptor {
    protected abstract var PRS: RSAkeygen
    protected abstract var messageInitial: ArrayList<Char> //Original message
    protected abstract var lettersDictionary: ArrayList<CoupleString>
    protected abstract var convert: BinaryConvert
    protected abstract var messageConverted: BigInteger //Message converted to binary state
    protected abstract var messageCrypted: String //Final message
    protected abstract var letterBinarySize: Int //Max size of binary value of letter in alphabet, used for dictionary filling
    protected abstract var numN: BigInteger
    protected abstract var numD: BigInteger
    protected abstract var numE: Int


    protected fun fillLeft(string: String, number: Int): String {
        var stringOut = ""
        for (i in 0..<number - string.length){
            stringOut += "0"
        }
        stringOut += string
        return stringOut
    }

    protected fun inputMessage(path: String){
        var lines = Files.readAllLines(Paths.get(path))
        var tempMessage = ""

        for (i in 0..<lines.size){
            tempMessage += lines[i]
        }

        for (i in 0..<tempMessage.length){
            messageInitial.add(tempMessage[i])
        }
    }

    protected abstract fun convert()
    protected abstract fun cryption()

    public abstract fun setMessage(message: String, keyString: String)

    public open fun getFinalMessage(): String {
        return this.messageCrypted
    }

    public fun getConvertedMessage(): String {
        return this.messageConverted.toString()
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