package com.example.ultimatesearch

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.example.ultimatesearch.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

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
    private val brickRows = 9

    private val brickColumns = 10
    private val brickWidth = 100
    private val brickHeight = 40
    private val brickMargin = 4
    private var isBallLaunched = false
    private var lives = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        scoreText = findViewById(R.id.scoreValue)
        paddle = findViewById(R.id.paddle)
        ball = findViewById(R.id.ball)
        brickContainer = findViewById(R.id.brickContainer)

        val newGame: Button = findViewById(R.id.newGame)
        newGame.setOnClickListener(){
            initializeBricks()
        }

    }

    private fun initializeBricks(){
        val brickWidthMargin = (brickWidth + brickMargin)

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
                brickParams.setMargins(brickMargin, brickMargin, brickMargin, brickMargin)
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

    }
}