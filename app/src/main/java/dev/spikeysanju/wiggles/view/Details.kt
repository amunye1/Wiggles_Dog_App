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

import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.GoogleMap
import dev.spikeysanju.wiggles.R
import dev.spikeysanju.wiggles.component.DogInfoCard
import dev.spikeysanju.wiggles.component.InfoCard
import dev.spikeysanju.wiggles.component.OwnerCard
import dev.spikeysanju.wiggles.data.FakeDogDatabase
import dev.spikeysanju.wiggles.view.maps.LocationViewModel
import android.Manifest
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.heightIn
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

var currentLocation : Location? = null
var fusedLocationProviderClient: FusedLocationProviderClient? = null
val REQUESTCODE =101
@Composable
fun Details(navController: NavController, id: Int) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Details") },
                backgroundColor = MaterialTheme.colors.background,
                contentColor = colorResource(id = R.color.text),
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp, 24.dp)
                            .clickable {
                                navController.navigateUp()
                            },
                        tint = colorResource(id = R.color.text)
                    )
                }
            )
        },

        content = {
            DetailsView(id)
        }
    )
}

@Composable
fun MyLocationComponent(viewModel: LocationViewModel) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            viewModel.getLocation()
        } else {
            // Handle the case where permission is denied.
        }
    }

    // Initialization logic for ViewModel
    DisposableEffect(context) {
        viewModel.initLocationClient(context)
        onDispose { }
    }

    LaunchedEffect(key1 = true) {
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    // Now `viewModel.location` holds the latest location or null.
    // You can pass this state to your `AndroidView` composable to update the `MapView`.
    val location = viewModel.location.value

    // Display the map
    AndroidView(
        modifier = Modifier.fillMaxSize()
            .fillMaxWidth() // Fill the width of LazyColumn
            .fillMaxHeight(),
        factory = { ctx ->
            MapView(ctx).apply {
                onCreate(null)
                onResume()
//                getMapAsync { googleMap ->
//                    // When the map is ready, update it with the user's location
//                    location?.let {
//                        val userLatLng = LatLng(it.latitude, it.longitude)
//                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 12f))
//                        googleMap.addMarker(MarkerOptions().position(userLatLng).title("Your Location"))
//                    }
//                }

                getMapAsync { googleMap ->
                    location?.let { loc ->
                        Log.d("MyLocationComponent", "Adding marker at lat: ${loc.latitude}, lng: ${loc.longitude}")
                        val userLatLng = LatLng(loc.latitude, loc.longitude)
                        googleMap.clear() // Clear the previous markers
                        googleMap.addMarker(
                            MarkerOptions()
                                .position(userLatLng)
                                .title("Your Location")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                        )
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 12f))
                    } ?: Log.d("MyLocationComponent", "Location is null")
                }

            }

        },


        update = { mapView ->
            mapView.getMapAsync { googleMap ->
                location?.let { loc ->
                    val userLatLng = LatLng(loc.latitude, loc.longitude)
                    googleMap.clear() // Clear all markers before adding a new one
                    googleMap.addMarker(
                        MarkerOptions()
                            .position(userLatLng)
                            .title("Your Location")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    )
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 12f))
                }
            }
        }
    )
}


@Composable
fun DetailsView(id: Int) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxHeight()
            .background(color = colorResource(id = R.color.background))
    ) {

        val dog = FakeDogDatabase.dogList[id]

        // Basic details
        item {
            dog.apply {

                val dogImage: Painter = painterResource(id = dog.image)
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(346.dp),
                    painter = dogImage,
                    alignment = Alignment.CenterStart,
                    contentDescription = "",
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))
                DogInfoCard(name, gender, location)
            }
        }
        item {
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(300.dp) // Set the height you want for the MapView
            ) {
                MyLocationComponent(viewModel = LocationViewModel())
            }
        }

        // My story details
        item {
            dog.apply {

                Spacer(modifier = Modifier.height(24.dp))
                Title(title = "My Story")
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = about,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 0.dp, 16.dp, 0.dp),
                    color = colorResource(id = R.color.text),
                    style = MaterialTheme.typography.body2,
                    textAlign = TextAlign.Start
                )
            }
        }

        // Quick info
        item {
            dog.apply {

                Spacer(modifier = Modifier.height(24.dp))
                Title(title = "Dog info")
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 0.dp, 16.dp, 0.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InfoCard(title = "Age", value = dog.age.toString().plus(" yrs"))
                    InfoCard(title = "Color", value = color)
                    InfoCard(title = "Weight", value = weight.toString().plus("Kg"))
                }
            }
        }

        // Owner info
        item {
            dog.apply {

                Spacer(modifier = Modifier.height(24.dp))
                Title(title = "Owner info")
                Spacer(modifier = Modifier.height(16.dp))
                owner.apply {
                    OwnerCard(name, bio, image)
                }
            }
        }

        // CTA - Adopt me button
        item {
            Spacer(modifier = Modifier.height(36.dp))
            Button(
                onClick = { /* Do something! */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(16.dp, 0.dp, 16.dp, 0.dp),
                colors = ButtonDefaults.textButtonColors(
                    backgroundColor = colorResource(id = R.color.blue),
                    contentColor = Color.White
                )
            ) {
                Text("Adopt me")
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun Title(title: String) {
    Text(
        text = title,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 0.dp, 0.dp, 0.dp),
        color = colorResource(id = R.color.text),
        style = MaterialTheme.typography.subtitle1,
        fontWeight = FontWeight.W600,
        textAlign = TextAlign.Start
    )
}
