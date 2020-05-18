package software.ninetofive.locationfinder

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import software.ninetofive.locationfinder.model.Accuracy
import software.ninetofive.locationfinder.modules.DaggerLocationFinderComponent
import software.ninetofive.locationfinder.modules.LocationModule
import java.lang.RuntimeException
import javax.inject.Inject

class LocationFinder(
    private val accuracy: Accuracy?,
    private val context: Context,
    private val timeOut: Long?
) {

    @Inject lateinit var client: FusedLocationProviderClient

    init {
        DaggerLocationFinderComponent.builder()
            .locationModule(LocationModule(context))
            .build().inject(this)
    }

    suspend fun findCurrentLocation() {
    }

    fun startBackgroundUpdates() {

    }

    inner class Builder {

        private var accuracy: Accuracy? = null
        private var context: Context? = null
        private var timeOut: Long? = null

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

        fun build(): LocationFinder {
            return context?.let { LocationFinder(accuracy, it, timeOut) }
                ?: throw RuntimeException("Some mandatory values has not been set")
        }
    }
}