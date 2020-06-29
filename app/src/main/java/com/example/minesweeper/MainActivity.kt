package com.example.minesweeper

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_custom_board.*

class MainActivity : AppCompatActivity() {
    private val sharedPrefFile = "kotlinsharedpreference"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Adding Create custom board
        var c = 0
        custom_board.setOnClickListener {
            if (c < 1) {
                c++
                supportFragmentManager
                    .beginTransaction()
                    .add(R.id.container, CustomBoard())
                    .commit()

            }
        }

        //Removing Fragment if custom board is not selected
        Easy.setOnClickListener {
            c = 0
            val frag = supportFragmentManager.findFragmentById(R.id.container)
            frag?.let {
                supportFragmentManager.beginTransaction().remove(frag).commit()
            }
        }
        Medium.setOnClickListener {
            c = 0
            val frag = supportFragmentManager.findFragmentById(R.id.container)
            frag?.let {
                supportFragmentManager.beginTransaction().remove(frag).commit()
            }
        }
        Hard.setOnClickListener {
            c = 0
            val frag = supportFragmentManager.findFragmentById(R.id.container)
            frag?.let {
                supportFragmentManager.beginTransaction().remove(frag).commit()
            }
        }

        //Starting the Game
        Start.setOnClickListener {
            k++

            if (Difficulty.checkedRadioButtonId == -1) {
                Toast.makeText(applicationContext, "Please select Difficulty", Toast.LENGTH_SHORT)
                    .show()
            } else {
                //one of the radio buttons is checked
                when {
                    Easy.isChecked -> {
                        createBoardActivity(4, 2, 1)
                    }
                    Medium.isChecked -> {
                        createBoardActivity(12, 12, 30)
                    }
                    Hard.isChecked -> {
                        createBoardActivity(16, 16, 60)
                    }
                    custom_board.isChecked -> {
                        custom()
                    }
                }
            }

        }
//Showing Best Time
        best.setOnClickListener {

            val sharedPreferences: SharedPreferences =
                this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)

            val l = sharedPreferences.getString("lastgame", "00 Min 00 Sec")


            var s = "Best time is ${time()} \n\nTime of Last game won is $l "
            val builder = AlertDialog.Builder(this)
            with(builder) {

                setTitle("Your Best Score!!")
                setMessage(s)

                setPositiveButton("Ok") { _, _ ->
                }
                setNeutralButton("Share") { _, _ ->
                    shareScore()
                }
                show()
            }
        }
    }

    fun EditText.toInt() = text.toString().toInt()
    private fun createBoardActivity(height: Int, width: Int, amountOfMines: Int) {
        val intent = Intent(this, gamescreen::class.java).apply {
            gamescreen.run {
                putExtra(INTENT_MINES, amountOfMines)
                putExtra(INTENT_WIDTH, width)
                putExtra(INTENT_HEIGHT, height)

            }
        }
        startActivity(intent)

    }

    var min = IntArray(50)
    var sec = IntArray(50)
    var k = 0
    var b = ""

    //Getting best time to be Displayed
    fun time(): String {
        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        var q = sharedPreferences.getInt("min", 0)
        var p = sharedPreferences.getInt("sec", 2)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        min[0] = sharedPreferences.getInt("minu", 0)
        sec[0] = sharedPreferences.getInt("seco", 2)
        println(p)

        if (k == 1) {
            min[0] = q
            sec[0] = p
            b = "${min[0]} Min ${sec[0]} Sec"
        } else if (min[0] <= q && sec[0] < p) {
            b = " ${min[0]} Min ${sec[0]} Sec"
        } else {
            min[0] = q
            sec[0] = p
            b = "${min[0]} Min ${sec[0]} Sec"
        }
        editor.putInt("minu", min[0])
        editor.putInt("seco", sec[0])
        println(k)
        return b
    }

    private fun custom() {
        var width_int: Int
        var height_int = 0
        var mines_int = 0

        val width_string: String = width_.text.toString()
        val height_string: String = height_.text.toString()
        val mines_string: String = mines_.text.toString()

        if (width_string.isNotBlank() && height_string.isNotBlank() && mines_string.isNotBlank()) {
            width_int = width_string.toInt()
            height_int = height_string.toInt()
            mines_int = mines_string.toInt()
            if (width_int <= 10 || width_int > 25) {
                Toast.makeText(
                    applicationContext,
                    "Width should be between 11 and 25",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (height_int < 8 || height_int > 20) {
                Toast.makeText(
                    applicationContext,
                    "Height should be between 9 and 20 ",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (mines_int < width_int) {
                Toast.makeText(
                    applicationContext,
                    "Number of Mines should be at least equal to Width",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (height_int > width_int) {
                Toast.makeText(
                    applicationContext,
                    "Height should be less than or equal to Width",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (mines_int <= ((width_int * height_int) / 4)) {
                createBoardActivity(height_int, width_int, mines_int)

            } else {
                Toast.makeText(
                    applicationContext,
                    "Mines count should be less than 1/4 of the minesweeper grid size.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(
                applicationContext,
                "Please enter valid number of Height, Width and Mines",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun shareScore() {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                "Hi, My Best Time for Minesweeper Game is ${time()}.\n If you want play Download now using ....... "
            )
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
        //Log.i("Main : Share",(findViewById<TextView>(R.id.textViewBestGameTime) as TextView).text.toString())
    }
}

