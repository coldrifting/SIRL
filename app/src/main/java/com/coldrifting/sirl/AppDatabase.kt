package com.coldrifting.sirl

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Database(entities = [Store::class, StoreLocation::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDAO
}

@Dao
interface AppDAO {
    // Stores
    @Query("SELECT * FROM Store")
    fun allStores() : Flow<List<Store>>

    @Upsert
    suspend fun addStore(data : Store)

    @Delete(entity = Store::class)
    suspend fun deleteStore(storeId: StoreId)

    @Query("SELECT COALESCE((SELECT MIN(storeId) FROM Store), -1)")
    suspend fun firstStoreIdOrDefault() : Int

    // Store Aisle Locations
    @Query("SELECT * FROM StoreLocation ORDER BY sortingPrefix")
    fun allLocations() : Flow<List<StoreLocation>>

    @Upsert
    suspend fun addLocation(data : StoreLocation)

    @Upsert
    suspend fun addLocations(data : List<StoreLocation>)

    @Delete(entity = StoreLocation::class)
    suspend fun deleteLocation(storeLocationId: StoreLocationId)

    @Query("SELECT MAX(sortingPrefix) FROM StoreLocation")
    suspend fun maxSortingPrefixValue() : Int

    @Query("UPDATE StoreLocation SET locationName = :newLocationName WHERE locationId = :locationId")
    fun updateLocation(locationId: Int, newLocationName: String)
}

@Entity
data class Store(
    @PrimaryKey(autoGenerate = true)
    var storeId: Int = 0,
    var storeName: String)

@Entity
data class StoreId(
    val storeId: Int)

@Entity
data class StoreLocation(
    @PrimaryKey(autoGenerate = true)
    val locationId: Int = 0,
    val storeId: Int,
    val locationName: String,
    val sortingPrefix: Int = 0)

@Entity
data class StoreLocationId(
    val locationId: Int)