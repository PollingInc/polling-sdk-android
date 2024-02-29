package com.polling.sdk;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.net.URLEncoder;

public class WebViewBottom extends BottomSheetDialogFragment
{
    private final String url;
    private final String customerId;
    private final String apiKey;

    public WebViewBottom(String url, String customerId, String apiKey) {
        this.url = url;
        this.customerId = customerId;
        this.apiKey = apiKey;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_bottom_sheet_webview, container, false);
        WebView webView = WebViewConfigs.ApplyDefault(v.findViewById(R.id.webview));

        StringBuilder buffer = new StringBuilder(url);

        if (customerId != null && !customerId.isEmpty() && apiKey != null && !apiKey.isEmpty()) {
            buffer.append("?customer_id=").append(URLEncoder.encode(customerId));
            buffer.append("&api_key=").append(URLEncoder.encode(apiKey));
        }
        //else ?

        webView.loadUrl(buffer.toString());

        return v;
    }

}
