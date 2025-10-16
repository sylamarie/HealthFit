package com.syla.healthfit.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch

@Composable
fun NavigationDrawer(
    currentDestination: AppDestination,
    onDestinationSelected: (AppDestination) -> Unit,
    topBarTitle: String,
    content: @Composable () -> Unit
) {
    val drawerState = androidx.compose.material3.rememberDrawerState(initialValue = androidx.compose.material3.DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column(modifier = Modifier.fillMaxHeight()) {
                AppDestination.drawerDestinations.forEach { destination ->
                    NavigationDrawerItem(
                        label = { Text(text = stringResource(id = destination.titleRes)) },
                        icon = { Icon(imageVector = destination.icon, contentDescription = null) },
                        selected = destination == currentDestination,
                        onClick = {
                            scope.launch { drawerState.close() }
                            onDestinationSelected(destination)
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = topBarTitle) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = null)
                        }
                    }
                )
            },
            content = { padding ->
                androidx.compose.foundation.layout.Box(modifier = Modifier.padding(padding)) {
                    content()
                }
            }
        )
    }
}