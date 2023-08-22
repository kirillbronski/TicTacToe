package com.kbcoding.tictactoe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kbcoding.tictactoe.databinding.ActivityMainBinding
import com.kbcoding.tictactoe.presentation.utils.Cell
import com.kbcoding.tictactoe.presentation.utils.TicTacToeField
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private var isFirstPlayer = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cvTicTacToeView.ticTacToeField = TicTacToeField(10, 10)
        binding.cvTicTacToeView.actionListener = { row, column, field ->
            val cell = field.getCell(row, column)
            if (cell == Cell.EMPTY) {
                if (isFirstPlayer) {
                    field.setCell(row, column, Cell.PLAYER_ONE)
                } else {
                    field.setCell(row, column, Cell.PLAYER_TWO)
                }
                isFirstPlayer = !isFirstPlayer
            }
        }
    }
}