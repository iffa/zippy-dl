package xyz.santeri.zippy.ui.download

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import net.grandcentrix.thirtyinch.TiPresenter
import net.grandcentrix.thirtyinch.rx.RxTiPresenterSubscriptionHandler
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import timber.log.Timber
import xyz.santeri.zippy.App
import xyz.santeri.zippy.model.DownloadInfo
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author Santeri Elo
 */
@Singleton
class DownloadPresenter(context: Context, val url: String?) : TiPresenter<DownloadView>() {
    private val rxHelper: RxTiPresenterSubscriptionHandler = RxTiPresenterSubscriptionHandler(this)

    @Inject lateinit var downloadManager: DownloadManager

    var downloadInfo: DownloadInfo? = null

    init {
        App.get(context).component.inject(this)
    }

    @Subscribe
    fun onDownloadUrlEvent(event: DownloadUrlEvent) {
        downloadInfo = event.info
        view.showTitle(downloadInfo!!.title)
        view.showReady()
    }

    fun onPermissionGranted() {
        if (downloadInfo != null) downloadFile()
    }

    private fun downloadFile() {
        val realUrl = Uri.parse(url!!.split("/v/")[0] + downloadInfo!!.downloadUrl)
        val request = DownloadManager.Request(realUrl)

        request.setTitle(downloadInfo?.title)

        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, downloadInfo!!.title)

        downloadManager.enqueue(request)

        Timber.d("Started download, URL: '%s'", realUrl)

        view.showDownloading()
    }

    @Subscribe
    @SuppressWarnings("UnusedProperty")
    fun onParseErrorEvent(event: ParseErrorEvent) {
        Timber.e("Failed to parse download link from page")
        view.showError()
    }

    override fun onWakeUp() {
        super.onWakeUp()

        rxHelper.manageViewSubscription(view.onDownloadClicked().subscribe {
            Timber.d("Download button clicked")
            view.checkPermissions()
        })

        if (downloadInfo == null) {
            if (url == null) {
                Timber.w("Activity started without data, can't load download link")
                view.showInfo()
            } else {
                view.showParsing()
                view.parseWebsite(url)
            }
        }
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

class DownloadUrlEvent(val info: DownloadInfo)

class ParseErrorEvent()