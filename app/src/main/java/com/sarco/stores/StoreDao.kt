package com.sarco.stores

import androidx.room.*

//DATA ACCESS OBJECT
@Dao
interface StoreDao {
//    es una interfaz que se encarga de manejar las request a la base de datos.

    @Query("SELECT * FROM StoreEntity")
    fun getAllStores(): MutableList<StoreEntity>
//aca definimos que al momento de insertar, se retorne un valor Long que hace referencia al ID

    @Query("SELECT * FROM StoreEntity where id = :id")
    fun getStoreById(id: Long): StoreEntity
    @Insert
    fun addStore(storeEntity: StoreEntity): Long

    @Update
    fun updateStore(storeEntity: StoreEntity)

    @Delete
    fun deleteStore(storeEntity: StoreEntity)

}