/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.util;

import com.liferay.petra.nio.CharsetEncoderUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.FileComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PwdGenerator;
import com.liferay.portal.kernel.util.StreamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.Writer;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Queue;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Brian Wing Shun Chan
 * @author Alexander Chow
 */
public class FileImpl implements com.liferay.portal.kernel.util.File {

	public static FileImpl getInstance() {
		return _fileImpl;
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

		return StringBundler.concat(
			fileNameWithParentheticalSuffix, StringPool.PERIOD, extension);
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

		Queue<File> queue = new LinkedList<>();

		queue.add(source);

		String basePath = source.getPath();

		int prefixLength = basePath.length() + 1;

		File directory;

		while ((directory = queue.poll()) != null) {
			File[] files = directory.listFiles();

			if (files == null) {
				continue;
			}

			for (File file : files) {
				String path = file.getPath();

				File targetFile = new File(
					destination, path.substring(prefixLength));

				if (file.isFile()) {
					StreamUtil.transfer(
						new FileInputStream(file),
						new FileOutputStream(targetFile));
				}
				else {
					targetFile.mkdir();

					queue.add(file);
				}
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
		if (!source.exists()) {
			return;
		}

		mkdirsParentFile(destination);

		StreamUtil.transfer(
			new FileInputStream(source), new FileOutputStream(destination));
	}

	@Override
	public void copyFile(String source, String destination) throws IOException {
		copyFile(new File(source), new File(destination));
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
	public File createTempFile(InputStream inputStream) throws IOException {
		File file = createTempFile(StringPool.BLANK);

		write(file, inputStream);

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
			return file.delete();
		}

		return false;
	}

	@Override
	public boolean delete(String file) {
		return delete(new File(file));
	}

	@Override
	public void deltree(File directory) {
		if (directory.isDirectory()) {
			Deque<File> deleteDeque = new LinkedList<>();

			deleteDeque.push(directory);

			Queue<File> visitQueue = new LinkedList<>();

			visitQueue.add(directory);

			File curDirectory;

			while ((curDirectory = visitQueue.poll()) != null) {
				File[] files = curDirectory.listFiles();

				if (files == null) {
					continue;
				}

				for (File file : files) {
					if (file.isFile()) {
						file.delete();
					}
					else {
						visitQueue.add(file);

						deleteDeque.push(file);
					}
				}
			}

			deleteDeque.forEach(File::delete);
		}
		else {
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
	public byte[] getBytes(InputStream inputStream) throws IOException {
		return getBytes(inputStream, -1);
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

		return StringPool.BLANK;
	}

	@Override
	public String getMD5Checksum(File file) throws IOException {
		try (FileInputStream fileInputStream = new FileInputStream(file)) {
			return DigesterUtil.digestHex(DigesterUtil.MD5, fileInputStream);
		}
	}

	@Override
	public String getPath(String fullFileName) {
		int x = fullFileName.lastIndexOf(CharPool.SLASH);
		int y = fullFileName.lastIndexOf(CharPool.BACK_SLASH);

		if ((x == -1) && (y == -1)) {
			return StringPool.SLASH;
		}

		return fullFileName.substring(0, Math.max(x, y));
	}

	@Override
	public String getShortFileName(String fullFileName) {
		int x = fullFileName.lastIndexOf(CharPool.SLASH);
		int y = fullFileName.lastIndexOf(CharPool.BACK_SLASH);

		return fullFileName.substring(Math.max(x, y) + 1);
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

			return true;
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

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

		return dirs.toArray(new String[0]);
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

		return files.toArray(new String[0]);
	}

	@Override
	public String[] listFiles(String fileName) {
		if (Validator.isNull(fileName)) {
			return new String[0];
		}

		return listFiles(new File(fileName));
	}

	@Override
	public void mkdirs(File file) {
		file.mkdirs();
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

		mkdirs(file);
	}

	@Override
	public boolean move(File source, File destination) {
		if (!source.exists()) {
			return false;
		}

		destination.delete();

		try {
			if (source.isDirectory()) {
				if (!source.renameTo(destination)) {
					copyDirectory(source, destination);

					deltree(source);
				}
			}
			else {
				if (!source.renameTo(destination)) {
					copyFile(source, destination);

					delete(source);
				}
			}
		}
		catch (IOException ioException) {
			if (_log.isDebugEnabled()) {
				_log.debug(ioException);
			}

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

		return StringUtil.replace(
			s, StringPool.RETURN_NEW_LINE, StringPool.NEW_LINE);
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

		for (File file : files) {
			if (file.isDirectory()) {
				directoryList.add(file);
			}
			else {
				fileList.add(file);
			}
		}

		directoryList.addAll(fileList);

		return directoryList.toArray(new File[0]);
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

		return fileName;
	}

	@Override
	public String stripParentheticalSuffix(String fileName) {
		StringBundler sb = new StringBundler(3);

		String fileNameWithoutExtension = stripExtension(fileName);

		sb.append(
			StringUtil.stripParentheticalSuffix(fileNameWithoutExtension));

		String extension = getExtension(fileName);

		if (Validator.isNotNull(extension)) {
			sb.append(StringPool.PERIOD);
			sb.append(extension);
		}

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
		catch (IOException ioException) {
			if (_log.isDebugEnabled()) {
				_log.debug(ioException);
			}
		}

		return list;
	}

	@Override
	public List<String> toList(String fileName) {
		try {
			return toList(new FileReader(fileName));
		}
		catch (IOException ioException) {
			if (_log.isDebugEnabled()) {
				_log.debug(ioException);
			}

			return new ArrayList<>();
		}
	}

	@Override
	public Properties toProperties(FileInputStream fileInputStream) {
		Properties properties = new Properties();

		try {
			properties.load(fileInputStream);
		}
		catch (IOException ioException) {
			if (_log.isDebugEnabled()) {
				_log.debug(ioException);
			}
		}

		return properties;
	}

	@Override
	public Properties toProperties(String fileName) {
		try {
			return toProperties(new FileInputStream(fileName));
		}
		catch (IOException ioException) {
			if (_log.isDebugEnabled()) {
				_log.debug(ioException);
			}

			return new Properties();
		}
	}

	@Override
	public void touch(File file) throws IOException {
		if (file.exists()) {
			file.setLastModified(System.currentTimeMillis());
		}
		else {
			file.createNewFile();
		}
	}

	@Override
	public void touch(String fileName) throws IOException {
		touch(new File(fileName));
	}

	@Override
	public void unzip(File source, File destination) {
		try (InputStream inputStream = new FileInputStream(source);
			ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {

			ZipEntry entry = null;

			while ((entry = zipInputStream.getNextEntry()) != null) {
				File destinationFile = new File(destination, entry.getName());

				String destinationFileCanonicalPath =
					destinationFile.getCanonicalPath();

				if (!destinationFileCanonicalPath.startsWith(
						destination.getCanonicalPath() + File.separator)) {

					if (_log.isWarnEnabled()) {
						_log.warn("Invalid entry name: " + entry.getName());
					}

					continue;
				}

				if (entry.isDirectory()) {
					destinationFile.mkdirs();
				}
				else {
					File parentFile = destinationFile.getParentFile();

					parentFile.mkdirs();

					try (OutputStream outputStream = new FileOutputStream(
							destinationFile)) {

						StreamUtil.transfer(
							zipInputStream, outputStream, false);
					}
				}
			}
		}
		catch (IOException ioException) {
			ReflectionUtil.throwException(ioException);
		}
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
	public void write(File file, InputStream inputStream) throws IOException {
		mkdirsParentFile(file);

		StreamUtil.transfer(inputStream, new FileOutputStream(file));
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
	public void write(String fileName, InputStream inputStream)
		throws IOException {

		write(new File(fileName), inputStream);
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
		catch (SecurityException securityException) {
			if (_log.isDebugEnabled()) {
				_log.debug(securityException);
			}

			// We may have the permission to write a specific file without
			// having the permission to check if the parent file exists

		}
	}

	private static final String[] _SAFE_FILE_NAME_1 = {
		StringPool.AMPERSAND, StringPool.CLOSE_PARENTHESIS,
		StringPool.OPEN_PARENTHESIS, StringPool.SEMICOLON
	};

	private static final String[] _SAFE_FILE_NAME_2 = {
		PropsUtil.get(PropsKeys.DL_STORE_FILE_IMPL_SAFE_FILE_NAME_2_AMPERSAND),
		PropsUtil.get(
			PropsKeys.DL_STORE_FILE_IMPL_SAFE_FILE_NAME_2_CLOSE_PARENTHESIS),
		PropsUtil.get(
			PropsKeys.DL_STORE_FILE_IMPL_SAFE_FILE_NAME_2_OPEN_PARENTHESIS),
		PropsUtil.get(PropsKeys.DL_STORE_FILE_IMPL_SAFE_FILE_NAME_2_SEMICOLON)
	};

	private static final Log _log = LogFactoryUtil.getLog(FileImpl.class);

	private static final FileImpl _fileImpl = new FileImpl();

}