package com.example.ept.dicover.discovery

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.animation.AccelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import kotlin.math.abs

/**
 * description ： 原生层叠式循环卡片 View
 * email : 3014386984@qq.com
 * date : 2026/7/22
 */
class LoopingCardStackView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    // ========== 配置参数 ==========
    var maxVisibleCards = 3           // 可见卡片层数
    var cardScaleStep = 0.05f         // 每层缩放差值
    var cardTranslationYStep = 20f    // 每层Y轴偏移(px)
    var swipeThreshold = 0.3f         // 滑动触发阈值(占宽度比例)
    var flyOutDuration = 300L         // 飞出动画时长(ms)
    var rewindDuration = 300L         // 回弹动画时长(ms)

    // ========== 回调 ==========
    /** 卡片被滑出时回调: direction=true为右滑, false为左滑 */
    var onCardSwiped: ((direction: Boolean) -> Unit)? = null
    /** 需要提供卡片View的工厂方法 */
    var cardProvider: (() -> View)? = null
    /** 卡片被循环放回底部时回调，可用于重新绑定数据 */
    var onCardRecycled: ((recycledView: View) -> Unit)? = null

    // ========== 内部状态 ==========
    private val cardStack = ArrayDeque<View>()
    private var isAnimating = false
    private var isSwiping = false
    private lateinit var gestureDetector: GestureDetector
    private var downX = 0f
    private var downY = 0f
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop

    init {
        clipChildren = false
        clipToPadding = false

        gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent): Boolean = !isAnimating && cardStack.isNotEmpty()

            override fun onScroll(
                e1: MotionEvent?, e2: MotionEvent,
                distanceX: Float, distanceY: Float
            ): Boolean {
                if (isAnimating || cardStack.isEmpty()) return false
                val topCard = cardStack.first()
                topCard.translationX = e2.x - (e1?.x ?: e2.x)
                topCard.translationY = (e2.y - (e1?.y ?: e2.y)) * 0.3f // Y轴阻尼
                topCard.rotation = topCard.translationX / width * 20f
                if (!isSwiping) {
                    isSwiping = true
                    topCard.setLayerType(View.LAYER_TYPE_HARDWARE, null)
                }
                return true
            }

            override fun onFling(
                e1: MotionEvent?, e2: MotionEvent,
                velocityX: Float, velocityY: Float
            ): Boolean {
                if (isAnimating || cardStack.isEmpty()) return false
                val topCard = cardStack.first()
                val ratio = abs(topCard.translationX) / width.coerceAtLeast(1)
                val fastEnough = abs(velocityX) > 800
                if (ratio >= swipeThreshold || fastEnough) {
                    flyOut(topCard, topCard.translationX > 0)
                } else {
                    rewind(topCard)
                }
                return true
            }
        })
    }

    // ========== 触摸事件 ==========

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downX = ev.x
                downY = ev.y
                gestureDetector.onTouchEvent(ev)
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = ev.x - downX
                val dy = ev.y - downY
                if (abs(dx) > touchSlop && abs(dx) > abs(dy)) {
                    parent?.requestDisallowInterceptTouchEvent(true)
                    return true
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
            }
        }
        return gestureDetector.onTouchEvent(event)
    }

    // ========== 公开API ==========

    /** 初始化并填充卡片栈 */
    fun start() {
        removeAllViews()
        cardStack.clear()
        fillStack()
    }

    /** 手动触发顶层卡片滑出(如按钮操作) */
    fun swipeTop(direction: Boolean) {
        if (isAnimating || cardStack.isEmpty()) return
        flyOut(cardStack.first(), direction)
    }

    // ========== 核心逻辑 ==========

    private fun fillStack() {
        while (cardStack.size < maxVisibleCards) {
            val card = cardProvider?.invoke() ?: break
            addView(card, 0) // index=0 → 视觉最底层
            cardStack.addLast(card)
        }
        updateTransforms()
        bindTopGesture()
    }

    /** 更新所有可见卡片的缩放和位移 */
    private fun updateTransforms() {
        cardStack.forEachIndexed { index, view ->
            val scale = 1f - index * cardScaleStep
            view.apply {
                scaleX = scale.coerceAtLeast(0.8f)
                scaleY = scale.coerceAtLeast(0.8f)
                translationY = index * cardTranslationYStep
                translationX = 0f
                rotation = 0f
                alpha = 1f
                visibility = if (index < maxVisibleCards) VISIBLE else GONE
                elevation = (maxVisibleCards - index).toFloat()
            }
        }
    }

    /** 仅给顶层卡片绑定触摸事件 */
    private fun bindTopGesture() {
        cardStack.forEachIndexed { index, view ->
            view.setOnTouchListener { v, event ->
                if (index == 0 && !isAnimating) {
                    gestureDetector.onTouchEvent(event)
                } else {
                    false
                }
            }
        }
    }

    /** ⭐ 飞出动画 + 循环回收 */
    private fun flyOut(card: View, toRight: Boolean) {
        isAnimating = true
        val targetX = if (toRight) width * 1.5f else -width * 1.5f

        card.animate()
            .translationX(targetX)
            .rotation(if (toRight) 30f else -30f)
            .alpha(0.8f)
            .setDuration(flyOutDuration)
            .setInterpolator(AccelerateInterpolator())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    // ★ 循环核心：从栈顶移除 → 重置 → 放回栈底
                    cardStack.removeFirst()
                    removeView(card)

                    card.apply {
                        translationX = 0f; translationY = 0f
                        rotation = 0f; alpha = 1f
                        scaleX = 1f; scaleY = 1f
                    }

                    // 通知外部重新绑定数据
                    onCardRecycled?.invoke(card)

                    // 加回栈底(视觉最下层)
                    addView(card, 0)
                    cardStack.addLast(card)

                    updateTransforms()
                    bindTopGesture()
                    isAnimating = false
                    isSwiping = false
                    card.setLayerType(View.LAYER_TYPE_NONE, null)

                    onCardSwiped?.invoke(toRight)
                }
            })
            .start()
    }

    /** 未达阈值时回弹 */
    private fun rewind(card: View) {
        isAnimating = true
        card.animate()
            .translationX(0f)
            .translationY(0f)
            .rotation(0f)
            .setDuration(rewindDuration)
            .setInterpolator(OvershootInterpolator(1.5f))
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    isAnimating = false
                    isSwiping = false
                    card.setLayerType(View.LAYER_TYPE_NONE, null)
                }
            })
            .start()
    }
}
