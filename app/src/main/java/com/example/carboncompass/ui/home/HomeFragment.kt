package com.example.carboncompass.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.carboncompass.databinding.FragmentHomeBinding
import kotlin.random.Random

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Access the ProgressBars using the binding object
        val progressBarToday: ProgressBar = binding.progressBarToday
        val progressBarYesterday: ProgressBar = binding.progressBar2Yesterday
        val updateBtn:Button=binding.update
        val enterGoal:EditText=binding.todayGoal
        //
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        enterGoal.setText((sharedPref?.let { (it.getString("GOAL_VALUE", "")) }))
        updateBtn.setOnClickListener(){
            if (sharedPref != null) {
                with (sharedPref.edit()) {
                    putString("GOAL_VALUE",enterGoal.text.toString())
                    apply()
                }
            }
        }
        // Generate random values between 0 and 100
        val randomProgressToday = Random.nextInt(0, 101)
        val randomProgressYesterday = Random.nextInt(0, 101)

        // Set the progress value to each ProgressBar
        progressBarToday.progress = randomProgressToday
        progressBarYesterday.progress = randomProgressYesterday

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
