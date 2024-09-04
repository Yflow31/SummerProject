package com.somaiya.summer_project.Fragments


import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
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
import com.somaiya.summer_project.ApplyForForm
import com.somaiya.summer_project.MainActivity
import com.somaiya.summer_project.MiniRecycler.LeaderboardRecyclerAdapter
import com.somaiya.summer_project.MiniRecycler.MiniRecyclerAdapter
import com.somaiya.summer_project.MiniRecycler.MiniRecyclerData
import com.somaiya.summer_project.R
import com.somaiya.summer_project.applyform.Model.ApplyFormData
import com.somaiya.summer_project.utils.ApprovalConstant
import com.somaiya.summer_project.utils.CalculateDayOfWeek
import com.somaiya.summer_project.utils.IntegerValueFormatter
import com.somaiya.summer_project.utils.PercentageValueFormatter
import com.somaiya.summer_project.utils.ROLE
import com.somaiya.summer_project.utils.TipConstant
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.min


class HistoryFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private val user = Firebase.auth.currentUser


    private lateinit var progress_text: TextView
    private lateinit var tips_text: TextView
    private lateinit var new_late_request: TextView
    private lateinit var leaderboard_txt: TextView
    private lateinit var times_late_text: TextView


    private lateinit var miniRecycler: RecyclerView
    private lateinit var adapter: MiniRecyclerAdapter
    private lateinit var leaderboardAdapter: LeaderboardRecyclerAdapter
    private lateinit var leaderboard_RecyclerView: RecyclerView

    private lateinit var dataList: MutableList<ApplyFormData>
    private lateinit var dataList1: MutableList<ApplyFormData>

    private lateinit var graph_layout_student: RelativeLayout
    private lateinit var piechart_for_student: RelativeLayout

    private lateinit var graph_layout_teacher: RelativeLayout
    private lateinit var piechart_for_teacher: RelativeLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        db = FirebaseFirestore.getInstance()

        miniRecycler = view.findViewById(R.id.late_request_RecyclerView)
        leaderboard_RecyclerView = view.findViewById(R.id.leaderboard_RecyclerView)

        dataList = mutableListOf()
        dataList1 = mutableListOf()
        adapter = MiniRecyclerAdapter(dataList)
        leaderboardAdapter = LeaderboardRecyclerAdapter(dataList1)

        miniRecycler.adapter = adapter
        leaderboard_RecyclerView.adapter = leaderboardAdapter

        //TextView
        progress_text = view.findViewById(R.id.progress_text)
        tips_text = view.findViewById(R.id.tips_text)
        new_late_request = view.findViewById(R.id.new_late_request)
        leaderboard_txt = view.findViewById(R.id.leaderboard_txt)
        times_late_text = view.findViewById(R.id.times_late_text)

        //Layouts
        graph_layout_student = view.findViewById(R.id.graph_layout_student)
        graph_layout_teacher = view.findViewById(R.id.graph_layout_teacher)
        piechart_for_student = view.findViewById(R.id.piechart_for_student)
        piechart_for_teacher = view.findViewById(R.id.piechart_for_teacher)

        //Check if user is logged in or not

        if (user == null) {
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        val todayDate = getTodayDate()
        Log.d("todayDate", "onCreateView: ${todayDate.toString()} ")
        //Data to show progress in Chart for students
        if (user != null) {
            val user_ref = db.collection("USERS").document(user.uid)
            user_ref.get().addOnSuccessListener {
                val role = it.getString("role")
                if (role != null) {
                    when (role) {
                        "admin" -> {
                            db.collection("ReasonsForAdmin")
                                .whereEqualTo("currentdate", todayDate)
                                .get()
                                .addOnSuccessListener { result ->
                                    val count = result.size()
                                    progress_text.text = "${(count)}"
                                    times_late_text.text = "Late Entries Today"
                                }
                                .addOnFailureListener { exception ->
                                    // Handle any errors
                                    Log.e("FirebaseError", "Error getting documents: ", exception)
                                }
                        }

                        "student" -> {
                            user_ref.collection("reasons").get().addOnSuccessListener {
                                progress_text.text = it.size().toString() + "/7"
                            }
                        }
                    }

                }
            }
        }


            // Fetching tips for users

            val tipsList = mutableListOf<String>()

            db.collection("TIPS")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val tip = document.getString(TipConstant.text.name)
                        if (tip != null) {
                            tipsList.add(tip)
                        }
                    }

                    if (tipsList.isNotEmpty()) {
                        // Select a random tip from the list
                        val randomTip = tipsList.random()
                        // Display the random tip in your app
                        tips_text.text =
                            randomTip // Assuming you have a TextView to display the tip
                    } else {
                        // Handle case where no tips are available
                        tips_text.text = "No tips available"
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("Firestore", "Error getting documents: ", exception)
                    tips_text.text = "Failed to load tips"
                }


            //Show Visiblity of graphs for teachers and students

            if (user != null) {
                val userDocRef = db.collection("USERS").document(user.uid)
                userDocRef.get().addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val role = documentSnapshot.getString("role")
                        if (role != null) {
                            when (role) {
                                "admin" -> {
                                    graph_layout_teacher.visibility = View.VISIBLE
                                    piechart_for_teacher.visibility = View.VISIBLE
                                    leaderboard_RecyclerView.visibility = View.VISIBLE
                                    leaderboard_txt.visibility = View.VISIBLE
                                }

                                "student" -> {
                                    graph_layout_student.visibility = View.VISIBLE
                                    piechart_for_student.visibility = View.VISIBLE
                                    miniRecycler.visibility = View.VISIBLE
                                }
                            }
                        }
                    }
                }
            }

            //Fetching date for teachers

            val barChartTeacher = view.findViewById<BarChart>(R.id.barChart_for_teacher)
            val pieChartForTeacher = view.findViewById<PieChart>(R.id.barChart_for_teacher_2)

            val initialCountForTeacher = 100f
            val dayCountsForTeachhers = mutableMapOf<Int, Float>()
            for (i in 0..6) {
                dayCountsForTeachhers[i] = initialCountForTeacher
            }
            db.collection("ReasonsForAdmin").get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val currentDate = document.getString("currentdate")
                        if (currentDate != null) {
                            val dayCalculator = CalculateDayOfWeek()
                            val dayOfWeek = dayCalculator.calculateDayOfWeek(currentDate)

                            // Decrease the count by 1 for each late attendance
                            dayCountsForTeachhers[dayOfWeek] = dayCountsForTeachhers.getOrDefault(
                                dayOfWeek,
                                initialCountForTeacher
                            ) - 1
                        }
                    }

                    Log.d("DayCounts", dayCountsForTeachhers.toString())
                    val barEntries = ArrayList<BarEntry>()

                    // Populate the bar entries with the updated counts
                    for (i in 0..6) {
                        val count = dayCountsForTeachhers[i] ?: initialCountForTeacher
                        barEntries.add(BarEntry(i.toFloat(), count))
                    }

                    Log.d("BarEntries", barEntries.toString())

                    val barDataSet = BarDataSet(barEntries, "")
                    barDataSet.color = resources.getColor(R.color.primary)
                    barDataSet.setDrawValues(false)

                    val barData = BarData(barDataSet)
                    barChartTeacher.data = barData
                    barChartTeacher.description.isEnabled = false
                    barChartTeacher.invalidate() // Refresh the chart
                }
                .addOnFailureListener { exception ->
                    Log.w("Firestore", "Error getting documents: ", exception)
                }

            val barEntriesForTeacher = ArrayList<BarEntry>()
            val barDataSetForTeacher = BarDataSet(barEntriesForTeacher, "")
            val barDataForTeacher = BarData(barDataSetForTeacher)

            // Customizing the chart appearance
            barChartTeacher.axisRight.isEnabled = false
            barChartTeacher.xAxis.granularity = 1f
            barChartTeacher.xAxis.labelRotationAngle = 45f

            barChartTeacher.setScaleEnabled(false)  // Disable both zooming and scaling
            barChartTeacher.setPinchZoom(false)     // Disable pinch-to-zoom

            // Disable grid lines
            barChartTeacher.xAxis.setDrawGridLines(false)
            barChartTeacher.axisLeft.setDrawGridLines(false)
            barChartTeacher.axisRight.setDrawGridLines(false)
            barChartTeacher.legend.isEnabled = false


            val labelColorForTeacher = resources.getColor(R.color.secondary_graph_label_color)

            barChartTeacher.xAxis.textColor = labelColorForTeacher  // X-axis labels
            barChartTeacher.axisLeft.textColor = labelColorForTeacher  // Left Y-axis labels
            barChartTeacher.axisRight.textColor = labelColorForTeacher  // Right Y-axis labels
            barChartTeacher.legend.textColor = labelColorForTeacher  // Legend

            barChartTeacher.xAxis.gridColor = labelColorForTeacher  // X-axis grid lines
            barChartTeacher.axisLeft.gridColor = labelColorForTeacher  // Left Y-axis grid lines
            barChartTeacher.axisRight.gridColor = labelColorForTeacher  // Right Y-axis grid lines

            // Load custom font
            val typefaceForTeacher =
                Typeface.createFromAsset(requireContext().assets, "font/lexend_regular.ttf")


            barChartTeacher.xAxis.typeface = typefaceForTeacher  // X-axis labels
            barChartTeacher.axisLeft.typeface = typefaceForTeacher  // Left Y-axis labels
            barChartTeacher.axisRight.typeface = typefaceForTeacher  // Right Y-axis labels
            barChartTeacher.legend.typeface = typefaceForTeacher  // Legend

            barChartTeacher.axisLeft.valueFormatter = PercentageValueFormatter()
            barChartTeacher.axisRight.valueFormatter = PercentageValueFormatter()

            val daysForTeacher = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            barChartTeacher.xAxis.valueFormatter = IndexAxisValueFormatter(daysForTeacher)
            barDataForTeacher.setValueFormatter(IntegerValueFormatter())

            barChartTeacher.animateY(1000)


            //Fetching date for students

            val barChart = view.findViewById<BarChart>(R.id.barChart)
            val pieChart: PieChart = view.findViewById(R.id.barChart_2)

            val initialCount = 100f
            val dayCounts = mutableMapOf<Int, Float>()

            // Initialize each day with 100 for students
            for (i in 0..6) {
                dayCounts[i] = initialCount
            }

            db.collection("USERS").document(user?.uid ?: "")
                .collection("reasons")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val currentDate = document.getString("currentdate")
                        if (currentDate != null) {
                            val dayCalculator = CalculateDayOfWeek()
                            val dayOfWeek = dayCalculator.calculateDayOfWeek(currentDate)

                            // Decrease the count by 1 for each late attendance
                            dayCounts[dayOfWeek] =
                                dayCounts.getOrDefault(dayOfWeek, initialCount) - 1
                        }
                    }

                    Log.d("DayCounts", dayCounts.toString())
                    val barEntries = ArrayList<BarEntry>()

                    // Populate the bar entries with the updated counts
                    for (i in 0..6) {
                        val count = dayCounts[i] ?: initialCount
                        barEntries.add(BarEntry(i.toFloat(), count))
                    }

                    Log.d("BarEntries", barEntries.toString())

                    val barDataSet = BarDataSet(barEntries, "")
                    barDataSet.color = resources.getColor(R.color.primary)
                    barDataSet.setDrawValues(false)

                    val barData = BarData(barDataSet)
                    barChart.data = barData
                    barChart.description.isEnabled = false
                    barChart.invalidate() // Refresh the chart
                }
                .addOnFailureListener { exception ->
                    Log.w("Firestore", "Error getting documents: ", exception)
                }


            val barEntries = ArrayList<BarEntry>()
            val barDataSet = BarDataSet(barEntries, "")
            val barData = BarData(barDataSet)
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
            val typeface =
                Typeface.createFromAsset(requireContext().assets, "font/lexend_regular.ttf")


            barChart.xAxis.typeface = typeface  // X-axis labels
            barChart.axisLeft.typeface = typeface  // Left Y-axis labels
            barChart.axisRight.typeface = typeface  // Right Y-axis labels
            barChart.legend.typeface = typeface  // Legend

            barChart.axisLeft.valueFormatter = PercentageValueFormatter()
            barChart.axisRight.valueFormatter = PercentageValueFormatter()


            val daysForStudent = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            barChart.xAxis.valueFormatter = IndexAxisValueFormatter(daysForStudent)
            barData.setValueFormatter(IntegerValueFormatter())



            barChart.animateY(1000)


            //Check how many times user has been late

            var accepted = 0
            var rejected = 0
            var total = 0

            db.collection("USERS").document(user?.uid ?: "")
                .collection("reasons").get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        total = task.result.size()
                        for (document in task.result) {
                            val approvalStatus = document.getString("approvalStatus")
                            if (approvalStatus == ApprovalConstant.ACCEPTED.name) {
                                accepted++
                            } else if (approvalStatus == ApprovalConstant.REJECTED.name) {
                                rejected++
                            }
                        }
                        //Pie chart
                        // Create some data entries
                        val entries = ArrayList<PieEntry>()
                        entries.add(PieEntry(accepted.toFloat(), "On Time"))
                        entries.add(PieEntry(rejected.toFloat(), "Late"))


                        // Create a DataSet
                        val dataSet = PieDataSet(entries, "Categories")
                        dataSet.colors = listOf(
                            resources.getColor(R.color.primary_variant_1),
                            resources.getColor(R.color.primary_variant_2)
                        ) // Set colors


                        // Create a PieData object and set it to the chart
                        val data = PieData(dataSet)
                        data.setValueFormatter(PercentageValueFormatter())
                        data.setValueTextSize(14f)
                        data.setValueTypeface(typeface)
                        pieChart.data = data

                        Log.d(
                            "Total Check",
                            "Status count: Accepted: $accepted, Rejected: $rejected, Total: $total "
                        )
                    } else {
                        Log.w("FirestoreCount", "Error getting documents.", task.exception)
                    }
                }


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

            //PieChart For Teachers

            var acceptedForTeacher = 0
            var rejectedForTeacher = 0
            var totalForTeacher = 0

            db.collection("ReasonsForAdmin").get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    total = task.result.size()
                    for (document in task.result) {
                        val approvalStatus = document.getString("approvalStatus")
                        if (approvalStatus == ApprovalConstant.ACCEPTED.name) {
                            acceptedForTeacher++
                        } else if (approvalStatus == ApprovalConstant.REJECTED.name) {
                            rejectedForTeacher++
                        }
                    }
                    //Pie chart
                    // Create some data entries
                    val entriesForTeacher = ArrayList<PieEntry>()
                    entriesForTeacher.add(PieEntry(acceptedForTeacher.toFloat(), "On Time"))
                    entriesForTeacher.add(PieEntry(rejectedForTeacher.toFloat(), "Late"))


                    // Create a DataSet
                    val dataSetForTeachers = PieDataSet(entriesForTeacher, "Categories")
                    dataSetForTeachers.colors = listOf(
                        resources.getColor(R.color.primary_variant_1),
                        resources.getColor(R.color.primary_variant_2)
                    ) // Set colors


                    // Create a PieData object and set it to the chart
                    val data = PieData(dataSetForTeachers)
                    data.setValueFormatter(PercentageValueFormatter())
                    data.setValueTextSize(14f)
                    data.setValueTypeface(typefaceForTeacher)
                    pieChartForTeacher.data = data

                    Log.d(
                        "Total Check for teachers Pie Chart ",
                        "Status count: Accepted: $acceptedForTeacher, Rejected: $rejectedForTeacher, Total: $totalForTeacher "
                    )
                } else {
                    Log.w(
                        "FirestoreCount for Pie Chart Teachers",
                        "Error getting documents.",
                        task.exception
                    )
                }
            }

            // Customize the chart
            pieChartForTeacher.setUsePercentValues(true)
            pieChartForTeacher.legend.isEnabled = false
            pieChartForTeacher.description.isEnabled = false
            pieChartForTeacher.isDrawHoleEnabled = true
            pieChartForTeacher.setHoleColor(Color.WHITE)
            pieChartForTeacher.setCenterTextColor(resources.getColor(R.color.secondary_graph_label_color))
            pieChartForTeacher.setEntryLabelTypeface(typefaceForTeacher)
            pieChartForTeacher.setCenterTextTypeface(typefaceForTeacher)
            pieChartForTeacher.setEntryLabelColor(resources.getColor(R.color.primary))
            pieChartForTeacher.setCenterTextSize(14f)
            pieChartForTeacher.setEntryLabelTextSize(14f)
            pieChartForTeacher.centerText = "Overall"
            pieChartForTeacher.animateY(1000)


            //Recycler

            if (user != null) {
                val userDocRef = db.collection("USERS").document(user.uid)
                userDocRef.get().addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val role = documentSnapshot.getString("role")
                        if (role != null) {
                            when (role) {
                                "admin" -> {
                                    db.collection("ReasonsForAdmin").get()
                                        .addOnSuccessListener { querySnapshot ->
                                            for (document in querySnapshot.documents) {


                                                val userEmail = document.getString("email")
                                                val location = document.getString("location")
                                                val reasonForBeingLate =
                                                    document.getString("reasonForBeingLate")
                                                val timesLate = document.getString("timesLate")
                                                val reasonId = document.getString("reasonId")
                                                val approvalStatus =
                                                    document.getString("approvalStatus")
                                                val date = document.getString("currentdate")
                                                val time = document.getString("currenttime")
                                                val subject = document.getString("subject")
                                                val faculty = document.getString("faculty")
                                                val selectedTimeSlot =
                                                    document.getString("selectedTimeSlot")


                                                if (location != null && reasonForBeingLate != null && timesLate != null && userEmail != null) {
                                                    val formData = ApplyFormData(
                                                        reasonForBeingLate = reasonForBeingLate, // Use named parameters for clarity
                                                        location = location,
                                                        timesLate = timesLate,
                                                        email = userEmail,
                                                        reasonId = reasonId ?: "",
                                                        approvalStatus = approvalStatus
                                                            ?: ApprovalConstant.PENDING.name,
                                                        role = role,
                                                        currentdate = date ?: "",
                                                        currenttime = time ?: "",
                                                        subject = subject ?: "",
                                                        faculty = faculty ?: "",
                                                        selectedTimeSlot = selectedTimeSlot ?: ""
                                                    )
                                                    dataList.add(formData)
                                                    dataList1.add(formData)
                                                }
                                            }
                                            val leaderboardMap = mutableMapOf<String, ApplyFormData>()

                                            for (item in dataList1) {
                                                val email = item.email
                                                val currentTimesLate = item.timesLate.toInt()

                                                val existingItem = leaderboardMap[email]

                                                // If there's no existing item or the current one has a higher timesLate, update the map
                                                if (existingItem == null || currentTimesLate > existingItem.timesLate.toInt()) {
                                                    leaderboardMap[email] = item
                                                }
                                            }

                                            val leaderboardList = leaderboardMap.values.toList()

                                            val sortedLeaderboardList = leaderboardList.sortedByDescending { it.timesLate.toInt() }

                                            dataList1.clear()
                                            dataList1.addAll(sortedLeaderboardList)
                                            leaderboardAdapter.notifyDataSetChanged()

                                            val sortedList =
                                                dataList.sortedWith(compareBy<ApplyFormData> {
                                                    when (it.approvalStatus) {
                                                        ApprovalConstant.PENDING.name -> 1
                                                        ApprovalConstant.ACCEPTED.name -> 2
                                                        ApprovalConstant.REJECTED.name -> 3
                                                        else -> 4 // Default for unexpected values
                                                    }
                                                }.thenBy { it.email }
                                                    .thenByDescending { it.timesLate.toInt() }
                                                    .thenByDescending {
                                                        // Assuming `currentdate` is a String field in your ApplyFormData class
                                                        SimpleDateFormat(
                                                            "dd-MM-yyyy",
                                                            Locale.getDefault()
                                                        ).parse(it.currentdate)
                                                    }.thenByDescending {
                                                        SimpleDateFormat(
                                                            "mm:HH",
                                                            Locale.getDefault()
                                                        ).parse(it.currenttime)
                                                    }
                                                )

                                            dataList.clear()
                                            dataList.addAll(sortedList)
                                            adapter.notifyDataSetChanged()
                                        }
                                }

                                "student" -> {
                                    db.collection("USERS").document(user.uid)
                                        .collection("reasons").get()
                                        .addOnSuccessListener { querySnapshot ->
                                            for (document in querySnapshot.documents) {
                                                val userEmail = document.getString("email")
                                                val location = document.getString("location")
                                                val reasonForBeingLate =
                                                    document.getString("reasonForBeingLate")
                                                val timesLate = document.getString("timesLate")
                                                val reasonId = document.getString("reasonId")
                                                val approvalStatus =
                                                    document.getString("approvalStatus")
                                                val date = document.getString("currentdate")
                                                val time = document.getString("currenttime")
                                                val subject = document.getString("subject")
                                                val faculty = document.getString("faculty")
                                                val selectedTimeSlot =
                                                    document.getString("selectedTimeSlot")


                                                if (location != null && reasonForBeingLate != null && timesLate != null && userEmail != null) {
                                                    val formData = ApplyFormData(
                                                        reasonForBeingLate = reasonForBeingLate, // Use named parameters for clarity
                                                        location = location,
                                                        timesLate = timesLate,
                                                        email = userEmail,
                                                        reasonId = reasonId ?: "",
                                                        approvalStatus = approvalStatus
                                                            ?: ApprovalConstant.PENDING.name,
                                                        role = role,
                                                        currentdate = date ?: "",
                                                        currenttime = time ?: "",
                                                        subject = subject ?: "",
                                                        faculty = faculty ?: "",
                                                        selectedTimeSlot = selectedTimeSlot ?: ""
                                                    )
                                                    dataList.add(formData)
                                                }
                                            }

                                            val leaderboardMap = mutableMapOf<String, ApplyFormData>()

                                            for (item in dataList1) {
                                                val email = item.email
                                                val currentTimesLate = item.timesLate.toInt()

                                                val existingItem = leaderboardMap[email]

                                                // If there's no existing item or the current one has a higher timesLate, update the map
                                                if (existingItem == null || currentTimesLate > existingItem.timesLate.toInt()) {
                                                    leaderboardMap[email] = item
                                                }
                                            }

                                            val leaderboardList = leaderboardMap.values.toList()

                                            val sortedLeaderboardList = leaderboardList.sortedByDescending { it.timesLate.toInt() }

                                            dataList1.clear()
                                            dataList1.addAll(sortedLeaderboardList)
                                            leaderboardAdapter.notifyDataSetChanged()


                                            val sortedList =
                                                dataList.sortedWith(compareBy<ApplyFormData> {
                                                    when (it.approvalStatus) {
                                                        ApprovalConstant.PENDING.name -> 1
                                                        ApprovalConstant.ACCEPTED.name -> 2
                                                        ApprovalConstant.REJECTED.name -> 3
                                                        else -> 4 // Default for unexpected values
                                                    }
                                                }.thenBy { it.email }
                                                    .thenByDescending { it.timesLate.toInt() }
                                                    .thenByDescending {
                                                        // Assuming `currentdate` is a String field in your ApplyFormData class
                                                        SimpleDateFormat(
                                                            "dd-MM-yyyy",
                                                            Locale.getDefault()
                                                        ).parse(it.currentdate)
                                                    }.thenByDescending {
                                                        SimpleDateFormat(
                                                            "mm:HH",
                                                            Locale.getDefault()
                                                        ).parse(it.currenttime)
                                                    }
                                                )

                                            dataList.clear()
                                            dataList.addAll(sortedList)
                                            adapter.notifyDataSetChanged()
                                        }
                                }
                            }
                        } else {
                            Log.d("UserRole", "Role field is missing")
                        }
                    } else {
                        Log.d("UserDoc", "User document does not exist")
                    }
                }.addOnFailureListener { e ->
                    Log.e("FirebaseError", "Error fetching user document", e)
                }
            }

            new_late_request.setOnClickListener {
                val intent = Intent(activity, ApplyForForm::class.java)
                startActivity(intent)
            }


            return view
        }

    private fun getTodayDate(): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()) // Use the same date format as in Firebase
        return sdf.format(Date())
    }

}