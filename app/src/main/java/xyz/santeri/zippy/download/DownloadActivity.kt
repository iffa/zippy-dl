package xyz.santeri.zippy.download

import android.os.Bundle
import android.view.View
import com.jakewharton.rxbinding.view.RxView
import kotlinx.android.synthetic.main.activity_download.*
import net.grandcentrix.thirtyinch.TiActivity
import net.grandcentrix.thirtyinch.TiView
import net.grandcentrix.thirtyinch.callonmainthread.CallOnMainThread
import rx.Observable
import timber.log.Timber
import xyz.santeri.zippy.R
import xyz.santeri.zippy.download.web.DownloadClient
import xyz.santeri.zippy.download.web.DownloadJsInterface

/**
 * @author Santeri Elo
 */
class DownloadActivity : TiActivity<DownloadPresenter, DownloadView>(), DownloadView {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        overridePendingTransition(R.anim.right_in_fade, 0)

        setContentView(R.layout.activity_download)

        val uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
        window.decorView.systemUiVisibility = uiOptions

        if (intent.data == null) {
            Timber.e("Activity started without intent data")
        } else {
            prepareWebView(intent.data.toString())
        }
    }

    private fun prepareWebView(url: String) {
        webview.settings.javaScriptEnabled = true
        webview.settings.blockNetworkImage = true

        webview.setWebViewClient(DownloadClient())

        // Second argument must match the one set in DownloadClient
        webview.addJavascriptInterface(DownloadJsInterface(), "dank")

        /*
        webview.evaluateJavascript(
                "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();",
                { html ->

                })
                */

        webview.loadUrl(url)
    }

    override fun showReady() {
    }

    override fun showError() {
    }

    override fun showDownloading() {
    }

    override fun onDownloadClicked() = RxView.clicks(download)

    override fun providePresenter(): DownloadPresenter = DownloadPresenter(this)

    override fun finish() {
        super.finish()
        overridePendingTransition(0, R.anim.right_out_fade)
    }
}

interface DownloadView : TiView {
    fun onDownloadClicked(): Observable<Void>

    @CallOnMainThread
    fun showReady()

    @CallOnMainThread
    fun showError()

    @CallOnMainThread
    fun showDownloading()
}
