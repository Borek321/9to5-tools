package software.ninetofive.locationfinder

import software.ninetofive.locationfinder.model.Accuracy
import software.ninetofive.locationfinder.options.LocationFinderOption
import javax.inject.Inject
import javax.inject.Singleton

class LocationFinder(
    private val accuracy: Accuracy?
) {

    fun findCurrentLocation() {

    }

    fun startBackgroundUpdates() {
        
    }

    inner class Builder {

        private var accuracy: Accuracy? = null

        fun accuracy(accuracy: Accuracy): Builder {
            this.accuracy = accuracy
            return this
        }

        fun build(): LocationFinder {
            return LocationFinder(accuracy)
        }
    }
}