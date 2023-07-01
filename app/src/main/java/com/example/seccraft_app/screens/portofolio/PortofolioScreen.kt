package com.example.seccraft_app.screens.portofolio

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.seccraft_app.Collection.User.DataUser
import com.example.seccraft_app.Collection.portofolio.DataPortofolio
import com.example.seccraft_app.Collection.portofolio.LikePortofolio
import com.example.seccraft_app.R
import com.example.seccraft_app.navigation.Screens
import com.example.seccraft_app.ui.theme.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jet.firestore.JetFirestore
import com.jet.firestore.getListOfObjects

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortofolioScreen(navController: NavHostController) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screens.AddPortofolio.route) },
                shape = CircleShape,
                containerColor = secondary
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.plus),
                    contentDescription = "",
                    tint = Color.White,
                )
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bg)
                .padding(it)
        ) {
            Card(
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 0.dp,
                    bottomStart = 25.dp,
                    bottomEnd = 25.dp
                ),
                colors = CardDefaults.cardColors(
                    primary
                )
            ) {


            }
            Column(modifier = Modifier.padding(top = 28.dp)) {
                Text(
                    text = stringResource(id = R.string.portofolio),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 28.dp, start = 20.dp)
                )

                var kategoriText by remember { mutableStateOf("") }
                TextField(
                    value = kategoriText,
                    readOnly = true,
                    placeholder = { Text(stringResource(id = R.string.kategori)) },
                    onValueChange = {
                        kategoriText = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp)),
                    textStyle = MaterialTheme.typography.labelMedium,
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.arrow_down),
                            contentDescription = "",
                            modifier = Modifier
                                .padding(15.dp)
                                .size(18.dp)
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = Color.Black,
                        placeholderColor = icon_faded,
                        cursorColor = Color.Black,
                        containerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    )

                )

                // Terkini dan populer

                var selected by remember {
                    mutableStateOf(true)
                }

                Row(modifier = Modifier.padding(start = 20.dp, top = 28.dp)) {
                    Card(
                        modifier = Modifier
                            .clickable {
                                selected = true
                            },
                        colors = if (selected) CardDefaults.cardColors(secondary) else CardDefaults.cardColors(
                            Color.White
                        ),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.terkini),
                            style = MaterialTheme.typography.labelMedium,
                            color = if (selected) Color.White else secondary,
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 20.dp)
                        )
                    }

                    Card(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .clickable {
                                selected = false
                            },
                        colors = if (!selected) CardDefaults.cardColors(secondary) else CardDefaults.cardColors(
                            Color.White
                        ),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.populer),
                            color = if (!selected) Color.White else secondary,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 20.dp)
                        )
                    }

                }

                var dataPortofolio by remember { mutableStateOf(listOf<DataPortofolio>()) }
                var sort by remember { mutableStateOf("") }
                if (selected){
                    sort = "time"
                }else{
                    sort = "like"

                }

                JetFirestore(
                    path = { collection("portofolio") },
                    queryOnCollection = {
                        if(selected) orderBy("time", Query.Direction.DESCENDING) else orderBy("like", Query.Direction.DESCENDING)
                    },
                    onSingleTimeCollectionFetch = { values, exception ->
                        dataPortofolio = values.getListOfObjects()
                    },
                ) {
                    //item Pola
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        reverseLayout = false,
                        contentPadding = PaddingValues(top = 12.dp, start = 20.dp, end = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 12.dp)
                    ) {
                        items(dataPortofolio) { portofolio ->
                            var user by remember { mutableStateOf(DataUser()) }
                            JetFirestore(
                                path = { document("users/${portofolio.idUser}") },
                                onSingleTimeDocumentFetch = { value, exception ->
                                    val email = value!!.getString("email").toString()
                                    val name = value.getString("name").toString()
                                    val number = value.getString("number").toString()
                                    val image = value.getString("image").toString()

                                    user = user.copy(image, name, email, number)
                                }
                            ) {

                                val name = user.name.substringBefore(' ')

                                val title = if (portofolio.judul.length < 8) {
                                    portofolio.judul.substring(0, portofolio.judul.length)
                                } else {
                                    portofolio.judul.substring(0, 8) + "..."
                                }

                                Log.d("itung String", "PortofolioScreen: ${title}")

                                CardItem(
                                    id = portofolio.id,
                                    idUser = portofolio.idUser,
                                    image = portofolio.image,
                                    title = title,
                                    name = name,
                                    titleFull = portofolio.judul,
                                    nameFull = user.name,
                                    imageUser = user.image,
                                    deskripsi = portofolio.deskripsi,
                                    kategori = portofolio.kategori,
                                    navController = navController
                                )

                            }
                        }
                    }
                }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CardItem(
    image: String,
    title: String,
    name: String,
    idUser: String,
    id: String,
    imageUser: String,
    deskripsi: String,
    kategori: String,
    titleFull: String,
    nameFull: String,
    navController: NavHostController
) {

    var likeCount by remember { mutableStateOf(0) }
    var dataLike by remember { mutableStateOf(listOf<LikePortofolio>()) }
    var likeData by remember { mutableStateOf(LikePortofolio()) }

    val showDialog = remember { mutableStateOf(false) }

    if (showDialog.value) {
        CardItemDetail(
            titleFull,
            likeCount,
            likeData.like,
            deskripsi,
            nameFull,
            imageUser,
            kategori,
            image,
            id,
            navController
        ) {
            showDialog.value = it
        }
    }

    JetFirestore(
        path = { collection("portofolio/$id/like") },
        onRealtimeCollectionFetch = { values, exception ->
            dataLike = values.getListOfObjects()
        },
    ) {

        likeCount = dataLike.count {
            it.like
        }

        Card(
            colors = CardDefaults.cardColors(primary),
            modifier = Modifier
                .wrapContentHeight()
                .combinedClickable(
                    onClick = {
                        navController.navigate("portofolio_detail_screen/$id")
                    },
                    onLongClick = {
                        showDialog.value = true
                    }
                ),
            elevation = CardDefaults.cardElevation(2.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Card(
                    colors = CardDefaults.cardColors(Color.Gray),
                    shape = RoundedCornerShape(bottomStart = 0.dp, bottomEnd = 0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(model = image),
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .padding(bottom = 14.dp, top = 8.dp)
                ) {
                    Column() {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = name,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))

                    val auth = Firebase.auth
                    val idCurrentUser = auth.currentUser!!.uid


                    JetFirestore(
                        path = { document("portofolio/$id/like/$idCurrentUser") },
                        onRealtimeDocumentFetch = { value, exception ->

                            val idUserLike = value!!.getString("idUser").toString()
                            val like = value.getBoolean("like")

                            likeData = if (like == null) likeData.copy(
                                idUserLike,
                                false
                            ) else likeData.copy(idUserLike, like)

                            Log.d("Isi Like", "CardItem: $like")
                        }
                    ) {

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                painter = painterResource(id = R.drawable.love),
                                contentDescription = "",
                                tint = if (likeData.like) Color.Red else Color.Black,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable {
                                        if (likeData.like) {
                                            LikePto(id, false, likeCount)
                                        } else {
                                            LikePto(id, true, likeCount)
                                        }
                                    }
                            )
                            Text(
                                text = likeCount.toString(),
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }

                }


            }

        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardItemDetail(
    title: String,
    likeCount: Int,
    like: Boolean,
    deskripsi: String,
    name: String,
    imageUser: String,
    kategori: String,
    image: String,
    id: String,
    navController: NavHostController,
    setShowDialog: (Boolean) -> Unit
) {
    Dialog(onDismissRequest = { setShowDialog(false) }) {
        Box(
            contentAlignment = Alignment.Center,
        ) {
            Card(
                colors = CardDefaults.cardColors(primary),
            ) {
                Column(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(model = image),
                            contentDescription = "",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Row(modifier = Modifier.padding(top = 24.dp)) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            fontSize = 20.sp,
                            modifier = Modifier.fillMaxWidth(0.7f)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                painter = painterResource(id = R.drawable.love),
                                contentDescription = "",
                                tint = if (like) Color.Red else Color.Black,
                                modifier = Modifier
                                    .size(32.dp)
                                    .padding(bottom = 2.dp)
                                    .clickable {
                                        if (like) {
                                            LikePto(id, false, likeCount)
                                        } else {
                                            LikePto(id, true, likeCount)
                                        }

                                    }
                            )
                            Text(
                                text = likeCount.toString(),
                                style = MaterialTheme.typography.labelMedium
                            )

                        }
                    }

                    Text(
                        text = deskripsi,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Card(modifier = Modifier.size(32.dp), shape = CircleShape) {
                            Image(
                                painter = rememberAsyncImagePainter(model = imageUser),
                                contentDescription = "",
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Text(
                            text = name,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    if (kategori != "") {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.padding(top = 24.dp),
                            colors = CardDefaults.cardColors(
                                secondary
                            )
                        ) {
                            Text(
                                text = kategori,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White,
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                            )
                        }
                    }

                    Text(
                        text = stringResource(id = R.string.lihat),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.End,
                        color = secondary,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier
                            .padding(top = 18.dp)
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate("portofolio_detail_screen/$id")
                            }
                    )

                }
            }
        }
    }

}

fun LikePto(id: String, like: Boolean, likeCount: Int) {
    val auth = Firebase.auth
    val db = Firebase.firestore
    val idCurrentUser = auth.currentUser!!.uid

    val data = LikePortofolio(idUser = idCurrentUser, like = like)

    db.document("portofolio/$id").update("like", likeCount)

    db.collection("portofolio/$id/like/").document(idCurrentUser).set(data)

}

