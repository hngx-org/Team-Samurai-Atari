package com.example.ultimatesearch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //This block of code implements a fade in animation in the splash screen
        val splashImage : ImageView = findViewById(R.id.splashScreen)
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        splashImage.startAnimation(fadeInAnimation)

        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}