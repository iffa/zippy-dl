package xyz.santeri.zippy.ui.download.web

import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import org.greenrobot.eventbus.EventBus
import org.jsoup.Jsoup
import xyz.santeri.zippy.model.DownloadInfo
import xyz.santeri.zippy.ui.download.DownloadUrlEvent
import xyz.santeri.zippy.ui.download.ParseErrorEvent

/**
 * @author Santeri Elo
 */
class DownloadClient : WebViewClient() {
    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)

        view?.loadUrl(String.format("javascript:window.dank.getHtml('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>', '%s');", url))
    }
}

class DownloadJsInterface {
    @JavascriptInterface fun getHtml(html: String, url: String) {
        // Just do the parsing here to keep HTML logic coupled
        val document = Jsoup.parse(html)

        if (document.select("a#dlbutton").size > 0) {
            val downloadUrl = document.select("a#dlbutton").first().attr("href")
            val title = document.select("div.left > font[style$='line-height:20px; font-size: 14px;']")
                    .first().text()

            EventBus.getDefault().post(DownloadUrlEvent(DownloadInfo(downloadUrl, title)))
        } else {
            EventBus.getDefault().post(ParseErrorEvent())
        }
    }
}