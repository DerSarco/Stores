package com.sarco.stores

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

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


//    definimos una constante que hara referencia a un objeto tipo Migrationm
//    el cual recibe las versiones de la base de datos que vamos a migrar desde hasta
    val MIGRATION_1_2 = object : Migration(1,2) {
//        se sobreescribe la función migrate que se encarga de alterar la tabla
        override fun migrate(database: SupportSQLiteDatabase) {
//    se llama al parametro database, donde le entregamos la Query que queremos ejecutar.
//    en este caso es la alteración de la tabla StoreEntity, donde agregamos la nueva columna
//    photo url, y para los demas entradas existentes diremos que no seran nulas y por defecto
//    tendran un texto en blanco
            database.execSQL("ALTER TABLE StoreEntity ADD COLUMN photoUrl TEXT NOT NULL DEFAULT ''")
        }
    }
        database = Room.databaseBuilder(this,
            StoreDatabase::class.java,
            "StoreDatabase")
//                le decimos al databaseBuilde que migre la base de datos
            .addMigrations(MIGRATION_1_2)
            .build()
    }

}