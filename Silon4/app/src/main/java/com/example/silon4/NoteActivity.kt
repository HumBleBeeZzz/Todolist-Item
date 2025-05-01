package com.example.silon4

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class NoteActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAddNote: Button
    private lateinit var btnBackToHome: Button
    private lateinit var btnPickImage: Button
    private lateinit var previewImage: ImageView
    private lateinit var titleInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var createdDateInput: EditText
    private lateinit var dueDateInput: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var spinnerStatus: Spinner

    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private var selectedImageUri: String? = null
    private val PICK_IMAGE_REQUEST = 1001

    private var todoList = mutableListOf<TodoItem>()
    private lateinit var adapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        recyclerView = findViewById(R.id.recyclerViewNotes)
        btnAddNote = findViewById(R.id.btnAddNote)
        btnBackToHome = findViewById(R.id.btnBackToHome)
        btnPickImage = findViewById(R.id.btnPickImage)
        previewImage = findViewById(R.id.previewImage)
        titleInput = findViewById(R.id.editTextTitle)
        descriptionInput = findViewById(R.id.editTextDescription)
        createdDateInput = findViewById(R.id.editTextCreatedDate)
        dueDateInput = findViewById(R.id.editTextDueDate)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        spinnerStatus = findViewById(R.id.spinnerStatus)

        val categories = arrayOf("ส่วนตัว", "เรียน", "ร้านค้า", "อื่น ๆ")
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = categoryAdapter

        val statuses = arrayOf("ยังไม่เริ่ม", "กำลังทำ", "เสร็จแล้ว")
        val statusAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statuses)
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatus.adapter = statusAdapter

        sharedPreferences = getSharedPreferences("TodoPrefs", Context.MODE_PRIVATE)

        createdDateInput.setOnClickListener {
            showDatePicker { date -> createdDateInput.setText(date) }
        }

        dueDateInput.setOnClickListener {
            showDatePicker { date -> dueDateInput.setText(date) }
        }

        btnPickImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        loadNotes()

        adapter = NoteAdapter(todoList) { position ->
            showEditDialog(position, categories, statuses, categoryAdapter, statusAdapter)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        btnAddNote.setOnClickListener {
            val title = titleInput.text.toString()
            val description = descriptionInput.text.toString()
            val createdDate = createdDateInput.text.toString()
            val dueDate = dueDateInput.text.toString()
            val category = spinnerCategory.selectedItem.toString()
            val status = spinnerStatus.selectedItem.toString()

            if (title.isNotEmpty() && createdDate.isNotEmpty() && dueDate.isNotEmpty()) {
                val item = TodoItem(title, description, createdDate, dueDate, category, status, selectedImageUri)
                todoList.add(item)
                saveNotes()
                adapter.notifyItemInserted(todoList.size - 1)
                clearInputs()
                Toast.makeText(this, "เพิ่มงานแล้ว", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "กรุณากรอกข้อมูลให้ครบ", Toast.LENGTH_SHORT).show()
            }
        }

        btnBackToHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            val uri = data?.data
            if (uri != null) {
                selectedImageUri = uri.toString()
                previewImage.setImageURI(uri)
            }
        }
    }

    private fun showEditDialog(position: Int, categories: Array<String>, statuses: Array<String>, categoryAdapter: ArrayAdapter<String>, statusAdapter: ArrayAdapter<String>) {
        val selectedItem = todoList[position]
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_note, null)
        val titleEdit = dialogView.findViewById<EditText>(R.id.editTitle)
        val descEdit = dialogView.findViewById<EditText>(R.id.editDescription)
        val createdDateEdit = dialogView.findViewById<EditText>(R.id.editCreatedDate)
        val dueDateEdit = dialogView.findViewById<EditText>(R.id.editDueDate)
        val categoryEdit = dialogView.findViewById<Spinner>(R.id.spinnerEditCategory)
        val statusEdit = dialogView.findViewById<Spinner>(R.id.spinnerEditStatus)

        titleEdit.setText(selectedItem.title)
        descEdit.setText(selectedItem.description)
        createdDateEdit.setText(selectedItem.createdDate)
        dueDateEdit.setText(selectedItem.dueDate)

        categoryEdit.adapter = categoryAdapter
        statusEdit.adapter = statusAdapter
        categoryEdit.setSelection(categories.indexOf(selectedItem.category))
        statusEdit.setSelection(statuses.indexOf(selectedItem.status))

        createdDateEdit.setOnClickListener {
            showDatePicker { date -> createdDateEdit.setText(date) }
        }

        dueDateEdit.setOnClickListener {
            showDatePicker { date -> dueDateEdit.setText(date) }
        }

        AlertDialog.Builder(this)
            .setTitle("แก้ไขงาน")
            .setView(dialogView)
            .setPositiveButton("บันทึก") { _, _ ->
                val updatedItem = TodoItem(
                    titleEdit.text.toString(),
                    descEdit.text.toString(),
                    createdDateEdit.text.toString(),
                    dueDateEdit.text.toString(),
                    categoryEdit.selectedItem.toString(),
                    statusEdit.selectedItem.toString(),
                    selectedItem.imageUri
                )
                todoList[position] = updatedItem
                saveNotes()
                adapter.notifyItemChanged(position)
            }
            .setNegativeButton("ยกเลิก", null)
            .setNeutralButton("ลบ") { _, _ ->
                todoList.removeAt(position)
                saveNotes()
                adapter.notifyItemRemoved(position)
            }
            .show()
    }

    private fun saveNotes() {
        val editor = sharedPreferences.edit()
        val json = gson.toJson(todoList)
        editor.putString("todo_list", json)
        editor.apply()
    }

    private fun loadNotes() {
        val json = sharedPreferences.getString("todo_list", null)
        if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<MutableList<TodoItem>>() {}.type
            todoList = gson.fromJson(json, type)
        }
    }

    private fun clearInputs() {
        titleInput.text.clear()
        descriptionInput.text.clear()
        createdDateInput.text.clear()
        dueDateInput.text.clear()
        spinnerCategory.setSelection(0)
        spinnerStatus.setSelection(0)
        previewImage.setImageDrawable(null)
        selectedImageUri = null
    }

    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
            onDateSelected(formattedDate)
        }, year, month, day)

        datePicker.show()
    }
}

data class TodoItem(
    val title: String,
    val description: String,
    val createdDate: String,
    val dueDate: String,
    val category: String,
    val status: String,
    val imageUri: String? = null
)
