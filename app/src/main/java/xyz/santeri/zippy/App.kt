package xyz.santeri.zippy

import android.app.Application
import android.content.Context
import timber.log.Timber

/**
 * @author Santeri Elo
 */
class App : Application() {
    lateinit var component: AppComponent

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        component = DaggerAppComponent.builder().appModule(AppModule(this)).build()
    }

    companion object {
        fun get(context: Context): App = (context.applicationContext as App)
    }
}