package ge.nikka.gtutables

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetDefaults
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ge.nikka.gtutables.MainActivity.Companion.ActionButton
import ge.nikka.gtutables.MainActivity.Companion.ActionCheckbox
import ge.nikka.gtutables.MainActivity.Companion.AnimatedNativeEditText
import ge.nikka.gtutables.MainActivity.Companion.isSearching
import ge.nikka.gtutables.MainActivity.Companion.notFound
import ge.nikka.gtutables.ui.theme.GTUTablesTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.core.content.edit

class MainActivity : ComponentActivity() {

    companion object {
        @Composable
        fun AnimatedNativeEditText(
            text: String,
            onTextChanged: (String) -> Unit,
            onTap: () -> Unit = {},
            hint: String = "",
            maxLine: Int = 1,
            isSingle: Boolean = false,
            modifier: Modifier
        ) {
            var visible by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                visible = true
            }

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(300, easing = FastOutSlowInEasing)) +
                        slideInVertically(
                            initialOffsetY = { it / 3 },
                            animationSpec = tween(300, easing = FastOutSlowInEasing)
                        ),
                exit = ExitTransition.None
            ) {
                NativeComposeEditText(
                    text = text,
                    onTextChanged = onTextChanged,
                    onTap = {
                        onTap()
                    },
                    hint = hint,
                    maxLine = maxLine,
                    isSingle = isSingle,
                    modifier = modifier
                )
            }
        }

        @Composable
        fun NativeComposeEditText(
            text: String,
            onTextChanged: (String) -> Unit,
            onTap: () -> Unit = {},
            hint: String = "",
            maxLine: Int = 1,
            isSingle: Boolean = false,
            modifier: Modifier = Modifier
        ) {
            val interactionSource = remember { MutableInteractionSource() }
            val pressed by interactionSource.collectIsPressedAsState()
            val scale by animateFloatAsState(targetValue = if (pressed) 0.95f else 1f, label = "scaleAnim")
            if (pressed) onTap()

            val fontFamily = FontFamily(
                Font(R.font.googlereg)
            )

            Box(
                modifier = modifier
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .height(with(LocalDensity.current) { 110.toDp() })
                    .background(
                        color = Color.DarkGray,
                        shape = RoundedCornerShape(with(LocalDensity.current) { 42.toDp() })
                    )
                    .padding(horizontal = with(LocalDensity.current) { 30.toDp() }),
                contentAlignment = Alignment.CenterStart
            ) {
                if (text.isEmpty()) {
                    Text(
                        text = hint,
                        color = Color.Gray,
                        fontSize = 17.sp,
                        fontFamily = fontFamily
                    )
                }

                BasicTextField(
                    value = text,
                    onValueChange = onTextChanged,
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = 17.sp,
                        fontFamily = fontFamily
                    ),
                    singleLine = isSingle,
                    maxLines = maxLine,
                    cursorBrush = SolidColor(Color.White),
                    interactionSource = interactionSource,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = {}
                        )
                )
            }
        }

        @Composable
        fun ActionButton(text: String, onClick: () -> Unit, enabled: Boolean) {
            val interactionSource = remember { MutableInteractionSource() }
            val scale by animateFloatAsState(
                targetValue = if (interactionSource.collectIsPressedAsState().value) 0.95f else 1f,
                animationSpec = tween(150)
            )
            val alpha = if (enabled) 1f else 0.4f
            Button(
                onClick = {
                    if (enabled) onClick()
                },
                //enabled = enabled,
                interactionSource = interactionSource,
                modifier = Modifier
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        alpha = alpha
                    )
                    .padding(2.dp)
            ) {
                Text(
                    text,
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.googlebold))
                )
            }
        }

        @Composable
        fun ActionCheckbox(
            checked: Boolean,
            onCheckedChange: (Boolean) -> Unit,
            enabled: Boolean = true,
            label: String
        ) {
            val interactionSource = remember { MutableInteractionSource() }

            val scale by animateFloatAsState(
                targetValue = if (interactionSource.collectIsPressedAsState().value) 0.9f else 1f,
                animationSpec = tween(150)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale
                    )
                    .padding(4.dp)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        onCheckedChange(!checked)
                    }
            ) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = null,
                    enabled = enabled,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.googlebold)),
                    color = Color.White
                )
            }
        }

        var notFound = mutableStateOf(false)
        var isSearching = mutableStateOf(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GTUTablesTheme {
                MainScreen()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Singleton.instance.cleanUp()
    }

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    var prefs: SharedPreferences = context.getSharedPreferences("Table", Context.MODE_PRIVATE);

    val blurRad = remember { Animatable(0f) }

    LaunchedEffect(isSearching.value || notFound.value) {
        val targetBlur = if (isSearching.value || notFound.value) 10f else 0f
        blurRad.animateTo(
            targetValue = targetBlur,
            animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .offset(y = (-60).dp)
            .blur(blurRad.value.dp),
        contentAlignment = Alignment.Center
    ) {
        var visible by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            visible = true
        }

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(300, easing = FastOutSlowInEasing)) +
                    slideInVertically(
                        initialOffsetY = { it / 3 },
                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                    ),
            exit = ExitTransition.None
        ) {
            Card(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(24.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2C)),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .background(Color.Transparent),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    var query by remember { mutableStateOf("") }
                    var isChecked by remember { mutableStateOf(false) }
                    if (prefs.contains("checkbox"))
                        isChecked = prefs.getBoolean("checkbox", true)
                    var tmp: Boolean by remember { mutableStateOf(true) }
                    if (isChecked && tmp) {
                        if (prefs.contains("table_id"))
                            query = prefs.getString("table_id", null).toString()
                        tmp = false
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("GTU Table Fetcher",
                        fontSize = 28.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontFamily = FontFamily(Font(R.font.googlebold))
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    AnimatedNativeEditText(
                        text = query,
                        onTextChanged = { query = it },
                        hint = "Group Code…",
                        maxLine = 1,
                        isSingle = true,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    var visible2 by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) {
                        visible2 = true
                    }
                    AnimatedVisibility(
                        visible = visible2,
                        enter = fadeIn(animationSpec = tween(300, easing = FastOutSlowInEasing)) +
                                slideInVertically(
                                    initialOffsetY = { it / 3 },
                                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                                ),
                        exit = ExitTransition.None
                    ) {
                        ActionCheckbox(
                            checked = isChecked,
                            onCheckedChange = {
                                isChecked = it
                                prefs.edit {
                                    putBoolean("checkbox", it)
                                }
                                if (isChecked) {
                                    if (prefs.contains("table_id"))
                                        query = prefs.getString("table_id", null).toString()
                                }
                            },
                            label = "Save Group ID"
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    var visible3 by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) {
                        visible3 = true
                    }
                    val scope = rememberCoroutineScope()
                    AnimatedVisibility(
                        visible = visible3,
                        enter = fadeIn(animationSpec = tween(300, easing = FastOutSlowInEasing)) +
                                slideInVertically(
                                    initialOffsetY = { it / 3 },
                                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                                ),
                        exit = ExitTransition.None
                    ) {
                        ActionButton(
                            onClick = {
                                Utils.dismissKeyboard(context as Activity)
                                if (!Utils.isInternetAvailable(context)) {
                                    Toast.makeText(context, "No Internet Connection!", Toast.LENGTH_LONG).show()
                                    return@ActionButton
                                }
                                scope.launch(Dispatchers.IO) {
                                    try {
                                        isSearching.value = true
                                        Singleton.instance.data = Utils.sendAndReceive(query)
                                        Singleton.instance.table = query
                                        if (Singleton.instance.data.equals("NOT_FOUND")
                                            || Singleton.instance.data!!.contains("ECONNREFUSED")
                                            || Singleton.instance.data!!.contains("UnknownHostException")
                                        ) {
                                            isSearching.value = false
                                            notFound.value = true
                                        } else {
                                            prefs.edit {
                                                putString("table_id", query)
                                            }
                                            isSearching.value = false
                                            withContext(Dispatchers.Main) {
                                                context.startActivity(Intent(context, TableActivity::class.java))
                                            }
                                        }
                                        //Utils.writeTextToFile(context, "last-table.txt", Singleton.instance.data!!)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Error fetching data: $e", Toast.LENGTH_LONG).show()
                                    }
                                }
                            },
                            text = "Fetch",
                            enabled = if (query.isNotEmpty()) true else false
                        )
                    }
                }
            }
        }

        if (isSearching.value) {
            val bottomSheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true,
                confirmValueChange = { newState ->
                    newState != SheetValue.Hidden
                }
            )
            ModalBottomSheet(
                onDismissRequest = {},
                sheetState = bottomSheetState,
                containerColor = Color(0xFF191919),
                contentColor = Color.White,
                properties = ModalBottomSheetDefaults.properties(
                    shouldDismissOnBackPress = false
                ),
                dragHandle = {
                    Box(
                        modifier = Modifier
                            .width(0.dp)
                            .height(0.dp)
                            .background(Color.Transparent)
                    )
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CircularProgressIndicator(
                            color = Color.Blue,
                            modifier = Modifier
                                .size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Loading...",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.google)),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
        if (!isSearching.value && notFound.value) {
            ModalBottomSheet(
                onDismissRequest = {
                    notFound.value = false
                    Singleton.instance.cleanUp()
                },
                containerColor = Color(0xFF191919),
                contentColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Error",
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = FontFamily(Font(R.font.googlebold))
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        modifier = Modifier
                            .padding(start = 32.dp, end = 32.dp),
                        text = "Table ${Singleton.instance.table} not found!",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontFamily = FontFamily(Font(R.font.google)),
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ActionButton(
                            onClick = {
                                notFound.value = false
                            },
                            text = "Close",
                            enabled = true
                        )
                    }
                }
            }
        }
    }
}
