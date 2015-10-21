package com.qhad.ads.sdk.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Browser;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.qhad.ads.sdk.adsinterfaces.IQhAdEventListener;
import com.qhad.ads.sdk.core.AD_TYPE;
import com.qhad.ads.sdk.core.QhAdView;
import com.qhad.ads.sdk.core.QhMraidInterface;
import com.qhad.ads.sdk.logs.QHADLog;
import com.qhad.ads.sdk.logs.QhAdErrorCode;
import com.qhad.ads.sdk.model.QhAdModel;
import com.qhad.ads.sdk.res.ResourceType;
import com.qhad.ads.sdk.res.TrackType;
import com.qhad.ads.sdk.task.ClickTask;
import com.qhad.ads.sdk.task.DownloadeTask;
import com.qhad.ads.sdk.task.TrackRunable;
import com.qhad.ads.sdk.utils.AdCounter;
import com.qhad.ads.sdk.utils.Utils;
import com.qhad.ads.sdk.vo.CommonAdVO;

import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Duan
 */
@SuppressLint("SetJavaScriptEnabled")
public class AdWebView extends WebView {

    public static int count = 0;
    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            QhAdView adView = (QhAdView) msg.obj;
            adView.closeAds();
        }

    };
    private final String JavaScriptInterfaceName = "mvad";
    private final Handler uiHandler;
    private AdWebView webview = null;
    private CommonAdVO vo = null;
    private QhMraidInterface mraid = null;
    private DownloadeTask downloader = null;
    private Context context = null;
    private IQhAdEventListener adEventListener = null;
    private QhAdView adView = null;
    private ClickTask clickTask;
    private QhAdView.Listener listener = null;
    private Boolean isInitMraid = false;
    private WebViewClient webViewclient = new WebViewClient() {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (isInitMraid) {
                if (vo.adm_type == ResourceType.DSP_HTML5) {
                    try {
                        if (adEventListener != null) {
                            adEventListener.onAdviewClicked();
                        }
                        QhAdModel.getInstance().getTrackManager().RegisterTrack(vo, TrackType.CLICK_AD);

                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        Uri content_url = Uri.parse(url);
                        intent.setData(content_url);
                        context.startActivity(intent);

                        if (adEventListener != null) {
                            adEventListener.onAdviewIntoLandpage();
                        }
                    } catch (Exception e) {
                        QHADLog.e(QhAdErrorCode.CLICK_SYSTEMBROSWER_ERROR, "DSP HTML Landing:" + url, e, vo);
                    }

                    return true;
                }
            }

            return super.shouldOverrideUrlLoading(view, url);
        }

        @SuppressWarnings("deprecation")
        @Override
        public android.webkit.WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            if (url.contains("tt/Post")) {
                QHADLog.i(count + "");
                AdWebView.count++;
            }
            return null;
        }

        public void onPageStarted(WebView webView, String url, android.graphics.Bitmap favicon) {
            if (isInitMraid) {
                if (vo.adm_type == ResourceType.DSP_HTML5) {
                    try {
                        webView.stopLoading();

                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        Uri content_url = Uri.parse(url);
                        intent.setData(content_url);
                        context.startActivity(intent);

                        if (adEventListener != null) {
                            adEventListener.onAdviewIntoLandpage();
                        }
                    } catch (Exception e) {
                        QHADLog.e(QhAdErrorCode.CLICK_SYSTEMBROSWER_ERROR, "DSP HTML Landing:" + url, e, vo);
                    }
                }
            }
        }

        public void onLoadResource(WebView view, String url) {

        }

        public void onPageFinished(WebView webView, String url) {
            if (vo.adm_type == ResourceType.DSP_HTML5) {
                webView.loadUrl("javascript:document.body.style.margin=0");
            }
            if (!isInitMraid) {
                webview.setVisibility(View.VISIBLE);
                isInitMraid = true;
            }

            if (listener != null)
                listener.onRenderedSucceed();

            if (adEventListener != null)
                adEventListener.onAdviewRendered();
        }

    };
    private WebChromeClient webChromeClient = new WebChromeClient() {

        public void onProgressChanged(WebView view, int newProgress) {

        }

    };


    @Deprecated
    public AdWebView(Context context, Handler uiHandler) {
        super(context);
        this.uiHandler = uiHandler;
    }

    /**
     * @param uiHandler
     */
    @SuppressLint("NewApi")
    public AdWebView(Context _context, String adw, String adh, AD_TYPE adtype, Handler uiHandler) {
        super(_context);
        context = _context;
        this.uiHandler = uiHandler;
        webview = this;
        mraid = new QhMraidInterface(adw, adh, adtype) {

            @Override
            @TargetApi(11)
            @JavascriptInterface
            public void mraidMiddlewareDone() {
                try {
                    int p_width = webview.getWidth();
                    int p_height = webview.getHeight();
                    int p_x = 0;
                    int p_y = 0;
                    if (Build.VERSION.SDK_INT >= 11) {
                        p_x = (int) webview.getX();
                        p_y = (int) webview.getY();
                    }

                    int offx = 0;
                    int offy = 0;
                    final int[] wh = Utils.getDeviceScreenSizeWithInt();
                    final String param = p_width + "," +
                            p_height + "," +
                            p_x + "," +
                            p_y + "," +
                            offx + "," +
                            offy + "," +
                            wh[0] + "," +
                            wh[1];

                    webview.post(new Runnable() {
                        @Override
                        public void run() {
                            webview.loadUrl("javascript:window.mraid.bridgeInit(" + param + ")");
                            webview.loadUrl("javascript:window.mraid.deviceInit(\"android\")");
                        }
                    });

                } catch (Exception e) {
                    QHADLog.e(QhAdErrorCode.AD_SHOW_ERROR, "MRAID Interface init failed", e);
                }

            }

            @Override
            @JavascriptInterface
            public Boolean close() {
                if (adEventListener != null) {
                    adEventListener.onAdviewClosed();
                }

                Message msg = new Message();
                msg.obj = adView;
                AdWebView.gethandler().sendMessage(msg);
                return true;
            }

            @Override
            @JavascriptInterface
            public Boolean open(String url) {
                if (url == null) {
                    return false;
                }

                if (adEventListener != null) {
                    adEventListener.onAdviewClicked();
                }

                QHADLog.d("Open url " + url);
                QhAdModel.getInstance().getTrackManager().RegisterTrack(vo, TrackType.CLICK_AD);
                try {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(url);
                    intent.setData(content_url);
                    context.startActivity(intent);

                    if (adEventListener != null) {
                        adEventListener.onAdviewIntoLandpage();
                    }
                } catch (Exception e) {
                    QHADLog.e(QhAdErrorCode.CLICK_MRAID_CALL_ERROR, "MRAID Open URL:" + url, e, vo);
                }

				/*Inner Open
                if (landingPage == null) {
					landingPage = new LandingPage(context);
				}
				landingPage.showLanding(url, adEventListener, adView);

				if (adEventListener != null) {
					adEventListener.onAdviewIntoLandpage();
				}
				*/
                return true;
            }

            @Override
            @JavascriptInterface
            public Boolean deeplink(String urljson) {
                String url = null;
                try {
                    JSONObject jsonObject = new JSONObject(urljson);
                    String deeplink = jsonObject.getString("deep_link");
                    JSONArray jsonArray = jsonObject.getJSONArray("deep_link_click_tk");
                    url = jsonObject.getString("landing_page");
                    ArrayList<String> click_tk = new ArrayList<String>();
                    int i = 0;
                    int len = jsonArray.length();
                    for (i = 0; i < len; i++) {
                        QHADLog.e(jsonArray.getString(i));
                        click_tk.add(jsonArray.getString(i));
                    }

                    if (tryOpenDeepLink(deeplink, click_tk))
                        return true;

                } catch (JSONException e) {
                    QHADLog.e(QhAdErrorCode.AD_JSON_PARSE_ERROR, "AD JSON Parse Error:", e);
                }

                if (url == null) {
                    return false;
                }

                if (adEventListener != null) {
                    adEventListener.onAdviewClicked();
                }

                QHADLog.d("Handle url " + url);
                vo.ld = url;
                if (clickTask == null) {
                    clickTask = new ClickTask(vo, null, null, context);
                }
                clickTask.onClick(null, null);

                return true;
            }

            private boolean tryOpenDeepLink(String deeplink, List click_tk) {
                QHADLog.d("try open deep link");
                if (deeplink == null || deeplink.equals("")) {
                    return false;
                }
                try {
                    AdCounter.increment(AdCounter.ACTION_DEEP_LINK_OPEN_START);
                    Intent intent = Intent.parseUri(deeplink, Intent.URI_INTENT_SCHEME);
                    boolean canProcess = context.getPackageManager().queryIntentActivities(intent,
                            PackageManager.GET_ACTIVITIES).size() > 0;
                    if (!canProcess)
                        return false;
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setComponent(null);
                    intent.putExtra(Browser.EXTRA_APPLICATION_ID,
                            context.getPackageName());
                    context.startActivity(intent);
                    AdCounter.increment(AdCounter.ACTION_SDK_TRACK_CLICK);
                    new Thread(new TrackRunable(click_tk, TrackType.CLICK_AD)).start();
                    AdCounter.increment(AdCounter.ACTION_DEEP_LINK_OPEN_SUCCESS);
                    QHADLog.d("deep link open");
                    return true;
                } catch (Throwable e) {
                    AdCounter.increment(AdCounter.ACTION_DEEP_LINK_OPEN_FAILED);
                    QHADLog.e(QhAdErrorCode.CLICK_DEEP_LINK_ERROR, "deep link open error.", e, vo);
                    return false;
                }
            }


            @Override
            @JavascriptInterface
            public Boolean appDownload(String url, String pn) {
                if (downloader == null) {
                    downloader = new DownloadeTask(AdWebView.this.uiHandler);
                }

                if (adEventListener != null) {
                    adEventListener.onAdviewClicked();
                }

                if (!downloader.isDownloading()) {
                    CommonAdVO vo = new CommonAdVO();
                    vo.ld = url;
                    vo.pkg = pn;
                    downloader.downloadApp(vo, context);
                } else {
                    QHADLog.d("下载应用:已经在下载");
                }
                return true;
            }

            @Override
            @JavascriptInterface
            public Boolean callTelephone(String callnum) {
                if (adEventListener != null) {
                    adEventListener.onAdviewClicked();
                }

                try {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + callnum));
                    context.startActivity(intent);
                    return true;
                } catch (Exception e) {
                    QHADLog.e(QhAdErrorCode.CLICK_MRAID_CALL_ERROR, "MRAID callTelephone:" + callnum, e, vo);
                    return false;
                }
            }

            @Override
            @JavascriptInterface
            public Boolean sendSMS(String callnum, String content) {
                try {
                    if (adEventListener != null) {
                        adEventListener.onAdviewClicked();
                    }

                    Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                    sendIntent.setData(Uri.parse("smsto:" + callnum));
                    sendIntent.putExtra("sms_body", content);
                    context.startActivity(sendIntent);
                    return true;
                } catch (Exception e) {
                    QHADLog.e(QhAdErrorCode.CLICK_MRAID_CALL_ERROR, "MRAID sendSMS:" + callnum + " Content:" + content, e, vo);
                    return false;
                }
            }

            @Override
            @JavascriptInterface
            public Boolean sendMail(String emailadd, String title, String content) {
                try {
                    if (adEventListener != null) {
                        adEventListener.onAdviewClicked();
                    }

                    Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                    sendIntent.setData(Uri.parse("mailto:" + emailadd));
                    sendIntent.putExtra(Intent.EXTRA_SUBJECT, title);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, content);
                    context.startActivity(sendIntent);
                    return true;
                } catch (Exception e) {
                    QHADLog.e(QhAdErrorCode.CLICK_MRAID_CALL_ERROR, "MRAID sendMail:" + emailadd + " Title:" + title, e, vo);
                    return false;
                }
            }
        };
        this.setBackgroundColor(Color.TRANSPARENT);
        this.setVisibility(View.INVISIBLE);
        this.getSettings().setJavaScriptEnabled(true);
        this.getSettings().setDefaultTextEncodingName(HTTP.UTF_8);
        this.setHorizontalScrollBarEnabled(false);
        this.setVerticalScrollBarEnabled(false);
        this.setWebViewClient(webViewclient);
        this.setWebChromeClient(webChromeClient);
        this.addJavascriptInterface(mraid, JavaScriptInterfaceName);
    }

    private static Handler gethandler() {
        return handler;
    }

    public void showAD(CommonAdVO vo, IQhAdEventListener _adEventListener, QhAdView _adView, String hash, QhAdView.Listener listener) {
        this.vo = vo;
        this.adEventListener = _adEventListener;
        this.adView = _adView;
        this.listener = listener;
        this.clearCache(true);
        this.loadDataWithBaseURL(null, vo.html, "text/html", HTTP.UTF_8, null);

//	    try {
//			InputStream is = context.getAssets().open("hh");
//			int size = is.available();
//
//			byte[] buffer = new byte[size];
//			is.read(buffer);
//			is.close();
//
//			String text = new String(buffer, "UTF-8");
//			this.loadDataWithBaseURL(null, text, "text/html", HTTP.UTF_8, null);
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
    }
}

class OriginalRunable implements Runnable {

    private QhAdView adView = null;

    public OriginalRunable(QhAdView _adView) {
        adView = _adView;
    }

    @Override
    public void run() {
        adView.closeAds();
    }
}