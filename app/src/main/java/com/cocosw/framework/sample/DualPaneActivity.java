package com.cocosw.framework.sample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.cocosw.framework.core.Base;
import com.cocosw.framework.core.BaseFragment;
import com.cocosw.framework.core.DialogResultListener;
import com.cocosw.framework.sample.views.TwoPanelsLayout;

import butterknife.InjectView;


public class DualPaneActivity extends Base<Void> implements TwoPanelsLayout.PanelSlideListener,
        DialogResultListener {

    @InjectView(R.id.panel)
    TwoPanelsLayout mPanel;

    private Fragment master;
    private Fragment detail;

    @Override
    public int layoutId() {
        return R.layout.two_panel;
    }

    @Override
    protected void init(Bundle saveBundle) throws Exception {
        mPanel.setPanelSlideListener(this);
        mPanel.setSliderFadeColor(getResources().getColor(R.color.semitransparent));

        if (saveBundle == null) {
            master = onCreateMasterPane();
            master.setArguments(intentToFragmentArguments(getIntent()));
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.coco_master, master, "master")
                    .commit();
        } else {
            master = getSupportFragmentManager().findFragmentByTag(
                    "master");
            detail = getSupportFragmentManager().findFragmentByTag("detail");
        }

        CharSequence customTitle = getIntent().getStringExtra(
                Intent.EXTRA_TITLE);
        if (customTitle == null && master instanceof BaseFragment) {
            customTitle = ((BaseFragment<?>) master).getTitle();
        }
        mPanel.openPane();
        setTitle(!TextUtils.isEmpty(customTitle) ? customTitle : getTitle());
    }


    /**
     * Converts an intent into a {@link Bundle} suitable for use as fragment
     * arguments.
     */
    public static Bundle intentToFragmentArguments(final Intent intent) {
        final Bundle arguments = new Bundle();
        if (intent == null) {
            return arguments;
        }

        final Uri data = intent.getData();
        if (data != null) {
            arguments.putParcelable("_uri", data);
        }

        final Bundle extras = intent.getExtras();
        if (extras != null) {
            arguments.putAll(intent.getExtras());
        }

        return arguments;
    }

    /**
     * Converts a fragment arguments bundle into an intent.
     */
    public static Intent fragmentArgumentsToIntent(final Bundle arguments) {
        final Intent intent = new Intent();
        if (arguments == null) {
            return intent;
        }

        final Uri data = arguments.getParcelable("_uri");
        if (data != null) {
            intent.setData(data);
        }

        intent.putExtras(arguments);
        intent.removeExtra("_uri");
        return intent;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }

    /**
     * Called in <code>onCreate</code> when the fragment constituting this
     * activity is needed. The returned fragment's arguments will be set to the
     * intent used to invoke this activity.
     */
    protected Fragment onCreateMasterPane() {
        return Fragment.instantiate(this, getIntent().getAction());
    }

    public Fragment getMaster() {
        return master;
    }

    public Fragment getDetail() {
        return detail;
    }

    public void openDetail(Class<? extends BaseFragment> target, Intent extra) {
        detail = Fragment.instantiate(this, target.getName());
        detail.setArguments(intentToFragmentArguments(extra));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.coco_detail, detail, "detail")
                .commit();
        mPanel.closePane();
    }

    public void openDetail(Class<? extends BaseFragment> target, Fragment from, Intent extra, int requestCode) {
        detail = Fragment.instantiate(this, target.getName());
        detail.setArguments(intentToFragmentArguments(extra));
        detail.setTargetFragment(from,requestCode);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.coco_detail, detail, "detail")
                .commit();
        mPanel.closePane();
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {

    }

    @Override
    public void onPanelOpened(View panel) {

    }

    @Override
    public void onPanelClosed(View panel) {

    }

    @Override
    public void onBackPressed() {
        if (mPanel.isOpen())
            super.onBackPressed();
        else mPanel.closePane();
    }
}
