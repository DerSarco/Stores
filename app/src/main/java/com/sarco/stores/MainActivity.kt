package com.sarco.stores

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sarco.stores.databinding.ActivityMainBinding
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainActivity : AppCompatActivity(), OnClickListener, MainAux {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mAdapter: StoreAdapter
    private lateinit var mGridLayout: GridLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

//        mBinding.btnSave.setOnClickListener {
//            val store = StoreEntity(name = mBinding.etName.text.toString().trim())
//            Thread{
//                StoreApplication.database.storeDao().addStore(store)
//            }.start()
//            mAdapter.add(store)
//        }
//      función que se encarga de levantar el fragment al momento de dar click en nuestro
//        floating action button.
        mBinding.fab.setOnClickListener {
            launchEditFragment()
        }
//llamada a función para bindeo de datos en la aplicación
        setupRecyclerView()
    }
//le definimos un nuevo parametro de tipo bundle, args, se inicializa en nulo por que puede
//    que no se ejecute desde el item del recycler view.
    private fun launchEditFragment(args: Bundle? = null) {
//        instanciamos una nueva clase del fragmento creado para poder llamarlo
        val fragment = EditStoreFragment()


//        si los argumentos vienen completados, se los entregamos al fragmento,
        if(args != null) fragment.arguments = args
//        para manejar el fragmento, necesitamos al fragmentManager e iniciar su transaccion
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
//        definimos donde queremos que se muestre nuestro fragmento, en este caso, definimos
//        un id para el constraint layout del mainactivity.
        fragmentTransaction.add(R.id.containerMain, fragment)
//        con esta función le decimos a nuestra app que al apretar el boton para volver,
//        vuelva a la pantalla anterior.
        fragmentTransaction.addToBackStack(null)
//        con commit levantamos el fragmento en la vista definida
        fragmentTransaction.commit()

        hideFab();
    }

//
    private fun setupRecyclerView() {
//    inicializamos el adaptador, este adaptador solicita el listado de elementos y el listener
        mAdapter = StoreAdapter(mutableListOf(), this)
//    grid layout se encarga de manejar los elementos del recyclerview, para este caso, se uso
//    un grid de 2 columnas


        mGridLayout = GridLayoutManager(this, resources.getInteger(R.integer.main_columns))
//    obtenemos las tiendas que estan almacenadas en nuestra base de datos de room
        getStores()
//    seteamos nuestro recyclerView con los parametros instanciados arriba
        mBinding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = mGridLayout
            adapter = mAdapter
        }
    }
//función que trae la información desde la base de datos de Room
    private fun getStores(){
        doAsync {
            val stores = StoreApplication.database.storeDao().getAllStores()
            uiThread {
                mAdapter.setStores(stores)
            }
        }

    }


    /*
    * OnClickListener
    * */

//    en el evento on click del item, definimos lo que se hará al momento de pinchar sobre
//    una tiennda
    override fun onClick(storeId: Long) {
//    se declara una constante de tipo bundle
        val args = Bundle()
//    dentro de la misma hacemos un put del dato, en este caso un Long, se define una clave
//    y su valor
        args.putLong(getString(R.string.arg_id), storeId)
//      llamamos al launcher del fragment entregando los argumentos
        launchEditFragment(args)
    }

//    hacemos el override respectivo de la función que se encuentra en la interfaz,
//    usando Anko en este caso (Anko esta deprecado) definimos que sucedera con el estado
//    de favorito.
    override fun onFavoriteStore(storeEntity: StoreEntity) {
        storeEntity.isFavorite = !storeEntity.isFavorite
//    función asincrona que se encarga de actualizar el dato en BD
        doAsync {
            StoreApplication.database.storeDao().updateStore(storeEntity)
            uiThread{
//                actualizamos la vista avisando que un dato fue actualizado.
                updateStore(storeEntity)
            }
        }
    }

//    función para eliminar la tienda
    override fun onDeleteStore(storeEntity: StoreEntity) {
        val items = resources.getStringArray(R.array.array_options_item)

    MaterialAlertDialogBuilder(this)
        .setTitle(R.string.dialog_options_title)
        .setItems(items) { _, which ->
            when(which){
                0 -> confirmDelete(storeEntity)

                1 -> dial(storeEntity.phone)

                2 -> goToWebsite(storeEntity.website)
            }
        }
        .show()
}

    private fun confirmDelete(storeEntity: StoreEntity){
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_delete_title)
            .setPositiveButton(R.string.dialog_delete_button) { _, _ ->
                //    con Anko usamor el async para llamar a la bd y eliminar el objeto.
                doAsync {
                    StoreApplication.database.storeDao().deleteStore(storeEntity)
                    uiThread {
//                actualizamos la vista una vez el objeto haya sido eliminado.
                        mAdapter.delete(storeEntity)
                    }
                }
            }
            .setNegativeButton(R.string.dialog_cancel_button, null)
            .show()
    }

    private fun dial(phone: String){
        val callIntent = Intent().apply {
            action = Intent.ACTION_DIAL
            data = Uri.parse("tel:$phone")
        }
      startIntent(callIntent)
    }

    private fun goToWebsite(website: String){
        if(website.isEmpty()){
            Toast.makeText(this, R.string.main_error_no_website, Toast.LENGTH_LONG).show()
        }else {
            val websiteIntent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(website)
            }
            startIntent(websiteIntent)
        }
    }

    private fun startIntent(intent: Intent){
        if(intent.resolveActivity(packageManager) != null) startActivity(intent) else
            Toast.makeText(this, R.string.main_error_no_resolve, Toast.LENGTH_LONG).show()
    }

    /*
    * */

//    función que se encarga de mostrar u ocultar el floating action button
    override fun hideFab(isVisible: Boolean) {
        if (isVisible) mBinding.fab.show() else mBinding.fab.hide()
    }

    //función que sirve para comunicar el fragment con la activity, se encarga de llamar la
//    función del adapter para agregar una nueva tienda
    override fun addStore(storeEntity: StoreEntity) {
        mAdapter.add(storeEntity)
    }

    override fun updateStore(storeEntity: StoreEntity) {
        mAdapter.update(storeEntity)
    }
}