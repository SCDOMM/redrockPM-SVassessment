package com.example.ept.person.pgc

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.example.ept.person.R
import com.example.ept.person.pgc.adapter.CreatorAdapter
import com.example.ept.person.pgc.fragment.AlbumFragment
import com.example.ept.person.pgc.fragment.FragmentInterface
import com.example.ept.person.pgc.fragment.MainPageFragment
import com.example.ept.person.pgc.fragment.WorkFragment
import com.example.ept.person.pgc.viewmodel.CreatorViewModel
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.therouter.router.Route

@Route(path = "http://therouter.com/person/pgc")
class CreatorActivity : AppCompatActivity() {
    private lateinit var aplCreatorDefault: AppBarLayout
   private lateinit var ivCreatorBackground: ImageView
    private lateinit var ivCreatorProfile: com.google.android.material.imageview.ShapeableImageView
    private lateinit var tvCreatorName: TextView
    private lateinit var tvCreatorDesc: TextView
    private lateinit var tvCreatorUnfold: TextView
    private lateinit var tvCreatorFans: TextView
    private lateinit var tvCreatorFollow: TextView
    private lateinit var tvCreatorBadge: TextView
    private lateinit var tvCreatorPopular: TextView
    private lateinit var btnCreatorAdd: Button
    private lateinit var srlCreatorDefault: SwipeRefreshLayout
    private lateinit var tlCreatorDefault: TabLayout
    private lateinit var creatorViewModel: CreatorViewModel
    private var fragmentList = mutableListOf<FragmentInterface>()
    private lateinit var vp2CreatorDefault: ViewPager2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_creator)
        aplCreatorDefault = findViewById(R.id.apl_creator_default)
        ivCreatorBackground = findViewById(R.id.iv_creator_background)
        ivCreatorProfile = findViewById(R.id.iv_creator_profile)
        tvCreatorName = findViewById(R.id.tv_creator_name)
        tvCreatorDesc = findViewById(R.id.tv_creator_desc)
        tvCreatorUnfold = findViewById(R.id.tv_creator_unfold)
        tvCreatorFans = findViewById(R.id.tv_creator_fans)
        tvCreatorFollow = findViewById(R.id.tv_creator_follow)
        tvCreatorBadge = findViewById(R.id.tv_creator_badge)
        tvCreatorPopular = findViewById(R.id.tv_creator_popular)
        btnCreatorAdd = findViewById(R.id.btn_creator_add)
        srlCreatorDefault = findViewById(R.id.srl_creator_default)
        vp2CreatorDefault = findViewById(R.id.vp2_creator_default)
        tlCreatorDefault=findViewById(R.id.tl_creator_default)
        creatorViewModel= ViewModelProvider(this)[CreatorViewModel::class.java]
        initEvent()
        initTabs()
        initRefresh()
    }
    fun initEvent(){
        fragmentList.add(object : FragmentInterface {
            override fun back(): Fragment {
                return MainPageFragment()
            }
        })
        fragmentList.add(object : FragmentInterface {
            override fun back(): Fragment {
                return WorkFragment()
            }
        })
        fragmentList.add(object : FragmentInterface {
            override fun back(): Fragment {
                return AlbumFragment()
            }
        })
        val adapter = CreatorAdapter(fragmentList, this)
        vp2CreatorDefault.adapter = adapter

    }
    fun initTabs() {
        TabLayoutMediator(tlCreatorDefault, vp2CreatorDefault) { p0, p1 ->
            when (p1) {
                0 -> {
                    p0.text = "主页"
                }

                1 -> {
                    p0.text = "作品"
                }

                2 -> {
                    p0.text = "专辑"
                }
            }
        }.attach()
    }
    fun initRefresh(){
        aplCreatorDefault.addOnOffsetChangedListener { layout, i ->
            val isExpanded = i == 0
            // 完全展开时允许刷新，否则禁用刷新
            srlCreatorDefault.isEnabled = isExpanded
        }
        srlCreatorDefault.setOnRefreshListener {
            srlCreatorDefault.isRefreshing=false
        }







    }


}