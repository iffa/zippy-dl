package xyz.santeri.zippy.ui.download.web

import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import org.greenrobot.eventbus.EventBus
import org.jsoup.Jsoup
import timber.log.Timber
import xyz.santeri.zippy.model.DownloadInfo
import xyz.santeri.zippy.ui.download.DownloadUrlEvent
import xyz.santeri.zippy.ui.download.ParseErrorEvent

/**
 * @author Santeri Elo
 */
class DownloadClient : WebViewClient() {
    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)

        view?.loadUrl("javascript:window.dank.getHtml('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');")
    }
}

class DownloadJsInterface {
    @JavascriptInterface fun getHtml(html: String) {
        // Just do the parsing here to keep HTML logic coupled
        val document = Jsoup.parse(html)

        if (document.select("a#dlbutton").size > 0) {
            val downloadUrl = document.select("a#dlbutton").first().attr("href")
            val title = downloadUrl.split("/")[4].replace("%20", " ")

            Timber.d("File title: '%s'", title)
            EventBus.getDefault().post(DownloadUrlEvent(DownloadInfo(downloadUrl, title)))
        } else {
            EventBus.getDefault().post(ParseErrorEvent())
        }
    }
}