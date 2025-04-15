/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.upload.web.internal;

import com.liferay.document.library.configuration.DLConfiguration;
import com.liferay.document.library.configuration.DLFileEntryMimeTypeConfiguration;
import com.liferay.document.library.exception.DLStorageQuotaExceededException;
import com.liferay.document.library.kernel.antivirus.AntivirusScannerException;
import com.liferay.document.library.kernel.exception.DuplicateFileEntryException;
import com.liferay.document.library.kernel.exception.FileExtensionException;
import com.liferay.document.library.kernel.exception.FileMimeTypeException;
import com.liferay.document.library.kernel.exception.FileNameException;
import com.liferay.document.library.kernel.exception.FileSizeException;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.servlet.ServletResponseConstants;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.upload.UploadRequestSizeException;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PropsValues;
import com.liferay.upload.UploadResponseHandler;

import jakarta.portlet.PortletRequest;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sergio González
 */
@Component(
	configurationPid = "com.liferay.document.library.configuration.DLConfiguration",
	property = "upload.response.handler=multiple",
	service = UploadResponseHandler.class
)
public class MultipleUploadResponseHandler implements UploadResponseHandler {

	@Override
	public JSONObject onFailure(
			PortletRequest portletRequest, PortalException portalException)
		throws PortalException {

		String message = StringPool.BLANK;
		int status = 0;

		if (portalException instanceof AntivirusScannerException ||
			portalException instanceof DLStorageQuotaExceededException ||
			portalException instanceof DuplicateFileEntryException ||
			portalException instanceof FileExtensionException ||
			portalException instanceof FileMimeTypeException ||
			portalException instanceof FileNameException ||
			portalException instanceof FileSizeException ||
			portalException instanceof UploadRequestSizeException) {

			ThemeDisplay themeDisplay =
				(ThemeDisplay)portletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			if (portalException instanceof AntivirusScannerException) {
				AntivirusScannerException antivirusScannerException =
					(AntivirusScannerException)portalException;

				message = themeDisplay.translate(
					antivirusScannerException.getMessageKey());

				status = ServletResponseConstants.SC_FILE_ANTIVIRUS_EXCEPTION;
			}

			if (portalException instanceof DLStorageQuotaExceededException) {
				message = themeDisplay.translate(
					"you-have-exceeded-the-x-storage-quota-for-this-instance",
					_language.formatStorageSize(
						PropsValues.DATA_LIMIT_DL_STORAGE_MAX_SIZE,
						themeDisplay.getLocale()));
				status = ServletResponseConstants.SC_FILE_SIZE_EXCEPTION;
			}
			else if (portalException instanceof DuplicateFileEntryException) {
				message = themeDisplay.translate(
					"please-enter-a-unique-document-name");
				status = ServletResponseConstants.SC_DUPLICATE_FILE_EXCEPTION;
			}
			else if (portalException instanceof FileExtensionException) {
				message = themeDisplay.translate(
					"please-enter-a-file-with-a-valid-extension-x",
					_getAllowedFileExtensions());
				status = ServletResponseConstants.SC_FILE_EXTENSION_EXCEPTION;
			}
			else if (portalException instanceof FileMimeTypeException) {
				message = themeDisplay.translate(
					"please-enter-a-file-with-a-valid-mime-type-x",
					_getAllowedMimeTypes(themeDisplay));
				status = ServletResponseConstants.SC_FILE_MIME_TYPE_EXCEPTION;
			}
			else if (portalException instanceof FileNameException) {
				message = themeDisplay.translate(
					"please-enter-a-file-with-a-valid-file-name");
				status = ServletResponseConstants.SC_FILE_NAME_EXCEPTION;
			}
			else if (portalException instanceof FileSizeException) {
				FileSizeException fileSizeException =
					(FileSizeException)portalException;

				message = themeDisplay.translate(
					"please-enter-a-file-with-a-valid-file-size-no-larger-" +
						"than-x",
					_language.formatStorageSize(
						fileSizeException.getMaxSize(),
						themeDisplay.getLocale()));

				status = ServletResponseConstants.SC_FILE_SIZE_EXCEPTION;
			}
			else if (portalException instanceof UploadRequestSizeException) {
				status =
					ServletResponseConstants.SC_UPLOAD_REQUEST_SIZE_EXCEPTION;
			}
		}

		return JSONUtil.put(
			"message", message
		).put(
			"status", status
		);
	}

	@Override
	public JSONObject onSuccess(
			UploadPortletRequest uploadPortletRequest, FileEntry fileEntry)
		throws PortalException {

		return JSONUtil.put(
			"groupId", fileEntry.getGroupId()
		).put(
			"name", fileEntry.getTitle()
		).put(
			"title", uploadPortletRequest.getFileName("file")
		).put(
			"uuid", fileEntry.getUuid()
		);
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_dlConfiguration = ConfigurableUtil.createConfigurable(
			DLConfiguration.class, properties);
	}

	private String _getAllowedFileExtensions() {
		String[] allowedFileExtensions = _dlConfiguration.fileExtensions();

		return StringUtil.merge(
			allowedFileExtensions, StringPool.COMMA_AND_SPACE);
	}

	private String _getAllowedMimeTypes(ThemeDisplay themeDisplay)
		throws ConfigurationException {

		DLFileEntryMimeTypeConfiguration dlFileEntryMimeTypeConfiguration =
			_configurationProvider.getCompanyConfiguration(
				DLFileEntryMimeTypeConfiguration.class,
				themeDisplay.getCompanyId());

		return StringUtil.merge(
			dlFileEntryMimeTypeConfiguration.fileMimeTypes(),
			StringPool.COMMA_AND_SPACE);
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	private volatile DLConfiguration _dlConfiguration;

	@Reference
	private Language _language;

}