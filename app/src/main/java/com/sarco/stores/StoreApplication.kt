package com.sarco.stores

import android.app.Application
import androidx.room.Room

class StoreApplication: Application() {

//    companion object se encarga de utilizarse como un singleton, esto para poder
//    acceder a esta variable desde cualquier parte de la aplicación
    companion object{
        lateinit var database: StoreDatabase
    }
//para poder utilizar este patron singleton y usar Room, debemos sobreescribir la función de
//    onCreate.
    override fun onCreate() {
        super.onCreate()
//        igualamos la variable database, del companion object a una instancia de Room.databaseBuilder
//    a esta debemos entregarle la clase a la que hara referencia esta base de datos.
        database = Room.databaseBuilder(this,
            StoreDatabase::class.java,
            "StoreDatabase").build()
    }

}