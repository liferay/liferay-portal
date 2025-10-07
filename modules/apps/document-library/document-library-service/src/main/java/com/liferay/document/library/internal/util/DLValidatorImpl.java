/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.util;

import com.liferay.document.library.configuration.DLConfiguration;
import com.liferay.document.library.configuration.DLFileEntryMimeTypeConfiguration;
import com.liferay.document.library.internal.configuration.helper.DLSizeLimitConfigurationHelper;
import com.liferay.document.library.kernel.exception.FileExtensionException;
import com.liferay.document.library.kernel.exception.FileMimeTypeException;
import com.liferay.document.library.kernel.exception.FileNameException;
import com.liferay.document.library.kernel.exception.FileSizeException;
import com.liferay.document.library.kernel.exception.FolderNameException;
import com.liferay.document.library.kernel.exception.InvalidFileVersionException;
import com.liferay.document.library.kernel.exception.SourceFileNameException;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.document.library.kernel.util.DLValidator;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.upload.configuration.UploadServletRequestConfigurationProvider;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeFormatter;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portlet.documentlibrary.webdav.DLWebDAVUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	configurationPid = "com.liferay.document.library.configuration.DLConfiguration",
	service = DLValidator.class
)
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

		name = _replaceDLCharLastBlacklist(name);

		name = _replaceDLNameBlacklist(name);

		return _replaceDLWebDAVSubstitutionChar(name);
	}

	@Override
	public long getMaxAllowableSize(long groupId, String mimeType) {
		return getMaxAllowableSize(groupId, mimeType, 0);
	}

	@Override
	public long getMaxAllowableSize(long groupId, String mimeType, long limit) {
		long companyId = _getCompanyId(groupId);

		return _min(
			limit,
			_min(
				_getGlobalMaxAllowableSize(companyId, groupId),
				_min(
					_dlSizeLimitConfigurationHelper.getCompanyMimeTypeSizeLimit(
						companyId, mimeType),
					_dlSizeLimitConfigurationHelper.getGroupMimeTypeSizeLimit(
						groupId, mimeType))));
	}

	@Override
	public Map<String, Long> getMimeTypeSizeLimit(long groupId) {
		Map<String, Long> mimeTypeSizeLimit = new HashMap<>(
			_dlSizeLimitConfigurationHelper.getGroupMimeTypeSizeLimit(groupId));

		Map<String, Long> companyMimeTypeSizeLimit =
			_dlSizeLimitConfigurationHelper.getCompanyMimeTypeSizeLimit(
				_getCompanyId(groupId));

		companyMimeTypeSizeLimit.forEach(
			(key, value) -> mimeTypeSizeLimit.merge(
				key, value, (value1, value2) -> _min(value1, value2)));

		return mimeTypeSizeLimit;
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
			throw new FolderNameException(
				"Invalid folder name " + directoryName);
		}
	}

	@Override
	public void validateFileExtension(String fileName)
		throws FileExtensionException {

		if (CompanyThreadLocal.isInitializingPortalInstance()) {
			return;
		}

		boolean validFileExtension = false;

		for (String fileExtension : _dlConfiguration.fileExtensions()) {
			String fileNameExtension = StringUtil.toLowerCase(
				FileUtil.getExtension(fileName));

			if (StringPool.STAR.equals(fileExtension) ||
				StringUtil.equals(
					fileNameExtension,
					StringUtil.toLowerCase(
						StringUtil.replace(
							fileExtension, CharPool.PERIOD,
							StringPool.BLANK)))) {

				validFileExtension = true;

				break;
			}
		}

		if (!validFileExtension) {
			throw new FileExtensionException.InvalidExtension(
				"Invalid file extension for " + fileName);
		}
	}

	@Override
	public void validateFileMimeType(long companyId, String mimeType)
		throws PortalException {

		if (CompanyThreadLocal.isInitializingPortalInstance()) {
			return;
		}

		boolean validFileMimeType = false;

		DLFileEntryMimeTypeConfiguration dlFileEntryMimeTypeConfiguration =
			_configurationProvider.getCompanyConfiguration(
				DLFileEntryMimeTypeConfiguration.class, companyId);

		for (String fileMimeType :
				dlFileEntryMimeTypeConfiguration.fileMimeTypes()) {

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
			throw new FileNameException("Invalid file name " + fileName);
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
			throw new FileSizeException(
				"File size is zero for " + fileName,
				getMaxAllowableSize(groupId, mimeType, 0));
		}

		validateFileSize(groupId, fileName, mimeType, bytes.length);
	}

	@Override
	public void validateFileSize(
			long groupId, String fileName, String mimeType, File file)
		throws FileSizeException {

		if (file == null) {
			throw new FileSizeException(
				"File is null for " + fileName,
				getMaxAllowableSize(groupId, mimeType, 0));
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
				throw new FileSizeException(
					"Input stream is null for " + fileName,
					getMaxAllowableSize(groupId, mimeType, 0));
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

		long maxSize = getMaxAllowableSize(groupId, mimeType, 0);

		if ((maxSize > 0) && (size > maxSize)) {
			throw new FileSizeException(
				StringBundler.concat(
					size, " exceeds the maximum permitted size of ", maxSize,
					" for file ", fileName),
				maxSize);
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

			throw new SourceFileNameException(
				StringBundler.concat(
					"File extension ", sourceFileExtension,
					" is invalid for file name ", sourceFileName));
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

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_dlConfiguration = ConfigurableUtil.createConfigurable(
			DLConfiguration.class, properties);
	}

	protected void setConfigurationProvider(
		ConfigurationProvider configurationProvider) {

		_configurationProvider = configurationProvider;
	}

	protected void setDLConfiguration(DLConfiguration dlConfiguration) {
		_dlConfiguration = dlConfiguration;
	}

	protected void setGroupLocalService(GroupLocalService groupLocalService) {
		_groupLocalService = groupLocalService;
	}

	protected void setUploadServletRequestConfigurationHelper(
		UploadServletRequestConfigurationProvider
			uploadServletRequestConfigurationProvider) {

		_uploadServletRequestConfigurationProvider =
			uploadServletRequestConfigurationProvider;
	}

	private long _getCompanyId(long groupId) {
		Group group = _groupLocalService.fetchGroup(groupId);

		if (group == null) {
			return CompanyThreadLocal.getCompanyId();
		}

		return group.getCompanyId();
	}

	private long _getGlobalMaxAllowableSize(long companyId, long groupId) {
		return _min(
			_uploadServletRequestConfigurationProvider.getMaxSize(),
			_min(
				_dlSizeLimitConfigurationHelper.getCompanyFileMaxSize(
					companyId),
				_dlSizeLimitConfigurationHelper.getGroupFileMaxSize(groupId)));
	}

	private long _min(long a, long b) {
		if (a == 0) {
			return b;
		}

		if (b == 0) {
			return a;
		}

		return Math.min(a, b);
	}

	private String _replaceDLCharLastBlacklist(String title) {
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

	private String _replaceDLNameBlacklist(String title) {
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

	private String _replaceDLWebDAVSubstitutionChar(String title) {
		return StringUtil.replace(
			title, PropsValues.DL_WEBDAV_SUBSTITUTION_CHAR,
			StringPool.UNDERLINE);
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	private volatile DLConfiguration _dlConfiguration;

	@Reference
	private DLSizeLimitConfigurationHelper _dlSizeLimitConfigurationHelper;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private UploadServletRequestConfigurationProvider
		_uploadServletRequestConfigurationProvider;

}