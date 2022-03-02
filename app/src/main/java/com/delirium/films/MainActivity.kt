package com.delirium.films

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.delirium.films.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var appBarNavController: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bindingMain = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingMain.root)
        supportActionBar?.hide()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.myNavHostFragment) as NavHostFragment

        val navController = navHostFragment.navController
        appBarNavController = AppBarConfiguration(navController.graph)
        bindingMain.toolBar.setupWithNavController(navController, appBarNavController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.myNavHostFragment)

        return navController.navigateUp(appBarNavController)
                || super.onSupportNavigateUp()
    }
}