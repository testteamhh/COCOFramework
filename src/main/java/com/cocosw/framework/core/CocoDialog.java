package com.cocosw.framework.core;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import com.cocosw.accessory.views.CocoBundle;
import com.cocosw.framework.R;


public class CocoDialog extends BaseDialog<Void> {

	private Fragment mFragment;
	private int style;

	private CocoDialog() {

	}

	private CocoDialog(final int style) {
		this.style = style;
	}

	/**
	 * Called in <code>onCreate</code> when the fragment constituting this
	 * activity is needed. The returned fragment's arguments will be set to the
	 * intent used to invoke this activity.
	 */
	protected Fragment onCreatePane() {
		mFragment = Fragment.instantiate(getActivity(), getArguments()
				.getString("_fragment"));
		mFragment.setArguments(getArguments());
		if (mFragment instanceof BaseFragment) {
			((BaseFragment<?>) mFragment).parentDialog = this;
		}
		return mFragment;
	}

	public Fragment getFragment() {
		return mFragment;
	}

	@Override
	public int layoutId() {
		return R.layout.activity_singlepane_empty;
	}

	@Override
	protected void setupUI(final View view, final Bundle bundle)
			throws Exception {
		if (bundle == null) {
			mFragment = onCreatePane();
  			getChildFragmentManager().beginTransaction()
					.add(R.id.root_container, mFragment, "single_pane")
					.commit();
		} else {
			mFragment = getChildFragmentManager().findFragmentByTag(
					"single_pane");
		}
	}

	@Override
	public int getStyle() {
		return style;
	}

	public static class Builder {

		private final Class<? extends Fragment> fragment;
		private Bundle arg;
		private int style = android.R.style.Theme_Translucent;

		public static int DIALOGSTYLE = R.style.dialog;
		public static int TRANSPARENTSTYLR = android.R.style.Theme_Translucent;

		public Builder(final Class<? extends Fragment> fragment) {
			this.fragment = fragment;
		}

		public Builder arg(final Bundle arg) {
			this.arg = arg;
			return this;
		}

		public Builder arg(final CocoBundle arg) {
			this.arg = arg.getBundle();
			return this;
		}

		public Builder style(final int style) {
			this.style = style;
			return this;
		}

		public Builder show(final FragmentManager fragmentManager) {
			final Bundle b = new Bundle();
			b.putString("_fragment", fragment.getName());
			if (arg != null) {
				b.putAll(arg);
			}
			final CocoDialog d = new CocoDialog(style);
			d.setArguments(b);
			BaseDialog.show(fragmentManager, d);
			return this;
		}
	}

}
