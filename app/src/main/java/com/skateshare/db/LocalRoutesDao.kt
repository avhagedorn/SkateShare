package com.skateshare.db

import androidx.room.*
import com.skateshare.models.Route

@Dao
interface LocalRoutesDao {

    @Insert
    fun insert(route: Route)

    @Update
    fun update(route: Route)

    @Delete
    fun delete(route: Route)

    @Query("SELECT COUNT(*) FROM my_routes")
    fun getNumPrivateRoutes() : Int

    @Query("DELETE FROM my_routes")
    fun deleteALl()

    @Query("SELECT SUM(length_mi) FROM my_routes")
    fun getTotalDistanceMi() : Double

    @Query("SELECT SUM(length_km) FROM my_routes")
    fun getTotalDistanceKm() : Double

    @Query("SELECT SUM(duration_millis) FROM my_routes")
    fun getTotalRideTimeMillis() : Long

    @Query("SELECT * FROM my_routes ORDER BY avg_speed LIMIT :limit OFFSET :offset")
    fun routesBySpeed(limit: Int, offset: Int) : List<Route>

    @Query("SELECT * FROM my_routes ORDER BY start_time_millis LIMIT :limit OFFSET :offset")
    fun routesByDate(limit: Int, offset: Int) : List<Route>

    @Query("SELECT * FROM my_routes ORDER BY duration_millis LIMIT :limit OFFSET :offset")
    fun routesByDuration(limit: Int, offset: Int) : List<Route>

    @Query("SELECT * FROM my_routes ORDER BY length_km LIMIT :limit OFFSET :offset")
    fun routesByDistance(limit: Int, offset: Int) : List<Route>
}