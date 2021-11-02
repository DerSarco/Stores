package com.sarco.stores

import android.content.Context
import android.os.Bundle
import android.text.BoringLayout
import android.text.Editable
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.sarco.stores.databinding.FragmentEditStoreBinding
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class EditStoreFragment : Fragment() {

    //    la variable binding se inicializará una vez el fragmento sea llamado.
    private lateinit var mBinding: FragmentEditStoreBinding

    //    variable que de tipo MainActivity, que nos sirve para acceder a las funcones de esta
    private var mActivity: MainActivity? = null
    private var mIsEditMode: Boolean = false
    private var mStoreEntity: StoreEntity? = null


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

//      verificamos si los argumentos vienen definidos, como puede que sean nulos se compelta con ?
        val id = arguments?.getLong(getString(R.string.arg_id), 0)

        if (id != null && id != 0L) {
            mIsEditMode = true
            getStore(id)
        } else {
            mIsEditMode = false
            mStoreEntity = StoreEntity(name = "", phone = "", photoUrl = "")
        }

        setupActionBar()
        setupTextFields()
    }

    private fun setupActionBar() {
//        una vez que en el ciclo de vida del fragmento, este es creado, instanciamos la
//        MainActivity dentro de la variable mActivity, definiendo que puede que sea nula con ?
        mActivity = activity as? MainActivity
        //mostramos el boton de regresar.
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        seteamos el titulo para el fragmento
        mActivity?.supportActionBar?.title = if (!mIsEditMode)  getString(R.string.edit_store_title_add)
                                            else  getString(R.string.edit_store_title_edit)

        setHasOptionsMenu(true)
    }

    private fun setupTextFields() {
        //        generamos un evento para el edit text, donde obtendremos la URL y mostraremos un
//        preview con Glide

        with(mBinding){ etName.addTextChangedListener{
                validateFields(tilName)
            }
            etPhone.addTextChangedListener{
                validateFields(tilPhone)
            }
            etPhotoUrl.addTextChangedListener{
                validateFields(tilPhotoUrl)
                loadImageUrl(it.toString().trim())
            }
        }

    }

    private fun loadImageUrl(url: String){
        Glide.with(this)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .into(mBinding.imgPhoto)
    }

    private fun getStore(id: Long) {
        doAsync {
            mStoreEntity = StoreApplication.database.storeDao().getStoreById(id)
            uiThread {
                if (mStoreEntity != null) setUiStore(mStoreEntity!!)
            }
        }
    }

    private fun setUiStore(storeEntity: StoreEntity) {
        with(mBinding) {
            etName.text = storeEntity.name.editable()
            etPhone.text = storeEntity.phone.editable()
            etWebsite.text = storeEntity.website.editable()
            etPhotoUrl.text = storeEntity.photoUrl.editable()

        }
    }

    private fun String.editable(): Editable = Editable.Factory.getInstance().newEditable(this)

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    //este override define que acciones se realizaran en la barra superior de opciones, donde alojamos
//    algunos botones
    override fun onOptionsItemSelected(item: MenuItem): Boolean {


        return when (item.itemId) {
            /*al clickear en el boton de back, este volvera a la main activity*/
            android.R.id.home -> {
                mActivity?.onBackPressed()
                true
            }
//            al dar click en el boton de guardar este almacenara nuestra infiormación en Room
            R.id.action_save -> {
//                hacemos una nueva instancia de la clase Store, entregando los parametros
//                para accionar sus setter.
//                val store = StoreEntity(name = mBinding.etName.text.toString().trim(),
//                    phone = mBinding.etPhone.text.toString().trim(),
//                    website = mBinding.etWebsite.toString().trim(),
//                photoUrl = mBinding.etPhotoUrl.text.toString().trim())
                if (mStoreEntity != null && validateFields(mBinding.tilPhotoUrl,
                        mBinding.tilPhone,
                        mBinding.tilName)) {
                    with(mStoreEntity!!) {
                        name = mBinding.etName.text.toString().trim()
                        phone = mBinding.etPhone.text.toString().trim()
                        website = mBinding.etWebsite.text.toString().trim()
                        photoUrl = mBinding.etPhotoUrl.text.toString().trim()
                    }

                    doAsync {
//                    llamamos al Dao para agregar una nueva tienda a la BD
                        if (mIsEditMode) StoreApplication.database.storeDao()
                            .updateStore(mStoreEntity!!)
                        else mStoreEntity!!.id =
                            StoreApplication.database.storeDao().addStore(mStoreEntity!!)
                        uiThread {
//                        llamamos a la función de la main activity que se encarga de avisar a la
//                        interfaz que hubo una actualización de datos.

//                        ocultamos el teclado

                            hideKeyboard()

                            if (mIsEditMode) {
                                mActivity?.updateStore(mStoreEntity!!)
                                Snackbar.make(
                                    mBinding.root,
                                    R.string.edit_store_message_update_success,
                                    Snackbar.LENGTH_SHORT
                                ).show()

                            } else {
                                mActivity?.addStore(mStoreEntity!!)

//                        mostramos un mensaje para dar aviso del success de la entrada en BD
//                        Snackbar.make(mBinding.root, R.string.edit_store_message_success,
//                            Snackbar.LENGTH_SHORT)
//                            .show()

                                Toast.makeText(
                                    mActivity, R.string.edit_store_message_success,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
//                        volvemos a la main activity.
                                mActivity?.onBackPressed()
                            }
                        }
                    }
                }
                true
            }
            else -> return super.onOptionsItemSelected(item)

        }
    }

    private fun validateFields(vararg textFields: TextInputLayout): Boolean{
        var isValid = true

        for (textField in textFields){
            if(textField.editText?.text.toString().trim().isEmpty()){
                textField.error = getString(R.string.helper_required)
                textField.requestFocus()
                isValid = false
            }else textField.error = null

        }

        if(!isValid) Snackbar.make(mBinding.root,
            R.string.edit_store_message_valid,
            Snackbar.LENGTH_SHORT).show()

        return isValid
    }

    private fun validateFields(): Boolean {
        var isValid = true;

        if(mBinding.etPhotoUrl.text.toString().trim().isEmpty()){
            mBinding.tilPhotoUrl.error = getString(R.string.helper_required)
            mBinding.etPhotoUrl.requestFocus()
            isValid = false
        }

        if(mBinding.etPhone.text.toString().trim().isEmpty()){
            mBinding.tilPhone.error = getString(R.string.helper_required)
            mBinding.etPhone.requestFocus()
            isValid = false
        }

        if(mBinding.etName.text.toString().trim().isEmpty()){
            mBinding.tilName.error = getString(R.string.helper_required)
            mBinding.etName.requestFocus()
            isValid = false
        }

        return isValid
    }

    //    función encargada de ocultar el teclado
    private fun hideKeyboard() {
        val inm = mActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (view != null) {
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