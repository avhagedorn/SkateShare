package com.skateshare.android

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import com.skateshare.db.LocalRoutesDao
import com.skateshare.db.LocalRoutesDatabase
import com.skateshare.models.Route
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

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

    private fun generateDummyData() : HashMap<String, Any> {

        val altitude = mutableListOf<Double>()
        val speed = mutableListOf<Float>()
        val routeData = mutableListOf<LatLng>()

        for (i in 0..1200) {
            val x = Math.random() * 100.0
            val y = Math.random() * 100.0

            speed.add(y.toFloat())
            routeData.add(LatLng(x, y))
        }

        return hashMapOf(
            "path" to PolyUtil.encode(routeData),
            "rawAltitude" to altitude,
            "rawSpeed" to speed
        )
    }

    @Test
    @Throws(Exception::class)
    fun insertRoute() = runBlocking {
        for (i in 0 until 5) {
            val rawData = generateDummyData()
            val rawRoute = rawData["path"] as String
            val rawAltitude = rawData["rawAltitude"] as MutableList<Double>
            val rawSpeed = rawData["rawSpeed"] as MutableList<Float>


            localRoutesDao.insert(
                Route(
                    time_start = 0L,
                    duration = 2700000L,
                    length_km = 0.0,
                    length_mi = 0.0,
                    speed = rawSpeed,
                    path = rawRoute
                )
            )
        }
        assertEquals(localRoutesDao.getNumPrivateRoutes(), 5)
        localRoutesDao.deleteAll()
        assertEquals(localRoutesDao.getNumPrivateRoutes(), 0)
    }

    @Test
    @Throws(Exception::class)
    fun totalRidingDuration() = runBlocking {
        val iterations = 5
        var targetDuration = 0L
        for (i in 0 until iterations) {
            val rawData = generateDummyData()
            val rawRoute = rawData["path"] as String
            val rawAltitude = rawData["rawAltitude"] as MutableList<Double>
            val rawSpeed = rawData["rawSpeed"] as MutableList<Float>

            localRoutesDao.insert(
                Route(
                    time_start = 0L,
                    duration = 2700L,
                    length_km = 0.0,
                    length_mi = 0.0,
                    speed = rawSpeed,
                    path = rawRoute
                )
            )
        }
        assertEquals(localRoutesDao.getTotalRideTimeSeconds(), iterations * 270L)
        localRoutesDao.deleteAll()
    }

    @Test
    @Throws(Exception::class)
    fun queryRoutesByStartTime() = runBlocking {
        var startTime = 1000L
        for (i in 0 until 15) {
            val rawData = generateDummyData()
            val rawRoute = rawData["path"] as String
            val rawAltitude = rawData["rawAltitude"] as MutableList<Double>
            val rawSpeed = rawData["rawSpeed"] as MutableList<Float>

            localRoutesDao.insert(
                Route(
                    time_start = startTime,
                    duration = 2700000L,
                    length_km = 0.0,
                    length_mi = 0.0,
                    speed = rawSpeed,
                    path = rawRoute
                )
            )
            startTime += 1000L
        }
        val query = localRoutesDao.routesByDate(10, 0)

        assertEquals(query.size, 10)
        assertEquals(query.last().time_start, 6000L)
        localRoutesDao.deleteAll()
    }
}