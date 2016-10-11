package xyz.santeri.zippy.ui.download

import android.Manifest
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.github.jksiezni.permissive.Permissive
import com.google.android.gms.ads.AdRequest
import com.jakewharton.rxbinding.view.RxView
import kotlinx.android.synthetic.main.activity_download.*
import net.grandcentrix.thirtyinch.TiActivity
import net.grandcentrix.thirtyinch.TiView
import net.grandcentrix.thirtyinch.callonmainthread.CallOnMainThread
import rx.Observable
import timber.log.Timber
import xyz.santeri.zippy.R
import xyz.santeri.zippy.ui.download.web.DownloadClient
import xyz.santeri.zippy.ui.download.web.DownloadJsInterface

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

        loadAds()
    }

    private fun loadAds() {
        val adRequest = AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("84275ACA55FEE01D25BACD7DC1A42F7A")
                .build()

        adView.loadAd(adRequest)
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

    override fun showInfo() {
        fileTitle.setText(R.string.info)
    }

    override fun checkPermissions() {
        Permissive.Request(Manifest.permission.WRITE_EXTERNAL_STORAGE).whenPermissionsGranted {
            presenter.onPermissionGranted()
        }.whenPermissionsRefused {
            Timber.e("Permission was refused - can't download")
        }.execute(this)
    }

    override fun showReady() {
        download.isEnabled = true

        Toast.makeText(this, R.string.download_ready, Toast.LENGTH_LONG).show()
    }

    override fun showError() {
        fileTitle.setText(R.string.error_parse)

        Toast.makeText(this, R.string.error_parse, Toast.LENGTH_LONG).show()
    }

    override fun showTitle(title: String) {
        fileTitle.text = title
    }

    override fun showDownloading() {
        fileTitle.setText(R.string.downloading)
        Toast.makeText(this, R.string.downloading, Toast.LENGTH_LONG).show()
    }

    override fun showParsing() {
        fileTitle.setText(R.string.parsing)
        Toast.makeText(this, R.string.parsing, Toast.LENGTH_LONG).show()
    }

    override fun parseWebsite(url: String) {
        prepareWebView(url)
    }

    override fun onDownloadClicked() = RxView.clicks(download)

    override fun providePresenter(): DownloadPresenter {
        return DownloadPresenter(this, intent.data?.toString())
    }

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

    @CallOnMainThread
    fun showParsing()

    @CallOnMainThread
    fun showTitle(title: String)

    @CallOnMainThread
    fun parseWebsite(url: String)

    @CallOnMainThread
    fun checkPermissions()

    @CallOnMainThread
    fun showInfo()
}
