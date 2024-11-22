package com.polling.sdk.dialogs;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.polling.sdk.R;
import com.polling.sdk.models.RequestIdentification;
import com.polling.sdk.dialogs.helpers.WebViewConfigs;

public class WebViewBottomSheet extends BottomSheetDialogFragment
{
    private final String url;
    private final RequestIdentification requestIdentification;

    public WebViewBottomSheet(String url, RequestIdentification requestIdentification) {
        this.url = url;
        this.requestIdentification = requestIdentification;
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
        WebView webView = WebViewConfigs.applyDefault(v.findViewById(R.id.webview));

        String endpoint = requestIdentification.ApplyKeyToURL(url);
        endpoint = requestIdentification.ApplyCompletionBypassToURL(endpoint);

        webView.loadUrl(endpoint);

        return v;
    }

}
