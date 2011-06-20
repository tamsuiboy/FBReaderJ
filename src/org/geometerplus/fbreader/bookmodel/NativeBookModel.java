/*
 * Copyright (C) 2011 Geometer Plus <contact@geometerplus.com>
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

package org.geometerplus.fbreader.bookmodel;


import org.geometerplus.zlibrary.core.image.ZLImageMap;
import org.geometerplus.zlibrary.text.model.*;

import org.geometerplus.fbreader.library.Book;


public class NativeBookModel extends BookModel {

	private ZLImageMap myImageMap;

	private ZLTextModel myBookTextModel;

	NativeBookModel(Book book) {
		super(book);
	}

	public void initBookModel(String[] imageIds, int[] imageIndices, int[] imageOffsets,
			String imageDirectoryName, String imageFileExtension, int imageBlocksNumber) {
		myImageMap = new ZLCachedImageMap(imageIds, imageIndices, imageOffsets, imageDirectoryName, imageFileExtension, imageBlocksNumber);
	}

	public ZLTextModel createTextModel(String id, String language,
			int paragraphsNumber, int[] entryIndices, int[] entryOffsets,
			int[] paragraphLenghts, int[] textSizes, byte[] paragraphKinds,
			String directoryName, String fileExtension, int blocksNumber) {
		if (myImageMap == null) {
			throw new RuntimeException("NativeBookModel hasn't been initialized with initBookModel method");
		}
		return new ZLTextNativeModel(id, language, paragraphsNumber, entryIndices, entryOffsets, paragraphLenghts, textSizes, paragraphKinds, directoryName, fileExtension, blocksNumber, myImageMap);
	}


	public void setBookTextModel(ZLTextModel model) {
		myBookTextModel = model;
	}


	@Override
	public ZLTextModel getTextModel() {
		return myBookTextModel;
	}

	@Override
	public ZLTextModel getFootnoteModel(String id) {
		return null;
	}

	@Override
	public Label getLabel(String id) {
		return null;
	}
}