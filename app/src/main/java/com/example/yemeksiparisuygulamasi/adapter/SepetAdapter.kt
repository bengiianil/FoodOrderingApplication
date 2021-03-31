package com.example.yemeksiparisuygulamasi.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.yemeksiparisuygulamasi.R
import com.example.yemeksiparisuygulamasi.model.Basket
import com.example.yemeksiparisuygulamasi.model.Food
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject

class SepetAdapter(private var myContext:Context, private var SepetListesi:ArrayList<Basket>, private var totalPriceView: TextView)
    :RecyclerView.Adapter<SepetAdapter.CardViewDesignHolder>(){

    inner class CardViewDesignHolder(design:View) : RecyclerView.ViewHolder(design){

        var cardViewSepet : CardView

        var imageViewYemekResim : ImageView
        var imageButtonDelete : ImageButton


        var textViewYemekAdi : TextView
        var textViewSiparisAdet : TextView
        var textViewTotalFiyat : TextView

        init{
            cardViewSepet = design.findViewById(R.id.cardViewSepet)

            imageViewYemekResim = design.findViewById(R.id.imageViewYemekResim)
            imageButtonDelete = design.findViewById(R.id.imageButtonDelete)

            textViewYemekAdi = design.findViewById(R.id.textViewYemekAdi)
            textViewSiparisAdet = design.findViewById(R.id.textViewSiparisAdet)
            textViewTotalFiyat = design.findViewById(R.id.textViewTotalFiyat)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewDesignHolder {
        val design = LayoutInflater.from(myContext).inflate(R.layout.sepet_tasarim, parent, false)
        return CardViewDesignHolder(design)
    }

    override fun onBindViewHolder(holder: CardViewDesignHolder, position: Int) {
        val sepet = SepetListesi.get(position)


        holder.textViewYemekAdi.text = sepet.yemekler.yemek_adi

        holder.textViewSiparisAdet.text = "${sepet.yemek_siparis_adet}"

        holder.textViewTotalFiyat.text = "${(sepet.yemek_siparis_adet)*(sepet.yemekler.yemek_fiyat)} \u20BA"


        val url = "http://kasimadalan.pe.hu/yemekler/resimler/${sepet.yemekler.yemek_resim_adi}"
        Picasso.get().load(url).into(holder.imageViewYemekResim)

        holder.imageButtonDelete.setOnClickListener {

            val webServiceUrl = "http://kasimadalan.pe.hu/yemekler/delete_sepet_yemek.php"

            val requestToUrl = object : StringRequest(Request.Method.POST, webServiceUrl, Response.Listener{ responseOfUrl ->

                Toast.makeText(myContext, "${sepet.yemekler.yemek_adi} Sepetten silindi.", Toast.LENGTH_SHORT).show()
                getBasket()

            }, Response.ErrorListener {
                Log.e("Sil","Hata")
                Toast.makeText(myContext, "Hata oluştu", Toast.LENGTH_SHORT).show()
            }) {

                override fun getParams(): MutableMap<String, String> {

                    val parameter = HashMap<String, String>()

                    parameter["yemek_id"] = ((sepet.yemekler).yemek_id).toString()

                    return parameter
                }
            }
            //SepetListesi.clear()

            notifyDataSetChanged()

            Volley.newRequestQueue(myContext).add(requestToUrl)
        }
        holder.cardViewSepet.setOnClickListener {

        }

    }

    override fun getItemCount(): Int {
        return SepetListesi.size
    }

    fun getBasket() {
        val webServiceUrl = "http://kasimadalan.pe.hu/yemekler/tum_sepet_yemekler.php"

        val requestToUrl = StringRequest(Request.Method.GET, webServiceUrl, { responseOfUrl ->

        var tempSepetListesi = ArrayList<Basket>()
            tempSepetListesi = arrayListOf()
            tempSepetListesi.clear()

            var yemek_total_fiyat = 0

            try {
                val jsonObj = JSONObject(responseOfUrl)
                val sepet = jsonObj.getJSONArray("sepet_yemekler")


                for (index in 0 until sepet.length()) {

                    val s = sepet.getJSONObject(index)

                    val yemek_id = s.getInt("yemek_id")
                    val yemek_adi = s.getString("yemek_adi")
                    val yemek_resim_adi = s.getString("yemek_resim_adi")
                    val yemek_fiyat = s.getInt("yemek_fiyat")
                    val yemek_siparis_adet = s.getInt("yemek_siparis_adet")

                    yemek_total_fiyat += (yemek_fiyat)*(yemek_siparis_adet)

                    val yemek = Food(yemek_id, yemek_adi, yemek_resim_adi, yemek_fiyat)
                    val sepettekiler = Basket(yemek, yemek_siparis_adet)
                    val sepettekiler2 = HashMap<Int, Int>()
                    sepettekiler2[yemek_id] = yemek_siparis_adet


                    tempSepetListesi.add(sepettekiler)
                }


            }
            catch (e: JSONException) {
                e.printStackTrace()
            }

            if(tempSepetListesi.isEmpty()){
                totalPriceView.text = "Sepetinizde Ürün Bulunmamaktadır"
            }
            else{
                totalPriceView.text = "Genel Toplam: ${yemek_total_fiyat} \u20ba"
            }
            SepetListesi = tempSepetListesi

            notifyDataSetChanged()
        }, Response.ErrorListener { Log.e("Hata", "Tüm Yemek Okuma") })

        Volley.newRequestQueue(myContext).add(requestToUrl)
    }

}