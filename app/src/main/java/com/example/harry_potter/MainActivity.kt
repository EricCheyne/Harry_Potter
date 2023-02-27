package com.example.harry_potter

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.android.volley.Request
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONArray
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.example.harry_potter_volley_json_revisted_f21.R
import com.squareup.picasso.Picasso

/*
    In this version we access a slight variation on the JSON
    file.  This time the JSON starts with an array instead of an
    object.
*/

/*
    Use Volley to read JSON (about Harry Potter Movies) from the Internet
    Display information about movie user chooses from Spinner
    Display image from Internet using Picasso
    New Intent takes user to Youtube for movie trailer clip
*/

/*
    Using the web involves a client-server interaction
    A client requests; a server responds
    Volley manages that interaction
    in this case we are requesting JSON
    We listen for the response and the possible error
*/

/*
    Edit build.gradle
    implementation("com.android.volley:volley:1.2.1")
    implementation 'com.squareup.picasso:picasso:2.71828'

    Edit Android Manifest
    <uses-permission android:name="android.permission.INTERNET" />
    add following attribute to <application> tag
    android:usesCleartextTraffic="true"
*/

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //connect interface and code
        val spTitle = findViewById<Spinner>(R.id.spTitle)
        val tvDirector = findViewById<TextView>(R.id.tvDirector)
        val tvWriter = findViewById<TextView>(R.id.tvWriter)
        val tvProducer = findViewById<TextView>(R.id.tvProducer)
        val tvMusic = findViewById<TextView>(R.id.tvMusic)
        val ivPoster = findViewById<ImageView>(R.id.ivPoster)
        val btnTrailer = findViewById<Button>(R.id.btnTrailer)

        val url: String = "http://www1.lasalle.edu/~blum/c341wks/Harry_Potter_2/Potter_start_square.json";
        val baseURL: String= "http://www1.lasalle.edu/~blum/c341wks/Harry_Potter_2/"
        /*
            Note that the URL is http: and not https:
            Android programs assume the more secure https:
            so if you are using an older http: protocol,
            you must explicitly say so in the manifest
            android:usesCleartextTraffic="true"
        */
        val filmTitles: MutableList<String> = ArrayList()  //to use with spinner
        var myJSONarray: JSONArray

        val requestQueue = Volley.newRequestQueue(this)

        // Initialize a new JsonArrayRequest instance
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            Response.Listener<JSONArray> {response ->
                // Do something with response
                // Process the JSON -- Get the JSON array
                myJSONarray = response

                // Loop through the array elements -- no iterator in JSON array
                for (i in 0 until myJSONarray.length()) {
                    val movie = myJSONarray.getJSONObject(i)
                    filmTitles.add(movie.getString("title"))
                }

                //use array of string to populate a spinner
                val aaTitles = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, filmTitles)
                spTitle.adapter = aaTitles

                // when item chosen in spinner display same thing in textview
                spTitle.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                        val tvUserItem = view as TextView
                        Toast.makeText(applicationContext, "You chose " + tvUserItem.text.toString(), Toast.LENGTH_SHORT).show()

                        tvDirector.text =myJSONarray.getJSONObject(i).getString("director")
                        tvWriter.text =myJSONarray.getJSONObject(i).getString("writer")
                        tvProducer.text =myJSONarray.getJSONObject(i).getString("producer")
                        tvMusic.text = myJSONarray.getJSONObject(i).getString("music")
                        Picasso.get().load( baseURL+myJSONarray.getJSONObject(i).getString("image")).into(ivPoster)
                    }

                    override fun onNothingSelected(adapterView: AdapterView<*>) {}
                }//end onItemSelectedListener

                // ******* button --> GO TO YOUTUBE  *************
                btnTrailer.setOnClickListener {
                    val id = spTitle.selectedItemPosition
                    //Toast.makeText(getApplicationContext(), ""+id, Toast.LENGTH_LONG).show();

                    try {
                        val ytc = myJSONarray.getJSONObject(id).getString("youtubecode")
                        //Toast.makeText(getApplicationContext(), ytc, Toast.LENGTH_LONG).show();
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$ytc")))
                    } catch (e: JSONException) { }
                }// end onclickListener
            },//end Response Listener
            Response.ErrorListener { error ->
                // Do something when error occurred
                Toast.makeText(this@MainActivity,error.toString(),Toast.LENGTH_LONG).show()
            }//end onErrorListener
        ) //end JSONObjectRequest

        // Add JsonObjectRequest to the RequestQueue
        // EASY TO FORGET -- NOTHING HAPPENS WITHOUT
        requestQueue.add(jsonArrayRequest)

    } //end onCreate
} //end mainActivity