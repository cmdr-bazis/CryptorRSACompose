package com.baz1s.cryptorrsacompose

import java.math.BigInteger

class Decoder : Cryptor() {
    override lateinit var PRS: RSAkeygen
    override var messageInitial = ArrayList<Char>()
    private var messageInitialDecoder = BigInteger.valueOf(0)
    override var lettersDictionary = ArrayList<CoupleString>()
    override var convert = BinaryConvert()
    override var messageConverted: BigInteger = BigInteger.valueOf(0)
    private var messageConvertedDecoder = ""
    override var messageCrypted: String = ""
    private var messageCryptedDecoder: BigInteger = BigInteger.valueOf(0)
    override var letterBinarySize: Int = 0
    override var numN = BigInteger.valueOf(0)
    override var numD = BigInteger.valueOf(0)
    override var numE: Int = 0
    override var messageCryptedAndConverted = ""



    override fun cryption() {
        this.messageCryptedDecoder = messageInitialDecoder.modPow(this.numD, this.numN)
    }

    override fun convert() {
        val messageConvertedString = this.messageCryptedDecoder.toString()
        var tempACIIString = ""

        for (i in 0..<messageConvertedString.length){
            tempACIIString += messageConvertedString[i]

            if (tempACIIString.length == 3){
                this.messageConvertedDecoder += tempACIIString.getCharFromASCIIString()
                tempACIIString = ""
            }
        }
    }

    override fun convertCrypted() {
        var messageConvertedTempString = ""
        for (i in 0..<messageCryptedAndConverted.length){
            messageConvertedTempString += this.fillLeft(messageCryptedAndConverted[i].code.toString(), 3)
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
        return this.messageInitialDecoder.toString()
    }

    override fun getCryptedMessage(): String {
        return this.messageInitialDecoder.toString()
    }
}