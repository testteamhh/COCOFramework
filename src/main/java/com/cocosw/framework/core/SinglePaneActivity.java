package com.cocosw.framework.core;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import com.actionbarsherlock.view.MenuItem;
import com.cocosw.framework.R;


/**
 * 这是一个可以把fragment作为容器的activity
 * 
 * @author kaliao
 */
public class SinglePaneActivity extends Base<Void> implements
		DialogResultListener {

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

	private Fragment mFragment;

	/**
	 * Called in <code>onCreate</code> when the fragment constituting this
	 * activity is needed. The returned fragment's arguments will be set to the
	 * intent used to invoke this activity.
	 */
	protected Fragment onCreatePane() {
		return Fragment.instantiate(this, getIntent().getAction());
	}

	public Fragment getFragment() {
		return mFragment;
	}

	@Override
	public int layoutId() {
		return R.layout.activity_singlepane_empty;
	}

	@Override
	protected void init(final Bundle saveBundle) throws Exception {

		if (saveBundle == null) {
			mFragment = onCreatePane();
			mFragment.setArguments(SinglePaneActivity
					.intentToFragmentArguments(getIntent()));
			getSupportFragmentManager().beginTransaction()
					.add(R.id.root_container, mFragment, "single_pane")
					.commit();
		} else {
			mFragment = getSupportFragmentManager().findFragmentByTag(
					"single_pane");
		}

		CharSequence customTitle = getIntent().getStringExtra(
				Intent.EXTRA_TITLE);
		if (customTitle == null && mFragment instanceof BaseFragment) {
			customTitle = ((BaseFragment<?>) mFragment).getTitle();
		}

		setTitle(!TextUtils.isEmpty(customTitle) ? customTitle : getTitle());

	}

	public static void start(final Class<Fragment> fragment,
			final Activity act, final Intent extras) {
		act.startActivity(new Intent(act, SinglePaneActivity.class).setAction(
				fragment.getName()).putExtras(extras));
	}

	public static void start(final Class<Fragment> fragment, final Activity act) {
		act.startActivity(new Intent(act, SinglePaneActivity.class)
				.setAction(fragment.getName()));
	}

}
