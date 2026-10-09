/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.zip.internal.reader;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import java.net.URI;

import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Sergio Sanchez
 */
public class NIOZipReader extends BaseZipReader {

	public NIOZipReader(File file) {
		super(file);
	}

	public NIOZipReader(InputStream inputStream) throws IOException {
		super(inputStream);
	}

	@Override
	public void close() {
		try {
			if ((_fileSystem != null) && _fileSystem.isOpen()) {
				_fileSystem.close();
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}
		finally {
			super.close();
		}
	}

	@Override
	public List<String> doGetEntries() throws IOException {
		if (!_initialized) {
			_initFile();
		}

		List<String> entries = new ArrayList<>();

		Files.walkFileTree(
			_rootPath,
			new SimpleFileVisitor<>() {

				@Override
				public FileVisitResult visitFile(
					Path path, BasicFileAttributes basicFileAttributes) {

					entries.add(_getRelativePath(path));

					return FileVisitResult.CONTINUE;
				}

			});

		return entries;
	}

	@Override
	public byte[] doGetEntryAsByteArray(String name) throws IOException {
		if (!_initialized) {
			_initFile();
		}

		Path path = _rootPath.resolve(name);

		if (Files.isRegularFile(path)) {
			return Files.readAllBytes(path);
		}

		return null;
	}

	@Override
	public InputStream doGetEntryAsInputStream(String name) throws IOException {
		if (!_initialized) {
			_initFile();
		}

		Path path = _rootPath.resolve(name);

		if (Files.isRegularFile(path)) {
			return Files.newInputStream(path);
		}

		return null;
	}

	@Override
	public List<String> doGetFolderEntries(String path) throws IOException {
		if (!_initialized) {
			_initFile();
		}

		Path folderPath = _rootPath.resolve(path);

		if (!Files.isDirectory(folderPath)) {
			return Collections.emptyList();
		}

		List<String> folderEntries = new ArrayList<>();

		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(
				folderPath)) {

			for (Path filePath : directoryStream) {
				if (!Files.isRegularFile(filePath)) {
					continue;
				}

				folderEntries.add(_getRelativePath(filePath));
			}
		}

		return folderEntries;
	}

	private String _getRelativePath(Path path) {
		return _rootPath.relativize(
			path
		).toString();
	}

	private void _initFile() {
		try {
			URI uri = URI.create("jar:" + file.toURI());

			try {
				_fileSystem = FileSystems.newFileSystem(
					uri,
					HashMapBuilder.put(
						"create", "false"
					).build());
			}
			catch (FileSystemAlreadyExistsException
						fileSystemAlreadyExistsException) {

				_log.error(fileSystemAlreadyExistsException);

				_fileSystem = FileSystems.getFileSystem(uri);
			}

			_rootPath = _fileSystem.getPath("/");

			_initialized = true;
		}
		catch (IOException ioException) {
			super.close();

			throw new UncheckedIOException(
				"Unable to initialize " + file.getPath(), ioException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(NIOZipReader.class);

	private FileSystem _fileSystem;
	private volatile boolean _initialized;
	private Path _rootPath;

}