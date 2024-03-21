package com.baz1s.cryptorrsacompose

class Encoder() : Cryptor() {
    override lateinit var PRS: RSAkeygen
    override var messageInitial = ArrayList<Char>()
    override var lettersDictionary = ArrayList<CoupleString>()
    override var convert = BinaryConvert()
    override var messageConverted: String = ""
    override var messageCrypted: String = ""
    override var letterBinarySize: Int = 0

    override fun convert(){
        this.replaceLetters()
        for (i in 0..<messageInitial.size){
            for (j in 0..<lettersDictionary.size){
                if (messageInitial[i] == lettersDictionary[j]._letterChar){
                    messageConverted += lettersDictionary[j]._binaryValue
                    break
                }
            }
        }
    }

    override fun cryption() {
        for (i in messageConverted.indices){
            if (((PRS.getPRS()[i] == '0') and (messageConverted[i] == '1')) or ((PRS.getPRS()[i] == '1') and (messageConverted[i] == '0'))){
                messageCrypted += '1'
            }
            else{
                messageCrypted += '0'
            }
        }
    }
}
