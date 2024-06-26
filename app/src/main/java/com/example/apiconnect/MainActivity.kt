package com.example.apiconnect

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.apiconnect.api.AuthState
import com.example.apiconnect.api.RepositoryImpl
import com.example.apiconnect.api.RetrofitInstance
import com.example.apiconnect.model.Result
import com.example.apiconnect.ui.theme.ApiConnectTheme
import com.example.apiconnect.viewmodel.ViewModelMain
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    //Создание провайдера ViewModel
    private val viewModel by viewModels<ViewModelMain>(factoryProducer = {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ViewModelMain(RepositoryImpl(RetrofitInstance.api))
                        as T
            }
        }
    })
    private val viewModelSmart by viewModels<ViewModelMain>(factoryProducer = {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ViewModelMain(RepositoryImpl(RetrofitInstance.apiSmartLab))
                        as T
            }
        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ApiConnectTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    sendCodeToEmail(viewModelSmart) //- отправка кода на почту
                    /*getCharacter(viewModel) //- получение данных методом get*/
                }
            }
        }
    }
}

@Composable
fun getCharacter(viewModel: ViewModelMain) {
    //Превязанная перменная для получения данных из API
    val characterList = viewModel.rickAndMorty.collectAsState().value

    /** Context — это объект, который предоставляет доступ к базовым функциям приложения в Android,
     таким как доступ к ресурсам, файловой системе, вызов активности и т. д.*/
    val context = LocalContext.current

    /** LaunchedEffect — это composable-функция, которая используется для запуска сопрограммы
    внутри области composable.Когда LaunchedEffect входит в композицию,
    он запускает сопрограмму и отменяет её при выходе из композиции .
    *
    LaunchedEffect принимает несколько ключей в качестве параметров,
    и если какой - либо из ключей изменяется, он может отменить существующую сопрограмму
    и запустить её снова . Это полезно для выполнения побочных эффектов,
    таких как сетевые вызовы или обновление базы данных, без блокировки потока пользовательского интерфейса*/
    LaunchedEffect(key1 = viewModel.showErrorToastChannel) {
        viewModel.showErrorToastChannel.collectLatest {
            if (true) {
                Toast.makeText(
                    context, "Error", Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    context, "Normas", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    //Если лист пустой, то будет отображаться progress bar
    if (characterList.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        //Иначе выводим данные в лист
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(16.dp)
        ) {
            items(characterList.size) { index ->
                Character(characterList[index])
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

//Отрисовка одного персонажа
@Composable
fun Character(res: Result) {
    val imageState = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current).data(res.image)
            .size(Size.ORIGINAL).build()
    ).state

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .height(300.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {

        if (imageState is AsyncImagePainter.State.Error) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        if (imageState is AsyncImagePainter.State.Success) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                painter = imageState.painter,
                contentDescription = res.name,
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = "${res.name} -- Location: ${res.location}$",
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = res.status,
            fontSize = 13.sp,
        )

    }
}

@Composable
fun sendCodeToEmail(viewModel: ViewModelMain) {
    val email = remember { mutableStateOf("") }
    val code = remember { mutableStateOf("") }

    val userState by viewModel.state
    var currentUserState by remember { mutableStateOf("") }

    Column(
        Modifier.padding(top = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        TextField(value = email.value, onValueChange = { newtext -> email.value = newtext })
        TextField(value = code.value, onValueChange = { newtext -> code.value = newtext })
        Button(
            onClick = {
                viewModel.signIn(email.value, code.value)
            }
        ) {
            Text(text = "Send code to email")
        }

        currentUserState = when (userState) {
            is AuthState.Loading -> {
                "Ожидание"
            }

            is AuthState.Success -> {
                (userState as AuthState.Success).message
            }

            is AuthState.Error -> {
                val message = (userState as AuthState.Error).message
                message
            }
        }

        if (currentUserState.isNotEmpty()) {
            Text(text = "Статус: ${currentUserState}",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 20.dp),
                textAlign = TextAlign.Center
            )
        }
    }

}

