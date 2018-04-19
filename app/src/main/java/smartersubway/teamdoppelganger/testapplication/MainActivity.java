package smartersubway.teamdoppelganger.testapplication;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.HttpCookie;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo("smartersubway.teamdoppelganger.testapplication", PackageManager.GET_SIGNATURES);
        }catch (Exception e){

        }


        if (packageInfo != null) {

            for (Signature signature : packageInfo.signatures) {
                try {
                    MessageDigest md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    System.out.println("parkTest = " + Base64.encodeToString(md.digest(), Base64.NO_WRAP));
                } catch (NoSuchAlgorithmException e) {
                    Log.w("dd", "Unable to get MessageDigest. signature=" + signature, e);
                }
            }

        }


        WebView webView = (WebView) findViewById(R.id.comicoWebView);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        // 2.3 갤럭시 s에서 스크롤 때문에 웹페이지가 안좋게 보여서 추가 적용 함
        webView.setVerticalScrollBarEnabled(false);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }


        String url = "https://alpha-booking.payco.com/beauty/map?latitude=37.551865&longitude=127.089672&targetType=self&channel=subway";
        String cookieString = CookieManager.getInstance().getCookie(url);
        if (cookieString != null) {
            System.out.println("parkTest = "+cookieString);
            CookieManager.getInstance().setCookie(url, cookieString);
        }

        webView.loadUrl(url);


        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //전화가 안되는 문제가 있어서 따로 적용  (기본 웹뷰에서는 됨)

                if (url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri
                            .parse(url));
                    startActivity(intent);
                } else if (url != null && (url.startsWith("https://play.google.com"))) {
                    try {
                        String tempURL = url.split("id=")[1];
                        Intent marketIntent = new Intent(Intent.ACTION_VIEW);
                        marketIntent.setData(Uri.parse("market://details?id=" + tempURL));
                        startActivity(marketIntent);
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (url.startsWith("http:") || url.startsWith("https:")) {
                    //mMsgView.setVisibility(View.VISIBLE);
                    view.loadUrl(url);
                } else if (url != null && url.startsWith("intent://")) {
                    try {
                        Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                        Intent existPackage = getPackageManager().getLaunchIntentForPackage(intent.getPackage());
                        if (existPackage != null) {
                            startActivity(intent);
                        } else {
                            Intent marketIntent = new Intent(Intent.ACTION_VIEW);
                            marketIntent.setData(Uri.parse("market://details?id=" + intent.getPackage()));
                            startActivity(marketIntent);
                        }
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (url != null && (url.startsWith("market://") || url.startsWith("intent:kakaolink:"))) {
                    try {
                        Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                        Intent existPackage = getPackageManager().getLaunchIntentForPackage(intent.getPackage());
                        if (existPackage != null) {
                            startActivity(intent);
                        } else {
                            Intent marketIntent = new Intent(Intent.ACTION_VIEW);
                            marketIntent.setData(Uri.parse("market://details?id=" + intent.getPackage()));
                            startActivity(marketIntent);
                        }
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }

            //앱 처음 접속 시 접속한다는 메시지 띄울 필요
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

            }


            //coupon error 날 시 처리 멘트
            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);

            }

        });

        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, android.os.Message resultMsg) {


                return true;
            }

            public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result) {
                new AlertDialog.Builder(getApplication())
                        .setTitle(" ")
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok,
                                new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        result.confirm();
                                    }
                                })
                        .setCancelable(false)
                        .create()
                        .show();

                return true;
            }

            public void onCloseWindow(WebView  w){
                finish();
                super.onCloseWindow(w);

            }

        });

    }



}
