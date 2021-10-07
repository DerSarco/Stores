package com.sarco.stores

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sarco.stores.databinding.FragmentEditStoreBinding

class EditStoreFragment : Fragment() {

//    la variable binding se inicializará una vez el fragmento sea llamado.
    private lateinit var mBinding: FragmentEditStoreBinding

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


}