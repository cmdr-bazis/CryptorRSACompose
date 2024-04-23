package com.baz1s.cryptorrsacompose

import java.math.BigInteger

class Decoder : Cryptor() {
    override lateinit var PRS: RSAkeygen
    override var messageInitial = ArrayList<Char>()
    override var lettersDictionary = ArrayList<CoupleString>()
    override var convert = BinaryConvert()
    override lateinit var messageConverted: BigInteger
    override var messageCrypted: String = ""
    override var letterBinarySize: Int = 0
    override lateinit var numN: BigInteger
    override lateinit var numD: BigInteger
    override var numE: Int = 0



    override fun cryption() {
//        this.messageConverted = this.numE.toBigInteger().modInverse()
    }

    override fun convert() {

    }

    override fun setMessage(message: String, keyString: String) {
        var keyList = ArrayList<String>()

        for (i in 0..<message.length){
            messageInitial.add(message[i])
        }

        this.numN = keyString.split(" ")[0].toBigInteger()
        this.numE = keyString.split(" ")[1].toInt()

        this.cryption()
        this.convert()
    }

    override fun getFinalMessage(): String {
        return this.messageConverted.toString()
    }
}