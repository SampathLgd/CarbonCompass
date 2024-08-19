package com.example.carboncompass.ui.activity
import CustomAdapter
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.carboncompass.R
import com.example.carboncompass.databinding.FragmentActivityBinding


class ActivityFragment : Fragment() {

    private var _binding: FragmentActivityBinding? = null
    public var CarbonEmissionMain = 0.0;

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)


        _binding = FragmentActivityBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val userTypeSpinner: Spinner = binding.spinner
        val motSpinner: Spinner = binding.MOTSpinner
        val fuelTypeSpinner: Spinner = binding.fuelType
        val fuelTypeLable: TextView = binding.fuelTypeLable
        val applyChangesBtn: Button = binding.applyChanges
        val kmsPerDay: EditText = binding.kmsTravelled
        val electricityConsumed: EditText = binding.electricityConsumed
        val genderRadio: RadioGroup = binding.gender
        val dietRadio: RadioGroup = binding.diet
        val acCount: EditText = binding.numberofAc
        val refgCount: EditText = binding.numberofRefg


        //user type spinner config.
        val userTypes = listOf("Individual", "Organization")
        val imagesUserTypes = listOf(R.drawable.baseline_person_24, R.drawable.baseline_business_24)
        val customAdapterUserTypes = CustomAdapter(requireActivity(), userTypes, imagesUserTypes)
        userTypeSpinner.adapter = customAdapterUserTypes

        //mode of transport spinner config.
        val transportModes = listOf("Car", "Bike",  "Public Transport","Bicycle", "Walk")
        val images = listOf(R.drawable.baseline_directions_car_24, R.drawable.baseline_directions_bike_24,
            R.drawable.baseline_directions_bus_24, R.drawable.baseline_pedal_bike_24,
            R.drawable.baseline_directions_walk_24)
        val customAdapter = CustomAdapter(requireActivity(), transportModes, images)
        motSpinner.adapter = customAdapter
        //fuel type spinner config.
        ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.fuelType_spinner,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            fuelTypeSpinner.adapter = adapter
        }
        motSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedUserType = parent.getItemAtPosition(position).toString()
                if (selectedUserType=="Car"){
                    fuelTypeLable.visibility = View.VISIBLE
                    fuelTypeSpinner.visibility = View.VISIBLE
                }else{
                    fuelTypeLable.visibility = View.GONE
                    fuelTypeSpinner.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle the case where nothing is selected, if necessary
            }
        }

        //initiate form
        if (sharedPref != null) {
            kmsPerDay.setText(sharedPref.getString("KMS_TRAVELLED", ""))
            electricityConsumed.setText(sharedPref.getString("ELECTRICITY", ""))
            motSpinner.setSelection(sharedPref.getLong("MOT",0).toInt())
            genderRadio.check(sharedPref.getInt("GENDER",-1))
            dietRadio.check(sharedPref.getInt("DIET",-1))
            acCount.setText(sharedPref.getString("AC_COUNT",""))
            refgCount.setText(sharedPref.getString("REFG_COUNT",""))
        }

        applyChangesBtn.setOnClickListener{
            CarbonEmissionMain = 0.0;
            val mot = motSpinner.selectedItem.toString()
            var distance = binding.kmsTravelled.text.toString().toIntOrNull()
            if(distance==null) distance = 0;
            if(mot=="Car"){
                val fuel_type = binding.fuelType.toString()
                calcTravel(mot,distance,fuel_type)
            }else{
                calcTravel(mot,distance)
            }
            val electricity = binding.electricityConsumed.text.toString().toIntOrNull()
            if(electricity!=null) CarbonEmissionMain+=(0.713*electricity)/30

            if(binding.diet.findViewById<RadioButton>(binding.diet.checkedRadioButtonId)!=null && binding.gender.findViewById<RadioButton>(binding.gender.checkedRadioButtonId)!=null){
                val gender = binding.gender.findViewById<RadioButton>(binding.gender.checkedRadioButtonId).text.toString()
                val diet = binding.diet.findViewById<RadioButton>(binding.diet.checkedRadioButtonId).text.toString()
                if(gender=="Male"){
                    if(diet=="Veg") CarbonEmissionMain+=0.723
                    else CarbonEmissionMain+=1.031
                }else{
                    if(diet=="Veg") CarbonEmissionMain+=0.583
                    else CarbonEmissionMain+=0.892
                }
            }
            //put form values in local storage
            with (sharedPref?.edit()) {
                this?.putLong("FINAL_EMISSION", java.lang.Double.doubleToRawLongBits(CarbonEmissionMain))
                this?.putString("ELECTRICITY", electricityConsumed.text.toString())
                this?.putString("KMS_TRAVELLED", kmsPerDay.text.toString())
                Log.d(motSpinner.selectedItemId.toString(),"mot test")
                this?.putLong("MOT", motSpinner.selectedItemId)
                if(mot=="car") this?.putLong("FUEL_TYPE", fuelTypeSpinner.selectedItemId)
                this?.putInt("GENDER", genderRadio.checkedRadioButtonId)
                this?.putInt("DIET", dietRadio.checkedRadioButtonId)
                this?.putString("AC_COUNT", acCount.text.toString())
                this?.putString("REFG_COUNT", refgCount.text.toString())
                this?.apply()
            }
            Log.d(CarbonEmissionMain.toString(),"final Emission")
        }

        return root
    }

    private fun calcTravel(mot: String, distance: Int,fuelType: String="") {
        if(mot=="Car"){
            if(fuelType=="Petrol") CarbonEmissionMain+=(distance/19.0)*2.31f
            else CarbonEmissionMain+=(distance/19.0)*2.68f
        }else if(mot=="Bike"){
            CarbonEmissionMain+=(distance/35.0)*2.31f
        }else if(mot=="Public Transport"){
            CarbonEmissionMain+=((distance/4.2)*2.68f)/75
        }else if(mot=="Bicycle"){
            CarbonEmissionMain+=distance*0.021
        }else if(mot=="Walk"){
            CarbonEmissionMain+=distance*0.058
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}