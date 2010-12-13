/*
 * Copyright (C) 2010 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.android.fbreader.library;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.geometerplus.android.fbreader.FBReader;
import org.geometerplus.fbreader.Paths;
import org.geometerplus.zlibrary.core.filesystem.ZLFile;
import org.geometerplus.zlibrary.core.resources.ZLResource;
import org.geometerplus.zlibrary.ui.android.R;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public final class FileManager extends ListActivity {
	public static String FILE_MANAGER_PATH = "file_manager.path";
	public static String LOG = "FileManager";
	private Thread myCurFilterThread;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v(LOG, "onCreate()");
		super.onCreate(savedInstanceState);

		List<FileItem> items = Collections.synchronizedList(new ArrayList<FileItem>());
		FManagerAdapter adapter = new FManagerAdapter(this, items, R.layout.library_tree_item);
		setListAdapter(adapter);

		ReturnRes returnRes = new ReturnRes(items, adapter);
		SmartFilter filter = new SmartFilter(this, returnRes);
		
		String path = getIntent().getExtras().getString(FileManager.FILE_MANAGER_PATH);
		if (path.equals("")) {
			initfill(items, adapter);
		} else {
			fill(path, filter);
		}
			
		getListView().setTextFilterEnabled(true);
		getListView().setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				step(((TextView)view.findViewById(R.id.library_tree_item_childrenlist)).getText().toString());
			}
		});
	}
	
	private void step(String path) {
		ZLFile file = ZLFile.createFileByPath(path);
		if (file.isDirectory() || file.isArchive()) {
			if (myCurFilterThread != null) {		// TODO question!
				myCurFilterThread.interrupt();
			}
			Intent i = new Intent(this, FileManager.class);
			i.putExtra(FileManager.FILE_MANAGER_PATH, path);
			startActivity(i);
		}
		else {
			openBook(path);
		}
	}

	private void initfill(List<FileItem> items, FManagerAdapter adapter){
		// FIXME does not work if I try to add FileItems directly into the adapter
		ZLResource resource = ZLResource.resource("fmanagerView");
		String nameRoot = resource.getResource("root").getValue();
		String nameSdcard = resource.getResource("sdcard").getValue();
		String nameBooks = resource.getResource("books").getValue();
		
		final ZLFile root = ZLFile.createFileByPath("/");
		final ZLFile sdCard = ZLFile.createFileByPath(Environment.getExternalStorageDirectory().getPath());
		final ZLFile booksDirectory = ZLFile.createFileByPath(Paths.BooksDirectoryOption().getValue());
		
		items.add(new FileItem(root, nameRoot));
		items.add(new FileItem(sdCard, nameSdcard));
		items.add(new FileItem(booksDirectory, nameBooks));

		for (FileItem o : items){
			adapter.add(o);
		}
	}
	
	private void fill(String path, SmartFilter filter){
		ZLFile file = ZLFile.createFileByPath(path);
		filter.setPreferences(file);
		myCurFilterThread = new Thread(null, filter, "MagentoBackground");
		myCurFilterThread.start();
	}

	private void openBook(String path){
		Intent i = new Intent(this, FBReader.class);
		i.setAction(Intent.ACTION_VIEW);
		i.putExtra(FBReader.BOOK_PATH_KEY, path);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(i);
	}
}