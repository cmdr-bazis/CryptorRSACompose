package com.baz1s.cryptorrsacompose

import java.math.BigInteger

class Encoder() : Cryptor() {
    override lateinit var PRS: RSAkeygen
    var messageInitial = ArrayList<Char>()
    override var convert = BinaryConvert()
    private lateinit var messageConverted: BigInteger
    private lateinit var messageCrypted: String
    override lateinit var numN: BigInteger
    override lateinit var numD: BigInteger
    override  lateinit var numE: BigInteger
    override lateinit var messageCryptedAndConverted: String


    override fun convert(){
        var messageConvertedTempString = ""
        for (i in 0..<messageInitial.size){
            messageConvertedTempString += this.fillLeft(messageInitial[i].code.toString(), 4)
        }

        messageConverted =  ("1$messageConvertedTempString").toBigInteger()
    }


    override fun cryption() {
        var messageCryptedBigInt = BigInteger.valueOf(0)

        messageCryptedBigInt = messageConverted.modPow(this.numE, this.numN)

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
        this.numE = keyString.split(" ")[1].toBigInteger()

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
