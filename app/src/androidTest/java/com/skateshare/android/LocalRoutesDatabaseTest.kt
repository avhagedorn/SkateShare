package com.skateshare.android

import android.util.Log
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.gms.maps.model.LatLng
import com.skateshare.db.LocalRoutesDao
import com.skateshare.db.LocalRoutesDatabase
import com.skateshare.models.Route
import com.skateshare.services.MapService
import junit.framework.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.text.ChoiceFormat.nextDouble
import java.util.*
import kotlin.collections.HashMap
import kotlin.random.Random.Default.nextDouble

@RunWith(AndroidJUnit4::class)
class LocalRoutesDatabaseTest {

    private lateinit var localRoutesDao: LocalRoutesDao
    private lateinit var db: LocalRoutesDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, LocalRoutesDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        localRoutesDao = db.localRoutesDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    private fun generateDummyData() : HashMap<String, MutableList<out Any>> {

        val altitude = mutableListOf<Double>()
        val speed = mutableListOf<Float>()
        val routeData = mutableListOf<LatLng>()

        for (i in 0..1200) {
            val x = Math.random() * 100.0
            val y = Math.random() * 100.0

            altitude.add(x)
            speed.add(y.toFloat())
            routeData.add(LatLng(x, y))
        }

        return hashMapOf(
            "rawRoute" to routeData,
            "rawAltitude" to altitude,
            "rawSpeed" to speed
        )
    }

    @Test
    @Throws(Exception::class)
    fun insertRoute() {
        for (i in 0 until 5) {
            val rawData = generateDummyData()
            val rawRoute = rawData["rawRoute"] as MutableList<LatLng>
            val rawAltitude = rawData["rawAltitude"] as MutableList<Double>
            val rawSpeed = rawData["rawSpeed"] as MutableList<Float>

            val lats = mutableListOf<Double>()
            val lngs = mutableListOf<Double>()

            rawRoute.forEach {
                lats.add(it.latitude)
                lngs.add(it.longitude)
            }

            localRoutesDao.insert(
                Route(
                    time_start = 0L,
                    duration = 2700000L,
                    length_km = 0.0,
                    length_mi = 0.0,
                    altitude = rawAltitude,
                    speed = rawSpeed,
                    lat_path = lats,
                    lng_path = lngs
                )
            )
        }
        assertEquals(localRoutesDao.getNumPrivateRoutes(), 5)
        localRoutesDao.deleteALl()
        assertEquals(localRoutesDao.getNumPrivateRoutes(), 0)
    }

    @Test
    @Throws(Exception::class)
    fun totalRidingDuration() {
        val iterations = 5
        var targetDuration = 0L
        for (i in 0 until iterations) {
            val rawData = generateDummyData()
            val rawRoute = rawData["rawRoute"] as MutableList<LatLng>
            val rawAltitude = rawData["rawAltitude"] as MutableList<Double>
            val rawSpeed = rawData["rawSpeed"] as MutableList<Float>

            val lats = mutableListOf<Double>()
            val lngs = mutableListOf<Double>()

            rawRoute.forEach {
                lats.add(it.latitude)
                lngs.add(it.longitude)
            }

            localRoutesDao.insert(
                Route(
                    time_start = 0L,
                    duration = 2700000L,
                    length_km = 0.0,
                    length_mi = 0.0,
                    altitude = rawAltitude,
                    speed = rawSpeed,
                    lat_path = lats,
                    lng_path = lngs
                )
            )
        }
        assertEquals(localRoutesDao.getTotalRideTimeMillis(), iterations * 2700000L)
        localRoutesDao.deleteALl()
    }

    @Test
    @Throws(Exception::class)
    fun queryRoutesByStartTime() {
        var startTime = 1000L
        for (i in 0 until 15) {
            val rawData = generateDummyData()
            val rawRoute = rawData["rawRoute"] as MutableList<LatLng>
            val rawAltitude = rawData["rawAltitude"] as MutableList<Double>
            val rawSpeed = rawData["rawSpeed"] as MutableList<Float>

            val lats = mutableListOf<Double>()
            val lngs = mutableListOf<Double>()

            rawRoute.forEach {
                lats.add(it.latitude)
                lngs.add(it.longitude)
            }

            localRoutesDao.insert(
                Route(
                    time_start = startTime,
                    duration = 2700000L,
                    length_km = 0.0,
                    length_mi = 0.0,
                    altitude = rawAltitude,
                    speed = rawSpeed,
                    lat_path = lats,
                    lng_path = lngs
                )
            )
            startTime += 1000L
        }
        val query = localRoutesDao.routesByDate(10, 0)

        assertEquals(query.size, 10)
        assertEquals(query.last().time_start, 10000L)
        localRoutesDao.deleteALl()
    }
}