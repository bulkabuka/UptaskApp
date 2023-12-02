package org.leftbrained.uptaskapp

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import org.jetbrains.exposed.sql.transactions.transaction
import org.leftbrained.uptaskapp.classes.UptaskDb
import org.leftbrained.uptaskapp.classes.User
import org.leftbrained.uptaskapp.classes.connectToDb
import org.leftbrained.uptaskapp.ui.theme.AppTheme

fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("No activity")
}

@Composable
fun AuthActivity(navController: NavController) {
    var login by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    var isClearedLogin by remember {
        mutableStateOf(false)
    }
    var isClearedPassword by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    val activity = context.findActivity()
    AppTheme {
        Column(
            Modifier
                .padding(16.dp)
                .padding(top = 48.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Rounded.AccountCircle,
                contentDescription = "Auth account icon",
                Modifier
                    .size(64.dp)
                    .padding(bottom = 24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                "Sign in to your account",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            OutlinedTextField(
                leadingIcon = { Icon(Icons.Rounded.Email, "Email icon") },
                trailingIcon = {
                    IconButton(
                        onClick = { login = "" },
                        enabled = isClearedLogin
                    ) { Icon(Icons.Rounded.Clear, "Clear icon") }
                },
                value = login,
                onValueChange = {
                    login = it
                    isClearedLogin = it.isNotEmpty()
                },
                label = { Text("Login") },
                maxLines = 1
            )
            OutlinedTextField(
                leadingIcon = { Icon(Icons.Rounded.Lock, "Password icon") },
                trailingIcon = {
                    IconButton(
                        onClick = { password = "" },
                        enabled = isClearedPassword
                    ) { Icon(Icons.Rounded.Clear, "Clear icon") }
                },
                value = password,
                onValueChange = {
                    password = it
                    isClearedPassword = it.isNotEmpty()
                },
                label = { Text("Password") },
                maxLines = 1
            )
            Button(onClick = {
                if (login == "" || password == "") {
                    Toast.makeText(
                        activity,
                        "Please fill all fields",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@Button
                }
                connectToDb()
                val user = transaction { User.find { UptaskDb.Users.login eq login }.firstOrNull() }
                if (user == null) {
                    Toast.makeText(
                        activity,
                        "User with this login doesn't exist",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@Button
                }
                if (user.password != password) {
                    Toast.makeText(
                        activity,
                        "Incorrect password",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@Button
                }
                val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("user", user.id.value.toString())
                    apply()
                }
                navController.navigate("taskList/${user.id.value}")
            }) {
                Text(text = "Sign in")
                Spacer(Modifier.size(8.dp))
                Icon(Icons.Rounded.ArrowForward, "Arrow forward icon")
            }
            Text(
                "Don't have an account?",
                style = MaterialTheme.typography.bodyMedium
            )
            TextButton(onClick = {
                navController.navigate("register")
            }) {
                Text(text = "Sign up")
            }
        }
    }
}

@Preview(device = "spec:width=411dp,height=891dp", showSystemUi = true)
@Composable
fun AuthActivityPreview() {
    AuthActivity(rememberNavController())
}