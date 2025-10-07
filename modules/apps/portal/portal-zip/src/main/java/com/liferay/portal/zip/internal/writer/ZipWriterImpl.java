/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.zip.internal.writer;

import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.memory.DeleteFileFinalizeAction;
import com.liferay.petra.memory.FinalizeManager;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.kernel.zip.ZipWriter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import java.lang.reflect.Field;

import java.net.URI;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Raymond Augé
 */
public class ZipWriterImpl implements ZipWriter {

	public ZipWriterImpl() {
		this(
			new File(
				StringBundler.concat(
					SystemProperties.get(SystemProperties.TMP_DIR),
					StringPool.SLASH, PortalUUIDUtil.generate(), ".zip")));

		FinalizeManager.register(
			_file, new DeleteFileFinalizeAction(_file.getAbsolutePath()),
			FinalizeManager.PHANTOM_REFERENCE_FACTORY);
	}

	public ZipWriterImpl(File file) {
		_file = file.getAbsoluteFile();

		URI fileURI = _file.toURI();

		_uri = URI.create("jar:file:" + fileURI.getRawPath());

		try (FileSystem fileSystem = FileSystems.newFileSystem(
				_uri, Collections.singletonMap("create", "true"))) {
		}
		catch (IOException ioException) {
			throw new UncheckedIOException(ioException);
		}
	}

	@Override
	public void addEntry(String name, byte[] bytes) throws IOException {
		if (bytes == null) {
			return;
		}

		if (ExportImportThreadLocal.isExportInProcess()) {
			if (_exportEntries == null) {
				_exportEntries = new LinkedList<>();
			}

			_exportEntries.add(
				new AbstractMap.SimpleImmutableEntry<>(name, bytes));

			_exportEntriesBytes += bytes.length;

			if (_exportEntriesBytes >=
					PropsValues.ZIP_FILE_WRITER_EXPORT_BUFFER_SIZE) {

				_writeExportEntries();
			}

			return;
		}

		try (FileSystem fileSystem = FileSystems.newFileSystem(
				_uri, Collections.emptyMap())) {

			Path path = fileSystem.getPath(name);

			_createDirectories(fileSystem, path.getParent());

			Files.write(
				path, bytes, StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
		}
	}

	@Override
	public void addEntry(String name, InputStream inputStream)
		throws IOException {

		if (inputStream == null) {
			return;
		}

		if (ExportImportThreadLocal.isExportInProcess()) {
			addEntry(name, StreamUtil.toByteArray(inputStream));

			return;
		}

		try (FileSystem fileSystem = FileSystems.newFileSystem(
				_uri, Collections.emptyMap())) {

			Path path = fileSystem.getPath(name);

			_createDirectories(fileSystem, path.getParent());

			Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
		}
	}

	@Override
	public void addEntry(String name, String s) throws IOException {
		if (s == null) {
			return;
		}

		addEntry(name, s.getBytes(StringPool.UTF8));
	}

	@Override
	public void addEntry(String name, StringBuilder sb) throws IOException {
		if (sb == null) {
			return;
		}

		addEntry(name, sb.toString());
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link #getFile()}
	 */
	@Deprecated
	@Override
	public byte[] finish() throws IOException {
		return FileUtil.getBytes(getFile());
	}

	@Override
	public File getFile() {
		if (_exportEntries != null) {
			_writeExportEntries();
		}

		return _file;
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link #getFile()}
	 */
	@Deprecated
	@Override
	public String getPath() {
		return _file.getPath();
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link #getFile()}
	 */
	@Deprecated
	@Override
	public void umount() {
	}

	private void _createDirectories(FileSystem fileSystem, Path parentPath)
		throws IOException {

		if ((parentPath == null) || Files.exists(parentPath)) {
			return;
		}

		Files.createDirectories(parentPath);

		boolean needUTF8 = false;

		String pathString = parentPath.toString();

		for (int i = 0; i < pathString.length(); i++) {
			if (pathString.charAt(i) > 128) {
				needUTF8 = true;

				break;
			}
		}

		if (!needUTF8) {
			return;
		}

		try {
			Class<?> fileSystemClass = fileSystem.getClass();

			Field inodesField = fileSystemClass.getDeclaredField("inodes");

			inodesField.setAccessible(true);

			Map<?, ?> inodes = (Map<?, ?>)inodesField.get(fileSystem);

			for (Object inode : inodes.keySet()) {
				try {
					Class<?> inodeClass = inode.getClass();

					Field flagField = inodeClass.getDeclaredField("flag");

					flagField.setAccessible(true);

					int flag = flagField.getInt(inode);

					if ((flag & 2048) == 0) {
						flagField.setInt(inode, flag | 2048);
					}
				}
				catch (NoSuchFieldException noSuchFieldException) {
					if (_log.isDebugEnabled()) {
						_log.debug(noSuchFieldException);
					}
				}
			}
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to force UTF-8 encoding for directory " +
						parentPath,
					reflectiveOperationException);
			}
		}
	}

	private void _writeExportEntries() {
		try (FileSystem fileSystem = FileSystems.newFileSystem(
				_uri, Collections.emptyMap())) {

			Iterator<Map.Entry<String, byte[]>> iterator =
				_exportEntries.iterator();

			while (iterator.hasNext()) {
				Map.Entry<String, byte[]> entry = iterator.next();

				iterator.remove();

				Path path = fileSystem.getPath(entry.getKey());

				_createDirectories(fileSystem, path.getParent());

				Files.write(
					path, entry.getValue(), StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING,
					StandardOpenOption.WRITE);
			}

			_exportEntries = null;
			_exportEntriesBytes = 0;
		}
		catch (IOException ioException) {
			throw new UncheckedIOException(ioException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(ZipWriterImpl.class);

	private List<Map.Entry<String, byte[]>> _exportEntries;
	private long _exportEntriesBytes;
	private final File _file;
	private final URI _uri;

}