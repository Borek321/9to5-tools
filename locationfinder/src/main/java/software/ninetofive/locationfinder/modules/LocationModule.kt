package software.ninetofive.locationfinder.modules

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module

@Module
class LocationModule(private val context: Context) {

    fun locationClient(): FusedLocationProviderClient {
        return FusedLocationProviderClient(context)
    }
}