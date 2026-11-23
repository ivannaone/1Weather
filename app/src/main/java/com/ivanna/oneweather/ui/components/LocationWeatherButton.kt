package com.ivanna.oneweather.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
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

    fun getDeviceLocation() {
        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        try {
            val provider = when {
                locationManager.isProviderEnabled(
                    LocationManager.NETWORK_PROVIDER
                ) -> LocationManager.NETWORK_PROVIDER

                locationManager.isProviderEnabled(
                    LocationManager.GPS_PROVIDER
                ) -> LocationManager.GPS_PROVIDER

                else -> null
            }

            if (provider == null) {
                isLoading = false
                message = if (isEnglish) {
                    "Please enable location services"
                } else {
                    "Aktifkan layanan lokasi terlebih dahulu"
                }
                return
            }

            val lastLocation = locationManager.getLastKnownLocation(provider)

            if (lastLocation != null) {
                isLoading = false
                message = ""
                onLocationFound(
                    lastLocation.latitude,
                    lastLocation.longitude
                )
                return
            }

            locationManager.requestSingleUpdate(
                provider,
                object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        isLoading = false
                        message = ""

                        onLocationFound(
                            location.latitude,
                            location.longitude
                        )
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
        } catch (e: SecurityException) {
            isLoading = false
            message = if (isEnglish) {
                "Location permission is required"
            } else {
                "Izin lokasi diperlukan"
            }
        } catch (e: Exception) {
            isLoading = false
            message = if (isEnglish) {
                "Failed to get device location"
            } else {
                "Gagal mendapatkan lokasi perangkat"
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted =
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (granted) {
            getDeviceLocation()
        } else {
            isLoading = false
            message = if (isEnglish) {
                "Location permission denied"
            } else {
                "Izin lokasi ditolak"
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