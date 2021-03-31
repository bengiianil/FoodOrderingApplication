package com.example.yemeksiparisuygulamasi.model

import java.io.Serializable

data class Basket(var yemekler: Food, var yemek_siparis_adet:Int):Serializable {
}