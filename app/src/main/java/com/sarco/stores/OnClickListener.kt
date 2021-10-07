package com.sarco.stores

//interfaz encargada de mantener el evento onclick para su respectivo override
interface OnClickListener {

    fun onClick(storeEntity: StoreEntity)
// nueva función que se encargara de manejar el cambio de estado de favoritos.
    fun onFavoriteStore(storeEntity: StoreEntity)
//    función de interfaz para eliminar una entrada
    fun onDeleteStore(storeEntity: StoreEntity)
}