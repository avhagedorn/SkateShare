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

    @Query("DELETE FROM my_routes")
    suspend fun deleteALl()

    @Query("SELECT SUM(length_mi) FROM my_routes")
    suspend fun getTotalDistanceMi() : Double

    @Query("SELECT SUM(length_km) FROM my_routes")
    suspend fun getTotalDistanceKm() : Double

    @Query("SELECT SUM(duration_millis) FROM my_routes")
    suspend fun getTotalRideTimeMillis() : Long

    @Query("SELECT * FROM my_routes ORDER BY avg_speed DESC LIMIT :limit OFFSET :offset")
    suspend fun routesBySpeed(limit: Int, offset: Int) : List<Route>

    @Query("SELECT * FROM my_routes ORDER BY start_time_millis DESC LIMIT :limit OFFSET :offset")
    suspend fun routesByDate(limit: Int, offset: Int) : List<Route>

    @Query("SELECT * FROM my_routes ORDER BY duration_millis DESC LIMIT :limit OFFSET :offset")
    suspend fun routesByDuration(limit: Int, offset: Int) : List<Route>

    @Query("SELECT * FROM my_routes ORDER BY length_km DESC LIMIT :limit OFFSET :offset")
    suspend fun routesByDistance(limit: Int, offset: Int) : List<Route>
}