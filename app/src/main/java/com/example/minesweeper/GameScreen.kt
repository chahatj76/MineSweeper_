package com.example.minesweeper

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog


import androidx.lifecycle.Observer


import androidx.lifecycle.ViewModelProviders
import com.example.minesweeper.Game.Game
import com.example.minesweeper.Game.GameViewModel
import com.example.minesweeper.Game.GameViewModelFactory


import kotlinx.android.synthetic.main.activity_gamescreen.*


open class gamescreen : AppCompatActivity() {


    private val sharedPrefFile = "kotlinsharedpreference"

    companion object {
        const val INTENT_WIDTH = "WIDTH"
        const val INTENT_HEIGHT = "HEIGHT"
        const val INTENT_MINES = "MINES"
        const val TAG = "gamescreen"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gamescreen)
        val width = intent.getIntExtra(INTENT_WIDTH, 15)
        val height = intent.getIntExtra(INTENT_HEIGHT, 15)
        val mines = intent.getIntExtra(INTENT_MINES, 15)
        var gameb = findViewById<androidx.gridlayout.widget.GridLayout>(R.id.game_board)
//        var obj = GameViewModel()

        fun stop() {
            val Chron = findViewById<Chronometer>(R.id.simpleChronometer)
            Chron.stop()
            var string = Chron.text.split(":").toTypedArray()
            var str = "${string[0]} Min ${string[1]} Sec"
            println(string[1])
            var i = string[0].toInt()
            var j = string[1].toInt()
            println(j)

            val sharedPreferences: SharedPreferences =
                this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            //editor.putString("besttime", str)
            editor.putString("lastgame", str)
            editor.putInt("min", i)
            editor.putInt("sec", j)
            editor.apply()
            editor.commit()

        }

        val Chron = findViewById<Chronometer>(R.id.simpleChronometer)

        val model: GameViewModel =
            ViewModelProviders.of(
                this,
                GameViewModelFactory(
                    height,
                    width,
                    mines
                )
            )
                .get(GameViewModel::class.java)

        minescount.text = mines.toString()
        //Observing the difference b/w Mines and Flags
        model.flagsLeft.observe(this, Observer {
            minescount.text = model.flagsLeft.value.toString()
        })
        //Observing the Game End
        model.winnState.observe(this, Observer {
            game_zoom.engine.zoomTo(1.0F, true)
            var titl = ""
            when (it) {
                Game.EndState.WON -> {
                    titl = "You won!"
                    stop()
                }
                Game.EndState.LOST -> {
                    titl = "You lost :("

                    Chron.stop()
                }
                Game.EndState.UNDECIDED -> titl = "You... huh"
            }

            val builder = AlertDialog.Builder(this)
            with(builder) {

                setTitle(titl)
                setMessage("Please Select a option:")

                setPositiveButton("Play Again") { _, _ ->
                    restart()

                }

                setNegativeButton("EXIT") { _, _ ->
                    finishAffinity()
                }

                setNeutralButton("Change Difficulty") { _, _ ->
//                    stop()
                    finish()
//                    val int = Intent(this@gamescreen, MainActivity::class.java)
//                    int.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                    startActivity(int)
                }
                show()
            }
        })
        gameb.addGame(model)
        Chron.start()


        val start = findViewById<Button>(R.id.restart)
        start.setOnClickListener {
            Chron.stop()
            val builder = AlertDialog.Builder(this)
            with(builder) {

                setTitle("Restart")
                setMessage("Are you sure you want to Restart?")

                setPositiveButton("Yes") { _, _ ->
                    restart()

                }

                setNegativeButton("No") { _, _ ->
                    Chron.start()
                }
                show()
            }

        }


    }
}

fun Activity.restart() {
    startActivity(intent)
    finish()
}




