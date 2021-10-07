package com.sarco.stores

import androidx.room.Entity
import androidx.room.PrimaryKey
//la data class se debe identificar como entidad, para asi decirle a la base de datos
//que esta clase debe utilizarse como base en la creación de los campos de la base de datos local
@Entity(tableName = "StoreEntity")
//con PrimaryKey, definimos cual es el campo clave en la base de datos, y autogenerate define que
//este campo sera autoincrementable.
data class StoreEntity(@PrimaryKey(autoGenerate = true) var id: Long = 0,
                       var name: String,
                       var phone: String = "",
                       var website: String = "",
                       var isFavorite: Boolean = false)
