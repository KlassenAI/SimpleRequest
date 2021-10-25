package com.example.simplerequest.main.view

import android.os.Bundle
import android.view.Menu
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.viewpager2.widget.ViewPager2
import com.example.simplerequest.R
import com.example.simplerequest.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener, IFragmentListener {

    companion object {
        private val tabArticles = arrayListOf("MVP", "MVVM", "MVI")
    }

    private lateinit var binding: ActivityMainBinding
    private var searchFragments: ArrayList<ISearch> = arrayListOf()
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            viewPager.adapter = FragmentAdapter(supportFragmentManager, lifecycle)

            createTabs(tabArticles)

            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    viewPager.currentItem = tab?.position!!
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}

                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })

            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    tabLayout.selectTab(tabLayout.getTabAt(position))
                }
            })
        }
    }

    private fun ActivityMainBinding.createTabs(texts: ArrayList<String>) {
        for(text in texts) tabLayout.addTab(tabLayout.newTab().setText(text))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)

        val search = menu?.findItem(R.id.menu_search)
        searchView = (search?.actionView as? SearchView)!!
        searchView.isSubmitButtonEnabled = true
        searchView.setOnQueryTextListener(this)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        for (searchFragment in searchFragments) {
            searchFragment.onTextQuery(query)
        }
        searchView.clearFocus()
        return true
    }

    override fun onQueryTextChange(newText: String): Boolean {
        return true
    }

    override fun addiSearch(iSearch: ISearch) {
        searchFragments.add(iSearch)
    }

    override fun removeISearch(iSearch: ISearch) {
        searchFragments.remove(iSearch)
    }
}