package com.anjegonz.chirp


import androidx.compose.runtime.*
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.anjegonz.auth.presentation.register_success.RegisterSuccessRoot
import com.anjegonz.core.designsystem.theme.ChirpTheme

@Composable
@Preview
fun App() {
    ChirpTheme {
        RegisterSuccessRoot(

        )
    }
}