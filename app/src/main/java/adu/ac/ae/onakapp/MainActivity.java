package adu.ac.ae.onakapp;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.ColorRes;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

import net.openid.appauth.AppAuthConfiguration;
import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ClientSecretBasic;
import net.openid.appauth.RegistrationRequest;
import net.openid.appauth.RegistrationResponse;
import net.openid.appauth.ResponseTypeValues;
import net.openid.appauth.browser.AnyBrowserMatcher;
import net.openid.appauth.browser.BrowserMatcher;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import adu.ac.ae.onakapp.config.AuthStateManager;
import adu.ac.ae.onakapp.config.Configuration;

public class MainActivity extends AppCompatActivity {
    private static final String EXTRA_FAILED = "failed";
    private static final int RC_AUTH = 100;
    private static final String TAG = "LoginActivity";
    private AuthorizationService mAuthService;
    private AuthStateManager mAuthStateManager;
    private Configuration mConfiguration;
    private ExecutorService mExecutor;
    private final AtomicReference<String> mClientId = new AtomicReference<>();
    private final AtomicReference<AuthorizationRequest> mAuthRequest = new AtomicReference<>();
    private final AtomicReference<CustomTabsIntent> mAuthIntent = new AtomicReference<>();
    private CountDownLatch mAuthIntentLatch = new CountDownLatch(1);
    private boolean mUsePendingIntents=false;
    @NonNull
    private BrowserMatcher mBrowserMatcher = AnyBrowserMatcher.INSTANCE;

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("OnStart");
        //startAuth() ;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mExecutor = Executors.newSingleThreadExecutor();
        mAuthStateManager = AuthStateManager.getInstance(this);
        mConfiguration = Configuration.getInstance(this);
        System.out.println(mConfiguration.getRedirectUri());
        if (!mConfiguration.isValid()) {
            System.out.println(mConfiguration.getConfigurationError());
            return;
        }
        if (mConfiguration.hasConfigurationChanged()) {
            // discard any existing authorization state due to the change of configuration
            Log.i(TAG, "Configuration change detected, discarding old state");
            mAuthStateManager.replace(new AuthState());
            mConfiguration.acceptConfiguration();
        }
        mExecutor.submit(this::initializeAppAuth);

    }
    private void createAuthRequest(@Nullable String loginHint) {
        Log.i(TAG, "Creating auth request for login hint: " + loginHint);
        AuthorizationRequest.Builder authRequestBuilder = new AuthorizationRequest.Builder(
            mAuthStateManager.getCurrent().getAuthorizationServiceConfiguration(),
            mClientId.get(),
            ResponseTypeValues.CODE,
            mConfiguration.getRedirectUri())
            .setScope(mConfiguration.getScope());

        if (!TextUtils.isEmpty(loginHint)) {
            authRequestBuilder.setLoginHint(loginHint);
        }

        mAuthRequest.set(authRequestBuilder.build());
    }

    private void recreateAuthorizationService() {
        if (mAuthService != null) {
            Log.i(TAG, "Discarding existing AuthService instance");
            mAuthService.dispose();
        }
        mAuthService = createAuthorizationService();
        mAuthRequest.set(null);
        mAuthIntent.set(null);
    }
    @WorkerThread
    private void initializeAppAuth() {
        Log.i(TAG, "Initializing AppAuth");
        recreateAuthorizationService();

        if (mAuthStateManager.getCurrent().getAuthorizationServiceConfiguration() != null) {
            // configuration is already created, skip to client initialization
            Log.i(TAG, "auth config already established");
            initializeClient();
            return;
        }

        // if we are not using discovery, build the authorization service configuration directly
        // from the static configuration values.
        if (mConfiguration.getDiscoveryUri() == null) {
            Log.i(TAG, "Creating auth config from res/raw/auth_config.json");
            AuthorizationServiceConfiguration config = new AuthorizationServiceConfiguration(
                mConfiguration.getAuthEndpointUri(),
                mConfiguration.getTokenEndpointUri(),
                mConfiguration.getRegistrationEndpointUri());

            mAuthStateManager.replace(new AuthState(config));
            initializeClient();
            return;
        }

        // WrongThread inference is incorrect for lambdas
        // noinspection WrongThread
        //  runOnUiThread(() -> displayLoading("Retrieving discovery document"));
        Log.i(TAG, "Retrieving OpenID discovery doc");
        AuthorizationServiceConfiguration.fetchFromUrl(
            mConfiguration.getDiscoveryUri(),
            this::handleConfigurationRetrievalResult,
            mConfiguration.getConnectionBuilder());
    }

    @MainThread
    private void handleConfigurationRetrievalResult(
        AuthorizationServiceConfiguration config,
        AuthorizationException ex) {
        if (config == null) {
            Log.i(TAG, "Failed to retrieve discovery document", ex);
            //   displayError("Failed to retrieve discovery document: " + ex.getMessage(), true);
            return;
        }

        Log.i(TAG, "Discovery document retrieved");
        mAuthStateManager.replace(new AuthState(config));
        mExecutor.submit(this::initializeClient);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //displayAuthOptions();
        System.out.println("Out Return from Browser");
        if (resultCode == RESULT_CANCELED) {
            //displayAuthCancelled();
        } else {
            System.out.println("Result "+data.getExtras().toString());
            /*Intent intent = new Intent(this, MainActivity.class);
            intent.putExtras(data.getExtras());
            startActivity(intent);*/
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @WorkerThread
    private void initializeClient() {
        if (mConfiguration.getClientId() != null) {
            Log.i(TAG, "Using static client ID: " + mConfiguration.getClientId());
            // use a statically configured client ID
            mClientId.set(mConfiguration.getClientId());
            runOnUiThread(this::initializeAuthRequest);
            return;
        }

        RegistrationResponse lastResponse =
            mAuthStateManager.getCurrent().getLastRegistrationResponse();
        if (lastResponse != null) {
            Log.i(TAG, "Using dynamic client ID: " + lastResponse.clientId);
            // already dynamically registered a client ID
            mClientId.set(lastResponse.clientId);
            runOnUiThread(this::initializeAuthRequest);
            return;
        }

        // WrongThread inference is incorrect for lambdas
        // noinspection WrongThread
        // runOnUiThread(() -> displayLoading("Dynamically registering client"));
        Log.i(TAG, "Dynamically registering client");

        RegistrationRequest registrationRequest = new RegistrationRequest.Builder(
            mAuthStateManager.getCurrent().getAuthorizationServiceConfiguration(),
            Collections.singletonList(mConfiguration.getRedirectUri()))
            .setTokenEndpointAuthenticationMethod(ClientSecretBasic.NAME)
            .build();

        mAuthService.performRegistrationRequest(
            registrationRequest,
            this::handleRegistrationResponse);
    }
    @MainThread
    private void initializeAuthRequest() {
        createAuthRequest("");
        warmUpBrowser();
        startAuth();
        // displayAuthOptions();
    }
    @MainThread
    private void handleRegistrationResponse(
        RegistrationResponse response,
        AuthorizationException ex) {
        mAuthStateManager.updateAfterRegistration(response, ex);
        if (response == null) {
            Log.i(TAG, "Failed to dynamically register client", ex);
            // displayErrorLater("Failed to register client: " + ex.getMessage(), true);
            return;
        }

        Log.i(TAG, "Dynamically registered client: " + response.clientId);
        mClientId.set(response.clientId);
        initializeAuthRequest();
    }
    private void warmUpBrowser() {
        mAuthIntentLatch = new CountDownLatch(1);
        mExecutor.execute(() -> {
            Log.i(TAG, "Warming up browser instance for auth request");
            CustomTabsIntent.Builder intentBuilder =
                mAuthService.createCustomTabsIntentBuilder(mAuthRequest.get().toUri());
            intentBuilder.setToolbarColor(getColorCompat(R.color.colorPrimary));
            mAuthIntent.set(intentBuilder.build());
            mAuthIntentLatch.countDown();
        });
    }
    @TargetApi(Build.VERSION_CODES.M)
    @SuppressWarnings("deprecation")
    private int getColorCompat(@ColorRes int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getColor(color);
        } else {
            return getResources().getColor(color);
        }
    }
    private AuthorizationService createAuthorizationService() {
        Log.i(TAG, "Creating authorization service");
        AppAuthConfiguration.Builder builder = new AppAuthConfiguration.Builder();
        builder.setBrowserMatcher(mBrowserMatcher);
        builder.setConnectionBuilder(mConfiguration.getConnectionBuilder());

        return new AuthorizationService(this, builder.build());
    }
    @MainThread
    void startAuth() {

        mExecutor.submit(this::doAuth);
    }
    @WorkerThread
    private void doAuth () {
        try {
            mAuthIntentLatch.await();
        } catch (InterruptedException ex) {
            Log.w(TAG, "Interrupted while waiting for auth intent");
        }

        if (mUsePendingIntents) {
            Intent completionIntent = new Intent(this, MainActivity.class);
            Intent cancelIntent = new Intent(this, MainActivity.class);
            cancelIntent.putExtra(EXTRA_FAILED, true);
            cancelIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            mAuthService.performAuthorizationRequest(
                mAuthRequest.get(),
                PendingIntent.getActivity(this, 0, completionIntent, 0),
                PendingIntent.getActivity(this, 0, cancelIntent, 0),
                mAuthIntent.get());
        } else {
            Intent intent = mAuthService.getAuthorizationRequestIntent(
                mAuthRequest.get(),
                mAuthIntent.get());
            startActivityForResult(intent, RC_AUTH);
        }
    }

}
