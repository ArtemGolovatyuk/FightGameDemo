package com.example.game123

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beust.klaxon.Json
import com.beust.klaxon.Klaxon
import com.example.game123.ui.theme.Game123Theme
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost



//private val klaxon = Klaxon()

data class Welcome (
    val name: String,
    val level: Long,
    val xp: Long,
    val health: Long,
    val armor: Long,
    val attack: Long,
    val luck: Long,
    val balance: Long,
    val alive: Boolean,

    @Json(name = "critical_attack")
    val criticalAttack: Long
) {}

private var errorText by mutableStateOf<String?>(null)

fun fetchCharacterData(onCharacterReceived: (Welcome) -> Unit) {
    val url = "http://192.168.1.9:5000/character1"

    // Отправить GET-запрос
    url.httpGet().responseString { _, _, result ->
        when (result) {
            is com.github.kittinunf.result.Result.Success -> {
                val json = result.value
                val character1 = Klaxon().parse<Welcome>(json)
                // Вызываем лямбда-функцию для обновления состояния
                onCharacterReceived(character1 ?: Welcome("", 0, 0, 0, 0, 0, 0, 0, false, 0))

                // Проверяем здоровье и устанавливаем имя победителя

            }
            is com.github.kittinunf.result.Result.Failure -> {
                val error = result.error
                errorText = "Ошибка при получении данных: $error" // Устанавливаем текст ошибки
            }
        }
    }
}


fun fetchCharacterData2(onCharacterReceived: (Welcome) -> Unit) {
    val url = "http://192.168.1.9:5000/character2"

    // Отправить GET-запрос
    url.httpGet().responseString { _, _, result ->
        when (result) {
            is com.github.kittinunf.result.Result.Success -> {
                val json = result.value
                val character2 = Klaxon().parse<Welcome>(json)
                // Вызываем лямбда-функцию для обновления состояния
                onCharacterReceived(character2 ?: Welcome("", 0, 0, 0, 0, 0, 0, 0, false, 0))

                // Проверяем здоровье и устанавливаем имя победителя

            }
            is com.github.kittinunf.result.Result.Failure -> {
                val error = result.error
                errorText = "Ошибка при получении данных: $error" // Устанавливаем текст ошибки
            }
        }
    }
}




fun sendPostRequestWithData() {
    val url = "http://192.168.1.9:5000/fight_character"
    val postData = "c1damage=damage"
    url
        .httpPost()
        .header("Content-Type" to "application/x-www-form-urlencoded")
        .body(postData)
        .response { _, _, result ->
            when (result) {
                is com.github.kittinunf.result.Result.Success -> {
                    val responseString = result.get()
                    // Обработайте успешный ответ
                }
                is com.github.kittinunf.result.Result.Failure -> {
                    val error = result.error
                    // Обработайте ошибку
                }
            }
        }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Game123Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting()
                }
            }
        }
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier) {
    var character1: Welcome? by remember { mutableStateOf(null) }
    var character2: Welcome? by remember { mutableStateOf(null) }
    var winner: String? by remember{ mutableStateOf(null) }

        if ((character2?.alive ) == false) {
            winner = character1?.name ?: "Unknown"
        }
        else if((character1?.alive  ) == false){
            winner = character2?.name ?: "Unknown"
        }


    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text("Name:${character1?.name} ", style = TextStyle(fontSize = 15.sp))
        Text("Health: ${character1?.health}")
        Text("Attack: ${character1?.attack}")
        Text("Critical_Attack: ${character1?.criticalAttack}")
        Text("Armor: ${character1?.armor}")
        Text("Alive: ${character1?.alive}")
        Text("Luck: ${character1?.luck}")
        Text("Balance: ${character1?.balance}")
        errorText?.let { error ->
            Text(error) // Используем красный цвет для текста ошибки
        }

    }
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.End
    ) {
        Text("Name: ${character2?.name}", style = TextStyle(fontSize = 15.sp))
        Text("Health: ${character2?.health}")
        Text("Attack: ${character2?.attack}")
        Text("Critical_Attack: ${character2?.criticalAttack}")
        Text("Armor: ${character2?.armor}")
        Text("Alive: ${character2?.alive}")
        Text("Luck: ${character2?.luck}")
        Text("Balance: ${character2?.balance}")
    }


    Column(
        Modifier
            .fillMaxSize()
            .padding(45.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        winner?.let { winner ->
            Text("Winner: $winner", style = TextStyle(fontSize = 15.sp))
        }
        Button(onClick = { fetchCharacterData { character1 = it } ; fetchCharacterData2 { character2 = it }; sendPostRequestWithData() } ) {
            Text("Load Character")

        }
    }
}




@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Game123Theme {
        Greeting()
    }
}
