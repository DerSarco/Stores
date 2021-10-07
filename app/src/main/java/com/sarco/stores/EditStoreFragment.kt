package com.sarco.stores

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.sarco.stores.databinding.FragmentEditStoreBinding
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class EditStoreFragment : Fragment() {

//    la variable binding se inicializará una vez el fragmento sea llamado.
    private lateinit var mBinding: FragmentEditStoreBinding
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

        mActivity = activity as? MainActivity
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mActivity?.supportActionBar?.title = getString(R.string.edit_store_title_add)

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when(item.itemId){
            android.R.id.home -> {
                mActivity?.onBackPressed()
                true
            }
            R.id.action_save -> {
                val store = StoreEntity(name = mBinding.etName.text.toString().trim(),
                    phone = mBinding.etPhone.text.toString().trim(),
                    website = mBinding.etWebsite.toString().trim())

                doAsync {
                    StoreApplication.database.storeDao().addStore(store)
                    uiThread {
                        mActivity?.addStore(store)
                        hideKeyboard()
                        Snackbar.make(mBinding.root, R.string.edit_store_message_success,
                            Snackbar.LENGTH_SHORT)
                            .show()
                        mActivity?.onBackPressed()
                    }
                }
                true
            }else  -> return super.onOptionsItemSelected(item)

        }
    }

    private fun hideKeyboard(){
        val inm = mActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if(view != null){
            inm.hideSoftInputFromWindow(requireView().windowToken, 0)
        }
    }

    override fun onDestroyView() {
        hideKeyboard()
        super.onDestroyView()
    }

    override fun onDestroy() {
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mActivity?.supportActionBar?.title = getString(R.string.app_name)
        mActivity?.hideFab(true)
        setHasOptionsMenu(false)
        super.onDestroy()

    }

}