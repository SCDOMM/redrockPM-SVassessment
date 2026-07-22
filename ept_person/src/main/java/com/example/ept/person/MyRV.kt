package com.example.ept.person

/**   
* 包名称： com.example.ept.person
* 类名称：MyRV
* 类描述：TODO
* 创建人：韦西波
* 创建时间：2026-07-22 12:01
*
*/
import android.content.Context
import android.util.AttributeSet
import android.view.ViewParent
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.NestedScrollingParent2
import androidx.recyclerview.widget.RecyclerView

/**
 * 绕过 ViewPager2 内部 RecyclerView 的嵌套滚动拦截，
 * 直接与 CoordinatorLayout 建立嵌套滚动连接。
 *
 * 原理：不再通过标准父链寻找 NestedScrollingParent（这样会被 VP2 内部 RV 拦截），
 * 而是直接调用 CoordinatorLayout 实现的 NestedScrollingParent2 接口方法。
 */
//class VP2NestedRecyclerView @JvmOverloads constructor(
//    context: Context,
//    attrs: AttributeSet? = null,
//    defStyleAttr: Int = 0
//) : RecyclerView(context, attrs, defStyleAttr) {
//
//    private var coordinatorLayout: CoordinatorLayout? = null
//    private var isDirectNestedScrollActive = false
//
//    init {
//        isNestedScrollingEnabled = true
//    }
//
//    override fun onAttachedToWindow() {
//        super.onAttachedToWindow()
//        findCoordinatorLayout()
//    }
//
//    private fun findCoordinatorLayout() {
//        var p: ViewParent? = parent
//        while (p != null) {
//            if (p is CoordinatorLayout) {
//                coordinatorLayout = p
//                break
//            }
//            p = p.parent
//        }
//    }
//
//    // ===== 完全接管嵌套滚动，跳过 VP2 内部 RV =====
//
//    override fun startNestedScroll(axes: Int, type: Int): Boolean {
//        val cl = coordinatorLayout
//        if (cl != null) {
//            // CoordinatorLayout 实现了 NestedScrollingParent2 接口
//            // 直接调用其接口方法，跳过 VP2 内部 RecyclerView
//            val started = (cl as NestedScrollingParent2).onStartNestedScroll(this, this, axes, type)
//            if (started) {
//                (cl as NestedScrollingParent2).onNestedScrollAccepted(this, this, axes, type)
//                isDirectNestedScrollActive = true
//                return true
//            }
//        }
//        // fallback: 让标准父链处理
//        return super.startNestedScroll(axes, type)
//    }
//
//    override fun dispatchNestedPreScroll(
//        dx: Int, dy: Int,
//        consumed: IntArray?,
//        offsetInWindow: IntArray?,
//        type: Int
//    ): Boolean {
//        if (isDirectNestedScrollActive && coordinatorLayout != null) {
//            val cl = coordinatorLayout!!
//
//            // 记录偏移前位置用于计算 offsetInWindow
//            val preLoc = if (offsetInWindow != null && offsetInWindow.size >= 2) {
//                IntArray(2).also { getLocationInWindow(it) }
//            } else null
//
//            // 创建 consumed 数组（如果传入为 null）
//            val innerConsumed = consumed ?: IntArray(2)
//
//            // ★ 直接调用 CoordinatorLayout 的 NestedScrollingParent2.onNestedPreScroll
//            // 这会触发 AppBarLayout.ScrollingViewBehavior 处理
//            (cl as NestedScrollingParent2).onNestedPreScroll(this, dx, dy, innerConsumed, type)
//
//            // 计算 offsetInWindow
//            if (preLoc != null && offsetInWindow != null) {
//                val curLoc = IntArray(2).also { getLocationInWindow(it) }
//                offsetInWindow[0] = curLoc[0] - preLoc[0]
//                offsetInWindow[1] = curLoc[1] - preLoc[1]
//            }
//
//            return innerConsumed[0] != 0 || innerConsumed[1] != 0
//        }
//        return super.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type)
//    }
//
//    override fun dispatchNestedScroll(
//        dxConsumed: Int, dyConsumed: Int,
//        dxUnconsumed: Int, dyUnconsumed: Int,
//        offsetInWindow: IntArray?
//    ): Boolean {
//        if (isDirectNestedScrollActive && coordinatorLayout != null) {
//            val cl = coordinatorLayout!!
//
//            val preLoc = if (offsetInWindow != null && offsetInWindow.size >= 2) {
//                IntArray(2).also { getLocationInWindow(it) }
//            } else null
//
//            // 使用 TYPE_TOUCH 调用 NestedScrollingParent2 的 onNestedScroll
//            (cl as NestedScrollingParent2).onNestedScroll(
//                this, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
//                android.view.ViewCompat.TYPE_TOUCH
//            )
//
//            if (preLoc != null && offsetInWindow != null) {
//                val curLoc = IntArray(2).also { getLocationInWindow(it) }
//                offsetInWindow[0] = curLoc[0] - preLoc[0]
//                offsetInWindow[1] = curLoc[1] - preLoc[1]
//            }
//
//            return true
//        }
//        return super.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow)
//    }
//
//    override fun stopNestedScroll(type: Int) {
//        if (isDirectNestedScrollActive && coordinatorLayout != null) {
//            (coordinatorLayout as NestedScrollingParent2).onStopNestedScroll(this, type)
//            isDirectNestedScrollActive = false
//            return
//        }
//        super.stopNestedScroll(type)
//    }
//
//    override fun hasNestedScrollingParent(type: Int): Boolean {
//        return isDirectNestedScrollActive || super.hasNestedScrollingParent(type)
//    }
//}