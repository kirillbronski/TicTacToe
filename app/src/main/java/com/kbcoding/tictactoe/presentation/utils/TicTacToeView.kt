package com.kbcoding.tictactoe.presentation.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import com.kbcoding.tictactoe.R
import java.lang.Integer.max
import kotlin.math.floor
import kotlin.math.min
import kotlin.properties.Delegates

typealias OnCellActionListener = (row: Int, column: Int, field: TicTacToeField) -> Unit

class TicTacToeView(
    context: Context,
    attributeSet: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
) : View(context, attributeSet, defStyleAttr, defStyleRes) {

    var ticTacToeField: TicTacToeField? = null
        set(value) {
            field?.listeners?.remove(listener)
            field = value
            field?.listeners?.add(listener)
            updateViewSize()
            requestLayout()
            invalidate()
        }

    var actionListener: OnCellActionListener? = null

    private var playerOneColor by Delegates.notNull<Int>()
    private var playerTwoColor by Delegates.notNull<Int>()
    private var gridColor by Delegates.notNull<Int>()

    private val fieldRect = RectF()
    private var cellSize = 0f
    private var cellPadding = 0f

    private val cellRect = RectF()

    private var currentRow: Int = -1
    private var currentColumn: Int = -1

    private lateinit var playerOnePaint: Paint
    private lateinit var playerTwoPaint: Paint
    private lateinit var currentCellPaint: Paint
    private lateinit var gridPaint: Paint

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
        initPaints()
        if (isInEditMode) {
            ticTacToeField = TicTacToeField(8, 6)
            ticTacToeField?.setCell(4, 2, Cell.PLAYER_ONE)
            ticTacToeField?.setCell(4, 3, Cell.PLAYER_TWO)
        }
        isFocusable = true
        isClickable = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            defaultFocusHighlightEnabled = false
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        ticTacToeField?.listeners?.add(listener)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        ticTacToeField?.listeners?.remove(listener)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateViewSize()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minWidth = suggestedMinimumWidth + paddingLeft + paddingRight
        val minHeight = suggestedMinimumHeight + paddingTop + paddingBottom

        val desiredCellSizeInPixel = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, DESIRED_CELL_SIZE, resources.displayMetrics
        ).toInt()

        val rows = ticTacToeField?.rows ?: 0
        val columns = ticTacToeField?.columns ?: 0

        val desiredWidth =
            max(minWidth, columns * desiredCellSizeInPixel + paddingLeft + paddingRight)
        val desiredHeight =
            max(minHeight, rows * desiredCellSizeInPixel + paddingTop + paddingBottom)

        setMeasuredDimension(
            resolveSize(desiredWidth, widthMeasureSpec),
            resolveSize(desiredHeight, heightMeasureSpec)
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (ticTacToeField == null) return
        if (cellSize == 0f) return
        if (fieldRect.width() <= 0) return
        if (fieldRect.height() <= 0) return

        drawGrid(canvas)
        drawCurrentCell(canvas)
        drawCells(canvas)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when(keyCode) {
            KeyEvent.KEYCODE_DPAD_DOWN -> moveCurrentCell(1, 0)
            KeyEvent.KEYCODE_DPAD_LEFT -> moveCurrentCell(0, -1)
            KeyEvent.KEYCODE_DPAD_RIGHT -> moveCurrentCell(0, 1)
            KeyEvent.KEYCODE_DPAD_UP -> moveCurrentCell(-1, 0)
            else -> super.onKeyDown(keyCode, event)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                updateCurrentCell(event)
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                updateCurrentCell(event)
                return true
            }

            MotionEvent.ACTION_UP -> {
                return performClick()
            }
        }
        return false
    }

    override fun performClick(): Boolean {
        super.performClick()
        val field = this.ticTacToeField ?: return false
        val row = currentRow
        val column = currentColumn
        if (row >= 0 && column >= 0 && row < field.rows && column < field.columns) {
            actionListener?.invoke(row, column, field)
            return true
        }
        return false
    }

    private fun moveCurrentCell(rowDiff: Int, columnDiff: Int): Boolean {
        val field = this.ticTacToeField ?: return false
        if (currentRow < 0 || currentColumn < 0 || currentRow >= field.rows
            || currentColumn >= field.columns) {
            currentRow = 0
            currentColumn = 0
            invalidate()
            return true
        } else {
            if (currentColumn + columnDiff < 0) return false
            if (currentColumn + columnDiff >= field.columns) return false
            if (currentRow + rowDiff < 0) return false
            if (currentRow + rowDiff >= field.rows) return false

            currentColumn += columnDiff
            currentRow += rowDiff
            invalidate()
            return true
        }
    }

    private fun drawCurrentCell(canvas: Canvas) {
        val field = this.ticTacToeField ?: return
        if (currentRow < 0 && currentColumn < 0 && currentRow >= field.rows
            && currentColumn >= field.columns) return

        val cell = getCellRect(currentRow, currentColumn)
        canvas.drawRect(
            cell.left - cellPadding,
            cell.top - cellPadding,
            cell.right + cellPadding,
            cell.bottom + cellPadding,
            currentCellPaint
        )
    }

    private fun updateCurrentCell(event: MotionEvent) {
        val field = this.ticTacToeField ?: return
        val row = getRow(event)
        val column = getColumn(event)
        if (row >= 0 && column >= 0 && row < field.rows && column < field.columns) {
            if (currentRow != row || currentColumn != column) {
                currentRow = row
                currentColumn = column
                invalidate()
            }
        } else {
            currentRow = -1
            currentColumn = -1
            invalidate()
        }
    }

    private fun getRow(event: MotionEvent): Int {
        return floor((event.y - fieldRect.top) / cellSize).toInt()
    }

    private fun getColumn(event: MotionEvent): Int {
        return floor((event.x - fieldRect.left) / cellSize).toInt()
    }

    private fun drawGrid(canvas: Canvas) {
        val field = this.ticTacToeField ?: return

        val startX = fieldRect.left
        val stopX = fieldRect.right
        for (i in 0..field.rows) {
            val y = fieldRect.top + cellSize * i
            canvas.drawLine(startX, y, stopX, y, gridPaint)
        }

        val startY = fieldRect.top
        val stopY = fieldRect.bottom
        for (i in 0..field.columns) {
            val x = fieldRect.left + cellSize * i
            canvas.drawLine(x, startY, x, stopY, gridPaint)
        }
    }

    private fun drawCells(canvas: Canvas) {
        val field = this.ticTacToeField ?: return
        for (row in 0 until field.rows) {
            for (column in 0 until field.columns) {
                val cell = field.getCell(row, column)
                if (cell == Cell.PLAYER_ONE) {
                    drawPlayerOne(canvas, row, column)
                } else if (cell == Cell.PLAYER_TWO) {
                    drawPlayerTwo(canvas, row, column)
                }
            }
        }
    }

    private fun drawPlayerOne(canvas: Canvas, row: Int, column: Int) {
        val cellRect = getCellRect(row, column)
        canvas.drawLine(
            cellRect.left, cellRect.top,
            cellRect.right, cellRect.bottom, playerOnePaint
        )
        canvas.drawLine(
            cellRect.right, cellRect.top,
            cellRect.left, cellRect.bottom, playerOnePaint
        )
    }

    private fun drawPlayerTwo(canvas: Canvas, row: Int, column: Int) {
        val cellRect = getCellRect(row, column)
        canvas.drawCircle(
            cellRect.centerX(),
            cellRect.centerY(),
            cellRect.width() / 2,
            playerTwoPaint
        )
    }

    private fun getCellRect(row: Int, column: Int): RectF {
        cellRect.apply {
            left = fieldRect.left + column * cellSize + cellPadding
            top = fieldRect.top + row * cellSize + cellPadding
            right = cellRect.left + cellSize - cellPadding * 2
            bottom = cellRect.top + cellSize - cellPadding * 2
        }
        return cellRect
    }

    private fun initPaints() {
        playerOnePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = playerOneColor
            style = Paint.Style.STROKE
            strokeWidth = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, PLAYER_STROKE_WIDTH, resources.displayMetrics
            )
        }

        playerTwoPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = playerTwoColor
            style = Paint.Style.STROKE
            strokeWidth = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, PLAYER_STROKE_WIDTH, resources.displayMetrics
            )
        }

        currentCellPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(230, 230, 230)
            style = Paint.Style.FILL
        }

        gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = gridColor
            style = Paint.Style.STROKE
            strokeWidth = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, GRID_STROKE_WIDTH, resources.displayMetrics
            )
        }
    }

    private fun updateViewSize() {
        val field = this.ticTacToeField ?: return

        val safeWidth = width - paddingLeft - paddingRight
        val safeHeight = height - paddingTop - paddingBottom

        val cellWidth = safeWidth / field.columns.toFloat()
        val cellHeight = safeHeight / field.rows.toFloat()

        cellSize = min(cellWidth, cellHeight)
        cellPadding = cellSize * 0.2f

        val fieldWidth = cellSize * field.columns
        val fieldHeight = cellSize * field.rows

        fieldRect.left = paddingLeft + (safeWidth - fieldWidth) / 2
        fieldRect.top = paddingTop + (safeHeight - fieldHeight) / 2
        fieldRect.right = fieldRect.left + fieldWidth
        fieldRect.bottom = fieldRect.top + fieldHeight
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

    private val listener: OnFieldChangedListener = {
        invalidate()
    }

    companion object {
        const val PLAYER_ONE_DEFAULT_COLOR = Color.GREEN
        const val PLAYER_TWO_DEFAULT_COLOR = Color.RED
        const val GRID_DEFAULT_COLOR = Color.GRAY

        const val DESIRED_CELL_SIZE = 50f
        const val PLAYER_STROKE_WIDTH = 3f
        const val GRID_STROKE_WIDTH = 1f
    }
}