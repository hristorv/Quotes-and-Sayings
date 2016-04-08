package tk.example.quotesandsayings.model;

public final class Constants {
	public final class CategoriesIndex {
		public static final int TOP_CATEGORY = 0;
		public static final int FAVOURITES_CATEGORY = 1;
	}

	public final class Extra {
		public static final String IMAGE_PARCEABLE = "tk.example.quotesandsayings.IMAGE";
		public static final int REQUEST_CODE_IMAGE = 1;
		public static final String IMAGE_POSITION = "tk.example.quotesandsayings.IMAGE_POSITION";
		public static final String CATEGORY_INDEX = "tk.example.quotesandsayings.CATEGORY_INDEX";
		public static final String SERVICE_TAG_KEY = "service";
		public static final String IMAGE_ARRAY = "tk.example.quotesandsayings.IMAGE_ARRAY";
		public static final String DEFAULT_PREFS_NAME = "tk.example.quotesandsayings_preferences";
		public static final String ALBUM_INDEX = "tk.example.quotesandsayings.ALBUM_INDEX";
		public static final String ALBUM_BOOLEAN = "tk.example.quotesandsayings.ALBUM_BOOLEAN";
		public static final String FAVORITES_BOOLEAN = "tk.example.quotesandsayings.FAVORITES_BOOLEAN";
		public static final String IS_FROM_WIDGET = "tk.example.quotesandsayings.IS_FROM_WIDGET";
		public static final String IS_OFFLINE = "tk.example.quotesandsayings.IS_OFFLINE";
	}

	public final class PreferencesKeys {
		public static final String KEY_FIRST_TIME_HELP = "tk.example.quotesandsayings.IS_FOR_FIRST_TIME";
		public static final String KEY_PREF_WALLPAPER_ACTIVE = "pref_wallpaper_active";
		public static final String KEY_PREF_NOTIFICATION_ACTIVE = "pref_notification_active";
		public static final String KEY_PREF_WALLPAPER_TIME = "pref_wallapper_interval";
		public static final String KEY_PREF_WALLPAPER_FILTER = "pref_wallapper_filter";
		public static final String KEY_PREF_WIDGET_TIME = "pref_widget_interval";
		public static final String KEY_PREF_WIDGET_FILTER = "pref_widget_filter";
		public static final String KEY_PREF_NOTIFICATION_TIME = "pref_notification_interval";
		public static final String KEY_PREF_NOTIFICATION_FILTER = "pref_notification_filter";
		public static final String CATEGORY_INDEX_WALLPAPER = "category_index_wallpaper";
		public static final String CATEGORY_INDEX_WIDGET = "category_index_widget";
		public static final String CATEGORY_INDEX_NOTIFICATION = "category_index_notification";
		public static final String IMAGE_INDEX_WALLPAPER = "image_index_wallpaper";
		public static final String IMAGE_INDEX_WIDGET = "image_index_widget";
		public static final String IMAGE_INDEX_NOTIFICATION = "image_index_notification";
		public static final String KEY_PREF_WIDGET_ACTIVE = "pref_widget_active";
		public static final String WIDTH_PREF = "pref_width";
		public static final String HEIGHT_PREF = "pref_height";
		public static final String REAL_WIDTH_PREF = "pref_real_width";
		public static final String REAL_HEIGHT_PREF = "pref_real_height";
		public static final String KEY_PREF_WALLPAPER_SCALING = "pref_wallpaper_scaling";
	}

	public final class ServicesID {
		public static final int NOTIFICATION_ALARM_ID = 2;
		public static final int WIDGET_ALARM_ID = 1;
		public static final int WALLPAPER_ALARM_ID = 0;
	}

	public final class DrawerIndexes {
		public static final int WALLPAPER = 3;
		public static final int NOTIFICATION = 4;
		public static final int WIDGET = 5;
		public static final int ABOUT = 6;
		public static final int HELP = 7;
		public static final int CATEGORIES = 1;
		public static final int ALBUMS = 2;
	}

	public final class Settings {
		public static final String WIDGET = "tk.example.quotesandsayings.widget";
		public static final String NOTIFICATION = "tk.example.quotesandsayings.notification";
		public static final String WALLPAPER = "tk.example.quotesandsayings.wallpaper";
		public static final String SETTINGS = "tk.example.quotesandsayings.settings";
	}

	public final class FragmentNames {
		public static final String HELP_FRAGMENT = "tk.example.quotesandsayings.help_fragment";
		public static final String CATEGORIES_FRAGMENT = "tk.example.quotesandsayings.categories_fragment";
		public static final String ALBUMS_FRAGMENT = "tk.example.quotesandsayings.albums_fragment";

	}

	public final class WallpaperScaling {
		public static final String DEFAULT = "Default (Recommended)";
		public static final String STRETCH = "Stretch";
		public static final String FIT = "Fit";
		public static final String CENTER = "Center";
	}
	
	public final class NotificationIDs {
		public static final int NOTIFICATION_IMAGE = 001;
		public static final int NOTIFICATION_NOTIF_ERROR = 002;
		public static final int NOTIFICATION_WALLPAPER_ERROR = 003;
	}

}
