package com.example.ept.dicover.discovery

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import kotlin.math.abs

/**
 * description ： 话题广场卡片堆叠自定义 View
 * email : 3014386984@qq.com
 * date : 2026/7/22  13:23
 */
class CardStackView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    // ========== 配置参数 ==========
    var maxVisibleCards = 3           // 可见卡片层数
    var cardScaleStep = 0.06f         // 每层缩放差值（增大让底层卡片更明显）
    var cardTranslationYStep = 40f    // 每层Y轴偏移(px)（增大让底层卡片从底部露出更多）
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
    private  var gestureDetector: GestureDetector
    private var downX = 0f
    private var downY = 0f
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop

    /** 顶层卡片的触摸监听器，只在 start() 中绑定一次，避免每张卡都创建匿名对象 */
    private val topCardTouchListener = OnTouchListener { v, event ->
        if (v == cardStack.firstOrNull() && !isAnimating) {
            gestureDetector.onTouchEvent(event)
        } else false
    }

    init {
        clipChildren = false
        clipToPadding = false

        gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent): Boolean = !isAnimating && cardStack.isNotEmpty()

            /** 轻触 → 触发顶层卡片的 OnClickListener 进入话题详情 */
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                if (isAnimating || cardStack.isEmpty()) return false
                return cardStack.first().performClick()
            }

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
                    topCard.setLayerType(LAYER_TYPE_HARDWARE, null)
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
        // 只给顶层一张卡片绑监听器，避免每次 recycle 重建
        cardStack.firstOrNull()?.setOnTouchListener(topCardTouchListener)
    }


    // ========== 核心逻辑 ==========

    private fun fillStack() {
        while (cardStack.size < maxVisibleCards) {
            val card = cardProvider?.invoke() ?: break
            addView(card, 0) // index=0 → 视觉最底层
            cardStack.addLast(card)
        }
        updateTransforms()
    }

    /** 更新所有可见卡片的缩放和位移 */
    private fun updateTransforms() {
        val visibleCount = minOf(cardStack.size, maxVisibleCards)
        // 容器底部留出空间给堆叠卡片露出
        val stackPadding = ((visibleCount - 1) * cardTranslationYStep + 30).toInt()
        setPadding(paddingLeft, paddingTop, paddingRight, stackPadding)

        cardStack.forEachIndexed { index, view ->
            if (index >= maxVisibleCards) {
                view.visibility = GONE
                return@forEachIndexed
            }
            // 所有卡片从 0.92 基准开始缩小，顶层不填满容器
            val stackIndex = minOf(index, visibleCount - 1)
            val baseScale = 0.92f
            val scale = (baseScale - stackIndex * cardScaleStep).coerceAtLeast(0.80f)
            view.apply {
                scaleX = scale
                scaleY = scale
                translationY = stackIndex * cardTranslationYStep
                translationX = 0f
                rotation = 0f
                alpha = 1f
                visibility = if (index < maxVisibleCards) VISIBLE else GONE
                elevation = (maxVisibleCards - index).toFloat()
            }
        }
    }

    /** View 从窗口移除时取消所有动画，防止 detached 后动画回调崩溃 */
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        cardStack.forEach { it.animate().cancel() }
        isAnimating = false
        isSwiping = false
    }

    /** 附加到窗口时遍历父视图层级，关闭裁剪，防止卡片拖动时被边界截断 */
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        var p = parent
        while (p is ViewGroup) {
            if (p.clipChildren) {
                p.clipChildren = false
            }
            p = p.parent
        }
    }

    /** 飞出动画 + 循环回收 */
    private fun flyOut(card: View, toRight: Boolean) {
        isAnimating = true
        // 统一在此启用硬件加速，swipeTop() 编程式调用也能覆盖
        card.setLayerType(LAYER_TYPE_HARDWARE, null)
        val targetX = if (toRight) width * 1.5f else -width * 1.5f

        card.animate()
            .translationX(targetX)
            .rotation(if (toRight) 30f else -30f)
            .alpha(0.8f)
            .setDuration(flyOutDuration)
            .setInterpolator(AccelerateInterpolator())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    try {
                        // ★ 循环核心：从栈顶移除 → 重置 → 放回栈底
                        cardStack.removeFirst()
                        removeView(card)

                        card.apply {
                            translationX = 0f; translationY = 0f
                            rotation = 0f; alpha = 1f
                            scaleX = 1f; scaleY = 1f
                        }

                        onCardRecycled?.invoke(card)

                        addView(card, 0)
                        cardStack.addLast(card)

                        updateTransforms()
                        // 给新的顶层卡片绑监听器
                        cardStack.firstOrNull()?.setOnTouchListener(topCardTouchListener)
                        onCardSwiped?.invoke(toRight)
                    } finally {
                        isAnimating = false
                        isSwiping = false
                        card.setLayerType(LAYER_TYPE_NONE, null)
                    }
                }
            })
            .start()
    }

    /** 未达阈值时回弹 */
    private fun rewind(card: View) {
        isAnimating = true
        card.setLayerType(LAYER_TYPE_HARDWARE, null)
        card.animate()
            .translationX(0f)
            .translationY(0f)
            .rotation(0f)
            .setDuration(rewindDuration)
            .setInterpolator(OvershootInterpolator(1.5f))
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    try {
                        // 动画结束后不需要额外操作
                    } finally {
                        isAnimating = false
                        isSwiping = false
                        card.setLayerType(LAYER_TYPE_NONE, null)
                    }
                }
            })
            .start()
    }
}
