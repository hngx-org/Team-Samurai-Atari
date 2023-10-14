package com.example.ultimatesearch

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout

import android.widget.Toast
import com.example.ultimatesearch.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var ballX = 0f
    private var ballY = 0f
    private var ballSpeedX = 0f
    private var ballSpeedY = 0f
    private var paddleX = 0f
    private var userScore = 0
    private val brickRows = 5

    private val brickColumns = 7
    private val brickWidth = 97
    private val brickHeight = 40
    private val brickMargin = 4
    private var isBallLaunched = false
    private var lives = 3

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeBricks()

        binding.newGame.setOnClickListener{
            movePaddle()
            binding.newGame.visibility = View.INVISIBLE
            start()
        }

    }

    private fun initializeBricks() {

        for (row in 0 until brickRows) {
            val rowLayout = LinearLayout(this)
            val rowLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            rowLayout.layoutParams = rowLayoutParams

            for (col in 0 until brickColumns) {
                val brick = View(this)
                val brickLayoutParams = LinearLayout.LayoutParams(brickWidth, brickHeight)
                brickLayoutParams.setMargins(brickMargin, brickMargin, brickMargin, brickMargin)
                brick.layoutParams = brickLayoutParams
                brick.setBackgroundResource(R.drawable.ic_launcher_background)
                brick.tag = R.drawable.ic_launcher_background // Set a tag to identify bricks

                rowLayout.addView(brick)
            }

            binding.brickContainer.addView(rowLayout)
        }
    }

    private fun moveBall(){
        ballX += ballSpeedX
        ballY += ballSpeedY

        binding.ball.x = ballX
        binding.ball.y = ballY
    }
    private fun movePaddle(x: Float){
        paddleX = x - (binding.paddle.width / 2)
        binding.paddle.x = paddleX
    }

    private fun checkCollision() {
        val screenWidth = resources.displayMetrics.widthPixels.toFloat()
        val screenHeight = resources.displayMetrics.heightPixels.toFloat()

        // Check collision with walls
        if (ballX <= 0 || ballX + binding.ball.width >= screenWidth) {
            ballSpeedX *= -1
        }
        if (ballY <= 0) {
            ballSpeedY *= -1
        }

        // Check collision with the paddle
        val paddle = binding.paddle
        if (ballY + binding.ball.height >= paddle.y &&
            ballY + binding.ball.height <= paddle.y + paddle.height &&
            ballX + binding.ball.width >= paddle.x &&
            ballX <= paddle.x + paddle.width
        ) {
            ballSpeedY *= -1
            userScore++
            updateScore()
        }

        // Check collision with the bottom wall when the paddle misses the ball
        if (ballY + binding.ball.height >= screenHeight) {
            handleMissedBall()
        }

        // Check collision with bricks
        for (row in 0 until brickRows) {
            val rowLayout = binding.brickContainer.getChildAt(row) as LinearLayout
            val rowTop = rowLayout.y + binding.brickContainer.y

            for (col in 0 until brickColumns) {
                val brick = rowLayout.getChildAt(col) as View

                if (brick.visibility == View.VISIBLE) {
                    val brickLeft = brick.x + rowLayout.x
                    val brickRight = brickLeft + brick.width
                    val brickTop = brick.y + rowTop
                    val brickBottom = brickTop + brick.height

                    if (ballX + binding.ball.width >= brickLeft &&
                        ballX <= brickRight &&
                        ballY + binding.ball.height >= brickTop &&
                        ballY <= brickBottom
                    ) {
                        handleBrickCollision(brick)
                        return
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateScore() {
        binding.scoreValue.text = "Score: userScore"
    }

    private fun handleMissedBall() {
        lives--
        if (lives > 0) {
            Toast.makeText(this, "$lives balls left", Toast.LENGTH_SHORT).show()
            setupPaddleTouchListener()
            resetBallPosition()
            start()
        } else gameOver()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupPaddleTouchListener() {
        binding.paddle.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    movePaddle(event.rawX)
                }
            }
            true
        }
    }

    private fun handleBrickCollision(brick: View) {
        brick.visibility = View.GONE
        ballSpeedY *= -1
        ballSpeedX *= -1
        userScore++
        updateScore()
    }



    private fun resetBallPosition(){
        //Reset the ball to its initial position
        val displayMetrics = resources.displayMetrics
        val screenDensity = displayMetrics.density
        val screenWidth = displayMetrics.widthPixels.toFloat()
        val screenHeight = displayMetrics.heightPixels.toFloat()

        ballX = (screenWidth / 2) - (binding.ball.width / 2)
        ballY= (screenHeight/2) - (binding.ball.height/2)

        binding.ball.x = ballX
        binding.ball.y = ballY

        //Reset ball speed
        ballSpeedY = 0 * screenDensity
        ballSpeedX = 0 * screenDensity

        paddleX = (screenWidth / 2) - (binding.paddle.width / 2)
        binding.paddle.x = paddleX
    }
    @SuppressLint("SetTextI18n")
    private fun gameOver(){
        binding.scoreValue.text = "Game Over"
        userScore = 0
        binding.newGame.visibility = View.VISIBLE
    }
    @SuppressLint("ClickableViewAccessibility")
    private fun movePaddle(){
        binding.paddle.setOnTouchListener{_, event ->
            when (event.action){
                MotionEvent.ACTION_MOVE -> {
                    movePaddle(event.rawX)
                }
            }
            true
        }
    }


    private var animator: ValueAnimator? = null // Class-level variable to track the animation

    private fun start() {
        movePaddle()

        if (animator != null && animator?.isRunning == true) {
            // Stop the previous animation if it's running
            animator?.cancel()
        }

        val displayMetrics = resources.displayMetrics
        val screenDensity = displayMetrics.density
        val screenWith = displayMetrics.widthPixels.toFloat()
        val screenHeight = displayMetrics.heightPixels.toFloat()

        paddleX = (screenWith / 2) - (binding.paddle.width / 2)
        binding.paddle.x = paddleX

        ballX = (screenWith / 2) - (binding.ball.width / 2)
        ballY = (screenHeight / 2) - (binding.ball.height / 2)

        val brickHeightWithMargin = (brickHeight + brickMargin * screenDensity).toInt()
        ballSpeedX = 3 * screenDensity
        ballSpeedY = -3 * screenDensity

        // Create a new ValueAnimator and start it
        animator = ValueAnimator.ofFloat(0f, 1f)
        animator?.duration = Long.MAX_VALUE
        animator?.interpolator = LinearInterpolator()
        animator?.addUpdateListener { _ ->
            moveBall()
            checkCollision()
        }
        animator?.start()
    }

}