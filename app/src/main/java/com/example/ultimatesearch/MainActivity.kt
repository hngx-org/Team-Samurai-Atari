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

        binding.ball.x = ballX
        binding.ball.y = ballY
    }
    private fun movePaddle(x: Float){
        paddleX = x - (binding.paddle.width / 2)
        binding.paddle.x = paddleX
    }
    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    private fun checkCollision(){
        //This block of code checks collision with walls
        val screenWidth = resources.displayMetrics.widthPixels.toFloat()
        val screenHeight = resources.displayMetrics.heightPixels.toFloat()

        if (ballX <= 0 || ballX + binding.ball.width >= screenWidth){
            ballSpeedX *= -1
        }
        if (ballY <= 0){
            ballSpeedY *= -1
        }

        //This block of code checks collision with the paddle
        if (ballY + binding.ball.height >= binding.paddle.y && ballY + binding.ball.height <= binding.paddle.y + binding.paddle.height
            && ballX + binding.ball.width >= binding.paddle.x && ballX <= binding.paddle.x + binding.paddle.width){
            ballSpeedY *= -1
            userScore++
            binding.scoreValue.text = "Score : $userScore"
        }

    }


}