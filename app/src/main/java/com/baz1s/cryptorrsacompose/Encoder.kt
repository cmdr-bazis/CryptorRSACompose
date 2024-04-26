package com.baz1s.cryptorrsacompose

import java.math.BigInteger

class Encoder() : Cryptor() {
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
    override var messageCryptedAndConverted = ""


    override fun convert(){
        var messageConvertedTempString = ""
        for (i in 0..<messageInitial.size){
            messageConvertedTempString += this.fillLeft(messageInitial[i].code.toString(), 3)
        }

        messageConverted = messageConvertedTempString.toBigInteger()
    }


    override fun cryption() {
        var messageCryptedBigInt = BigInteger.valueOf(0)

        messageCryptedBigInt = messageConverted.modPow(this.numE.toBigInteger(), this.numN)

        messageCrypted = messageCryptedBigInt.toString()
    }

    override fun convertCrypted() {
        var tempASCIIString = ""
        for (i in 0..<messageCrypted.length){
            tempASCIIString += messageCrypted[i]

            if (tempASCIIString.length == 3){
                this.messageCryptedAndConverted += tempASCIIString.getCharFromASCIIString()
                tempASCIIString = ""
            }
        }
    }

    override fun setMessage(message: String, keyString: String) {
        for (i in 0..<message.length){
            messageInitial.add(message[i])
        }

        this.numN = keyString.split(" ")[0].toBigInteger()
        this.numE = keyString.split(" ")[1].toInt()

        this.convert()
        this.cryption()
//        this.convertCrypted()
    }

    override fun getConvertedMessage(): String {
        return this.messageConverted.toString()
    }

    override fun getFinalMessage(): String {
        return this.messageCrypted
    }

    override fun getCryptedMessage(): String {
        return this.messageCrypted.toString()
    }
}
