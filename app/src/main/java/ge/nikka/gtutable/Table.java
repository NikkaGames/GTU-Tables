package ge.nikka.gtutable;

import android.animation.*;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.*;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.progressindicator.CircularProgressIndicator;

public class Table extends AppCompatActivity {
    private WebView webView;
    private ValueAnimator anim;

    private static Animation fadeout() {
		Animation fadeIn = new AlphaAnimation(0, 1);
		fadeIn.setDuration(1000);
		return fadeIn;
	}
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Singleton.getInstance().getData().length() < 3) {
            this.startActivity(new Intent(this, MainActivity.class));
            this.finish();
        }
        setContentView(R.layout.layout_table);

        CircularProgressIndicator prog = (CircularProgressIndicator)findViewById(R.id.prgc);
        prog.setVisibility(View.GONE);
        
        webView = findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    Toast.makeText(view.getContext(), url, Toast.LENGTH_SHORT).show();
                    prog.setVisibility(View.VISIBLE);
                    prog.setAnimation(fadeout());
                    if (url.startsWith("tg:") || url.startsWith("fb:")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        view.getContext().startActivity(intent);
                        return true; //with return true, the webview wont try rendering the url
                    } else if (url.startsWith("intent:")) {
                        while (view.canGoBack()) {
                            view.goBack();
                        }
                        return true; //with return true, the webview wont try rendering the url
                    }
                return false;
            }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            /*** Hide ProgressBar while page completely load ***/
            prog.setVisibility(View.GONE);

        }
        });
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false); 
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadData(Singleton.getInstance().getData(), "text/html", "UTF-8");
        
        
        anim = ValueAnimator.ofFloat(1.2f, 1f);
        anim.setDuration(500);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                webView.setScaleX((Float)animation.getAnimatedValue());
                webView.setScaleY((Float)animation.getAnimatedValue());
            }
        });
        anim.setRepeatCount(0);
        //anim.setRepeatMode(ValueAnimator.REVERSE);
        anim.start();
        webView.setAnimation(fadeout());
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) {
            webView.setAnimation(fadeout());
            anim.start();
        }
    }
    

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
