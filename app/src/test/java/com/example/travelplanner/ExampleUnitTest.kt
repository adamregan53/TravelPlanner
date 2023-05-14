package com.example.travelplanner

import com.google.firebase.firestore.GeoPoint
import org.junit.Test

import org.junit.Assert.*
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.GregorianCalendar
import java.util.TimeZone
import java.util.concurrent.TimeUnit

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun insertOpeningHours() {


        val closeHours = 1
        val closeMinutes = 0
        val openHours = 11
        val openMinutes = 0

        val closeTime:LocalTime = LocalTime.of(closeHours,closeMinutes)
        val openTime:LocalTime = LocalTime.of(openHours, openMinutes)

        val currentTime = LocalTime.of(LocalTime.now().hour, LocalTime.now().minute)
        val myTimeZone = TimeZone.getDefault().rawOffset

        val targetOffset = -240
        val getTimeZone = TimeZone.getAvailableIDs()[100]
        val getTimeZoneRaw = TimeZone.getTimeZone(TimeZone.getAvailableIDs()[100]).rawOffset
        val getTimeZoneOffsetMinutes = TimeUnit.MILLISECONDS.toMinutes(getTimeZoneRaw.toLong())

        val startDateSeconds = 1686916800
        val endDateSeconds = 1687546800
        val difference = endDateSeconds - startDateSeconds

        val calendar: GregorianCalendar = GregorianCalendar(2023,0,23)

        println("closeTime $closeTime")
        println("openTime $openTime")
        println("currentTime $currentTime $myTimeZone")
        println(getTimeZoneOffsetMinutes)

    }

    @Test
    fun findDifferenceInDates(){
        val startDateSeconds:Long = 1686916800
        val endDateSeconds:Long = 1687546800
        val difference = endDateSeconds - startDateSeconds
        val days = difference/86400
        val hours = difference/3600
        val startDate = getDateString(startDateSeconds)
        val endDate = getDateString(endDateSeconds)

        println("difference: $difference")
        println("days: $days")
        println("hours: $hours")
        println("startDate: $startDate")
        println("endDate: $endDate")

        var nextHour = startDateSeconds
        var slot = getDateString(nextHour)
        for(i in 1..hours){
            println(slot)
            println(nextHour)
            nextHour += 3600
            slot = getDateString(nextHour)

        }
    }

    private val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm")
    private fun getDateString(time: Long) : String = sdf.format(time * 1000)

    @Test
    fun hashmapTest(){
        val placeOpeningHoursHashMap = hashMapOf<Any, Any>()
        placeOpeningHoursHashMap["openDay"] = "MONDAY"
        placeOpeningHoursHashMap["openHours"] = 1

        val array: ArrayList<String> = ArrayList<String>()
        array.add("Monday")
        array.add("Tuesday")
        array.add("Wednesday")
        placeOpeningHoursHashMap["array"] = array

        val coordinates: GeoPoint = GeoPoint(40.7288053,-73.9896791)
        placeOpeningHoursHashMap["coordinates"] = coordinates

        println(placeOpeningHoursHashMap["openDay"])
        println(placeOpeningHoursHashMap["openHours"])
        println("array: ${placeOpeningHoursHashMap["array"]}")
        println("coordinates: ${placeOpeningHoursHashMap["coordinates"]}")

    }


}