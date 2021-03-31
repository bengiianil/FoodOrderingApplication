package com.example.yemeksiparisuygulamasi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.yemeksiparisuygulamasi.adapter.YemekAdapter
import com.example.yemeksiparisuygulamasi.fragment.MenuFragment
import com.example.yemeksiparisuygulamasi.fragment.SepetFragment
import com.example.yemeksiparisuygulamasi.model.Food
import kotlinx.android.synthetic.main.activity_menu.*
import org.json.JSONException
import org.json.JSONObject

class MenuActivity : AppCompatActivity() , SearchView.OnQueryTextListener {

    private lateinit var YemekListesi: ArrayList<Food>
    private lateinit var adapter: YemekAdapter
    private lateinit var tempFragment: Fragment
    var counter: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)


        toolbarMenu.title = "Menü"
        setSupportActionBar(toolbarMenu)


        RecyclerViewYemek.setHasFixedSize(true)
        RecyclerViewYemek.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)


        supportFragmentManager.beginTransaction().add(R.id.ConstraitLayout, MenuFragment()).commit()

        bottomNV.setOnNavigationItemSelectedListener { menuItem ->

            if(menuItem.itemId == R.id.actionMenu){
                tempFragment = MenuFragment()
                startActivity(Intent(this@MenuActivity, MenuActivity::class.java))
            }
            if(menuItem.itemId == R.id.actionSepet){
                tempFragment = SepetFragment()
                startActivity(Intent(this@MenuActivity, SepetActivity::class.java))
            }

            supportFragmentManager.beginTransaction().replace(R.id.ConstraitLayout, tempFragment).commit()

            true
        }


        YemekListesi = ArrayList()
        adapter = YemekAdapter(this@MenuActivity, YemekListesi, counter)
        RecyclerViewYemek.adapter = adapter

        tumYemekler()
    }

    override fun onBackPressed() {
        val yeniIntent = Intent(Intent.ACTION_MAIN)
        yeniIntent.addCategory(Intent.CATEGORY_HOME)
        yeniIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(yeniIntent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_arama_menu,menu)

        val item = menu.findItem(R.id.action_ara)
        val searchView = item.actionView as SearchView
        searchView.setOnQueryTextListener(this)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        Log.e("Gönderilen Arama Sonucu",query)
        yemekArama(query)
        return true
    }

    override fun onQueryTextChange(newText: String): Boolean {
        Log.e("Gönderilen Arama Sonucu",newText)
        yemekArama(newText)
        return true
    }

    fun tumYemekler(){
        val webServiceUrl = "http://kasimadalan.pe.hu/yemekler/tum_yemekler.php"

        val requestToUrl = StringRequest(Request.Method.GET, webServiceUrl, { responseOfUrl ->

            try{
                YemekListesi = ArrayList()

                val jsonObj = JSONObject(responseOfUrl)
                val yemekler = jsonObj.getJSONArray("yemekler")

                for(index in 0 until yemekler.length()){

                    val y = yemekler.getJSONObject(index)

                    val yemek_id = y.getInt("yemek_id")
                    val yemek_adi = y.getString("yemek_adi")
                    val yemek_resim_adi = y.getString("yemek_resim_adi")
                    val yemek_fiyat = y.getInt("yemek_fiyat")

                    val yemek = Food(yemek_id, yemek_adi, yemek_resim_adi, yemek_fiyat)

                    YemekListesi.add(yemek)

                }
                adapter = YemekAdapter(this@MenuActivity, YemekListesi, counter)
                RecyclerViewYemek.adapter = adapter

            }
            catch(e: JSONException){
                e.printStackTrace()
            }

        }, Response.ErrorListener { Log.e("Hata", "Tüm Yemek Okuma") })

        Volley.newRequestQueue(this@MenuActivity).add(requestToUrl)
    }

    fun yemekArama(keyWord:String){

        val webServiceUrl = "http://kasimadalan.pe.hu/yemekler/tum_yemekler_arama.php"

        val requestToUrl = object : StringRequest(Request.Method.POST, webServiceUrl, Response.Listener { responseOfUrl ->

           // YemekListesi = ArrayList()
            YemekListesi.clear()

            try{

                val jsonObj = JSONObject(responseOfUrl)
                val yemekler = jsonObj.getJSONArray("yemekler")

                for(index in 0 until yemekler.length()){

                    val y = yemekler.getJSONObject(index)

                    val yemek_id = y.getInt("yemek_id")
                    val yemek_adi = y.getString("yemek_adi")
                    val yemek_resim_adi = y.getString("yemek_resim_adi")
                    val yemek_fiyat = y.getInt("yemek_fiyat")

                    val yemek = Food(yemek_id, yemek_adi, yemek_resim_adi, yemek_fiyat)

                    YemekListesi.add(yemek)
                }


                adapter.notifyDataSetChanged()
            }
            catch(e:JSONException){
                e.printStackTrace()
            }

        },Response.ErrorListener { Log.e("Hata", "Tüm Yemek Arama") }) {

            override fun getParams(): MutableMap<String, String> {
                val parameter = HashMap<String, String>()
                parameter["yemek_adi"] = keyWord
                return parameter
            }

        }

        Volley.newRequestQueue(this@MenuActivity).add(requestToUrl)
    }
}
