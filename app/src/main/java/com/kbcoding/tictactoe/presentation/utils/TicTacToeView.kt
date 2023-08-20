package com.kbcoding.tictactoe.presentation.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import com.kbcoding.tictactoe.R
import kotlin.properties.Delegates

class TicTacToeView(
    context: Context,
    attributeSet: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
) : View(context, attributeSet, defStyleAttr, defStyleRes) {

    private var playerOneColor by Delegates.notNull<Int>()
    private var playerTwoColor by Delegates.notNull<Int>()
    private var gridColor by Delegates.notNull<Int>()

    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) :
            this(context, attributeSet, defStyleAttr, R.style.DefaultTicTacToeStyle)

    constructor(context: Context, attributeSet: AttributeSet?) :
            this(context, attributeSet, R.attr.ticTacToeFieldStyle)

    constructor(context: Context) : this(context, null)

    init {
        if (attributeSet != null) {
            initAttributes(attributeSet, defStyleAttr, defStyleRes)
        } else {
            initDefaultColors()
        }
    }

    private fun initAttributes(attributeSet: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        val typedArray = context.obtainStyledAttributes(
            attributeSet, R.styleable.TicTacToeView, defStyleAttr, defStyleRes
        )

        playerOneColor =
            typedArray.getColor(R.styleable.TicTacToeView_playerOneColor, PLAYER_ONE_DEFAULT_COLOR)
        playerTwoColor =
            typedArray.getColor(R.styleable.TicTacToeView_playerTwoColor, PLAYER_TWO_DEFAULT_COLOR)
        gridColor = typedArray.getColor(R.styleable.TicTacToeView_gridColor, GRID_DEFAULT_COLOR)

        typedArray.recycle()
    }

    private fun initDefaultColors() {
        playerOneColor = PLAYER_ONE_DEFAULT_COLOR
        playerTwoColor = PLAYER_TWO_DEFAULT_COLOR
        gridColor = GRID_DEFAULT_COLOR
    }

    companion object {
        const val PLAYER_ONE_DEFAULT_COLOR = Color.GREEN
        const val PLAYER_TWO_DEFAULT_COLOR = Color.RED
        const val GRID_DEFAULT_COLOR = Color.GRAY
    }
}