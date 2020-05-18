package software.ninetofive.locationfinder.modules

import dagger.Component
import software.ninetofive.locationfinder.LocationFinder

@Component(
    modules = [LocationModule::class]
)
interface LocationFinderComponent {

    fun inject(finder: LocationFinder)
}