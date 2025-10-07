/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v6_2_0.util;

import com.liferay.document.library.kernel.exception.FileExtensionException;
import com.liferay.document.library.kernel.exception.FileMimeTypeException;
import com.liferay.document.library.kernel.exception.FileNameException;
import com.liferay.document.library.kernel.exception.FileSizeException;
import com.liferay.document.library.kernel.exception.FolderNameException;
import com.liferay.document.library.kernel.exception.InvalidFileVersionException;
import com.liferay.document.library.kernel.exception.SourceFileNameException;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.document.library.kernel.util.DLValidator;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeFormatter;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portlet.documentlibrary.webdav.DLWebDAVUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.util.Collections;
import java.util.Map;

/**
 * @author     Adolfo Pérez
 * @deprecated As of Judson (7.1.x), replaced by {@link
 *             com.liferay.document.library.internal.util.DLValidatorImpl}
 */
@Deprecated
public final class DLValidatorImpl implements DLValidator {

	@Override
	public String fixName(String name) {
		if (Validator.isNull(name)) {
			return StringPool.UNDERLINE;
		}

		for (String blacklistChar : PropsValues.DL_CHAR_BLACKLIST) {
			name = StringUtil.replace(
				name, blacklistChar, StringPool.UNDERLINE);
		}

		name = replaceDLCharLastBlacklist(name);

		return replaceDLNameBlacklist(name);
	}

	/**
	 * @see com.liferay.portal.upload.internal.configuration.UploadServletRequestConfiguration#maxSize
	 */
	@Override
	public long getMaxAllowableSize(long groupId, String mimeType) {
		return 104857600;
	}

	@Override
	public long getMaxAllowableSize(long groupId, String mimeType, long limit) {
		return 104857600;
	}

	@Override
	public Map<String, Long> getMimeTypeSizeLimit(long groupId) {
		return Collections.emptyMap();
	}

	@Override
	public boolean isValidName(String name) {
		if (Validator.isNull(name)) {
			return false;
		}

		for (String blacklistChar : PropsValues.DL_CHAR_BLACKLIST) {
			if (name.contains(blacklistChar)) {
				return false;
			}
		}

		for (String blacklistLastChar : PropsValues.DL_CHAR_LAST_BLACKLIST) {
			if (blacklistLastChar.startsWith(UnicodeFormatter.UNICODE_PREFIX)) {
				blacklistLastChar = UnicodeFormatter.parseString(
					blacklistLastChar);
			}

			if (name.endsWith(blacklistLastChar)) {
				return false;
			}
		}

		String nameWithoutExtension = FileUtil.stripExtension(name);

		for (String blacklistName : PropsValues.DL_NAME_BLACKLIST) {
			if (StringUtil.equalsIgnoreCase(
					nameWithoutExtension, blacklistName)) {

				return false;
			}
		}

		return true;
	}

	@Override
	public void validateDirectoryName(String directoryName)
		throws FolderNameException {

		if (!isValidName(directoryName)) {
			throw new FolderNameException(directoryName);
		}
	}

	@Override
	public void validateFileExtension(String fileName)
		throws FileExtensionException {

		boolean validFileExtension = false;

		//String[] fileExtensions = PrefsPropsUtil.getStringArray(
		//	PropsKeys.DL_FILE_EXTENSIONS, StringPool.COMMA);
		String[] fileExtensions = {"*"};

		for (String fileExtension : fileExtensions) {
			if (StringPool.STAR.equals(fileExtension) ||
				StringUtil.endsWith(fileName, fileExtension)) {

				validFileExtension = true;

				break;
			}
		}

		if (!validFileExtension) {
			throw new FileExtensionException.InvalidExtension(fileName);
		}
	}

	@Override
	public void validateFileMimeType(long companyId, String mimeType)
		throws PortalException {

		boolean validFileMimeType = false;

		String[] fileMimeTypes = {"*"};

		for (String fileMimeType : fileMimeTypes) {
			if (StringPool.STAR.equals(fileMimeType) ||
				StringUtil.equalsIgnoreCase(mimeType, fileMimeType)) {

				validFileMimeType = true;

				break;
			}
		}

		if (!validFileMimeType) {
			throw new FileMimeTypeException(
				"Invalid file mime type " + mimeType);
		}
	}

	@Override
	public void validateFileName(String fileName) throws FileNameException {
		if (!isValidName(fileName)) {
			throw new FileNameException(fileName);
		}

		if (!DLWebDAVUtil.isRepresentableTitle(fileName)) {
			throw new FileNameException(
				"Unrepresentable WebDAV title for file name " + fileName);
		}
	}

	@Override
	public void validateFileSize(
			long groupId, String fileName, String mimeType, byte[] bytes)
		throws FileSizeException {

		if (bytes == null) {
			throw new FileSizeException(fileName);
		}

		validateFileSize(groupId, fileName, mimeType, bytes.length);
	}

	@Override
	public void validateFileSize(
			long groupId, String fileName, String mimeType, File file)
		throws FileSizeException {

		if (file == null) {
			throw new FileSizeException(fileName);
		}

		validateFileSize(groupId, fileName, mimeType, file.length());
	}

	@Override
	public void validateFileSize(
			long groupId, String fileName, String mimeType,
			InputStream inputStream)
		throws FileSizeException {

		try {
			if (inputStream == null) {
				throw new FileSizeException(fileName);
			}

			validateFileSize(
				groupId, fileName, mimeType, inputStream.available());
		}
		catch (IOException ioException) {
			throw new FileSizeException(ioException);
		}
	}

	@Override
	public void validateFileSize(
			long groupId, String fileName, String mimeType, long size)
		throws FileSizeException {

		//long maxSize = PrefsPropsUtil.getLong(PropsKeys.DL_FILE_MAX_SIZE);
		long maxSize = 0;

		if ((maxSize > 0) && (size > maxSize)) {
			throw new FileSizeException(fileName);
		}
	}

	@Override
	public void validateSourceFileExtension(
			String fileExtension, String sourceFileName)
		throws SourceFileNameException {

		String sourceFileExtension = FileUtil.getExtension(sourceFileName);

		if (Validator.isNotNull(sourceFileName) &&
			PropsValues.DL_FILE_EXTENSIONS_STRICT_CHECK &&
			!fileExtension.equals(sourceFileExtension)) {

			throw new SourceFileNameException(sourceFileExtension);
		}
	}

	@Override
	public void validateVersionLabel(String versionLabel)
		throws InvalidFileVersionException {

		if (Validator.isNull(versionLabel)) {
			return;
		}

		if (!DLUtil.isValidVersion(versionLabel)) {
			throw new InvalidFileVersionException(
				"Invalid version label " + versionLabel);
		}
	}

	protected String replaceDLCharLastBlacklist(String title) {
		String previousTitle = null;

		while (!title.equals(previousTitle)) {
			previousTitle = title;

			for (String blacklistLastChar :
					PropsValues.DL_CHAR_LAST_BLACKLIST) {

				if (blacklistLastChar.startsWith(
						UnicodeFormatter.UNICODE_PREFIX)) {

					blacklistLastChar = UnicodeFormatter.parseString(
						blacklistLastChar);
				}

				if (title.endsWith(blacklistLastChar)) {
					title = StringUtil.replaceLast(
						title, blacklistLastChar, StringPool.BLANK);
				}
			}
		}

		return title;
	}

	protected String replaceDLNameBlacklist(String title) {
		String extension = FileUtil.getExtension(title);
		String nameWithoutExtension = FileUtil.stripExtension(title);

		for (String blacklistName : PropsValues.DL_NAME_BLACKLIST) {
			if (StringUtil.equalsIgnoreCase(
					nameWithoutExtension, blacklistName)) {

				if (Validator.isNull(extension)) {
					return nameWithoutExtension + StringPool.UNDERLINE;
				}

				return StringBundler.concat(
					nameWithoutExtension, StringPool.UNDERLINE,
					StringPool.PERIOD, extension);
			}
		}

		return title;
	}

}