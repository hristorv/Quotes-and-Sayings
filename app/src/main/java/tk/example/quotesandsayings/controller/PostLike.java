package tk.example.quotesandsayings.controller;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;

import tk.example.quotesandsayings.R;
import tk.example.quotesandsayings.model.Image;
import tk.example.quotesandsayings.model.ImageData;
import android.content.Context;
import android.os.AsyncTask;

public class PostLike extends AsyncTask<Void, Void, Void> {

	private Context context;
	private String category;
	private String imageUrl;

	public PostLike(Context context, String category, String imageUrl) {
		this.context = context;
		this.category = category;
		this.imageUrl = imageUrl;
	}

	@Override
	protected Void doInBackground(Void... arg0) {
		postData();
		return null;
	}

	public void postData() {
		try {
			String param = "category=" + category + "&url=" + imageUrl;

			URL url = new URL(context.getString(R.string.likes_url));
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			urlConnection.setDoOutput(true);
			urlConnection.setRequestMethod("POST");
			urlConnection.setChunkedStreamingMode(0);
			DataOutputStream wr = new DataOutputStream(
					urlConnection.getOutputStream());
			wr.writeBytes(param);
			wr.flush();
			wr.close();

			BufferedReader in = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
