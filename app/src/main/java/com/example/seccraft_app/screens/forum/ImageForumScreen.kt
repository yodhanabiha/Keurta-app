package com.example.seccraft_app.screens.forum

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.seccraft_app.BottomBarScreen
import com.example.seccraft_app.collection.forum.ForumCollection
import com.example.seccraft_app.R
import com.example.seccraft_app.collection.forum.ForumUser
import com.example.seccraft_app.ui.theme.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageForumScreen(navController: NavHostController) {

    var textForum by remember { mutableStateOf("") }
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUri = uri
        }

    LaunchedEffect(launcher) {
        launcher.launch("image/*")
    }

    Surface(modifier = Modifier.fillMaxSize(), color = bg) {
        Column {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
                colors = CardDefaults.cardColors(
                    primary
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, top = 28.dp, bottom = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_left),
                        contentDescription = "",
                        tint = Color.Black,
                        modifier = Modifier.clickable {
                            navController.navigate(BottomBarScreen.Forum.route)
                        }
                    )

                    Text(
                        text = stringResource(id = R.string.forum),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(start = 26.dp)
                    )

                }
            }

            LazyColumn {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 20.dp, end = 20.dp, top = 32.dp),
                        colors = CardDefaults.cardColors(Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 12.dp)
                                .padding(top = 30.dp)
                        ) {

                            Text(
                                text = stringResource(id = R.string.topik_baru),
                                style = MaterialTheme.typography.titleLarge,
                                fontSize = 20.sp,
                                color = secondary
                            )

                            Card(
                                modifier = Modifier
                                    .clickable {
                                        launcher.launch("image/*")
                                    }
                                    .height(260.dp)
                                    .fillMaxWidth()
                                    .padding(top = 30.dp),
                                colors = CardDefaults.cardColors(Color(0xA3B9B9B9)),
                                shape = RectangleShape
                            ) {

                                if (imageUri != null) {
                                    Image(
                                        painter = rememberAsyncImagePainter(model = imageUri),
                                        contentDescription = "",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.FillBounds
                                    )
                                } else {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {

                                        Icon(
                                            painter = painterResource(id = R.drawable.image),
                                            contentDescription = "",
                                            tint = Color(0x9C3D3D3D),
                                            modifier = Modifier.size(56.dp)
                                        )

                                        Text(
                                            text = stringResource(id = R.string.upl_image),
                                            style = MaterialTheme.typography.titleLarge,
                                            color = Color(0x9C3D3D3D),
                                            modifier = Modifier.padding(top = 12.dp)
                                        )
                                    }
                                }

                            }

                            TextField(
                                value = textForum,
                                placeholder = { Text(stringResource(id = R.string.tuliskan_topik)) },
                                onValueChange = {
                                    textForum = it
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(250.dp)
                                    .padding(top = 30.dp)
                                    .border(
                                        border = BorderStroke(
                                            2.dp,
                                            Color(0xFFA19B9B)
                                        ), shape = RoundedCornerShape(6.dp)
                                    ),
                                textStyle = TextStyle(
                                    fontFamily = PoppinsFamily,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    platformStyle = PlatformTextStyle(includeFontPadding = false)
                                ),
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
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Spacer(modifier = Modifier.weight(1f))
                                Button(
                                    onClick = {
                                        val text = textForum
                                        uploadImage(text, navController, imageUri, context)
                                    },
                                    shape = RoundedCornerShape(6.dp),
                                    colors = ButtonDefaults.buttonColors(secondary),
                                    modifier = Modifier.padding(top = 12.dp, bottom = 24.dp),
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.submit),
                                        fontFamily = PoppinsFamily,
                                        fontSize = 12.sp,
                                        style = LocalTextStyle.current.copy(
                                            platformStyle = PlatformTextStyle(includeFontPadding = false)
                                        ),
                                        fontWeight = FontWeight(600),
                                        color = Color.White
                                    )
                                }
                            }

                        }

                    }
                }
            }

        }
    }
}

fun uploadImage(text: String, navController: NavHostController, imageUri: Uri?, context: Context) {

    val db = Firebase.firestore
    val auth = Firebase.auth
    val currentUser = auth.currentUser

    val timeNow = FieldValue.serverTimestamp()

    val id = db.collection("Forum").document().id
    var dataForum =
        ForumCollection(id = id, idUser = currentUser!!.uid, TextForum = text, time = timeNow)

    val forumUser = ForumUser(id, timeNow)

    if (imageUri != null) {
        val storageReference = FirebaseStorage.getInstance().reference
        val loc = storageReference.child("Forum/$id/ForumImage")
        val uploadTask = loc.putFile(imageUri)

        uploadTask.addOnSuccessListener { taskSnapshot ->
            taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                dataForum = dataForum.copy(image = it.toString())
                db.collection("Forum").document(id)
                    .set(dataForum)
                    .addOnSuccessListener {
                        db.collection("users/${currentUser.uid}/forum").document(id).set(forumUser)
                            .addOnSuccessListener {
                                navController.navigate(BottomBarScreen.Forum.route)
                            }

                    }
                    .addOnFailureListener { e ->

                    }
            }

        }
    } else {
        Toast.makeText(context, "Image harus di upload!!", Toast.LENGTH_LONG).show()
    }


}


