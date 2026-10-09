/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.zip.internal.reader;

import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.io.unsync.UnsyncFilterInputStream;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;

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
public class IOZipReader extends BaseZipReader {

	public IOZipReader(File file) {
		super(file);
	}

	public IOZipReader(InputStream inputStream) throws IOException {
		super(inputStream);
	}

	@Override
	public List<String> doGetEntries() throws IOException {
		List<String> entries = new ArrayList<>();

		try (ZipFile zipFile = new ZipFile(file)) {
			Enumeration<? extends ZipEntry> enumeration = zipFile.entries();

			while (enumeration.hasMoreElements()) {
				ZipEntry zipEntry = enumeration.nextElement();

				if (zipEntry.isDirectory()) {
					continue;
				}

				entries.add(zipEntry.getName());
			}

			return entries;
		}
	}

	@Override
	public byte[] doGetEntryAsByteArray(String name) throws IOException {
		return StreamUtil.toByteArray(doGetEntryAsInputStream(name));
	}

	@Override
	public InputStream doGetEntryAsInputStream(String name) throws IOException {
		final ZipFile zipFile = new ZipFile(file);

		ZipEntry zipEntry = zipFile.getEntry(name);

		if ((zipEntry != null) && !zipEntry.isDirectory()) {
			InputStream inputStream = zipFile.getInputStream(zipEntry);

			// Null check inputStream to overcome
			// jdk.util.zip.ensureTrailingSlash issue in between
			// [JDK 8u141, JDK 8u144)

			if (inputStream != null) {
				return new UnsyncFilterInputStream(inputStream) {

					@Override
					public void close() throws IOException {
						super.close();

						zipFile.close();
					}

				};
			}
		}

		zipFile.close();

		return null;
	}

	@Override
	public List<String> doGetFolderEntries(String path) throws IOException {
		Path javaPath = Paths.get(path);

		javaPath = javaPath.normalize();

		path = javaPath.toString();

		List<String> entries = new ArrayList<>();

		try (ZipFile zipFile = new ZipFile(file)) {
			Enumeration<? extends ZipEntry> enumeration = zipFile.entries();

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
		}

		return entries;
	}

}