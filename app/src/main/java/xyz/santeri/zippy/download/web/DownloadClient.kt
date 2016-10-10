package xyz.santeri.zippy.download.web

import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import org.greenrobot.eventbus.EventBus
import org.jsoup.Jsoup
import xyz.santeri.zippy.download.DownloadUrlEvent
import xyz.santeri.zippy.download.ParseErrorEvent

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
            EventBus.getDefault().post(DownloadUrlEvent(document.select("a#dlbutton").first().attr("href"), url))
        } else {
            EventBus.getDefault().post(ParseErrorEvent())
        }
    }
}