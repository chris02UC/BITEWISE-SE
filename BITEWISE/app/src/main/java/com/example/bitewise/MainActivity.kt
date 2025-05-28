//package com.example.bitewise
//
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.tooling.preview.Preview
//import com.example.bitewise.ui.theme.BITEWISETheme
//import com.example.bitewise.view.AppNavHost
//
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContent {
//            BITEWISETheme {
//                AppNavHost()
//            }
//        }
//    }
//}

package com.example.bitewise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.bitewise.ui.theme.BITEWISETheme
<<<<<<< Updated upstream
=======
import com.example.bitewise.view.AppNavHost
import com.google.firebase.FirebaseApp
>>>>>>> Stashed changes

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Firebase 초기화
        FirebaseApp.initializeApp(this)

        enableEdgeToEdge()
        setContent {
            BITEWISETheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
<<<<<<< Updated upstream

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BITEWISETheme {
        Greeting("Android")
    }
}
=======
>>>>>>> Stashed changes
