/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.upload.web.internal;

import com.liferay.document.library.configuration.DLConfiguration;
import com.liferay.document.library.configuration.DLFileEntryMimeTypeConfiguration;
import com.liferay.document.library.kernel.antivirus.AntivirusScannerException;
import com.liferay.document.library.kernel.exception.FileExtensionException;
import com.liferay.document.library.kernel.exception.FileMimeTypeException;
import com.liferay.document.library.kernel.exception.FileNameException;
import com.liferay.document.library.kernel.exception.FileSizeException;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.editor.constants.EditorConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.servlet.ServletResponseConstants;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.upload.UploadRequestSizeException;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.upload.UploadResponseHandler;

import jakarta.portlet.PortletRequest;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	configurationPid = "com.liferay.document.library.configuration.DLConfiguration",
	property = "upload.response.handler.system.default=true",
	service = UploadResponseHandler.class
)
public class DefaultUploadResponseHandler implements UploadResponseHandler {

	@Override
	public JSONObject onFailure(
			PortletRequest portletRequest, PortalException portalException)
		throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return JSONUtil.put(
			"error",
			() -> {
				if (!(portalException instanceof AntivirusScannerException) &&
					!(portalException instanceof FileExtensionException) &&
					!(portalException instanceof FileMimeTypeException) &&
					!(portalException instanceof FileNameException) &&
					!(portalException instanceof FileSizeException) &&
					!(portalException instanceof UploadRequestSizeException)) {

					return null;
				}

				int errorType = 0;
				String message = StringPool.BLANK;

				if (portalException instanceof AntivirusScannerException) {
					errorType =
						ServletResponseConstants.SC_FILE_ANTIVIRUS_EXCEPTION;

					AntivirusScannerException antivirusScannerException =
						(AntivirusScannerException)portalException;

					message = themeDisplay.translate(
						antivirusScannerException.getMessageKey());
				}
				else if (portalException instanceof FileExtensionException) {
					errorType =
						ServletResponseConstants.SC_FILE_EXTENSION_EXCEPTION;
					message = _getAllowedFileExtensions();
				}
				else if (portalException instanceof FileMimeTypeException) {
					errorType =
						ServletResponseConstants.SC_FILE_MIME_TYPE_EXCEPTION;
					message = themeDisplay.translate(
						"please-enter-a-file-with-a-valid-mime-type-x",
						_getAllowedMimeTypes(themeDisplay));
				}
				else if (portalException instanceof FileNameException) {
					errorType = ServletResponseConstants.SC_FILE_NAME_EXCEPTION;
				}
				else if (portalException instanceof FileSizeException) {
					errorType = ServletResponseConstants.SC_FILE_SIZE_EXCEPTION;

					FileSizeException fileSizeException =
						(FileSizeException)portalException;

					message = themeDisplay.translate(
						"please-enter-a-file-with-a-valid-file-size-no-" +
							"larger-than-x",
						_language.formatStorageSize(
							fileSizeException.getMaxSize(),
							themeDisplay.getLocale()));
				}
				else if (portalException instanceof
							UploadRequestSizeException) {

					errorType =
						ServletResponseConstants.
							SC_UPLOAD_REQUEST_SIZE_EXCEPTION;
				}

				return JSONUtil.put(
					"errorType", errorType
				).put(
					"message", message
				);
			}
		).put(
			"success", Boolean.FALSE
		);
	}

	@Override
	public JSONObject onSuccess(
			UploadPortletRequest uploadPortletRequest, FileEntry fileEntry)
		throws PortalException {

		return JSONUtil.put(
			"file",
			JSONUtil.put(
				"attributeDataImageId", EditorConstants.ATTRIBUTE_DATA_IMAGE_ID
			).put(
				"fileEntryId", fileEntry.getFileEntryId()
			).put(
				"groupId", fileEntry.getGroupId()
			).put(
				"mimeType", fileEntry.getMimeType()
			).put(
				"randomId",
				ParamUtil.getString(uploadPortletRequest, "randomId")
			).put(
				"title", fileEntry.getTitle()
			).put(
				"type", "document"
			).put(
				"url",
				() -> {
					ThemeDisplay themeDisplay =
						(ThemeDisplay)uploadPortletRequest.getAttribute(
							WebKeys.THEME_DISPLAY);

					return PortletFileRepositoryUtil.getPortletFileEntryURL(
						themeDisplay, fileEntry, StringPool.BLANK);
				}
			).put(
				"uuid", fileEntry.getUuid()
			)
		).put(
			"success", Boolean.TRUE
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