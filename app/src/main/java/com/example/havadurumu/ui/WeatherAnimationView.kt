package com.example.havadurumu.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random

class WeatherAnimationView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var weatherType: String? = null
    private val particles = mutableListOf<Particle>()
    private val paint = Paint()
    private var isAnimating = false
    private var lightningTime = 0L
    private val lightningDuration = 200L

    init {
        paint.isAntiAlias = true
    }

    fun setWeatherType(type: String?) {
        weatherType = type
        particles.clear()
        if (weatherType != null) {
            startAnimation()
        } else {
            stopAnimation()
        }
    }

    private fun startAnimation() {
        if (!isAnimating) {
            isAnimating = true
            invalidate()
        }
    }

    private fun stopAnimation() {
        isAnimating = false
        particles.clear()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (!isAnimating) return

        when (weatherType) {
            "Rain" -> drawRain(canvas)
            "Snow" -> drawSnow(canvas)
            "Clear" -> drawClear(canvas)
            "Clouds", "Few clouds", "Scattered clouds", "Broken clouds" -> drawCloudy(canvas)
            "Thunderstorm" -> drawThunderstorm(canvas)
        }

        invalidate()
    }

    private fun drawRain(canvas: Canvas) {
        if (particles.size < 50) {
            repeat(5) {
                particles.add(Particle(
                    x = Random.nextFloat() * width,
                    y = -10f,
                    speed = Random.nextFloat() * 10 + 5,
                    size = Random.nextFloat() * 5 + 2,
                    type = "Rain"
                ))
            }
        }

        paint.color = Color.parseColor("#87CEEB")
        paint.strokeWidth = 2f
        val iterator = particles.iterator()
        while (iterator.hasNext()) {
            val particle = iterator.next()
            particle.y += particle.speed
            canvas.drawLine(particle.x, particle.y, particle.x, particle.y + particle.size * 2, paint)
            if (particle.y > height) iterator.remove()
        }
    }

    private fun drawSnow(canvas: Canvas) {
        if (particles.size < 50) {
            repeat(5) {
                particles.add(Particle(
                    x = Random.nextFloat() * width,
                    y = -10f,
                    speed = Random.nextFloat() * 5 + 2,
                    size = Random.nextFloat() * 5 + 3,
                    type = "Snow"
                ))
            }
        }

        paint.color = Color.WHITE
        val iterator = particles.iterator()
        while (iterator.hasNext()) {
            val particle = iterator.next()
            particle.y += particle.speed
            particle.x += (Random.nextFloat() - 0.5f) * 2
            canvas.drawCircle(particle.x, particle.y, particle.size, paint)
            if (particle.y > height) iterator.remove()
        }
    }

    private fun drawClear(canvas: Canvas) {
        paint.color = Color.parseColor("#FFD700")
        paint.strokeWidth = 1f
        if (particles.size < 10) {
            repeat(2) {
                particles.add(Particle(
                    x = Random.nextFloat() * width,
                    y = Random.nextFloat() * height,
                    speed = Random.nextFloat() * 2 + 1,
                    size = Random.nextFloat() * 10 + 5,
                    type = "Clear"
                ))
            }
        }
        val iterator = particles.iterator()
        while (iterator.hasNext()) {
            val particle = iterator.next()
            particle.y -= particle.speed
            canvas.drawLine(particle.x, particle.y, particle.x + particle.size * 0.5f, particle.y - particle.size, paint)
            if (particle.y < 0) iterator.remove()
        }
    }

    private fun drawCloudy(canvas: Canvas) {
        paint.color = Color.parseColor("#E0E0E0")
        if (particles.size < 5) {
            repeat(1) {
                particles.add(Particle(
                    x = -50f,
                    y = Random.nextFloat() * height,
                    speed = Random.nextFloat() * 2 + 1,
                    size = Random.nextFloat() * 30 + 20,
                    type = "Cloudy"
                ))
            }
        }
        val iterator = particles.iterator()
        while (iterator.hasNext()) {
            val particle = iterator.next()
            particle.x += particle.speed
            canvas.drawCircle(particle.x, particle.y, particle.size, paint)
            if (particle.x > width + 50) iterator.remove()
        }
    }

    private fun drawThunderstorm(canvas: Canvas) {
        paint.color = Color.YELLOW
        paint.strokeWidth = 5f
        val currentTime = System.currentTimeMillis()
        if (currentTime - lightningTime > 1000 && Random.nextFloat() < 0.02f) {
            lightningTime = currentTime
            val startX = Random.nextFloat() * width
            val startY = 0f
            val endX = startX + (Random.nextFloat() - 0.5f) * 50
            val endY = height * 0.7f
            canvas.drawLine(startX, startY, endX, endY, paint)
        }

        drawRain(canvas)
    }

    data class Particle(
        var x: Float,
        var y: Float,
        var speed: Float,
        var size: Float,
        var type: String
    )
}