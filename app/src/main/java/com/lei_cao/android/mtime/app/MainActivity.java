package com.lei_cao.android.mtime.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.lei_cao.android.mtime.app.models.Movie;
import com.vungle.warren.InitCallback;
import com.vungle.warren.LoadAdCallback;
import com.vungle.warren.PlayAdCallback;
import com.vungle.warren.Vungle;

public class MainActivity extends AppCompatActivity implements MoviesFragment.MovieCallback {

    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    boolean mTwoPane = false;


    // Vungle appId
    String vungleAppId;
    String vungleDefaultPlacementId = "DEFAULT-9081923";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }

        MoviesFragment moviesFragment =  ((MoviesFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment_movies));
        moviesFragment.setTwoPanel(mTwoPane);

        initVungle();
    }

    private void initVungle() {
        // Init Vungle SDK
        vungleAppId = getResources().getString(R.string.vungle_app_id);
        Log.e("vungle lei-cao", vungleAppId);
        Vungle.init(vungleAppId, getApplicationContext(), new InitCallback() {
            @Override
            public void onSuccess() {
                // Initialization has succeeded and SDK is ready to load an ad or play one if there
                // is one pre-cached already

                loadVungleAd();
            }

            @Override
            public void onError(Throwable throwable) {
                // Initialization error occurred - throwable.getLocalizedMessage() contains error message
                Log.e("vungle lei-cao", "init error", throwable);
            }

            @Override
            public void onAutoCacheAdAvailable(String placementId) {
                // Callback to notify when an ad becomes available for the auto-cached placement
                // NOTE: This callback works only for the auto-cached placement. Otherwise, please use
                // LoadAdCallback with loadAd API for loading placements.
            }
        });
    }

    private void loadVungleAd() {
        if (Vungle.isInitialized()) {
            Vungle.loadAd(vungleDefaultPlacementId, new LoadAdCallback() {
                @Override
                public void onAdLoad(final String placementReferenceId) {
                    Log.d("vungle lei-cao", "OnLoad" + placementReferenceId);

                    /*
                    TODO fix crash when there is no delay
                    2019-03-16 23:14:54.902 14514-14514/com.lei_cao.android.mtime.app E/AndroidRuntime: FATAL EXCEPTION: main
                    Process: com.lei_cao.android.mtime.app, PID: 14514
                    java.lang.NullPointerException: Attempt to invoke interface method 'retrofit2.Call com.vungle.warren.network.VungleApi.willPlayAd(java.lang.String, java.lang.String, com.google.gson.JsonObject)' on a null object reference
                        at com.vungle.warren.network.VungleApiClient.willPlayAd(VungleApiClient.java:632)
                        at com.vungle.warren.Vungle.playAd(Vungle.java:685)
                        at com.lei_cao.android.mtime.app.MainActivity.playVungleAd(MainActivity.java:111)
                        at com.lei_cao.android.mtime.app.MainActivity.access$100(MainActivity.java:15)
                        at com.lei_cao.android.mtime.app.MainActivity$2.onAdLoad(MainActivity.java:86)
                        at com.vungle.warren.Vungle.loadAd(Vungle.java:907)
                        at com.vungle.warren.Vungle.loadAd(Vungle.java:851)
                        at com.lei_cao.android.mtime.app.MainActivity.loadVungleAd(MainActivity.java:81)
                        at com.lei_cao.android.mtime.app.MainActivity.access$000(MainActivity.java:15)
                        at com.lei_cao.android.mtime.app.MainActivity$1.onSuccess(MainActivity.java:61)
                        at com.vungle.warren.Vungle$2.onResponse(Vungle.java:476)
                        at com.vungle.warren.network.VungleApiClient$3.onResponse(VungleApiClient.java:514)
                        at retrofit2.ExecutorCallAdapterFactory$ExecutorCallbackCall$1$1.run(ExecutorCallAdapterFactory.java:68)
                        at android.os.Handler.handleCallback(Handler.java:873)
                        at android.os.Handler.dispatchMessage(Handler.java:99)
                        at android.os.Looper.loop(Looper.java:193)
                        at android.app.ActivityThread.main(ActivityThread.java:6669)
                        at java.lang.reflect.Method.invoke(Native Method)
                        at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:493)
                        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:858)
                     */
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            playVungleAd(placementReferenceId);
                        }
                    }, 1000);
                }

                @Override
                public void onError(String placementReferenceId, Throwable throwable) {
                    // Load ad error occurred - throwable.getLocalizedMessage() contains error message
                    Log.e("vungle lei-cao", "OnLoad error " + placementReferenceId, throwable);
                }
            });
        }
    }

    private void playVungleAd(String placementReferenceId) {
        if (Vungle.canPlayAd(placementReferenceId)) {
            Vungle.playAd(placementReferenceId, null, new PlayAdCallback() {
                @Override
                public void onAdStart(String placementReferenceId) {
                    Log.d("vungle lei-cao", "OnPlay" + placementReferenceId);
                }

                @Override
                public void onAdEnd(String placementReferenceId, boolean completed, boolean isCTAClicked) { }

                @Override
                public void onError(String placementReferenceId, Throwable throwable) {
                    // Play ad error occurred - throwable.getLocalizedMessage() contains error message
                    Log.e("vungle lei-cao", "OnPlay" + placementReferenceId, throwable);
                }
            });
        }
    }

    @Override
    public void onItemSelected(Movie movie) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_MOVIE, movie);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);
            fragment.mTwoPane = true;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class).putExtra(DetailFragment.DETAIL_MOVIE, movie);
            startActivity(intent);
        }
    }


    @Override
    public void onShowReviews(Movie movie) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_MOVIE, movie);

            ReviewFragment fragment = new ReviewFragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, ReviewActivity.class).putExtra(DetailFragment.DETAIL_MOVIE, movie);
            startActivity(intent);
        }
    }

}
