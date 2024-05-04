package com.baz1s.cryptorrsacompose

import java.math.BigInteger

class Decoder : Cryptor() {
    override lateinit var PRS: RSAkeygen
    override var convert = BinaryConvert()
    private lateinit var messageInitialDecoder: BigInteger
    private var messageConvertedDecoder: String = ""
    private lateinit var messageCryptedDecoder: BigInteger
    override lateinit var numN: BigInteger
    override lateinit var numD: BigInteger
    override lateinit var numE: BigInteger
    override var messageCryptedAndConverted = ""



    override fun cryption() {
        this.messageCryptedDecoder = messageInitialDecoder.modPow(this.numD, this.numN)
    }

    override fun convert() {
        val messageCryptedString = this.messageCryptedDecoder.toString().substring(1, this.messageCryptedDecoder.toString().length)
        var tempACIIString = ""

        for (i in 0..<messageCryptedString.length){
            tempACIIString += messageCryptedString[i]

            if (tempACIIString.length == 4){
                this.messageConvertedDecoder += tempACIIString.getCharFromASCIIString()
                tempACIIString = ""
            }
        }
    }

    override fun convertCrypted() {
        var messageConvertedTempString = ""
        for (i in 0..<messageCryptedAndConverted.length){
            messageConvertedTempString += this.fillLeft(messageCryptedAndConverted[i].code.toString(), 4)
        }

        messageInitialDecoder = messageConvertedTempString.toBigInteger()
    }



    override fun setMessage(message: String, keyString: String) {
        this.messageInitialDecoder= message.toBigInteger()

        this.numN = keyString.split(" ")[0].toBigInteger()
        this.numD = keyString.split(" ")[1].toBigInteger()

//        this.convertCrypted()
        this.cryption()
        this.convert()
    }

    override fun getFinalMessage(): String {
        return this.messageConvertedDecoder
    }

    override fun getConvertedMessage(): String {
        return this.messageCryptedDecoder.toString()
    }

    override fun getCryptedMessage(): String {
        return this.messageInitialDecoder.toString()
    }
}