package com.example.paidly.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NavigationDrawerContent(
    onDestinationClicked: (String) -> Unit
) {
    ModalDrawerSheet {
        Text(
            text = "Paidly",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        Divider()

        NavigationDrawerItem(
            label = { Text("To Pay") },
            icon = { Icon(Icons.Default.MoneyOff, contentDescription = null) },
            selected = false,
            onClick = { onDestinationClicked("to_pay") }
        )

        NavigationDrawerItem(
            label = { Text("To Receive") },
            icon = { Icon(Icons.Default.ReceiptLong, contentDescription = null) },
            selected = false,
            onClick = { onDestinationClicked("to_receive") }
        )

        NavigationDrawerItem(
            label = { Text("History") },
            icon = { Icon(Icons.Default.History, contentDescription = null) },
            selected = false,
            onClick = { onDestinationClicked("history") }
        )
    }
}
