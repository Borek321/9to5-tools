package software.ninetofive.locationfinder.options

class AccuracyOption(
    private val distanceInMeters: Int,
    private val dateEpoch: Long
) : LocationFinderOption {

}