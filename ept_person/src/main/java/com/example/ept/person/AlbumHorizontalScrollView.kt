package com.example.ept.person

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.HorizontalScrollView
import kotlin.math.abs

/**
 * 包名称： com.example.ept.person
 * 类名称：TouchUtils
 * 类描述：用来解决专辑的滑动冲突问题
 * 创建人：韦西波
 * 创建时间：2026-07-21 19:34
 *
 */
class AlbumHorizontalScrollView(context: Context, attr: AttributeSet) :
    HorizontalScrollView(context, attr) {
    private var startX = 0f
    private var isBeingDragged = false
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev == null) return super.dispatchTouchEvent(ev)
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                startX = ev.x
                isBeingDragged = false
                parent?.requestDisallowInterceptTouchEvent(false)
                return super.dispatchTouchEvent(ev)
            }
            MotionEvent.ACTION_MOVE -> {
                val currentX = ev.x
                val dx = currentX - startX
                if (abs(dx) > touchSlop && !isBeingDragged) {
                    isBeingDragged = true
                    if (dx > 0) {
                        //右滑
                        if (!canScrollHorizontally(-1)) {
                            parent?.requestDisallowInterceptTouchEvent(false)
                            return false
                        } else {
                            parent?.requestDisallowInterceptTouchEvent(true)
                        }
                    } else {
                        // 向左滑
                        if (!canScrollHorizontally(1)) {
                            parent?.requestDisallowInterceptTouchEvent(false)
                            return false
                        } else {
                            parent?.requestDisallowInterceptTouchEvent(true)
                        }
                    }
                }
                return super.dispatchTouchEvent(ev)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                parent?.requestDisallowInterceptTouchEvent(false)
                isBeingDragged = false
                startX = 0f
                return super.dispatchTouchEvent(ev)
            }
            else -> return super.dispatchTouchEvent(ev)
        }
    }
}
