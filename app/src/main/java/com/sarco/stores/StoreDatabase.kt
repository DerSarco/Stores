package com.sarco.stores

import androidx.room.Database
import androidx.room.RoomDatabase
//para decir que la clase es una clase de base de datos debemos usar el identificador @Database
@Database(
//    a la database debemos entregarle la entidad que usará para manejar la base de datos,
//    esto define los campos.
//    anteriormente teniamos la versión 1, fue cambiada por la versión 2 para que esta pueda ser
//    alterada.
    entities = arrayOf(StoreEntity::class),
    version = 2
)
//la clase de la base de datos debe ser abstracta y extender de RoomDatabase()
abstract class StoreDatabase: RoomDatabase() {
//    la funcion storeDao que extiende de la misma hace refeerencia a la interfaz que maneja
//    todos los request a la BD.
    abstract fun storeDao(): StoreDao

}