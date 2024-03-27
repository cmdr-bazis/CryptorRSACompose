package com.baz1s.cryptorrsacompose

class Decoder : Cryptor() {
    override lateinit var PRS: RSAkeygen
    override var messageInitial = ArrayList<Char>()
    override var lettersDictionary = ArrayList<CoupleString>()
    override var convert = BinaryConvert()
    override var messageConverted: String = ""
    override var messageCrypted: String = ""
    override var letterBinarySize: Int = 0


    override fun cryption() {
        for (i in messageInitial.indices){
            if (((messageInitial[i] == '1') and (PRS.getPRS()[i] == '0')) or ((messageInitial[i] == '0') and (PRS.getPRS()[i] == '1'))){
                messageCrypted += '1'
            }
            else{
                messageCrypted += '0'
            }
        }
    }

    override fun convert() {
        var index = 0
        while (index <= messageCrypted.length - letterBinarySize){
            messageConverted += this.findLetter(index)
            index += letterBinarySize
        }
    }

    override fun setMessage(message: String, keyString: String) {
        var keyList = ArrayList<String>()

        for (i in 0..<message.length){
            messageInitial.add(message[i])
        }

        for (i in 0..<4){ keyList.add(keyString.split(" ")[i]) }

        this.initializeParameters(keyList[0].toBigInteger(), keyList[1].toInt(), keyList[2].toInt(), keyList[3])
        this.cryption()
        this.convert()
    }

    override fun getFinalMessage(): String {
        return this.messageConverted
    }

}