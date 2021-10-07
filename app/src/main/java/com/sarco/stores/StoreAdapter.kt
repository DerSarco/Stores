package com.sarco.stores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sarco.stores.databinding.ItemStoreBinding


/*el adapter nos permite adaptar valga la redundancia el elemento que queremos utilizar
* en este caso item_store, podemos definir tanto el evento OnClick que se invoca desde este
* listener y sobreescribirlo*/
/*esta clase extiende al adapter de RecyclerView, donde le debemos entregar el Holder que mantiene
* la vista donde en en este caso se encuentra el item_store*/
class StoreAdapter(private var stores: MutableList<StoreEntity>, private var listener: OnClickListener):
    RecyclerView.Adapter<StoreAdapter.ViewHolder>() {

    //la m hace referencia a que es miembro de la clase.
//    lateinit indica que la variable inicializara mas adelante
//    en este caso es una variable de tipo context para obtener el contexto de la vista.
    private lateinit var mContext: Context

//    en esta funcion nos encargamos de inflar la vista y decirle a nuestro ViewHolder
//    donde se encuentra el item que queremos usar, en este caso es el item_store
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//    entregamos el contexto a la variable creada mas arriba para almacenarla y saber a que
//    vista nos referenciamos
        mContext = parent.context
//inflamos la vista donde esta el item_store para luego entregarla a nuestro ViewHolder
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_store, parent, false)

        return ViewHolder(view)
    }

//    esta función se encarga de bindear los datos en el item, hace una secuencia y dependiendo
//    de la cantidad de datos, este los va agregando.
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        obtenemos la tienda por posicion, entregada por la funcion en los parametros.
        val store = stores[position]
//con with, podemos saltarnos el llamar continuamente a holder, que es el ViewHolder que estamos
//    entregando al momento de instanciar la clase en el main
        with(holder){
//            seteamos la funcion de listener que tendra el item, en este caso onClick
            setListener(store)
//            bindeamos el nombre de la tienda en el elemento de texto.
            binding.tvName.text = store.name
            binding.cbFavorite.isChecked = store.isFavorite
        }
    }

//    entrega la cantidad de elemenntos que se encuentran en la lista.
    override fun getItemCount(): Int = stores.size

//    agregamos una nueva tienda a la lista mutable que tenemos definida en esta clase
    fun add(storeEntity: StoreEntity) {
        stores.add(storeEntity)
//    con esta funcion, aviasmos al adapter que los datos han sufrido cambios y debe actualizarse
        notifyDataSetChanged()
    }

//    Actualizamos la posición del listado
    fun update(storeEntity: StoreEntity) {
//    buscamos la posición del objeto dentro del listado
        val index = stores.indexOf(storeEntity)
//    si es distinto de -1 lo encontró
        if(index != -1){
//            actualizamos la posición del listado
            stores[index] = storeEntity
//            notificamos que un item fue cambiado para que se actualice.
            notifyItemChanged(index)
        }
    }

    //    Actualizamos la posición del listado
    fun delete(storeEntity: StoreEntity) {
//    buscamos la posición del objeto dentro del listado
        val index = stores.indexOf(storeEntity)
//    si es distinto de -1 lo encontró
        if(index != -1){
//            removemos el objeto de la posición
            stores.removeAt(index)
//            notificamos que un item fue eliminado para que se actualice.
            notifyItemRemoved(index)
        }
    }

//    seteamos la lista a la lista mutable genertal
    fun setStores(stores: MutableList<StoreEntity>) {
        this.stores = stores
        notifyDataSetChanged()
    }

    //    esta clase interna define el ViewHolder para utilizarlo dentro de esta misma,
//    obtenemos los elementos de la vista para ser utilizados mas arriba.
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
//    obtenemos el binding, osea los elementos de esta vista
        val binding = ItemStoreBinding.bind(view)

        fun setListener(storeEntity: StoreEntity){
            with(binding.root, {
//        función que se setea en cada elemento de la lista, se ejecuta como onClick
                setOnClickListener { listener.onClick(storeEntity) }
                setOnLongClickListener {
                    listener.onDeleteStore(storeEntity)
                    true
                }
            })

//          bindeamos la función de favoritos al checkbox pasandole la store como parametro.
            binding.cbFavorite.setOnClickListener { listener.onFavoriteStore(storeEntity) }

        }
    }
}