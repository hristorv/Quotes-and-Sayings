package tk.example.quotesandsayings.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

public class AlbumsData {
	private static AlbumsData instance = null;
	private Album[] albums;
	private String oldAlbumName;
	private Album deletedAlbum;
	private int deletedAlbumIndex;

	public AlbumsData() {
	}

	public static AlbumsData getInstance() {
		if (instance == null) {
			instance = new AlbumsData();
		}
		return instance;
	}

	public Album[] getAlbums() {
		return this.albums;
	}

	public void setAlbums(Album[] albums) {
		this.albums = albums;
	}

	public void initAlbums(Context context) {
		File myFile = new File(context.getFilesDir(), "albums.json");
		FileInputStream fIn = null;
		try {
			fIn = new FileInputStream(myFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
		String aDataRow = "";
		String aBuffer = ""; // Holds the text
		try {
			while ((aDataRow = myReader.readLine()) != null) {
				aBuffer += aDataRow;
			}
			myReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Gson gson = new Gson();
		setAlbums(gson.fromJson(aBuffer, Album[].class));
		if (getAlbums() == null) {
			setAlbums(new Album[0]);
		}
	}

	

	public void updateAlbumsFile(Context context) {
		Gson gson = new Gson();
		String jsonString = gson.toJson(getAlbums());
		File myFile = new File(context.getFilesDir(), "albums.json");
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(myFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
		try {
			myOutWriter.append(jsonString);
			myOutWriter.close();
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	

	public void createAlbumsFile(Context context) {
		File myFile = new File(context.getFilesDir(), "albums.json");
		try {
			myFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setOldAlbumName(String oldAlbumName) {
		this.oldAlbumName=oldAlbumName;
		
	}

	public String getOldAlbumName() {
		return oldAlbumName;
	}

	public void setDeletedAlbum(Album album) {
		this.deletedAlbum=album;
		
	}

	public void setDeletedAlbumIndex(int albumIndex) {
		this.deletedAlbumIndex=albumIndex;
		
	}

	public Album getDeletedAlbum() {
		return deletedAlbum;
	}

	public int getDeletedAlbumIndex() {
		return deletedAlbumIndex;
	}


}
