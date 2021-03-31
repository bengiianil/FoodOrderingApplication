package com.example.yemeksiparisuygulamasi.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.yemeksiparisuygulamasi.R
import com.example.yemeksiparisuygulamasi.SepetActivity
import com.example.yemeksiparisuygulamasi.model.Food
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.alertview_tasarim.view.*

class YemekAdapter(private var myContext:Context, private var YemekListesi:ArrayList<Food> , var counter:Int)
    :RecyclerView.Adapter<YemekAdapter.CardViewDesignHolder>(){

    inner class CardViewDesignHolder(design:View) : RecyclerView.ViewHolder(design){

        var cardViewYemek : CardView
        var textViewYemekAdi : TextView
        var imageViewYemekResim : ImageView

        init{
            cardViewYemek = design.findViewById(R.id.cardViewYemek)
            textViewYemekAdi = design.findViewById(R.id.textViewYemekAdi)
            imageViewYemekResim = design.findViewById(R.id.imageViewYemekResim)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewDesignHolder {
        val cardDesign = LayoutInflater.from(myContext).inflate(R.layout.yemek_tasarim, parent, false)
        return CardViewDesignHolder(cardDesign)
    }

    override fun onBindViewHolder(holder: CardViewDesignHolder, position: Int) {
        val yemek = YemekListesi.get(position)

        holder.textViewYemekAdi.text = "${yemek.yemek_adi}"

        val url = "http://kasimadalan.pe.hu/yemekler/resimler/${yemek.yemek_resim_adi}"
        Picasso.get().load(url).into(holder.imageViewYemekResim)


        holder.cardViewYemek.setOnClickListener {

            alertView(yemek)
        }

    }

    override fun getItemCount(): Int {
        return YemekListesi.size
    }

    fun alertView(yemek :Food){

        val design = LayoutInflater.from(myContext).inflate(R.layout.alertview_tasarim, null)

        val alert = AlertDialog.Builder(myContext)
                .setTitle("Ürün Detayı")
                .setView(design)

        counter = 1
        design.textViewSiparisAdet.text = counter.toString()

        design.textViewAlert.text = "${yemek.yemek_adi} - ${yemek.yemek_fiyat} \u20ba"

        val url = "http://kasimadalan.pe.hu/yemekler/resimler/${yemek.yemek_resim_adi}"
        Picasso.get().load(url).into(design.imageView)

        design.imageButtonAdd.setOnClickListener {
            counter++
            design.textViewSiparisAdet.text = counter.toString()
        }
        design.imageButtonRemove.setOnClickListener {
            if(counter != 1) {
                counter--
                design.textViewSiparisAdet.text = counter.toString()
            }
        }


        alert.setPositiveButton("Sepete Ekle"){ dialogInterface, i->

            Toast.makeText(myContext, "${yemek.yemek_adi} Sepete Eklendi", Toast.LENGTH_SHORT).show()

            addToBasket(yemek)

            notifyDataSetChanged()
        }
        alert.setNegativeButton("İptal"){ dialogInterface, i->

            Toast.makeText(myContext, "Seçim İptal Edildi", Toast.LENGTH_SHORT).show()
            dialogInterface.dismiss()
        }

        alert.create().show()

    }

    fun addToBasket(yemek: Food){

        val webServiceUrl = "http://kasimadalan.pe.hu/yemekler/insert_sepet_yemek.php"

        val requestToUrl = object : StringRequest(Request.Method.POST, webServiceUrl, { responseOfUrl ->

            Log.e("Sepete Ekleme:", responseOfUrl)

        }, Response.ErrorListener {
            Toast.makeText(myContext, "Hata oluştu", Toast.LENGTH_SHORT).show()
        }){

            override fun getParams(): MutableMap<String, String> {

                val parameter = HashMap<String, String>()

                parameter["yemek_siparis_adet"] = counter.toString()
                parameter["yemek_id"] = yemek.yemek_id.toString()
                parameter["yemek_adi"] = yemek.yemek_adi
                parameter["yemek_resim_adi"] = yemek.yemek_resim_adi
                parameter["yemek_fiyat"] = yemek.yemek_fiyat.toString()

                return parameter
            }
        }

        Volley.newRequestQueue(myContext).add(requestToUrl)
    }
}