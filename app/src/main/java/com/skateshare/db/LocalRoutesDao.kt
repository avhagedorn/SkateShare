package com.skateshare.db

import androidx.room.*
import com.skateshare.models.Route

@Dao
interface LocalRoutesDao {

    @Insert
    suspend fun insert(route: Route)

    @Update
    suspend fun update(route: Route)

    @Delete
    suspend fun delete(route: Route)

    @Query("SELECT COUNT(*) FROM my_routes")
    suspend fun getNumPrivateRoutes() : Int

    @Query("SELECT SUM(length_mi) FROM my_routes")
    suspend fun getTotalDistanceMi() : Double

    @Query("SELECT SUM(length_km) FROM my_routes")
    suspend fun getTotalDistanceKm() : Double

    @Query("SELECT AVG(avg_speed_km) FROM my_routes")
    suspend fun getAvgSpeedKm() : Float

    @Query("SELECT AVG(avg_speed_mi) FROM my_routes")
    suspend fun getAvgSpeedMi() : Float

    @Query("SELECT SUM(duration_seconds) FROM my_routes")
    suspend fun getTotalRideTimeSeconds() : Long

    @Query("SELECT * FROM my_routes WHERE ID = :paramId LIMIT 1")
    suspend fun getRouteById(paramId: Long) : Route?

    @Query("SELECT * FROM my_routes ORDER BY avg_speed_mi DESC LIMIT :limit OFFSET :offset")
    suspend fun routesBySpeed(limit: Int, offset: Int) : List<Route>

    @Query("SELECT * FROM my_routes ORDER BY start_time_millis DESC LIMIT :limit OFFSET :offset")
    suspend fun routesByDate(limit: Int, offset: Int) : List<Route>

    @Query("SELECT * FROM my_routes ORDER BY duration_seconds DESC LIMIT :limit OFFSET :offset")
    suspend fun routesByDuration(limit: Int, offset: Int) : List<Route>

    @Query("SELECT * FROM my_routes ORDER BY length_km DESC LIMIT :limit OFFSET :offset")
    suspend fun routesByDistance(limit: Int, offset: Int) : List<Route>
}