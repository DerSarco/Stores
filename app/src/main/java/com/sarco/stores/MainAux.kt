package com.sarco.stores

//interfaz encargada de generar las funciones que se utilizaran en la main activity
//con tal de poder ser llamadas por otra vista que extienda a esta
interface MainAux {

    fun hideFab(isVisible: Boolean = false)

    fun addStore(storeEntity: StoreEntity)

    fun updateStore(storeEntity: StoreEntity)


}