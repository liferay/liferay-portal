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
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.zip.ZipReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author Raymond Augé
 */
public class ZipReaderImpl implements ZipReader {

	public ZipReaderImpl(File file) {
		_file = file;
	}

	public ZipReaderImpl(InputStream inputStream) throws IOException {
		this(FileUtil.createTempFile(inputStream));

		_tempFile = _file;
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
			if (_tempFile != null) {
				_deleteFile(_tempFile);
			}
		}
	}

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

	public byte[] doGetEntryAsByteArray(String name) throws IOException {
		return StreamUtil.toByteArray(doGetEntryAsInputStream(name));
	}

	public InputStream doGetEntryAsInputStream(String name) throws IOException {
		ZipFile zipFile = _getZipFile();

		ZipEntry zipEntry = zipFile.getEntry(name);

		if ((zipEntry != null) && !zipEntry.isDirectory()) {
			return zipFile.getInputStream(zipEntry);
		}

		return null;
	}

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

	@Override
	public final List<String> getEntries() {
		try {
			List<String> entries = doGetEntries();

			Collections.sort(entries);

			return entries;
		}
		catch (IOException ioException) {
			throw new UncheckedIOException(ioException);
		}
	}

	@Override
	public final byte[] getEntryAsByteArray(String name) {
		if (Validator.isNull(name)) {
			return null;
		}

		if (name.startsWith(StringPool.SLASH)) {
			name = name.substring(1);
		}

		try {
			return doGetEntryAsByteArray(name);
		}
		catch (IOException ioException) {
			_log.error(ioException);
		}

		return null;
	}

	@Override
	public final InputStream getEntryAsInputStream(String name) {
		if (Validator.isNull(name)) {
			return null;
		}

		if (name.startsWith(StringPool.SLASH)) {
			name = name.substring(1);
		}

		try {
			return doGetEntryAsInputStream(name);
		}
		catch (IOException ioException) {
			throw new UncheckedIOException(ioException);
		}
	}

	@Override
	public final String getEntryAsString(String name) {
		byte[] bytes = getEntryAsByteArray(name);

		if (bytes != null) {
			return new String(bytes);
		}

		return null;
	}

	@Override
	public final List<String> getFolderEntries(String path) {
		if (Validator.isNull(path)) {
			return Collections.emptyList();
		}

		if (path.startsWith(StringPool.SLASH)) {
			path = path.substring(1);
		}

		try {
			List<String> folderEntries = doGetFolderEntries(path);

			Collections.sort(folderEntries);

			return folderEntries;
		}
		catch (IOException ioException) {
			throw new UncheckedIOException(ioException);
		}
	}

	private void _deleteFile(File file) {
		if (!file.delete() && _log.isWarnEnabled()) {
			_log.warn("Unable to delete file " + file.getAbsolutePath());
		}
	}

	private ZipFile _getZipFile() throws IOException {
		if (_zipFile == null) {
			_zipFile = new ZipFile(_file);
		}

		return _zipFile;
	}

	private static final Log _log = LogFactoryUtil.getLog(ZipReaderImpl.class);

	private final File _file;
	private File _tempFile;
	private ZipFile _zipFile;

}