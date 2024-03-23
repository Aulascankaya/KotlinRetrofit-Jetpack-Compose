package com.ulas.kotlinretrofitcompose

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ulas.kotlinretrofitcompose.model.CryptoModel
import com.ulas.kotlinretrofitcompose.service.CryptoAPI
import com.ulas.kotlinretrofitcompose.ui.theme.KotlinRetrofitComposeTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KotlinRetrofitComposeTheme {
                // A surface container using the 'background' color from the theme
               MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen() {
    var cryptoModels = remember { mutableStateListOf<CryptoModel>()}

    val BASE_URL = "https://raw.githubusercontent.com/"
    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CryptoAPI::class.java)

    val call = retrofit.getData()

    call.enqueue(object: Callback<List<CryptoModel>>{
        override fun onResponse(
            call: Call<List<CryptoModel>>,
            response: Response<List<CryptoModel>>
        ) {
            if(response.isSuccessful){
                response.body()?.let {
                   cryptoModels.addAll(it)
                }
            }
        }

        override fun onFailure(call: Call<List<CryptoModel>>, t: Throwable) {
            t.printStackTrace()
        }

    })


    Scaffold(topBar = { AppBar() }) {

        CryptoList(cryptos = cryptoModels)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar() {
    TopAppBar(title = { Text(text = "Retrofit Compose")},
        modifier = Modifier.padding(1.dp),
        scrollBehavior = null,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.LightGray)

    )
}


@Composable
fun CryptoList(cryptos : List<CryptoModel>){

    LazyColumn(contentPadding = PaddingValues(5.dp)){
        items(cryptos){crypto ->
            CryptoRow(crypto = crypto)
        }
    }
}

@Composable
fun CryptoRow(crypto: CryptoModel){
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 65.dp)
        .background(color = MaterialTheme.colorScheme.surface)) {
        Text(text = crypto.currency,
        style = MaterialTheme.typography.headlineLarge,
        modifier = Modifier.padding(2.dp),
        fontWeight = FontWeight.Bold
        )
        Text(text = crypto.price,
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(2.dp)
        )
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KotlinRetrofitComposeTheme {
       CryptoRow(crypto = CryptoModel("BTC","4854"))
    }
}