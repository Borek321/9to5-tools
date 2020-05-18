package software.ninetofive.locationfinder

import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.suspendCancellableCoroutine
import software.ninetofive.locationfinder.factory.LocationRequestFactory
import software.ninetofive.locationfinder.model.Accuracy
import software.ninetofive.locationfinder.modules.DaggerLocationFinderComponent
import software.ninetofive.locationfinder.modules.LocationModule
import software.ninetofive.locationfinder.options.LocationRequestOption
import java.lang.RuntimeException
import javax.inject.Inject
import kotlin.coroutines.suspendCoroutine

class LocationFinder(
    private val accuracy: Accuracy?,
    private val context: Context,
    private val timeOut: Long?,
    private val locationRequestOption: LocationRequestOption,
    private val looper: Looper
) {

    @Inject lateinit var client: FusedLocationProviderClient
    @Inject lateinit var requestFactory: LocationRequestFactory

    init {
        DaggerLocationFinderComponent.builder()
            .locationModule(LocationModule(context))
            .build().inject(this)
    }

    suspend fun findCurrentLocation(): Location? = suspendCancellableCoroutine { continuation ->
        val request = requestFactory.createRequest(locationRequestOption)
        val callBack: LocationCallback = object : LocationCallback() {

            override fun onLocationResult(result: LocationResult?) {
                if (continuation.isActive) {
                    continuation.resumeWith(Result.success(result?.lastLocation))
                    client.removeLocationUpdates(this)
                }
            }
        }
        client.requestLocationUpdates(request, callBack, looper)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    val exception: Exception = task.exception
                        ?: Exception("Could not start location updates")
                    continuation.resumeWith(Result.failure(exception))
                }
            }
    }

    fun startBackgroundUpdates() {

    }

    inner class Builder {

        private var accuracy: Accuracy? = null
        private var context: Context? = null
        private var timeOut: Long? = null
        private var locationRequestOption: LocationRequestOption = LocationRequestOption()
        private var looper: Looper = Looper.getMainLooper()

        fun accuracy(accuracy: Accuracy): Builder {
            this.accuracy = accuracy
            return this
        }

        fun context(context: Context): Builder {
            this.context = context
            return this
        }

        fun timeOut(timeOut: Long): Builder {
            this.timeOut = timeOut
            return this
        }

        fun requestOption(option: LocationRequestOption): Builder {
            this.locationRequestOption = option
            return this
        }

        fun looper(looper: Looper): Builder {
            this.looper = looper
            return this
        }

        fun build(): LocationFinder {
            return context?.let { LocationFinder(accuracy, it, timeOut, locationRequestOption, looper) }
                ?: throw RuntimeException("Some mandatory values has not been set")
        }
    }
}