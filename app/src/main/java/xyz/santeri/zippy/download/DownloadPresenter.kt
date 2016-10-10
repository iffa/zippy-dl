package xyz.santeri.zippy.download

import android.app.DownloadManager
import android.content.Context
import net.grandcentrix.thirtyinch.TiPresenter
import net.grandcentrix.thirtyinch.rx.RxTiPresenterSubscriptionHandler
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import timber.log.Timber
import xyz.santeri.zippy.App
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author Santeri Elo
 */
@Singleton
class DownloadPresenter constructor(context: Context) : TiPresenter<DownloadView>() {
    private val rxHelper: RxTiPresenterSubscriptionHandler = RxTiPresenterSubscriptionHandler(this)

    @Inject lateinit var downloadManager: DownloadManager

    lateinit var downloadUrl: String

    init {
        App.get(context).component.inject(this)
    }

    @Subscribe
    fun onDownloadUrlEvent(event: DownloadUrlEvent) {
        Timber.d("Successfully parsed download link: '%s'", event.downloadUrl)

        downloadUrl = event.downloadUrl
    }

    @Subscribe
    fun onParseErrorEvent(event: ParseErrorEvent) {
        Timber.e("Failed to parse download link from page")
    }

    override fun onWakeUp() {
        super.onWakeUp()

        rxHelper.manageViewSubscription(view.onDownloadClicked().subscribe {
            Timber.d("Download button clicked")
        })
    }

    override fun onCreate() {
        super.onCreate()

        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        EventBus.getDefault().unregister(this)
    }
}

class DownloadUrlEvent(val downloadUrl: String, val url: String)

class ParseErrorEvent()