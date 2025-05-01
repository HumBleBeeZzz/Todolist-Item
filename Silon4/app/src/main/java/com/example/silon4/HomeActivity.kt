package com.example.silon4

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // หาปุ่มใน layout
        val btnTakeNotes: Button = findViewById(R.id.btnTakeNotes)
        val btnLogout: Button = findViewById(R.id.btnLogout)

        // กดปุ่ม "จดโน้ตกัน" ไปยัง NoteActivity
        btnTakeNotes.setOnClickListener {
            val intent = Intent(this, NoteActivity::class.java)
            startActivity(intent)
        }

        // กดปุ่ม "Logout" กลับไปยัง LoginActivity
        btnLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // ปิด HomeActivity เพื่อไม่ให้กลับมา
        }
    }
}
