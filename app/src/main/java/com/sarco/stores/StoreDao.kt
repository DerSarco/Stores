package com.sarco.stores

import androidx.room.*

//DATA ACCESS OBJECT
@Dao
interface StoreDao {
//    es una interfaz que se encarga de manejar las request a la base de datos.

    @Query("SELECT * FROM StoreEntity")
    fun getAllStores(): MutableList<StoreEntity>

    @Insert
    fun addStore(storeEntity: StoreEntity)

    @Update
    fun updateStore(storeEntity: StoreEntity)

    @Delete
    fun deleteStore(storeEntity: StoreEntity)

}