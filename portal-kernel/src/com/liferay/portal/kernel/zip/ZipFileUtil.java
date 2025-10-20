/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.zip;

import com.liferay.portal.kernel.io.unsync.UnsyncFilterInputStream;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.util.zip.ZipFile;

/**
 * @author Shuyang Zhou
 */
public class ZipFileUtil {

	public static InputStream openInputStream(File file, String entryName)
		throws IOException {

		ZipFile zipFile = new ZipFile(file);

		return new ZipFileInputStream(
			zipFile.getInputStream(zipFile.getEntry(entryName)), zipFile);
	}

	private static class ZipFileInputStream extends UnsyncFilterInputStream {

		@Override
		public void close() throws IOException {
			try {
				inputStream.close();
			}
			finally {
				_zipFile.close();
			}
		}

		private ZipFileInputStream(InputStream inputStream, ZipFile zipFile) {
			super(inputStream);

			_zipFile = zipFile;
		}

		private final ZipFile _zipFile;

	}

}