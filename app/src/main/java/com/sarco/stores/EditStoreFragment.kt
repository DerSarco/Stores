package com.sarco.stores

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.sarco.stores.databinding.FragmentEditStoreBinding
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class EditStoreFragment : Fragment() {

//    la variable binding se inicializará una vez el fragmento sea llamado.
    private lateinit var mBinding: FragmentEditStoreBinding
//    variable que de tipo MainActivity, que nos sirve para acceder a las funcones de esta
    private var mActivity: MainActivity? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        seteamos el fragment en un binding para acceder a sus elementos
        mBinding = FragmentEditStoreBinding.inflate(inflater, container, false)
        // retornamos el binding inflado
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        una vez que en el ciclo de vida del fragmento, este es creado, instanciamos la
//        MainActivity dentro de la variable mActivity, definiendo que puede que sea nula con ?
        mActivity = activity as? MainActivity
        //mostramos el boton de regresar.
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        seteamos el titulo para el fragmento
        mActivity?.supportActionBar?.title = getString(R.string.edit_store_title_add)

        setHasOptionsMenu(true)

//        generamos un evento para el edit text, donde obtendremos la URL y mostraremos un
//        preview con Glide
        mBinding.etPhotoUrl.addTextChangedListener {
            Glide.with(this)
                .load(mBinding.etPhotoUrl.text.toString())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(mBinding.imgPhoto)


        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
//este override define que acciones se realizaran en la barra superior de opciones, donde alojamos
//    algunos botones
    override fun onOptionsItemSelected(item: MenuItem): Boolean {


        return when(item.itemId){
            /*al clickear en el boton de back, este volvera a la main activity*/
            android.R.id.home -> {
                mActivity?.onBackPressed()
                true
            }
//            al dar click en el boton de guardar este almacenara nuestra infiormación en Room
            R.id.action_save -> {
//                hacemos una nueva instancia de la clase Store, entregando los parametros
//                para accionar sus setter.
                val store = StoreEntity(name = mBinding.etName.text.toString().trim(),
                    phone = mBinding.etPhone.text.toString().trim(),
                    website = mBinding.etWebsite.toString().trim(),
                photoUrl = mBinding.etPhotoUrl.text.toString().trim())

                doAsync {
//                    llamamos al Dao para agregar una nueva tienda a la BD
                    store.id = StoreApplication.database.storeDao().addStore(store)
                    uiThread {
//                        llamamos a la función de la main activity que se encarga de avisar a la
//                        interfaz que hubo una actualización de datos.
                        mActivity?.addStore(store)
//                        ocultamos el teclado
                        hideKeyboard()
//                        mostramos un mensaje para dar aviso del success de la entrada en BD
//                        Snackbar.make(mBinding.root, R.string.edit_store_message_success,
//                            Snackbar.LENGTH_SHORT)
//                            .show()

                        Toast.makeText(mActivity, R.string.edit_store_message_success,
                            Toast.LENGTH_SHORT)
                            .show()
//                        volvemos a la main activity.
                        mActivity?.onBackPressed()
                    }
                }
                true
            }else  -> return super.onOptionsItemSelected(item)

        }
    }

//    función encargada de ocultar el teclado
    private fun hideKeyboard(){
        val inm = mActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if(view != null){
            inm.hideSoftInputFromWindow(requireView().windowToken, 0)
        }
    }
//
//función que se ejecuta antes del destroy, parte del ciclo de vida del fragmento
    override fun onDestroyView() {
//    antes del super aun poseemos control sobre el fragmento, ocultamos el teclado
        hideKeyboard()
        super.onDestroyView()
    }
//una vez destruida la vista se acciona esta función
    override fun onDestroy() {
//ocultamos el boton de regresar.
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
//    le entregamos el anterior titulo a la app
        mActivity?.supportActionBar?.title = getString(R.string.app_name)
//    mostramos el flaoting action button
        mActivity?.hideFab(true)
//    ocultamos el menu de la derecha, donde esta el boton de guardado.
        setHasOptionsMenu(false)
//    se destruye la vista.
        super.onDestroy()

    }

}