package com.example.hiringappv1.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.hiringappv1.Inicio
import com.example.hiringappv1.R
//
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"
//
//class
//MenuFragment : Fragment() {
//    private var param1: String? = null
//    private var param2: String? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_menu, container, false)
//    }
//
//    companion object {
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            MenuFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
//}

class MenuFragment : Fragment() {

    private lateinit var layoutBuscar: LinearLayout
    private lateinit var layoutInicio: LinearLayout
    private lateinit var layoutMas: LinearLayout

    private lateinit var iconBuscar: ImageView
    private lateinit var iconInicio: ImageView
    private lateinit var iconMas: ImageView

    private lateinit var textBuscar: TextView
    private lateinit var textInicio: TextView
    private lateinit var textMas: TextView

    private var currentSection: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentSection = arguments?.getString("current_section")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_menu, container, false)

        layoutBuscar = view.findViewById(R.id.layout_buscar)
        layoutInicio = view.findViewById(R.id.layout_inicio)
        layoutMas = view.findViewById(R.id.layout_mas)

        iconBuscar = view.findViewById(R.id.icon_buscar)
        iconInicio = view.findViewById(R.id.icon_inicio)
        iconMas = view.findViewById(R.id.icon_mas)

        textBuscar = view.findViewById(R.id.text_buscar)
        textInicio = view.findViewById(R.id.text_inicio)
        textMas = view.findViewById(R.id.text_mas)

        setMenuHighlight()

        layoutBuscar.setOnClickListener {
//            if (activity !is BuscarActivity) {
//                startActivity(Intent(activity, BuscarActivity::class.java))
//            }
        }

        layoutInicio.setOnClickListener {
            if (activity !is Inicio) {
                startActivity(Intent(activity, Inicio::class.java))
            }
        }

        layoutMas.setOnClickListener {
//            if (activity !is MasActivity) {
//                startActivity(Intent(activity, MasActivity::class.java))
//            }
        }

        return view
    }

    private fun setMenuHighlight() {
        when (currentSection) {
            "buscar" -> highlight(iconBuscar, textBuscar)
            "inicio" -> highlight(iconInicio, textInicio)
            "mas" -> highlight(iconMas, textMas)
        }
    }

    private fun highlight(icon: ImageView, text: TextView) {
        icon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black), android.graphics.PorterDuff.Mode.SRC_IN)
        text.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        icon.setBackgroundResource(R.drawable.menu_highlight_background)
    }

    companion object {
        fun newInstance(currentSection: String) = MenuFragment().apply {
            arguments = Bundle().apply {
                putString("current_section", currentSection)
            }
        }
    }
}
