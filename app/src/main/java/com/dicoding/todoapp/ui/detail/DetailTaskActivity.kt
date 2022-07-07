package com.dicoding.todoapp.ui.detail

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.todoapp.R
import com.dicoding.todoapp.ui.ViewModelFactory
import com.dicoding.todoapp.utils.DateConverter
import com.dicoding.todoapp.utils.TASK_ID

class DetailTaskActivity : AppCompatActivity() {

    private lateinit var detailTaskViewModel: DetailTaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)
        val factory = ViewModelFactory.getInstance(this)
        detailTaskViewModel= ViewModelProvider(this, factory).get(DetailTaskViewModel::class.java)

        //TODO 11 : Show detail task and implement delete action
        populateView()


    }

    private fun populateView(){
        val title: TextView = findViewById(R.id.detail_ed_title)
        val description: TextView = findViewById(R.id.detail_ed_description)
        val date: TextView = findViewById(R.id.detail_ed_due_date)
        val btnDelete: Button = findViewById(R.id.btn_delete_task)

        if (intent.hasExtra(TASK_ID)){
            val id = intent.getIntExtra(TASK_ID, -1)
            detailTaskViewModel.setTaskId(id)
            detailTaskViewModel.task.observe(this){dataTask ->
                if (dataTask != null){
                    title.text = dataTask.title
                    description.text = dataTask.description
                    date.text = DateConverter.convertMillisToString(dataTask.dueDateMillis)

                    btnDelete.setOnClickListener {
                        detailTaskViewModel.deleteTask()
                        finish()
                        Toast.makeText(applicationContext, "Task has been delete", Toast.LENGTH_SHORT).show()

                }
            }

        }



            //startActivity(Intent(this, TaskActivity::class.java))
        }

    }
}