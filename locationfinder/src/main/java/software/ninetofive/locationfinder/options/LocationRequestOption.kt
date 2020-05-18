package software.ninetofive.locationfinder.options

import com.google.android.gms.location.LocationRequest

class LocationRequestOption(
    val interval: Long = 1000L,
    val maxWaitTime: Long = 1500L,
    val fastestInterval: Long = 100L,
    val priority: Int = LocationRequest.PRIORITY_HIGH_ACCURACY
) {
}