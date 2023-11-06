/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.spikeysanju.wiggles.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dev.spikeysanju.wiggles.R
import dev.spikeysanju.wiggles.component.ItemDogCard
import dev.spikeysanju.wiggles.component.TopBar
import dev.spikeysanju.wiggles.model.Dog

@Composable
fun Home(navController: NavHostController, dogList: List<Dog>, toggleTheme: () -> Unit) {
    // State to hold the gender filter text
    var genderFilter by remember { mutableStateOf("") }
    LazyColumn {
        item {
            TopBar(
                onToggle = {
                    toggleTheme()
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = genderFilter,
                onValueChange = { genderFilter = it },
                label = { Text("Enter gender or name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(4.dp)), // Add background color and shape
                singleLine = true,
                leadingIcon = {
                    Icon(

                        painter= painterResource(id = R.drawable.ic_filled_search),

                        contentDescription = "A magnify glass to search",
                        tint = Color.Black
                    )
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    backgroundColor = Color.White,
                    cursorColor = Color.Black,
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black
                )
            )

        }
        items(dogList) {

                dogList.forEach {
                    if (genderFilter.isBlank() || it.gender.equals(genderFilter, ignoreCase = true) || it.name.equals(genderFilter, ignoreCase = true)) {
                    ItemDogCard(
                        it,
                        onItemClicked = { dog ->
                            navController.navigate("details/${dog.id}/${dog.name}/${dog.location}")
                        }
                    )
                }
            }
        }
    }
}
