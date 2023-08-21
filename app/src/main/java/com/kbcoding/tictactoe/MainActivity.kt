package com.kbcoding.tictactoe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kbcoding.tictactoe.databinding.ActivityMainBinding
import com.kbcoding.tictactoe.presentation.utils.Cell
import com.kbcoding.tictactoe.presentation.utils.TicTacToeField
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cvTicTacToeView.ticTacToeField = TicTacToeField(10, 10)
        binding.btnRandomField.setOnClickListener {
            binding.cvTicTacToeView.ticTacToeField = TicTacToeField(
                Random.nextInt(3, 10),
                Random.nextInt(3, 10)
            )
            for (i in 0..binding.cvTicTacToeView.ticTacToeField!!.rows) {
                for (j in 0..binding.cvTicTacToeView.ticTacToeField!!.columns) {
                    if (Random.nextBoolean()){
                        binding.cvTicTacToeView.ticTacToeField!!.setCell(i,j, Cell.PLAYER_ONE)
                    } else {
                        binding.cvTicTacToeView.ticTacToeField!!.setCell(i,j, Cell.PLAYER_TWO)
                    }
                }
            }
        }
    }
}