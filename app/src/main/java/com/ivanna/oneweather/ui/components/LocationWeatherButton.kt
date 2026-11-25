package com.ivanna.oneweather.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun LocationWeatherButton(
    selectedLanguage: String,
    onLocationFound: (Double, Double) -> Unit
) {
    val context = LocalContext.current
    val isEnglish = selectedLanguage == "English"

    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    fun showError() {
        isLoading = false
        message = if (isEnglish) {
            "Unable to get your location. Check GPS and try again."
        } else {
            "Lokasi tidak ditemukan. Periksa GPS dan coba lagi."
        }
    }

    fun useLocation(location: Location) {
        isLoading = false
        message = ""
        onLocationFound(location.latitude, location.longitude)
    }

    fun getDeviceLocation() {
        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        try {
            val providers = listOf(
                LocationManager.NETWORK_PROVIDER,
                LocationManager.GPS_PROVIDER
            ).filter { provider ->
                locationManager.isProviderEnabled(provider)
            }

            if (providers.isEmpty()) {
                isLoading = false
                message = if (isEnglish) {
                    "Please enable location services on your phone."
                } else {
                    "Aktifkan layanan lokasi di HP terlebih dahulu."
                }
                return
            }

            val lastLocation = providers
                .mapNotNull { provider ->
                    locationManager.getLastKnownLocation(provider)
                }
                .filter { location ->
                    System.currentTimeMillis() - location.time in 0..120000
                }
                .maxByOrNull { it.time }

            if (lastLocation != null) {
                useLocation(lastLocation)
                return
            }

            fun requestProvider(index: Int) {
                if (index >= providers.size) {
                    showError()
                    return
                }

                val provider = providers[index]

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    locationManager.getCurrentLocation(
                        provider,
                        null,
                        ContextCompat.getMainExecutor(context)
                    ) { location ->
                        if (location != null) {
                            useLocation(location)
                        } else {
                            requestProvider(index + 1)
                        }
                    }
                } else {
                    @Suppress("DEPRECATION")
                    locationManager.requestSingleUpdate(
                        provider,
                        object : LocationListener {
                            override fun onLocationChanged(location: Location) {
                                useLocation(location)
                            }

                            override fun onProviderEnabled(provider: String) {}

                            override fun onProviderDisabled(provider: String) {}

                            @Deprecated("Deprecated in Android")
                            override fun onStatusChanged(
                                provider: String?,
                                status: Int,
                                extras: Bundle?
                            ) {}
                        },
                        Looper.getMainLooper()
                    )
                }
            }

            requestProvider(0)

        } catch (e: SecurityException) {
            isLoading = false
            message = if (isEnglish) {
                "Location permission is required."
            } else {
                "Izin lokasi diperlukan."
            }
        } catch (e: Exception) {
            showError()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted =
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true

        val coarseGranted =
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fineGranted || coarseGranted) {
            getDeviceLocation()
        } else {
            isLoading = false
            message = if (isEnglish) {
                "Location permission denied."
            } else {
                "Izin lokasi ditolak."
            }
        }
    }

    Button(
        onClick = {
            isLoading = true
            message = ""

            val fineGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            val coarseGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (fineGranted || coarseGranted) {
                getDeviceLocation()
            } else {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        },
        modifier = Modifier.fillMaxWidth(),
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Text(
                if (isEnglish) {
                    "Use My Location"
                } else {
                    "Gunakan Lokasi Saya"
                }
            )
        }
    }

    if (message.isNotEmpty()) {
        Text(text = message)
    }
}