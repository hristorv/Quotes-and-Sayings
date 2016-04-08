package tk.example.quotesandsayings.view.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tk.example.quotesandsayings.R;
import tk.example.quotesandsayings.controller.AlbumsController;
import tk.example.quotesandsayings.model.AlbumsData;
import tk.example.quotesandsayings.model.Constants;
import tk.example.quotesandsayings.model.ImageData;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;

import com.afollestad.materialdialogs.prefs.MaterialMultiSelectListPreference;

public class WidgetConfigFragment extends PreferenceFragment {
	int mAppWidgetId;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Get the app widget ID
		Intent intent = getActivity().getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
		}

		addPreferencesFromResource(R.xml.widget_configuration);
		final MaterialMultiSelectListPreference listPreference = (MaterialMultiSelectListPreference) findPreference(Constants.PreferencesKeys.KEY_PREF_WIDGET_FILTER);
		setListPreference(listPreference);
		listPreference
				.setOnPreferenceClickListener(new MyPrefsOnClickListener());
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
			if (preference.getKey().equals(
					Constants.PreferencesKeys.KEY_PREF_WIDGET_FILTER)) {
				catEntries = ImageData.getInstance().getCategoriesTitles();
				catEntryValues = ImageData.getInstance().getCategoriesTitles();
			}
			if (catEntries != null && catEntryValues != null) {
				AlbumsData.getInstance().initAlbums(getActivity());
				CharSequence[] entries = concatArrays(catEntries, new AlbumsController().getAlbumTitles());
				CharSequence[] entryValues = concatArrays(catEntryValues,
						new AlbumsController().getAlbumTitles());
				list.setEntries(entries);
				list.setEntryValues(entryValues);

			}
		}
	}

	private class MyPrefsOnClickListener implements OnPreferenceClickListener {

		@Override
		public boolean onPreferenceClick(Preference preference) {
			setListPreference(preference);
			return false;
		}

	}
}
