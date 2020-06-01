package com.lijinjiliangcha.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_test.setOnClickListener {
            riv_arc.setRadiusToDp(10)
        }

        btn_test_2.setOnClickListener {
            val layoutParams = riv_arc.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.width = 500
            layoutParams.height = 500
            riv_arc.layoutParams = layoutParams
        }

    }
}
