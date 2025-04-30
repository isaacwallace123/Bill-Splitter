package com.example.billsplitting.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(navController: NavController) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val coarseLocationPermission = rememberPermissionState(Manifest.permission.ACCESS_COARSE_LOCATION)

    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    var cameraMoved by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(locationPermissionState.status) {
        if (!locationPermissionState.status.isGranted && !coarseLocationPermission.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
            Log.d("MapScreen", "User does not have permission")
        } else {
            val location = suspendCancellableCoroutine<Location?> { cont ->
                fusedLocationClient.getCurrentLocation(
                    com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                    null
                )
                    .addOnSuccessListener { cont.resume(it) }
                    .addOnFailureListener { cont.resume(null) }
            }

            location?.let {
                userLocation = LatLng(it.latitude, it.longitude)
                selectedLocation = LatLng(it.latitude, it.longitude)
            }

            Log.d("MapScreen", "Fetched current location: $location")
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (userLocation != null) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = locationPermissionState.status.isGranted),
                uiSettings = MapUiSettings(myLocationButtonEnabled = true),
                onMapLoaded = {
                    if (!cameraMoved && userLocation != null) {
                        cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(userLocation!!, 15f))
                        cameraMoved = true
                    }
                },
                onMapClick = { latLng ->
                    selectedLocation = latLng
                }
            ) {
                selectedLocation?.let {
                    Marker(
                        state = MarkerState(position = it),
                        title = "Selected Restaurant"
                    )
                }
            }

            selectedLocation?.let {
                Button(
                    onClick = {
                        navController.navigate("bills/create?lat=${it.latitude}&lng=${it.longitude}")
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text("Select This Restaurant")
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

suspend fun com.google.android.gms.tasks.Task<Location>.awaitOrNull(): Location? =
    suspendCancellableCoroutine { cont ->
        addOnSuccessListener { cont.resume(it) }
        addOnFailureListener { cont.resume(null) }
        cont.invokeOnCancellation {
            // Handle cancellation if needed
        }
    }
