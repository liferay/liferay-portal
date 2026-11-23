/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.zip.internal.reader;

import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author Raymond Augé
 */
public class ZipReaderImpl extends BaseZipReader {

	public ZipReaderImpl(File file) {
		super(file);
	}

	public ZipReaderImpl(InputStream inputStream) throws IOException {
		super(inputStream);
	}

	@Override
	public void close() {
		try {
			if (_zipFile != null) {
				_zipFile.close();
			}
		}
		catch (IOException ioException) {
			_log.error(ioException);
		}
		finally {
			super.close();
		}
	}

	@Override
	public List<String> doGetEntries() throws IOException {
		ZipFile zipFile = _getZipFile();

		Enumeration<? extends ZipEntry> enumeration = zipFile.entries();

		List<String> entries = new ArrayList<>();

		while (enumeration.hasMoreElements()) {
			ZipEntry zipEntry = enumeration.nextElement();

			if (zipEntry.isDirectory()) {
				continue;
			}

			entries.add(zipEntry.getName());
		}

		return entries;
	}

	@Override
	public byte[] doGetEntryAsByteArray(String name) throws IOException {
		return StreamUtil.toByteArray(doGetEntryAsInputStream(name));
	}

	@Override
	public InputStream doGetEntryAsInputStream(String name) throws IOException {
		ZipFile zipFile = _getZipFile();

		ZipEntry zipEntry = zipFile.getEntry(name);

		if ((zipEntry != null) && !zipEntry.isDirectory()) {
			return zipFile.getInputStream(zipEntry);
		}

		return null;
	}

	@Override
	public List<String> doGetFolderEntries(String path) throws IOException {
		Path javaPath = Paths.get(path);

		javaPath = javaPath.normalize();

		path = javaPath.toString();

		ZipFile zipFile = _getZipFile();

		Enumeration<? extends ZipEntry> enumeration = zipFile.entries();

		List<String> entries = new ArrayList<>();

		while (enumeration.hasMoreElements()) {
			ZipEntry zipEntry = enumeration.nextElement();

			if (zipEntry.isDirectory()) {
				continue;
			}

			String fileName = zipEntry.getName();

			int pos = fileName.lastIndexOf(CharPool.SLASH);

			String folderName = StringPool.BLANK;

			if (pos > 0) {
				folderName = fileName.substring(0, pos);
			}

			if (!folderName.equals(path)) {
				continue;
			}

			entries.add(fileName);
		}

		return entries;
	}

	private ZipFile _getZipFile() throws IOException {
		if (_zipFile == null) {
			_zipFile = new ZipFile(file);
		}

		return _zipFile;
	}

	private static final Log _log = LogFactoryUtil.getLog(ZipReaderImpl.class);

	private ZipFile _zipFile;

}