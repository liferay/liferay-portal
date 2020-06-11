/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.util;

import com.liferay.petra.nio.CharsetEncoderUtil;
import com.liferay.petra.process.ProcessCallable;
import com.liferay.petra.process.ProcessChannel;
import com.liferay.petra.process.ProcessException;
import com.liferay.petra.process.ProcessExecutor;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.pacl.DoPrivileged;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Digester;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.FileComparator;
import com.liferay.portal.kernel.util.PwdGenerator;
import com.liferay.portal.kernel.util.ReflectionUtil;
import com.liferay.portal.kernel.util.StreamUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.util.ant.ExpandTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.Writer;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;

import org.apache.commons.compress.archivers.zip.UnsupportedZipFeatureException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.txt.UniversalEncodingDetector;
import org.apache.tools.ant.DirectoryScanner;

import org.mozilla.intl.chardet.nsDetector;
import org.mozilla.intl.chardet.nsPSMDetector;

/**
 * @author Brian Wing Shun Chan
 * @author Alexander Chow
 */
@DoPrivileged
public class FileImpl implements com.liferay.portal.kernel.util.File {

	public static FileImpl getInstance() {
		return _instance;
	}

	@Override
	public String appendParentheticalSuffix(String fileName, String suffix) {
		String fileNameWithoutExtension = stripExtension(fileName);

		String fileNameWithParentheticalSuffix =
			StringUtil.appendParentheticalSuffix(
				fileNameWithoutExtension, suffix);

		String extension = getExtension(fileName);

		if (Validator.isNull(extension)) {
			return fileNameWithParentheticalSuffix;
		}

		StringBundler sb = new StringBundler(3);

		sb.append(fileNameWithParentheticalSuffix);
		sb.append(StringPool.PERIOD);
		sb.append(extension);

		return sb.toString();
	}

	@Override
	public String appendSuffix(String fileName, String suffix) {
		StringBundler sb = new StringBundler(4);

		String fileNameWithoutExtension = stripExtension(fileName);

		sb.append(fileNameWithoutExtension);

		sb.append(suffix);

		String extension = getExtension(fileName);

		if (Validator.isNotNull(extension)) {
			sb.append(StringPool.PERIOD);
			sb.append(extension);
		}

		return sb.toString();
	}

	@Override
	public void copyDirectory(File source, File destination)
		throws IOException {

		if (!source.exists() || !source.isDirectory()) {
			return;
		}

		mkdirs(destination);

		File[] fileArray = source.listFiles();

		for (int i = 0; i < fileArray.length; i++) {
			if (fileArray[i].isDirectory()) {
				copyDirectory(
					fileArray[i],
					new File(
						destination.getPath() + File.separator +
							fileArray[i].getName()));
			}
			else {
				copyFile(
					fileArray[i],
					new File(
						destination.getPath() + File.separator +
							fileArray[i].getName()));
			}
		}
	}

	@Override
	public void copyDirectory(String sourceDirName, String destinationDirName)
		throws IOException {

		copyDirectory(new File(sourceDirName), new File(destinationDirName));
	}

	@Override
	public void copyFile(File source, File destination) throws IOException {
		copyFile(source, destination, false);
	}

	@Override
	public void copyFile(File source, File destination, boolean lazy)
		throws IOException {

		if (!source.exists()) {
			return;
		}

		if (lazy) {
			String oldContent = null;

			try {
				oldContent = read(source);
			}
			catch (Exception e) {
				return;
			}

			String newContent = null;

			try {
				newContent = read(destination);
			}
			catch (Exception e) {
			}

			if ((oldContent == null) || !oldContent.equals(newContent)) {
				copyFile(source, destination, false);
			}
		}
		else {
			mkdirsParentFile(destination);

			StreamUtil.transfer(
				new FileInputStream(source), new FileOutputStream(destination));
		}
	}

	@Override
	public void copyFile(String source, String destination) throws IOException {
		copyFile(source, destination, false);
	}

	@Override
	public void copyFile(String source, String destination, boolean lazy)
		throws IOException {

		copyFile(new File(source), new File(destination), lazy);
	}

	@Override
	public File createTempFile() {
		return createTempFile(StringPool.BLANK);
	}

	@Override
	public File createTempFile(byte[] bytes) throws IOException {
		File file = createTempFile(StringPool.BLANK);

		write(file, bytes, false);

		return file;
	}

	@Override
	public File createTempFile(InputStream is) throws IOException {
		File file = createTempFile(StringPool.BLANK);

		write(file, is);

		return file;
	}

	@Override
	public File createTempFile(String extension) {
		return new File(createTempFileName(extension));
	}

	@Override
	public File createTempFile(String prefix, String extension) {
		return new File(createTempFileName(prefix, extension));
	}

	@Override
	public String createTempFileName() {
		return createTempFileName(null, null);
	}

	@Override
	public String createTempFileName(String extension) {
		return createTempFileName(null, extension);
	}

	@Override
	public String createTempFileName(String prefix, String extension) {
		StringBundler sb = new StringBundler(7);

		sb.append(SystemProperties.get(SystemProperties.TMP_DIR));
		sb.append(StringPool.SLASH);

		if (Validator.isNotNull(prefix)) {
			sb.append(prefix);
		}

		sb.append(Time.getTimestamp());
		sb.append(PwdGenerator.getPassword(8, PwdGenerator.KEY2));

		if (Validator.isFileExtension(extension)) {
			sb.append(StringPool.PERIOD);
			sb.append(extension);
		}

		return sb.toString();
	}

	@Override
	public File createTempFolder() throws IOException {
		File file = new File(createTempFileName());

		mkdirs(file);

		return file;
	}

	@Override
	public String decodeSafeFileName(String fileName) {
		return StringUtil.replace(
			fileName, _SAFE_FILE_NAME_2, _SAFE_FILE_NAME_1);
	}

	@Override
	public boolean delete(File file) {
		if (file != null) {
			boolean exists = true;

			try {
				exists = file.exists();
			}
			catch (SecurityException se) {

				// We may have the permission to delete a specific file without
				// having the permission to check if the file exists

			}

			if (exists) {
				return file.delete();
			}
		}

		return false;
	}

	@Override
	public boolean delete(String file) {
		return delete(new File(file));
	}

	@Override
	public void deltree(File directory) {
		if (directory.exists() && directory.isDirectory()) {
			File[] fileArray = directory.listFiles();

			for (int i = 0; i < fileArray.length; i++) {
				if (fileArray[i].isDirectory()) {
					deltree(fileArray[i]);
				}
				else {
					fileArray[i].delete();
				}
			}

			directory.delete();
		}
	}

	@Override
	public void deltree(String directory) {
		deltree(new File(directory));
	}

	@Override
	public String encodeSafeFileName(String fileName) {
		if (fileName == null) {
			return StringPool.BLANK;
		}

		return StringUtil.replace(
			fileName, _SAFE_FILE_NAME_1, _SAFE_FILE_NAME_2);
	}

	@Override
	public boolean exists(File file) {
		return file.exists();
	}

	@Override
	public boolean exists(String fileName) {
		return exists(new File(fileName));
	}

	@Override
	public String extractText(InputStream is, String fileName) {
		return extractText(is, fileName, -1);
	}

	@Override
	public String extractText(
		InputStream is, String fileName, int maxStringLength) {

		if (maxStringLength == 0) {
			return StringPool.BLANK;
		}

		String text = null;

		try {
			Tika tika = new Tika(TikaConfigHolder._tikaConfig);

			tika.setMaxStringLength(maxStringLength);

			boolean forkProcess = false;

			if (PropsValues.TEXT_EXTRACTION_FORK_PROCESS_ENABLED) {
				String mimeType = tika.detect(is);

				if (ArrayUtil.contains(
						PropsValues.TEXT_EXTRACTION_FORK_PROCESS_MIME_TYPES,
						mimeType)) {

					forkProcess = true;
				}
			}

			if (forkProcess) {
				Registry registry = RegistryUtil.getRegistry();

				ProcessChannel<String> processChannel = registry.callService(
					ProcessExecutor.class,
					processExecutor -> {
						try {
							return processExecutor.execute(
								PortalClassPathUtil.getPortalProcessConfig(),
								new ExtractTextProcessCallable(getBytes(is)));
						}
						catch (Exception e) {
							return ReflectionUtil.throwException(e);
						}
					});

				Future<String> future =
					processChannel.getProcessNoticeableFuture();

				text = future.get();
			}
			else {
				TikaInputStream tikaInputStream = TikaInputStream.get(is);

				if (!_isEmptyTikaInputStream(tikaInputStream)) {
					UniversalEncodingDetector universalEncodingDetector =
						new UniversalEncodingDetector();

					Metadata metadata = new Metadata();

					Charset charset = universalEncodingDetector.detect(
						tikaInputStream, metadata);

					String contentEncoding = StringPool.BLANK;

					if (charset != null) {
						contentEncoding = charset.name();
					}

					if (!contentEncoding.equals(StringPool.BLANK)) {
						metadata.set("Content-Encoding", contentEncoding);
						metadata.set(
							"Content-Type",
							"text/plain; charset=" + contentEncoding);
					}

					text = tika.parseToString(tikaInputStream, metadata);
				}
			}
		}
		catch (Throwable t) {
			Throwable throwable = ExceptionUtils.getRootCause(t);

			if (throwable instanceof EncryptedDocumentException ||
				throwable instanceof UnsupportedZipFeatureException) {

				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to extract text from an encrypted file " +
							fileName,
						t);
				}
			}
			else if (t instanceof TikaException) {
				if (_log.isWarnEnabled()) {
					_log.warn("Unable to extract text from " + fileName, t);
				}
			}
			else {
				_log.error(t, t);
			}
		}

		if (_log.isInfoEnabled()) {
			if (text == null) {
				_log.info("Text extraction failed for " + fileName);
			}
			else {
				_log.info("Text was extracted for " + fileName);
			}
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Extractor returned text:\n\n" + text);
		}

		if (text == null) {
			text = StringPool.BLANK;
		}

		return text;
	}

	@Override
	public String[] find(String directory, String includes, String excludes) {
		if (directory.length() > 0) {
			directory = replaceSeparator(directory);

			if (directory.charAt(directory.length() - 1) == CharPool.SLASH) {
				directory = directory.substring(0, directory.length() - 1);
			}
		}

		if (!exists(directory)) {
			if (_log.isWarnEnabled()) {
				_log.warn("Directory " + directory + " does not exist");
			}

			return new String[0];
		}

		DirectoryScanner directoryScanner = new DirectoryScanner();

		directoryScanner.setBasedir(directory);
		directoryScanner.setExcludes(StringUtil.split(excludes));
		directoryScanner.setIncludes(StringUtil.split(includes));

		directoryScanner.scan();

		String[] includedFiles = directoryScanner.getIncludedFiles();

		for (int i = 0; i < includedFiles.length; i++) {
			includedFiles[i] = directory.concat(
				StringPool.SLASH).concat(replaceSeparator(includedFiles[i]));
		}

		return includedFiles;
	}

	@Override
	public String getAbsolutePath(File file) {
		return StringUtil.replace(
			file.getAbsolutePath(), CharPool.BACK_SLASH, CharPool.SLASH);
	}

	@Override
	public byte[] getBytes(Class<?> clazz, String fileName) throws IOException {
		return getBytes(clazz.getResourceAsStream(fileName));
	}

	@Override
	public byte[] getBytes(File file) throws IOException {
		if ((file == null) || !file.exists()) {
			return null;
		}

		try (RandomAccessFile randomAccessFile = new RandomAccessFile(
				file, "r")) {

			byte[] bytes = new byte[(int)randomAccessFile.length()];

			randomAccessFile.readFully(bytes);

			return bytes;
		}
	}

	@Override
	public byte[] getBytes(InputStream is) throws IOException {
		return getBytes(is, -1);
	}

	@Override
	public byte[] getBytes(InputStream inputStream, int bufferSize)
		throws IOException {

		return getBytes(inputStream, bufferSize, true);
	}

	@Override
	public byte[] getBytes(
			InputStream inputStream, int bufferSize, boolean cleanUpStream)
		throws IOException {

		if (inputStream == null) {
			return null;
		}

		UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
			new UnsyncByteArrayOutputStream();

		StreamUtil.transfer(
			inputStream, unsyncByteArrayOutputStream, bufferSize,
			cleanUpStream);

		return unsyncByteArrayOutputStream.toByteArray();
	}

	@Override
	public String getExtension(String fileName) {
		if (fileName == null) {
			return null;
		}

		int pos = fileName.lastIndexOf(CharPool.PERIOD);

		if (pos > 0) {
			return StringUtil.toLowerCase(fileName.substring(pos + 1));
		}
		else {
			return StringPool.BLANK;
		}
	}

	@Override
	public String getMD5Checksum(File file) throws IOException {
		FileInputStream fileInputStream = null;

		try {
			fileInputStream = new FileInputStream(file);

			return DigesterUtil.digestHex(Digester.MD5, fileInputStream);
		}
		finally {
			StreamUtil.cleanUp(fileInputStream);
		}
	}

	@Override
	public String getPath(String fullFileName) {
		int x = fullFileName.lastIndexOf(CharPool.SLASH);
		int y = fullFileName.lastIndexOf(CharPool.BACK_SLASH);

		if ((x == -1) && (y == -1)) {
			return StringPool.SLASH;
		}

		String shortFileName = fullFileName.substring(0, Math.max(x, y));

		return shortFileName;
	}

	@Override
	public String getShortFileName(String fullFileName) {
		int x = fullFileName.lastIndexOf(CharPool.SLASH);
		int y = fullFileName.lastIndexOf(CharPool.BACK_SLASH);

		String shortFileName = fullFileName.substring(Math.max(x, y) + 1);

		return shortFileName;
	}

	@Override
	public boolean isAscii(File file) throws IOException {
		boolean ascii = true;

		nsDetector detector = new nsDetector(nsPSMDetector.ALL);

		try (InputStream inputStream = new FileInputStream(file)) {
			byte[] buffer = new byte[1024];

			int len = 0;

			while ((len = inputStream.read(buffer, 0, buffer.length)) != -1) {
				if (ascii) {
					ascii = detector.isAscii(buffer, len);

					if (!ascii) {
						break;
					}
				}
			}

			detector.DataEnd();
		}

		return ascii;
	}

	@Override
	public boolean isSameContent(File file, byte[] bytes, int length) {
		try (FileInputStream fileInputStream = new FileInputStream(file)) {
			FileChannel fileChannel = fileInputStream.getChannel();

			if (fileChannel.size() != length) {
				return false;
			}

			byte[] buffer = new byte[1024];

			ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);

			int bufferIndex = 0;
			int bufferLength = -1;

			while (((bufferLength = fileChannel.read(byteBuffer)) > 0) &&
				   (bufferIndex < length)) {

				for (int i = 0; i < bufferLength; i++) {
					if (buffer[i] != bytes[bufferIndex++]) {
						return false;
					}
				}

				byteBuffer.clear();
			}

			if ((bufferIndex != length) || (bufferLength != -1)) {
				return false;
			}
			else {
				return true;
			}
		}
		catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean isSameContent(File file, String s) {
		ByteBuffer byteBuffer = CharsetEncoderUtil.encode(
			StringPool.UTF8, CharBuffer.wrap(s));

		return isSameContent(file, byteBuffer.array(), byteBuffer.limit());
	}

	@Override
	public String[] listDirs(File file) {
		List<String> dirs = new ArrayList<>();

		File[] fileArray = file.listFiles();

		for (int i = 0; (fileArray != null) && (i < fileArray.length); i++) {
			if (fileArray[i].isDirectory()) {
				dirs.add(fileArray[i].getName());
			}
		}

		return dirs.toArray(new String[dirs.size()]);
	}

	@Override
	public String[] listDirs(String fileName) {
		return listDirs(new File(fileName));
	}

	@Override
	public String[] listFiles(File file) {
		List<String> files = new ArrayList<>();

		File[] fileArray = file.listFiles();

		for (int i = 0; (fileArray != null) && (i < fileArray.length); i++) {
			if (fileArray[i].isFile()) {
				files.add(fileArray[i].getName());
			}
		}

		return files.toArray(new String[files.size()]);
	}

	@Override
	public String[] listFiles(String fileName) {
		if (Validator.isNull(fileName)) {
			return new String[0];
		}

		return listFiles(new File(fileName));
	}

	@Override
	public void mkdirs(File file) throws IOException {
		FileUtils.forceMkdir(file);
	}

	@Override
	public void mkdirs(String pathName) {
		File file = new File(pathName);

		if (file.exists() && file.isDirectory()) {
			if (_log.isDebugEnabled()) {
				_log.debug("Directory " + pathName + " already exists");
			}

			return;
		}

		try {
			mkdirs(file);
		}
		catch (IOException ioe) {
			ReflectionUtil.throwException(ioe);
		}
	}

	@Override
	public boolean move(File source, File destination) {
		if (!source.exists()) {
			return false;
		}

		destination.delete();

		try {
			if (source.isDirectory()) {
				FileUtils.moveDirectory(source, destination);
			}
			else {
				FileUtils.moveFile(source, destination);
			}
		}
		catch (IOException ioe) {
			return false;
		}

		return true;
	}

	@Override
	public boolean move(String sourceFileName, String destinationFileName) {
		return move(new File(sourceFileName), new File(destinationFileName));
	}

	@Override
	public String read(File file) throws IOException {
		return read(file, false);
	}

	@Override
	public String read(File file, boolean raw) throws IOException {
		byte[] bytes = getBytes(file);

		if (bytes == null) {
			return null;
		}

		String s = new String(bytes, StringPool.UTF8);

		if (raw) {
			return s;
		}
		else {
			return StringUtil.replace(
				s, StringPool.RETURN_NEW_LINE, StringPool.NEW_LINE);
		}
	}

	@Override
	public String read(String fileName) throws IOException {
		return read(new File(fileName));
	}

	@Override
	public String replaceSeparator(String fileName) {
		return StringUtil.replace(
			fileName, CharPool.BACK_SLASH, CharPool.SLASH);
	}

	@Override
	public File[] sortFiles(File[] files) {
		if (files == null) {
			return null;
		}

		Arrays.sort(files, new FileComparator());

		List<File> directoryList = new ArrayList<>();
		List<File> fileList = new ArrayList<>();

		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				directoryList.add(files[i]);
			}
			else {
				fileList.add(files[i]);
			}
		}

		directoryList.addAll(fileList);

		return directoryList.toArray(new File[directoryList.size()]);
	}

	@Override
	public String stripExtension(String fileName) {
		if (fileName == null) {
			return null;
		}

		String ext = getExtension(fileName);

		if (ext.length() > 0) {
			return fileName.substring(0, fileName.length() - ext.length() - 1);
		}
		else {
			return fileName;
		}
	}

	@Override
	public String stripParentheticalSuffix(String fileName) {
		StringBundler sb = new StringBundler(3);

		String fileNameWithoutExtension = stripExtension(fileName);

		sb.append(
			StringUtil.stripParentheticalSuffix(fileNameWithoutExtension));

		sb.append(StringPool.PERIOD);

		String extension = getExtension(fileName);

		sb.append(extension);

		return sb.toString();
	}

	@Override
	public List<String> toList(Reader reader) {
		List<String> list = new ArrayList<>();

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(reader)) {

			String line = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				list.add(line);
			}
		}
		catch (IOException ioe) {
		}

		return list;
	}

	@Override
	public List<String> toList(String fileName) {
		try {
			return toList(new FileReader(fileName));
		}
		catch (IOException ioe) {
			return new ArrayList<>();
		}
	}

	@Override
	public Properties toProperties(FileInputStream fis) {
		Properties properties = new Properties();

		try {
			properties.load(fis);
		}
		catch (IOException ioe) {
		}

		return properties;
	}

	@Override
	public Properties toProperties(String fileName) {
		try {
			return toProperties(new FileInputStream(fileName));
		}
		catch (IOException ioe) {
			return new Properties();
		}
	}

	@Override
	public void touch(File file) throws IOException {
		FileUtils.touch(file);
	}

	@Override
	public void touch(String fileName) throws IOException {
		touch(new File(fileName));
	}

	@Override
	public void unzip(File source, File destination) {
		ExpandTask.expand(source, destination);
	}

	@Override
	public void write(File file, byte[] bytes) throws IOException {
		write(file, bytes, 0, bytes.length, false);
	}

	@Override
	public void write(File file, byte[] bytes, boolean append)
		throws IOException {

		write(file, bytes, 0, bytes.length, append);
	}

	@Override
	public void write(File file, byte[] bytes, int offset, int length)
		throws IOException {

		write(file, bytes, offset, bytes.length, false);
	}

	@Override
	public void write(
			File file, byte[] bytes, int offset, int length, boolean append)
		throws IOException {

		mkdirsParentFile(file);

		try (FileOutputStream fileOutputStream = new FileOutputStream(
				file, append)) {

			fileOutputStream.write(bytes, offset, length);
		}
	}

	@Override
	public void write(File file, InputStream is) throws IOException {
		mkdirsParentFile(file);

		StreamUtil.transfer(is, new FileOutputStream(file));
	}

	@Override
	public void write(File file, String s) throws IOException {
		write(file, s, false);
	}

	@Override
	public void write(File file, String s, boolean lazy) throws IOException {
		write(file, s, lazy, false);
	}

	@Override
	public void write(File file, String s, boolean lazy, boolean append)
		throws IOException {

		if (s == null) {
			return;
		}

		mkdirsParentFile(file);

		if (lazy && file.exists()) {
			String content = read(file);

			if (content.equals(s)) {
				return;
			}
		}

		try (Writer writer = new OutputStreamWriter(
				new FileOutputStream(file, append), StringPool.UTF8)) {

			writer.write(s);
		}
	}

	@Override
	public void write(String fileName, byte[] bytes) throws IOException {
		write(new File(fileName), bytes);
	}

	@Override
	public void write(String fileName, InputStream is) throws IOException {
		write(new File(fileName), is);
	}

	@Override
	public void write(String fileName, String s) throws IOException {
		write(new File(fileName), s);
	}

	@Override
	public void write(String fileName, String s, boolean lazy)
		throws IOException {

		write(new File(fileName), s, lazy);
	}

	@Override
	public void write(String fileName, String s, boolean lazy, boolean append)
		throws IOException {

		write(new File(fileName), s, lazy, append);
	}

	@Override
	public void write(String pathName, String fileName, String s)
		throws IOException {

		write(new File(pathName, fileName), s);
	}

	@Override
	public void write(String pathName, String fileName, String s, boolean lazy)
		throws IOException {

		write(new File(pathName, fileName), s, lazy);
	}

	@Override
	public void write(
			String pathName, String fileName, String s, boolean lazy,
			boolean append)
		throws IOException {

		write(new File(pathName, fileName), s, lazy, append);
	}

	protected void mkdirsParentFile(File file) throws IOException {
		File parentFile = file.getParentFile();

		if (parentFile == null) {
			return;
		}

		try {
			mkdirs(parentFile);
		}
		catch (SecurityException se) {

			// We may have the permission to write a specific file without
			// having the permission to check if the parent file exists

		}
	}

	private boolean _isEmptyTikaInputStream(TikaInputStream tikaInputStream)
		throws IOException {

		if (tikaInputStream.hasLength() && (tikaInputStream.getLength() > 0)) {
			return false;
		}

		byte[] bytes = new byte[1];

		int count = tikaInputStream.peek(bytes);

		if (count > 0) {
			return false;
		}

		return true;
	}

	private static final String[] _SAFE_FILE_NAME_1 = {
		StringPool.AMPERSAND, StringPool.CLOSE_PARENTHESIS,
		StringPool.OPEN_PARENTHESIS, StringPool.SEMICOLON
	};

	private static final String[] _SAFE_FILE_NAME_2 = {
		PropsValues.DL_STORE_FILE_IMPL_SAFE_FILE_NAME_2_AMPERSAND,
		PropsValues.DL_STORE_FILE_IMPL_SAFE_FILE_NAME_2_CLOSE_PARENTHESIS,
		PropsValues.DL_STORE_FILE_IMPL_SAFE_FILE_NAME_2_OPEN_PARENTHESIS,
		PropsValues.DL_STORE_FILE_IMPL_SAFE_FILE_NAME_2_SEMICOLON
	};

	private static final Log _log = LogFactoryUtil.getLog(FileImpl.class);

	private static final FileImpl _instance = new FileImpl();

	private static class ExtractTextProcessCallable
		implements ProcessCallable<String> {

		public ExtractTextProcessCallable(byte[] data) {
			_data = data;
		}

		@Override
		public String call() throws ProcessException {
			if (ArrayUtil.isEmpty(_data)) {
				return StringPool.BLANK;
			}

			Tika tika = new Tika(TikaConfigHolder._tikaConfig);

			try {
				return tika.parseToString(
					new UnsyncByteArrayInputStream(_data));
			}
			catch (Exception e) {
				throw new ProcessException(e);
			}
		}

		private static final long serialVersionUID = 1L;

		private final byte[] _data;

	}

	private static class TikaConfigHolder {

		private static final TikaConfig _tikaConfig;

		static {
			try {
				_tikaConfig = new TikaConfig();
			}
			catch (Exception e) {
				throw new ExceptionInInitializerError(e);
			}
		}

	}

}