package com.sarco.stores

//interfaz encargada de mantener el evento onclick para su respectivo override
interface OnClickListener {
//el onClick solo recibirá el id. por ende solo recibiremos un Lonh
//   esto por el tipo de dato definido en StoreEntity.
    fun onClick(storeId: Long)
// nueva función que se encargara de manejar el cambio de estado de favoritos.
    fun onFavoriteStore(storeEntity: StoreEntity)
//    función de interfaz para eliminar una entrada
    fun onDeleteStore(storeEntity: StoreEntity)
}