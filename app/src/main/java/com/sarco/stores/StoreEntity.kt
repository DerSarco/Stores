package com.sarco.stores

import androidx.room.Entity
import androidx.room.PrimaryKey
//la data class se debe identificar como entidad, para asi decirle a la base de datos
//que esta clase debe utilizarse como base en la creación de los campos de la base de datos local
@Entity(tableName = "StoreEntity")
//con PrimaryKey, definimos cual es el campo clave en la base de datos, y autogenerate define que
//este campo sera autoincrementable.

//el parametro photoUrl fue agregado, pero para poder avisar que tenemos que alterar la tabla
//se deben realizar algunas modificaciones.
//ver StoreApplication y StoreDatabase
data class StoreEntity(@PrimaryKey(autoGenerate = true) var id: Long = 0,
                       var name: String,
                       var phone: String,
                       var website: String = "",
                       var photoUrl: String,
                       var isFavorite: Boolean = false){

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StoreEntity

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
