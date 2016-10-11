package xyz.santeri.zippy

import android.app.Application
import android.app.DownloadManager
import android.content.Context
import dagger.Component
import dagger.Module
import dagger.Provides
import xyz.santeri.zippy.ui.download.DownloadPresenter
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Since this app is simple as fuck, we can keep all dependency injection stuff in the same file
 *
 * @author Santeri Elo
 */
@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent {
    fun inject(presenter: DownloadPresenter)
}

@Module
class AppModule constructor(val app: Application) {
    @Provides @Singleton fun provideApplication(): Application = app

    @Provides @Singleton @AppContext fun provideContext(): Context = app

    @Provides @Singleton fun provideDownloadManager(@AppContext context: Context): DownloadManager
            = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class AppContext