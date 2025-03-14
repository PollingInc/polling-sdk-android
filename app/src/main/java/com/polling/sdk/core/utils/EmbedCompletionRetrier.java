package com.polling.sdk.core.utils;

import android.os.CountDownTimer;

public class EmbedCompletionRetrier
{
    private int attempt = 0;
    public int maxAttempts = 2;
    public int baseDelayInMs = 1000; //1 sec

    public SingleCallback callback;

    public EmbedCompletionRetrier(SingleCallback callback, int maxAttempts, int baseDelayInMs)
    {
        this.callback = callback;
        this.maxAttempts = maxAttempts;
        this.baseDelayInMs = baseDelayInMs;
    }

    public void start()
    {
        executeAttempt();
    }

    private void executeAttempt() {
        if (attempt < maxAttempts) {

            if(attempt != 0)
            {
                callback.execute();
            }

            attempt++;
            System.out.println("Attempt " + attempt + ": Executing request...");

            boolean success = makeRequest();

            if (!success && attempt < maxAttempts) {
                int delay = (int) (baseDelayInMs * Math.pow(2, attempt - 1));
                System.out.println("Retrying in " + delay + "ms");

                new CountDownTimer(delay, delay) {
                    public void onTick(long millisUntilFinished) {}
                    public void onFinish() {
                        executeAttempt();
                    }
                }.start();
            } else {
                System.out.println(success ? "Request successful!" : "Max attempts reached. Giving up.");
            }
        }
    }

    private boolean makeRequest() {
        return false;
    }
}
