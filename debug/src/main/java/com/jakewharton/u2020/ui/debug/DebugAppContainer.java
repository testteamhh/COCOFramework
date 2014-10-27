package com.jakewharton.u2020.ui.debug;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.cocosw.accessory.utils.StringUtils;
import com.cocosw.framework.debug.BuildConfig;
import com.cocosw.framework.debug.R;
import com.jakewharton.madge.MadgeFrameLayout;
import com.jakewharton.scalpel.ScalpelFrameLayout;
import com.jakewharton.u2020.data.AnimationSpeed;
import com.jakewharton.u2020.data.ApiEndpoint;
import com.jakewharton.u2020.data.ApiEndpoints;
import com.jakewharton.u2020.data.NetworkProxy;
import com.jakewharton.u2020.data.PicassoDebugging;
import com.jakewharton.u2020.data.PixelGridEnabled;
import com.jakewharton.u2020.data.PixelRatioEnabled;
import com.jakewharton.u2020.data.ScalpelEnabled;
import com.jakewharton.u2020.data.ScalpelWireframeEnabled;
import com.jakewharton.u2020.data.SeenDebugDrawer;
import com.jakewharton.u2020.data.prefs.BooleanPreference;
import com.jakewharton.u2020.data.prefs.IntPreference;
import com.jakewharton.u2020.data.prefs.StringPreference;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.StatsSnapshot;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Set;
import java.util.TimeZone;



import timber.log.Timber;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static java.net.Proxy.Type.HTTP;

public class DebugAppContainer extends Activity {
    private static final DateFormat DATE_DISPLAY_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm a");

    private  OkHttpClient client;
    private  Picasso picasso;
    private  StringPreference networkEndpoint;
    private  StringPreference networkProxy;
    private  IntPreference animationSpeed;
    private  BooleanPreference picassoDebugging;
    private  BooleanPreference pixelGridEnabled;
    private  BooleanPreference pixelRatioEnabled;
    private  BooleanPreference scalpelEnabled;
    private  BooleanPreference scalpelWireframeEnabled;
    private  BooleanPreference seenDebugDrawer;


    public DebugAppContainer() {
    }

//    public DebugAppContainer(OkHttpClient client, Picasso picasso,
//                             @ApiEndpoint StringPreference networkEndpoint,
//                             @NetworkProxy StringPreference networkProxy,
//                             @AnimationSpeed IntPreference animationSpeed,
//                             @PicassoDebugging BooleanPreference picassoDebugging,
//                             @PixelGridEnabled BooleanPreference pixelGridEnabled,
//                             @PixelRatioEnabled BooleanPreference pixelRatioEnabled,
//                             @ScalpelEnabled BooleanPreference scalpelEnabled,
//                             @ScalpelWireframeEnabled BooleanPreference scalpelWireframeEnabled,
//                             @SeenDebugDrawer BooleanPreference seenDebugDrawer) {
//        this.client = client;
//        this.picasso = picasso;
//        this.networkEndpoint = networkEndpoint;
//        this.scalpelEnabled = scalpelEnabled;
//        this.scalpelWireframeEnabled = scalpelWireframeEnabled;
//        this.seenDebugDrawer = seenDebugDrawer;
//        this.networkProxy = networkProxy;
//        this.animationSpeed = animationSpeed;
//        this.picassoDebugging = picassoDebugging;
//        this.pixelGridEnabled = pixelGridEnabled;
//        this.pixelRatioEnabled = pixelRatioEnabled;
//    }

    LinearLayout drawerLayout;

    MadgeFrameLayout madgeFrameLayout;
    ScalpelFrameLayout scalpelFrameLayout;

    View contextualTitleView;
    LinearLayout contextualListView;

    Spinner endpointView;
    View endpointEditView;
    Spinner networkDelayView;
    Spinner networkVarianceView;
    Spinner networkErrorView;
    Spinner networkProxyView;
    Spinner networkLoggingView;


    Spinner uiAnimationSpeedView;
    Switch uiPixelGridView;
    Switch uiPixelRatioView;
    Switch uiScalpelView;
    Switch uiScalpelWireframeView;

    TextView buildNameView;
    TextView buildCodeView;
    TextView buildShaView;
    TextView buildDateView;

    TextView deviceMakeView;
    TextView deviceModelView;
    TextView deviceResolutionView;
    TextView deviceDensityView;
    TextView deviceReleaseView;
    TextView deviceApiView;

    Switch picassoIndicatorView;
    TextView picassoCacheSizeView;
    TextView picassoCacheHitView;
    TextView picassoCacheMissView;
    TextView picassoDecodedView;
    TextView picassoDecodedTotalView;
    TextView picassoDecodedAvgView;
    TextView picassoTransformedView;
    TextView picassoTransformedTotalView;
    TextView picassoTransformedAvgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        drawerLayout = (LinearLayout) findViewById(R.id.debug_drawer_layout);
        scalpelFrameLayout = (ScalpelFrameLayout) findViewById(R.id.debug_content);
        madgeFrameLayout = (MadgeFrameLayout) findViewById(R.id.madge_container);

        contextualTitleView = findViewById(R.id.debug_contextual_title);
        contextualListView = (LinearLayout) findViewById(R.id.debug_contextual_list);
        endpointView = (Spinner) findViewById(R.id.debug_network_endpoint);
        endpointEditView = findViewById(R.id.debug_network_endpoint_edit);
        endpointEditView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.d("Prompting to edit custom endpoint URL.");
                // Pass in the currently selected position since we are merely editing.
                showCustomEndpointDialog(endpointView.getSelectedItemPosition(), networkEndpoint.get());
            }
        });
        networkDelayView = (Spinner) findViewById(R.id.debug_network_delay);
        networkVarianceView = (Spinner) findViewById(R.id.debug_network_variance);
        networkErrorView = (Spinner) findViewById(R.id.debug_network_error);
        networkProxyView = (Spinner) findViewById(R.id.debug_network_proxy);
        networkLoggingView = (Spinner) findViewById(R.id.debug_network_logging);

        uiAnimationSpeedView = (Spinner) findViewById(R.id.debug_ui_animation_speed);
        uiPixelGridView = (Switch) findViewById(R.id.debug_ui_pixel_grid);
        uiPixelRatioView = (Switch) findViewById(R.id.debug_ui_pixel_ratio);
        uiScalpelView = (Switch) findViewById(R.id.debug_ui_scalpel);
        uiScalpelWireframeView = (Switch) findViewById(R.id.debug_ui_scalpel_wireframe);

        buildNameView = (TextView) findViewById(R.id.debug_build_name);
        buildCodeView = (TextView) findViewById(R.id.debug_build_code);
        buildShaView = (TextView) findViewById(R.id.debug_build_sha);
        buildDateView = (TextView) findViewById(R.id.debug_build_date);


        deviceMakeView = (TextView) findViewById(R.id.debug_device_make);
        deviceModelView = (TextView) findViewById(R.id.debug_device_model);
        deviceResolutionView = (TextView) findViewById(R.id.debug_device_resolution);
        deviceDensityView = (TextView) findViewById(R.id.debug_device_density);
        deviceReleaseView = (TextView) findViewById(R.id.debug_device_release);
        deviceApiView = (TextView) findViewById(R.id.debug_device_api);


        picassoIndicatorView = (Switch) findViewById(R.id.debug_picasso_indicators);
        picassoCacheSizeView = (TextView) findViewById(R.id.debug_picasso_cache_size);
        picassoCacheHitView = (TextView) findViewById(R.id.debug_picasso_cache_hit);
        picassoCacheMissView = (TextView) findViewById(R.id.debug_picasso_cache_miss);
        picassoDecodedView = (TextView) findViewById(R.id.debug_picasso_decoded);
        picassoDecodedTotalView = (TextView) findViewById(R.id.debug_picasso_decoded_total);
        picassoDecodedAvgView = (TextView) findViewById(R.id.debug_picasso_decoded_avg);
        picassoTransformedView = (TextView) findViewById(R.id.debug_picasso_transformed);
        picassoTransformedTotalView = (TextView) findViewById(R.id.debug_picasso_transformed_total);
        picassoTransformedAvgView = (TextView) findViewById(R.id.debug_picasso_transformed_avg);



        setContentView(R.layout.debug_activity_frame);

        // Manually find the debug drawer and inflate the drawer layout inside of it.
        ViewGroup drawer = (ScrollView) findViewById(R.id.debug_drawer);
        LayoutInflater.from(this).inflate(R.layout.debug_drawer_content, drawer);

        // Set up the contextual actions to watch views coming in and out of the content area.
        Set<ContextualDebugActions.DebugAction<?>> debugActions = Collections.emptySet();
        ContextualDebugActions contextualActions = new ContextualDebugActions(this, debugActions);
      //  content.setOnHierarchyChangeListener(HierarchyTreeChangeListener.wrap(contextualActions));

        refreshPicassoStats();

        // If you have not seen the debug drawer before, show it with a message
//    if (!seenDebugDrawer.get()) {
//      drawerLayout.postDelayed(new Runnable() {
//        @Override public void run() {
//          drawerLayout.openDrawer(Gravity.END);
//          Toast.makeText(activity, R.string.debug_drawer_welcome, Toast.LENGTH_LONG).show();
//        }
//      }, 1000);
//      seenDebugDrawer.set(true);
//    }

        setupNetworkSection();
        setupUserInterfaceSection();
        setupBuildSection();
        setupDeviceSection();
        setupPicassoSection();
    }

    private void setupNetworkSection() {
        final ApiEndpoints currentEndpoint = ApiEndpoints.from(networkEndpoint.get());
        final EnumAdapter<ApiEndpoints> endpointAdapter =
                new EnumAdapter<>(this, ApiEndpoints.class);
        endpointView.setAdapter(endpointAdapter);
        endpointView.setSelection(currentEndpoint.ordinal());
        endpointView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                ApiEndpoints selected = endpointAdapter.getItem(position);
                if (selected != currentEndpoint) {
                    if (selected == ApiEndpoints.CUSTOM) {
                        Timber.d("Custom network endpoint selected. Prompting for URL.");
                        showCustomEndpointDialog(currentEndpoint.ordinal(), "http://");
                    } else {
                        setEndpointAndRelaunch(selected.url);
                    }
                } else {
                    Timber.d("Ignoring re-selection of network endpoint %s", selected);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        final NetworkDelayAdapter delayAdapter = new NetworkDelayAdapter(this);


        final NetworkVarianceAdapter varianceAdapter = new NetworkVarianceAdapter(this);


        final NetworkErrorAdapter errorAdapter = new NetworkErrorAdapter(this);
        networkErrorView.setAdapter(errorAdapter);


        int currentProxyPosition = networkProxy.isSet() ? ProxyAdapter.PROXY : ProxyAdapter.NONE;
        final ProxyAdapter proxyAdapter = new ProxyAdapter(this, networkProxy);
        networkProxyView.setAdapter(proxyAdapter);
        networkProxyView.setSelection(currentProxyPosition);
        networkProxyView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (position == ProxyAdapter.NONE) {
                    Timber.d("Clearing network proxy");
                    networkProxy.delete();
                    client.setProxy(null);
                } else if (networkProxy.isSet() && position == ProxyAdapter.PROXY) {
                    Timber.d("Ignoring re-selection of network proxy %s", networkProxy.get());
                } else {
                    Timber.d("New network proxy selected. Prompting for host.");
                    showNewNetworkProxyDialog(proxyAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        // Only show the endpoint editor when a custom endpoint is in use.
        endpointEditView.setVisibility(currentEndpoint == ApiEndpoints.CUSTOM ? VISIBLE : GONE);

        if (currentEndpoint == ApiEndpoints.MOCK_MODE) {
            // Disable network proxy if we are in mock mode.
            networkProxyView.setEnabled(false);
            networkLoggingView.setEnabled(false);
        } else {
            // Disable network controls if we are not in mock mode.
            networkDelayView.setEnabled(false);
            networkVarianceView.setEnabled(false);
            networkErrorView.setEnabled(false);
        }


    }


    private void setupUserInterfaceSection() {
        final AnimationSpeedAdapter speedAdapter = new AnimationSpeedAdapter(this);
        uiAnimationSpeedView.setAdapter(speedAdapter);
        final int animationSpeedValue = animationSpeed.get();
        uiAnimationSpeedView.setSelection(
                AnimationSpeedAdapter.getPositionForValue(animationSpeedValue));
        uiAnimationSpeedView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                int selected = speedAdapter.getItem(position);
                if (selected != animationSpeed.get()) {
                    Timber.d("Setting animation speed to %sx", selected);
                    animationSpeed.set(selected);
                    applyAnimationSpeed(selected);
                } else {
                    Timber.d("Ignoring re-selection of animation speed %sx", selected);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        // Ensure the animation speed value is always applied across app restarts.
//        content.post(new Runnable() {
//            @Override
//            public void run() {
//                applyAnimationSpeed(animationSpeedValue);
//            }
//        });

        boolean gridEnabled = pixelGridEnabled.get();
        madgeFrameLayout.setOverlayEnabled(gridEnabled);
        uiPixelGridView.setChecked(gridEnabled);
        uiPixelRatioView.setEnabled(gridEnabled);
        uiPixelGridView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Timber.d("Setting pixel grid overlay enabled to " + isChecked);
                pixelGridEnabled.set(isChecked);
                madgeFrameLayout.setOverlayEnabled(isChecked);
                uiPixelRatioView.setEnabled(isChecked);
            }
        });

        boolean ratioEnabled = pixelRatioEnabled.get();
        madgeFrameLayout.setOverlayRatioEnabled(ratioEnabled);
        uiPixelRatioView.setChecked(ratioEnabled);
        uiPixelRatioView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Timber.d("Setting pixel scale overlay enabled to " + isChecked);
                pixelRatioEnabled.set(isChecked);
                madgeFrameLayout.setOverlayRatioEnabled(isChecked);
            }
        });

        boolean scalpel = scalpelEnabled.get();
        scalpelFrameLayout.setLayerInteractionEnabled(scalpel);
        uiScalpelView.setChecked(scalpel);
        uiScalpelWireframeView.setEnabled(scalpel);
        uiScalpelView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Timber.d("Setting scalpel interaction enabled to " + isChecked);
                scalpelEnabled.set(isChecked);
                scalpelFrameLayout.setLayerInteractionEnabled(isChecked);
                uiScalpelWireframeView.setEnabled(isChecked);
            }
        });

        boolean wireframe = scalpelWireframeEnabled.get();
        scalpelFrameLayout.setDrawViews(!wireframe);
        uiScalpelWireframeView.setChecked(wireframe);
        uiScalpelWireframeView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Timber.d("Setting scalpel wireframe enabled to " + isChecked);
                scalpelWireframeEnabled.set(isChecked);
                scalpelFrameLayout.setDrawViews(!isChecked);
            }
        });
    }

    private void setupBuildSection() {
        buildNameView.setText(BuildConfig.VERSION_NAME);
        buildCodeView.setText(String.valueOf(BuildConfig.VERSION_CODE));
        //  buildShaView.setText(BuildConfig.GIT_SHA);

        // Parse ISO8601-format time into local time.
        DateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        inFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
//      Date buildTime = inFormat.parse(BuildConfig.BUILD_TIME);
//      buildDateView.setText(DATE_DISPLAY_FORMAT.format(buildTime));
    }

    private void setupDeviceSection() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        String densityBucket = getDensityString(displayMetrics);
//    deviceMakeView.setText(Strings.truncateAt(Build.MANUFACTURER, 20));
//    deviceModelView.setText(Strings.truncateAt(Build.MODEL, 20));
        deviceResolutionView.setText(displayMetrics.heightPixels + "x" + displayMetrics.widthPixels);
        deviceDensityView.setText(displayMetrics.densityDpi + "dpi (" + densityBucket + ")");
        deviceReleaseView.setText(Build.VERSION.RELEASE);
        deviceApiView.setText(String.valueOf(Build.VERSION.SDK_INT));
    }

    private void setupPicassoSection() {
        boolean picassoDebuggingValue = picassoDebugging.get();
        picasso.setDebugging(picassoDebuggingValue);
        picassoIndicatorView.setChecked(picassoDebuggingValue);
        picassoIndicatorView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                Timber.d("Setting Picasso debugging to " + isChecked);
                picasso.setDebugging(isChecked);
                picassoDebugging.set(isChecked);
            }
        });

        refreshPicassoStats();
    }

    private void refreshPicassoStats() {
        StatsSnapshot snapshot = picasso.getSnapshot();
        String size = getSizeString(snapshot.size);
        String total = getSizeString(snapshot.maxSize);
        int percentage = (int) ((1f * snapshot.size / snapshot.maxSize) * 100);
        picassoCacheSizeView.setText(size + " / " + total + " (" + percentage + "%)");
        picassoCacheHitView.setText(String.valueOf(snapshot.cacheHits));
        picassoCacheMissView.setText(String.valueOf(snapshot.cacheMisses));
        picassoDecodedView.setText(String.valueOf(snapshot.originalBitmapCount));
        picassoDecodedTotalView.setText(getSizeString(snapshot.totalOriginalBitmapSize));
        picassoDecodedAvgView.setText(getSizeString(snapshot.averageOriginalBitmapSize));
        picassoTransformedView.setText(String.valueOf(snapshot.transformedBitmapCount));
        picassoTransformedTotalView.setText(getSizeString(snapshot.totalTransformedBitmapSize));
        picassoTransformedAvgView.setText(getSizeString(snapshot.averageTransformedBitmapSize));
    }

    private void applyAnimationSpeed(int multiplier) {
        try {
            Method method = ValueAnimator.class.getDeclaredMethod("setDurationScale", float.class);
            method.invoke(null, (float) multiplier);
        } catch (Exception e) {
            throw new RuntimeException("Unable to apply animation speed.", e);
        }
    }

    private static String getDensityString(DisplayMetrics displayMetrics) {
        switch (displayMetrics.densityDpi) {
            case DisplayMetrics.DENSITY_LOW:
                return "ldpi";
            case DisplayMetrics.DENSITY_MEDIUM:
                return "mdpi";
            case DisplayMetrics.DENSITY_HIGH:
                return "hdpi";
            case DisplayMetrics.DENSITY_XHIGH:
                return "xhdpi";
            case DisplayMetrics.DENSITY_XXHIGH:
                return "xxhdpi";
            case DisplayMetrics.DENSITY_XXXHIGH:
                return "xxxhdpi";
            case DisplayMetrics.DENSITY_TV:
                return "tvdpi";
            default:
                return "unknown";
        }
    }

    private static String getSizeString(long bytes) {
        String[] units = new String[]{"B", "KB", "MB", "GB"};
        int unit = 0;
        while (bytes >= 1024) {
            bytes /= 1024;
            unit += 1;
        }
        return bytes + units[unit];
    }

    private void showNewNetworkProxyDialog(final ProxyAdapter proxyAdapter) {
        final int originalSelection = networkProxy.isSet() ? ProxyAdapter.PROXY : ProxyAdapter.NONE;

        View view = LayoutInflater.from(this).inflate(R.layout.debug_drawer_network_proxy, null);
        final EditText host = (EditText) view.findViewById(R.id.debug_drawer_network_proxy_host);

        new AlertDialog.Builder(this) //
                .setTitle("Set Network Proxy")
                .setView(view)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        networkProxyView.setSelection(originalSelection);
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Use", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        String theHost = host.getText().toString();
                        if (!StringUtils.isBlank(theHost)) {
                            String[] parts = theHost.split(":", 2);
                            SocketAddress address =
                                    InetSocketAddress.createUnresolved(parts[0], Integer.parseInt(parts[1]));

                            networkProxy.set(theHost); // Persist across restarts.
                            proxyAdapter.notifyDataSetChanged(); // Tell the spinner to update.
                            networkProxyView.setSelection(ProxyAdapter.PROXY); // And show the proxy.

                            client.setProxy(new Proxy(HTTP, address));
                        } else {
                            networkProxyView.setSelection(originalSelection);
                        }
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        networkProxyView.setSelection(originalSelection);
                    }
                })
                .show();
    }

    private void showCustomEndpointDialog(final int originalSelection, String defaultUrl) {
        View view = LayoutInflater.from(this).inflate(R.layout.debug_drawer_network_endpoint, null);
        final EditText url = (EditText) view.findViewById(R.id.debug_drawer_network_endpoint_url);
        url.setText(defaultUrl);
        url.setSelection(url.length());

        new AlertDialog.Builder(this) //
                .setTitle("Set Network Endpoint")
                .setView(view)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        endpointView.setSelection(originalSelection);
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Use", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        String theUrl = url.getText().toString();
                        if (!StringUtils.isBlank(theUrl)) {
                            setEndpointAndRelaunch(theUrl);
                        } else {
                            endpointView.setSelection(originalSelection);
                        }
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        endpointView.setSelection(originalSelection);
                    }
                })
                .show();
    }

    private void setEndpointAndRelaunch(String endpoint) {
        Timber.d("Setting network endpoint to %s", endpoint);
        networkEndpoint.set(endpoint);

//        Intent newApp = new Intent(this, MainActivity.class);
//        newApp.setFlags(FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
//        this.startActivity(newApp);
       // U2020App.get(app).buildObjectGraphAndInject();
    }
}
