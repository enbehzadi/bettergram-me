package io.bettergram.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import io.bettergram.messenger.R;
import io.bettergram.service.MailChimpService;
import io.bettergram.telegram.messenger.AndroidUtilities;
import io.bettergram.telegram.messenger.ApplicationLoader;
import io.bettergram.telegram.messenger.LocaleController;
import io.bettergram.telegram.ui.ActionBar.AlertDialog;
import io.bettergram.utils.SpanBuilder;

import static android.text.TextUtils.isEmpty;
import static io.bettergram.service.MailChimpService.EXTRA_SUBSCRIBE_EMAIL;
import static io.bettergram.service.MailChimpService.EXTRA_SUBSCRIBE_NEWSLETTER;

public class SplashActivity extends Activity {

    private View layout02, layout01;
    private EditText emailEdit;
    private Button signUpButton;
    private CheckBox termsCheckbox, newsletterCheckbox;
    private ImageView overlayImage;
    private TextView termsText;

    private AlertDialog progressDialog;

    private boolean subscribeNewsletter;

    private SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mailchimp_subscribed", MODE_PRIVATE);

    /**
     * Receives data from {@link MailChimpService}
     */
    public BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            progressDialog.dismiss();
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                AndroidUtilities.runOnUIThread(() -> {
                    Intent intent1 = new Intent(SplashActivity.this, IntroActivity.class);
                    intent1.setData(getIntent().getData());
                    startActivity(intent1);
                    finish();
                }, 1500);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressDialog = new AlertDialog(this, 1);
        progressDialog.setMessage(LocaleController.getString("Please wait...", R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        overlayImage = findViewById(R.id.overlayImage);
        layout01 = findViewById(R.id.layout01);
        layout02 = findViewById(R.id.layout02);
        emailEdit = findViewById(R.id.emailEdit);
        signUpButton = findViewById(R.id.signUpButton);
        newsletterCheckbox = findViewById(R.id.newsletterCheckbox);
        newsletterCheckbox.setOnCheckedChangeListener((buttonView, subscribeNewsletter) -> {
            this.subscribeNewsletter = subscribeNewsletter;
        });
        termsCheckbox = findViewById(R.id.termsCheckbox);
        termsCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            signUpButton.setEnabled(isChecked);
        });
        termsText = findViewById(R.id.textTerms);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                widget.cancelPendingInputEvents();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse("https://bettergram.io/#myModal3"));
                startActivity(browserIntent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
            }
        };
        SpanBuilder spanBuilder = new SpanBuilder();
        spanBuilder.append(getString(R.string.i_agree_to_the_terms), clickableSpan, new ForegroundColorSpan(Color.WHITE));
        termsText.setText(spanBuilder.build());
        termsText.setMovementMethod(LinkMovementMethod.getInstance());
        overlayImage.animate()
                .translationY(overlayImage.getHeight())
                .alpha(0.0f)
                .setDuration(3500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        overlayImage.setVisibility(View.GONE);
                        layout01.setVisibility(View.VISIBLE);
                        layout02.setVisibility(View.VISIBLE);
                    }
                });
        signUpButton.setOnClickListener(v -> {
            String email = emailEdit.getEditableText().toString();
            if (!isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Intent intent = new Intent(this, MailChimpService.class);
                intent.putExtra(EXTRA_SUBSCRIBE_NEWSLETTER, subscribeNewsletter);
                intent.putExtra(EXTRA_SUBSCRIBE_EMAIL, email);
                startService(intent);
                progressDialog.show();
            } else {
                createAlertDialog(getString(R.string.invalid_email), getString(R.string.invalid_email_msg)).show();
            }
        });
        registerReceiver(this);

        Intent intent1 = new Intent(SplashActivity.this, IntroActivity.class);
//        intent1.setData(getIntent().getData());
        startActivity(intent1);
        finish();
    }

    private AlertDialog.Builder createAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        return builder;
    }

    public void startService(Activity activity) {
        Intent intent = new Intent(activity, MailChimpService.class);
        activity.startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this);
    }

    /**
     * Register {@link BroadcastReceiver} of {@link MailChimpService}
     */
    public void registerReceiver(Activity activity) {
        activity.registerReceiver(receiver, new IntentFilter(MailChimpService.NOTIFICATION));
    }

    /**
     * Unregister {@link BroadcastReceiver} of {@link MailChimpService}
     */
    public void unregisterReceiver(Activity activity) {
        activity.unregisterReceiver(receiver);
    }
}
