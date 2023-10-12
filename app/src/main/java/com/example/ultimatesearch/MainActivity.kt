package com.example.ultimatesearch

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet.Motion

class MainActivity : AppCompatActivity() {

    private lateinit var scoreText : TextView
    private lateinit var paddle: View
    private lateinit var ball: View
    private lateinit var brickContainer: LinearLayout

    private var ballX = 0f
    private var ballY = 0f
    private var ballSpeedX = 0f
    private var ballSpeedY = 0f
    private var paddleX = 0f
    private var userScore = 0
    private val brickRows = 5

    private val brickColumns = 10
    private val brickWidth = 100
    private val brickHeight = 40
    private val brickMagin = 4
    private var isBallLaunched = false
    private var lives = 3

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scoreText = findViewById(R.id.scoreValue)
        paddle = findViewById(R.id.paddle)
        ball = findViewById(R.id.ball)
        brickContainer = findViewById(R.id.brickContainer)

        val newgame: Button = findViewById(R.id.newGame)
        newgame.setOnClickListener(){
            initializeBricks()
            //movepaddle()
            newgame.visibility = View.INVISIBLE
            start()
        }

    }

    private fun initializeBricks(){
        val brickWIdthWithMargin = (brickWidth + brickMagin).toInt()

        for (row in 0 until brickRows){
            val rowLayout = LinearLayout(this)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            rowLayout.layoutParams = params
            for (col in 0 until brickColumns){
                val brick = View(this)
                val brickParams = LinearLayout.LayoutParams(brickWidth, brickHeight)
                brickParams.setMargins(brickMagin, brickMagin, brickMagin, brickMagin)
                brick.layoutParams = brickParams
                brick.setBackgroundResource(R.drawable.ic_launcher_background)
                rowLayout.addView(brick)
            }
            brickContainer.addView(rowLayout)
        }
    }
    private fun moveBall(){
        ballX += ballSpeedX
        ballY += ballSpeedY

        ball.x = ballX
        ball.y = ballY
    }
    private fun movePaddle(x: Float){
        paddleX = x - (paddle.width / 2)
        paddle.x = paddleX
    }
    @SuppressLint("ClickableViewAccessibility")
    private fun checkCollision(){
        //This block of code checks collision with walls
        val screenWidth = resources.displayMetrics.widthPixels.toFloat()
        val screenHeight = resources.displayMetrics.heightPixels.toFloat()

        if (ballX <= 0 || ballX + ball.width >= screenWidth){
            ballSpeedX *= -1
        }
        if (ballY <= 0){
            ballSpeedY *= -1
        }

        //This block of code checks collision with the paddle
        if (ballY + ball.height >= paddle.y && ballY + ball.height <= paddle.y + paddle.height
            && ballX + ball.width >= paddle.x && ballX <= paddle.x + paddle.width){
            ballSpeedY *= -1
            userScore++
            scoreText.text = "Score : $userScore"
        }
        //This block of code check for collision with the bottom wall..
        //..when the paddle misses the ball
        if (ballY + ball.height >= screenHeight) {
            resetBallPosition()
        }
        //Check collision with bricks
        for (row in 0 until brickRows){
            val rowLayout = brickContainer.getChildAt(row) as LinearLayout
            val rowTop = rowLayout.y + brickContainer.y
            val rowBottom = rowTop + rowLayout.height

            for (col in 0 until brickColumns){
                val brick = rowLayout.getChildAt(col) as View

                if (brick.visibility == View.VISIBLE){
                    val brickLeft = brick.x + rowLayout.x
                    val brickRight = brickLeft + brick.width
                    val brickTop = brick.y + rowTop
                    val brickBottom = brickTop + brick.height

                    if (ballX + ball.width >= brickLeft && ballX <= brickRight
                        && ballY + ball.height >= brickTop && ballY <= brickBottom){
                        brick.visibility = View.VISIBLE
                        ballSpeedY *= -1
                        userScore++
                        scoreText.text = "Score: $userScore"
                        return

                    }
                }
            }
        }
        //Check if paddle misses the ball and collides with wall
        if (ballY + ball.height >= screenHeight - 100){
            //reduce the number of lives
            lives--

            if (lives > 0){
                Toast.makeText(this, "$lives balls left", Toast.LENGTH_SHORT).show()

            }
            paddle.setOnTouchListener{_, event ->
                when (event.action){
                    MotionEvent.ACTION_MOVE -> {
                        movePaddle(event.rawX)
                    }
                }
                true
            }
            if (lives <= 0){
                //Game over if no more lives left
                gameOver()
            }else{
                //Return ball to initial position
                resetBallPosition()
                start()
            }
        }
    }
    private fun resetBallPosition(){
        //Reset the ball to its initial position
        val displayMetrics = resources.displayMetrics
        val screenDensity = displayMetrics.density
        val screenWidth = displayMetrics.widthPixels.toFloat()
        val screenHeight = displayMetrics.heightPixels.toFloat()

        ballX = (screenWidth / 2) - (ball.width / 2)
        ballY= (screenHeight/2) - (ball.height/2)

        ball.x = ballX
        ball.y = ballY

        //Reset ball speed
        ballSpeedY = 0 * screenDensity
        ballSpeedX = 0 * screenDensity

        paddleX = (screenWidth / 2) - (paddle.width / 2)
        paddle.x = paddleX
    }
    private fun gameOver(){
        scoreText.text = "Game Over"
        userScore = 0
        val newgame: Button = findViewById(R.id.newGame)
        newgame.visibility = View.VISIBLE
    }
    private fun movepaddle(){
        paddle.setOnTouchListener{_, event ->
            when (event.action){
                MotionEvent.ACTION_MOVE -> {
                    movePaddle(event.rawX)
                }
            }
            true
        }
    }
    private fun start(){
        movepaddle()
        val displayMetrics = resources.displayMetrics
        val screenDensity = displayMetrics.density
        val screenWith = displayMetrics.widthPixels.toFloat()
        val screenHeight = displayMetrics.heightPixels.toFloat()

        paddleX = (screenWith / 2) - (paddle.width / 2)
        paddle.x = paddleX

        ballX = (screenWith / 2) - (ball.width / 2)
        ballY = (screenHeight / 2) - (ball.height / 2)

        val brickHeightWithMargin = (brickHeight + brickMagin * screenDensity). toInt()
        ballSpeedX = 3 * screenDensity
        ballSpeedY = -3 * screenDensity

        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = Long.MAX_VALUE
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener { animation ->
            moveBall()
            checkCollision()
        }
        animator.start()
    }
}