package com.somaiya.summer_project.Fragments


import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.somaiya.summer_project.R
import com.somaiya.summer_project.utils.IntegerValueFormatter
import com.somaiya.summer_project.utils.PercentageValueFormatter


class HistoryFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var db_timesLateDisplay: TextView
    private val user = Firebase.auth.currentUser
    private lateinit var cd_profile: androidx.cardview.widget.CardView
    private lateinit var cd_dashboard: androidx.cardview.widget.CardView
    private lateinit var cd_home: androidx.cardview.widget.CardView
    private lateinit var cd_logout: androidx.cardview.widget.CardView

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        /*

                db = FirebaseFirestore.getInstance()
              */
        /*  db_timesLateDisplay = view.findViewById(R.id.db_timesLateDisplay)
                cd_profile = view.findViewById(R.id.cd_profile)
                cd_dashboard = view.findViewById(R.id.cd_dashboard)
                cd_home = view.findViewById(R.id.cd_home)
                cd_logout = view.findViewById(R.id.cd_logout)*//*


        //Check if user is logged in or not
        if (user == null) {
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        //Displaying Times Late from firestore to db_timesLateDisplay

        val navHostFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val homeToProfileaction = HistoryFragmentDirections.actionHistoryFragmentToProfileFragment()
        cd_profile.setOnClickListener {
            navController.navigate(homeToProfileaction)
        }

        val homeToDashboardaction = HistoryFragmentDirections.actionHistoryFragmentToHomeFragment()
        cd_dashboard.setOnClickListener {
            navController.navigate(homeToDashboardaction)
        }

        cd_home.setOnClickListener {
            Toast.makeText(requireContext(), "You are already at home", Toast.LENGTH_SHORT).show()
        }

        cd_logout.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }



        db.collection("USERS").document(user?.uid ?: "")
            .collection("reasons")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val count = task.result.size()
                    db_timesLateDisplay.text = count.toString()
                    Log.d("db_timesLateDisplay", "onCreate:"+ db_timesLateDisplay.text )
                } else {
                    Log.w("FirestoreCount", "Error getting documents.", task.exception)
                }
            }


*/

        val barChart = view.findViewById<BarChart>(R.id.barChart)
        val pieChart: PieChart = view.findViewById(R.id.barChart_2)

        val barEntries = ArrayList<BarEntry>()
        //calculate the late attendance
        barEntries.add(BarEntry(0f, 80f)) // Monday
        barEntries.add(BarEntry(1f, 90f)) // Tuesday
        barEntries.add(BarEntry(2f, 70f)) // Wednesday
        barEntries.add(BarEntry(3f, 60f)) // Thursday
        barEntries.add(BarEntry(4f, 75f)) // Friday
        barEntries.add(BarEntry(5f, 85f)) // Saturday
        barEntries.add(BarEntry(6f, 65f)) // Sunday

        val barDataSet = BarDataSet(barEntries,"")
        barDataSet.color = resources.getColor(R.color.primary)
        barDataSet.setDrawValues(false)

        val barData = BarData(barDataSet)
        barChart.data = barData
        barChart.description.isEnabled = false

        // Customizing the chart appearance
        barChart.axisRight.isEnabled = false
        barChart.xAxis.granularity = 1f
        barChart.xAxis.labelRotationAngle = 45f

        barChart.setScaleEnabled(false)  // Disable both zooming and scaling
        barChart.setPinchZoom(false)     // Disable pinch-to-zoom

        // Disable grid lines
        barChart.xAxis.setDrawGridLines(false)
        barChart.axisLeft.setDrawGridLines(false)
        barChart.axisRight.setDrawGridLines(false)
        barChart.legend.isEnabled = false





        val labelColor = resources.getColor(R.color.secondary_graph_label_color)

        barChart.xAxis.textColor = labelColor  // X-axis labels
        barChart.axisLeft.textColor = labelColor  // Left Y-axis labels
        barChart.axisRight.textColor = labelColor  // Right Y-axis labels
        barChart.legend.textColor = labelColor  // Legend

        barChart.xAxis.gridColor = labelColor  // X-axis grid lines
        barChart.axisLeft.gridColor = labelColor  // Left Y-axis grid lines
        barChart.axisRight.gridColor = labelColor  // Right Y-axis grid lines

        // Load custom font
        val typeface = Typeface.createFromAsset(requireContext().assets, "font/lexend_regular.ttf")


        barChart.xAxis.typeface = typeface  // X-axis labels
        barChart.axisLeft.typeface = typeface  // Left Y-axis labels
        barChart.axisRight.typeface = typeface  // Right Y-axis labels
        barChart.legend.typeface = typeface  // Legend

        barChart.axisLeft.valueFormatter = PercentageValueFormatter()
        barChart.axisRight.valueFormatter = PercentageValueFormatter()


        val days = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(days)
        barData.setValueFormatter(IntegerValueFormatter())


        barChart.animateY(1000)




        //Pie chart
        // Create some data entries
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(40f, "On Time"))
        entries.add(PieEntry(60f, "Late"))


        // Create a DataSet
        val dataSet = PieDataSet(entries, "Categories")
        dataSet.colors = listOf(resources.getColor(R.color.primary_variant_1), resources.getColor(R.color.primary_variant_2)) // Set colors


        // Create a PieData object and set it to the chart
        val data = PieData(dataSet)
        data.setValueFormatter(PercentageValueFormatter())
        data.setValueTextSize(14f)
        data.setValueTypeface(typeface)
        pieChart.data = data


        // Customize the chart
        pieChart.setUsePercentValues(true)
        pieChart.legend.isEnabled = false
        pieChart.description.isEnabled = false
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.WHITE)
        pieChart.setCenterTextColor(resources.getColor(R.color.secondary_graph_label_color))
        pieChart.setEntryLabelTypeface(typeface)
        pieChart.setCenterTextTypeface(typeface)
        pieChart.setEntryLabelColor(resources.getColor(R.color.primary))
        pieChart.setCenterTextSize(14f)
        pieChart.setEntryLabelTextSize(14f)
        pieChart.centerText = "Overall"
        pieChart.animateY(1000)




        return view
    }

}