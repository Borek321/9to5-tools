package software.ninetofive.tools.factory

import org.threeten.bp.LocalDate
import org.threeten.bp.Period
import javax.inject.Inject

open class DateFactory @Inject constructor() {

    open fun fromEpochDay(epoch: Long): LocalDate = LocalDate.ofEpochDay(epoch)

    open fun now(): LocalDate = LocalDate.now()

    open fun periodBetween(date: LocalDate, otherDate: LocalDate) = Period.between(date, otherDate)

}