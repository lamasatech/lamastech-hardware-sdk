package com.lamastech.rfidsample.rfid

sealed class SerialPath(val path:String,val bauteRate:Int) {
    object ttyS1 : SerialPath("/dev/ttyS1",9600)
    object ttyS3 : SerialPath("/dev/ttyS3",9600)
    object ttyS4 : SerialPath("/dev/ttyS4",115200)
}

