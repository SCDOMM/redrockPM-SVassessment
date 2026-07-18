package com.example.ept.search

import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.ept.search.fragment.InitialFragment
import com.example.ept.search.fragment.resultcontent.ResultFragment
import com.example.ept.search.fragment.SuggestionFragment
import com.example.ept.search.viewmodel.SearchViewModel

class SearchActivity : AppCompatActivity() {
    private lateinit var viewModel: SearchViewModel
    private lateinit var svSearchTop: SearchView
    private lateinit var initialFragment: InitialFragment
    private lateinit var suggestionFragment: SuggestionFragment
    private lateinit var resultFragment: ResultFragment

    private lateinit var tvSearchCancel: TextView

    private var currentFragment: Fragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        viewModel= ViewModelProvider(this)[SearchViewModel::class.java]
        svSearchTop=findViewById(R.id.sv_search_top)

        tvSearchCancel=findViewById(R.id.tv_search_cancel)
        initEvent()
        initSearch()
    }
    fun initEvent(){
        initialFragment = InitialFragment()
        suggestionFragment = SuggestionFragment()
        resultFragment = ResultFragment()
        supportFragmentManager.beginTransaction()
            .add(R.id.fl_search_container, initialFragment, "initial")
            .add(R.id.fl_search_container, suggestionFragment, "suggestion")
            .add(R.id.fl_search_container, resultFragment, "result")
            .hide(suggestionFragment)   // 默认隐藏联想
            .hide(resultFragment)       // 默认隐藏结果
            .show(initialFragment)      // 默认显示初始面板
            .commit()
        currentFragment = initialFragment
    }


    fun initSearch(){

        svSearchTop.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(p0: String?): Boolean {
                if (p0?.isBlank() ?: true){
                    showInitial()
                }else{
                    showSuggestion()
                }
                return false
            }

            override fun onQueryTextSubmit(p0: String?): Boolean {
                if (p0?.isBlank() ?: true){
                    svSearchTop.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(svSearchTop.windowToken, 0)
                    return true
                }else{
                    svSearchTop.clearFocus()
                    showResult()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(svSearchTop.windowToken, 0)
                }
                return true
            }
        })
    }
    fun showInitial(){
        switchTo(initialFragment)
    }
    fun showSuggestion(){
        switchTo(suggestionFragment)
    }
    fun showResult(){
        switchTo(resultFragment)
    }
    private fun switchTo(target: Fragment) {
        if (target != currentFragment) {
            supportFragmentManager.beginTransaction()
                .hide(currentFragment!!)
                .show(target)
                .commitNow()
            currentFragment = target
        }
    }

}