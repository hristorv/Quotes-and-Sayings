package tk.example.quotesandsayings.view.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tk.example.quotesandsayings.R;
import tk.example.quotesandsayings.controller.AlbumsController;
import tk.example.quotesandsayings.model.AlbumsData;
import tk.example.quotesandsayings.model.Constants;
import tk.example.quotesandsayings.model.ImageData;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.prefs.MaterialListPreference;
import com.afollestad.materialdialogs.prefs.MaterialMultiSelectListPreference;

public class SettingsFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FloatingActionButton fab = (FloatingActionButton) getActivity()
				.findViewById(R.id.fab);
		if (fab != null) {
			fab.hide();
			fab.setClickable(false);
		}
		String settings = getArguments().getString(Constants.Settings.SETTINGS);
		if (Constants.Settings.WALLPAPER.equals(settings)) {
			addPreferencesFromResource(R.xml.settings_wallpaper);
			final MaterialListPreference scalingPreference = (MaterialListPreference) findPreference(Constants.PreferencesKeys.KEY_PREF_WALLPAPER_SCALING);
			scalingPreference.setOnPreferenceClickListener(new ScalePrefListener());
			final MaterialMultiSelectListPreference listPreference = (MaterialMultiSelectListPreference) findPreference(Constants.PreferencesKeys.KEY_PREF_WALLPAPER_FILTER);
			setListPreference(listPreference);
			listPreference
					.setOnPreferenceClickListener(new MyPrefsOnClickListener());
		} else if (Constants.Settings.NOTIFICATION.equals(settings)) {
			addPreferencesFromResource(R.xml.settings_notification);
			final MaterialMultiSelectListPreference listPreference = (MaterialMultiSelectListPreference) findPreference(Constants.PreferencesKeys.KEY_PREF_NOTIFICATION_FILTER);
			setListPreference(listPreference);
			listPreference
					.setOnPreferenceClickListener(new MyPrefsOnClickListener());
		} else if (Constants.Settings.WIDGET.equals(settings)) {
			addPreferencesFromResource(R.xml.settings_widget);
			final MaterialMultiSelectListPreference listPreference = (MaterialMultiSelectListPreference) findPreference(Constants.PreferencesKeys.KEY_PREF_WIDGET_FILTER);
			setListPreference(listPreference);
			listPreference
					.setOnPreferenceClickListener(new MyPrefsOnClickListener());
		}

	}

	private void setScalingFooter(MaterialListPreference scalingPreference) {
		if (scalingPreference instanceof MaterialListPreference) {
			MaterialListPreference pref = (MaterialListPreference) scalingPreference;
			ListView list = ((MaterialDialog) pref.getDialog()).getListView();
			// Remove and reset the adapter so the addFooterView() method works
			// on pre 4.4 devices.
			ListAdapter adapter = list.getAdapter();
			list.setAdapter(null);

			ImageView image = new ImageView(getActivity());
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					25, 25);
			layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
			layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
			image.setScaleType(ScaleType.FIT_XY);
			image.setAdjustViewBounds(true);
			image.setImageResource(R.drawable.wallpaper_scaling);
			list.addFooterView(image, null, false);
			// Sets back the old adapter.
			list.setAdapter(adapter);
		}

	}

	String[] concatArrays(String[] first, String[] second) {
		List<String> both = new ArrayList<String>(first.length + second.length);
		Collections.addAll(both, first);
		Collections.addAll(both, second);
		return both.toArray(new String[both.size()]);
	}

	private void setListPreference(Preference preference) {
		if (preference instanceof MaterialMultiSelectListPreference) {
			MaterialMultiSelectListPreference list = (MaterialMultiSelectListPreference) preference;
			String[] catEntries = null;
			String[] catEntryValues = null;
			catEntries = ImageData.getInstance().getCategoriesTitles();
			catEntryValues = ImageData.getInstance().getCategoriesTitles();
			if (catEntries != null && catEntryValues != null) {
				CharSequence[] entries = concatArrays(catEntries,
						new AlbumsController().getAlbumTitles());
				CharSequence[] entryValues = concatArrays(catEntryValues,
						new AlbumsController().getAlbumTitles());
				list.setEntries(entries);
				list.setEntryValues(entryValues);

			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_settings, container,
				false);
		return rootView;
	}

	private class MyPrefsOnClickListener implements OnPreferenceClickListener {

		@Override
		public boolean onPreferenceClick(Preference preference) {
			setListPreference(preference);
			return false;
		}

	}
	
	private class ScalePrefListener implements OnPreferenceClickListener {

		@Override
		public boolean onPreferenceClick(Preference preference) {
			setScalingFooter((MaterialListPreference) preference);
			return false;
		}

	}

}
