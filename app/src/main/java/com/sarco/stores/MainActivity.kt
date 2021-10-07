package com.sarco.stores

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
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

        mBinding.fab.setOnClickListener {
            launchEditFragment()
        }

        setupRecyclerView()
    }

    private fun launchEditFragment() {
        val fragment = EditStoreFragment()

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.add(R.id.containerMain, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

        hideFab();
    }

    private fun setupRecyclerView() {
        mAdapter = StoreAdapter(mutableListOf(), this)
        mGridLayout = GridLayoutManager(this, 2)
        getStores()

        mBinding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = mGridLayout
            adapter = mAdapter
        }
    }

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
    override fun onClick(storeEntity: StoreEntity) {

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
                mAdapter.update(storeEntity)
            }
        }
    }

//    función para eliminar la tienda
    override fun onDeleteStore(storeEntity: StoreEntity) {
//    con Anko usamor el async para llamar a la bd y eliminar el objeto.
        doAsync {
            StoreApplication.database.storeDao().deleteStore(storeEntity)
            uiThread {
//                actualizamos la vista una vez el objeto haya sido eliminado.
                mAdapter.delete(storeEntity)
            }
        }
    }

    /*
    * */
    override fun hideFab(isVisible: Boolean) {
        if (isVisible) mBinding.fab.show() else mBinding.fab.hide()
    }

    override fun addStore(storeEntity: StoreEntity) {
        mAdapter.add(storeEntity)
    }

    override fun updateStore(storeEntity: StoreEntity) {
    }
}