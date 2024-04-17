package com.baz1s.cryptorrsacompose

import java.math.BigInteger
import kotlin.math.abs

class RSAkeygen {
    private var numP = 2735
    private var numQ = 6235
    private var numN = BigInteger.valueOf(0)
    private var numE = 7
    private var convert = BinaryConvert()
    private var PRSFinalOut: String = ""
    private var firstNumber = 12
    private var range = 1500

    constructor(numP: Int, numQ: Int, numE: Int, firstNumber: Int, range: Int){
        this.numP = numP
        this.numQ = numQ
        this.numE = numE
        this.firstNumber = firstNumber
        this.range = range
    }

    constructor(firstNumber: Int, range: Int){
        this.firstNumber = firstNumber
        this.range = range
    }

    constructor(numN: BigInteger, numE: Int, firstNumber: Int, range: Int){
        this.numN = numN
        this.numE = numE
        this.firstNumber = firstNumber
        this.range = range
    }

    public fun setPRS(){
        var nextNumberBigInt = BigInteger.valueOf(firstNumber.toLong())
        var stringOut = ""
        var tempNextNumberBigIntString = ""

        for (i in 0..<range){
            nextNumberBigInt = (nextNumberBigInt.pow(this.numE) % numN)

            tempNextNumberBigIntString = nextNumberBigInt.toString()

            if (tempNextNumberBigIntString[tempNextNumberBigIntString.length - 1].toInt() % 2 == 0){
                stringOut += "0"
            }
            else{
                stringOut += "1"
            }
        }
        this.PRSFinalOut = stringOut
    }

    public fun createKey(){

    }

    public fun getPRS(): String {
        return this.PRSFinalOut
    }

    public fun checkPRS(){
        print("PSQ String: ")
        println(this.getPRS())

        var nGramList = ArrayList<Couple>()
        var nGramListOut = ArrayList<Int>()
        var numZero = 0
        var numOne = 0
        var compareFlag = true
        var tempNGram = ""
        var tempNGramNumber = 0
        var currentSize = 3

        for (i in 0..<this.PRSFinalOut.length){
            if (this.PRSFinalOut[i] == '0'){ numZero += 1 }
            else if (this.PRSFinalOut[i] == '1'){ numOne += 1 }
        }

        for (i in 4..1023){
            nGramList.add(Couple(convert.convert(10, i, 2), 0))
        }

        for (i in 0..<nGramList.size){
            print(" " + nGramList[i]._stringValue)
        }

        for (i in 0..<nGramList.size){
            for (j in 0..<this.PRSFinalOut.length - 9){
                compareFlag = true
                for (k in 0..<nGramList[i]._stringValue.length){
                    if (nGramList[i]._stringValue[k] != this.PRSFinalOut[j + k]){
                        compareFlag = false
                        break
                    }
                }
                if (compareFlag){
                    nGramList[i]._intValue += 1
                }
            }
        }

        println()

        for (i in 0..<nGramList.size){
            if (currentSize == nGramList[i]._stringValue.length){
                nGramListOut.add(nGramList[i]._intValue)
            }
            else{
                print("$currentSize: ")
                for (j in 0..<nGramListOut.size){
                    print(nGramListOut[j])
                    print(", ")
                }
                nGramListOut.clear()
                currentSize += 1
                println()
            }
//            tempNGram = nGramList[i]._stringValue
//            tempNGramNumber = nGramList[i]._intValue
//            println("$tempNGram: $tempNGramNumber")
        }

        println("Number of 0's: $numZero")
        println("Number of 1's: $numOne")
    }
}