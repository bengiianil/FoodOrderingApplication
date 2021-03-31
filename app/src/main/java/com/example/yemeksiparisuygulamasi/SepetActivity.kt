package com.example.yemeksiparisuygulamasi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.yemeksiparisuygulamasi.adapter.SepetAdapter
import com.example.yemeksiparisuygulamasi.fragment.MenuFragment
import com.example.yemeksiparisuygulamasi.fragment.SepetFragment
import com.example.yemeksiparisuygulamasi.model.Basket
import com.example.yemeksiparisuygulamasi.model.Food
import kotlinx.android.synthetic.main.activity_sepet.*
import org.json.JSONException
import org.json.JSONObject

class SepetActivity : AppCompatActivity()  {
    private lateinit var adapter: SepetAdapter
    private lateinit var SepetListesi: ArrayList<Basket>
    private lateinit var tempFragment: Fragment


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sepet)

        toolbarSepet.title = "Sepetim"
        setSupportActionBar(toolbarSepet)

        supportFragmentManager.beginTransaction().add(R.id.ConstraitLayout, MenuFragment()).commit()

        bottomNV.setOnNavigationItemSelectedListener { menuItem ->

            if(menuItem.itemId == R.id.actionMenu){
                tempFragment = MenuFragment()
                startActivity(Intent(this@SepetActivity, MainActivity::class.java))
            }
            if(menuItem.itemId == R.id.actionSepet){
                tempFragment = SepetFragment()
                startActivity(Intent(this@SepetActivity, SepetActivity::class.java))
            }

            supportFragmentManager.beginTransaction().replace(R.id.ConstraitLayout, tempFragment).commit()

            true
        }


        RecyclerViewSepet.setHasFixedSize(true)
        RecyclerViewSepet.layoutManager = LinearLayoutManager(this)

        getBasket()

    }

    override fun onBackPressed() {
        startActivity(Intent(this@SepetActivity, MenuActivity::class.java))
    }

    fun getBasket() {
        val webServiceUrl = "http://kasimadalan.pe.hu/yemekler/tum_sepet_yemekler.php"

        val requestToUrl = StringRequest(Request.Method.GET, webServiceUrl, { responseOfUrl ->

            SepetListesi = ArrayList()
            SepetListesi.clear()

            try {
                val jsonObj = JSONObject(responseOfUrl)
                val sepet = jsonObj.getJSONArray("sepet_yemekler")



                var yemek_total_fiyat = 0
                for (index in 0 until sepet.length()) {

                    val s = sepet.getJSONObject(index)

                    val yemek_id = s.getInt("yemek_id")
                    val yemek_adi = s.getString("yemek_adi")
                    val yemek_resim_adi = s.getString("yemek_resim_adi")
                    val yemek_fiyat = s.getInt("yemek_fiyat")
                    val yemek_siparis_adet = s.getInt("yemek_siparis_adet")

                    yemek_total_fiyat += (yemek_fiyat)*(yemek_siparis_adet)
                    textViewSepetToplam.text = "Genel Toplam: ${yemek_total_fiyat} \u20ba"

                    val yemek = Food(yemek_id, yemek_adi, yemek_resim_adi, yemek_fiyat)
                    val sepettekiler = Basket(yemek, yemek_siparis_adet)
                    SepetListesi.add(sepettekiler)

                }

            }
            catch (e: JSONException) {
                e.printStackTrace()
            }

            if(SepetListesi.size == 0 || SepetListesi.size == null) {

                textViewSepetToplam.text = "Sepetinizde Ürün Bulunmamaktadır"
            }

            adapter = SepetAdapter(this, SepetListesi, textViewSepetToplam)
            RecyclerViewSepet.adapter = adapter

            adapter.notifyDataSetChanged()

        }, Response.ErrorListener { Log.e("Hata", "Tüm Yemek Okuma") })

        Volley.newRequestQueue(this).add(requestToUrl)
    }
}