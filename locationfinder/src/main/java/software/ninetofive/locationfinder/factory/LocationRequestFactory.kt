package software.ninetofive.locationfinder.factory

import com.google.android.gms.location.LocationRequest
import software.ninetofive.locationfinder.options.LocationRequestOption
import javax.inject.Inject

class LocationRequestFactory @Inject constructor() {

    fun createRequest(option: LocationRequestOption): LocationRequest {
        return LocationRequest.create()
            .setInterval(option.interval)
            .setMaxWaitTime(option.maxWaitTime)
            .setFastestInterval(option.fastestInterval)
            .setPriority(option.priority)
    }
}