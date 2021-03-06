package com.m3o.m3omobile.fragments.services

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.m3o.m3omobile.R
import com.m3o.m3omobile.databinding.FragmentServicesBinding

class ServicesFragment : Fragment() {

    private var _binding: FragmentServicesBinding? = null
    private lateinit var myContext: Context

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentServicesBinding.inflate(inflater, container, false)
        myContext = requireContext()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val list = mutableListOf<ServicesViewModel>()
        list.add(ServicesViewModel("Avatar"))
        list.add(ServicesViewModel("Bitcoin"))
        list.add(ServicesViewModel("GIFs"))
        list.add(ServicesViewModel("IP Geolocation"))
        list.add(ServicesViewModel("Jokes"))
        list.add(ServicesViewModel("Passwords"))
        list.add(ServicesViewModel("URLs"))

        binding.recycler.apply {
            layoutManager = GridLayoutManager(myContext, 3)
            adapter = ServicesAdapter(myContext, list) {
                openService(it)
            }
        }
    }

    private fun openService(service: String) {
        when (service) {
            "avatar" -> findNavController().navigate(R.id.AvatarFragment)
            "bitcoin" -> findNavController().navigate(R.id.BitcoinFragment)
            "gifs" -> findNavController().navigate(R.id.GIFsFragment)
            "ip_geolocation" -> findNavController().navigate(R.id.IP)
            "jokes" -> findNavController().navigate(R.id.JokesFragment)
            "passwords" -> findNavController().navigate(R.id.PasswordsFragment)
            "urls" -> findNavController().navigate(R.id.URLsFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
