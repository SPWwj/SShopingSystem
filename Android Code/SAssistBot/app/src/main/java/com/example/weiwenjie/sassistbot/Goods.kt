package com.example.weiwenjie.sassistbot

class Goods{
    var name:String?=null
    var des:String?=null
    var image:Int?=null
    var number: Int=0
    constructor(name:String,des:String,image:Int){
        this.name=name
        this.des=des
        this.image=image
        number+=1
    }

    fun getGoodsName():String?{
        return this.name
    }
    fun addNumbe(i :Int){
        number+=i
    }




}