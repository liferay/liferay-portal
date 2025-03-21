/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.portlet.action;

import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.util.DLValidator;
import com.liferay.item.selector.ItemSelectorUploadResponseHandler;
import com.liferay.journal.configuration.JournalFileUploadsConfiguration;
import com.liferay.journal.constants.JournalConstants;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.ImageTypeException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.servlet.ServletResponseConstants;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.upload.UniqueFileNameProvider;
import com.liferay.upload.UploadFileEntryHandler;
import com.liferay.upload.UploadHandler;
import com.liferay.upload.UploadResponseHandler;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletRequest;

import java.io.IOException;
import java.io.InputStream;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(
	configurationPid = "com.liferay.journal.configuration.JournalFileUploadsConfiguration",
	property = {
		"jakarta.portlet.name=" + JournalPortletKeys.JOURNAL,
		"mvc.command.name=/journal/image_editor",
		"mvc.command.name=/journal/upload_image"
	},
	service = MVCActionCommand.class
)
public class UploadImageMVCActionCommand extends BaseMVCActionCommand {

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_journalFileUploadsConfiguration = ConfigurableUtil.createConfigurable(
			JournalFileUploadsConfiguration.class, properties);
	}

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		_uploadHandler.upload(
			_imageJournalUploadFileEntryHandler,
			_imageJournalUploadResponseHandler, actionRequest, actionResponse);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UploadImageMVCActionCommand.class);

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private DLValidator _dlValidator;

	private final ImageJournalUploadFileEntryHandler
		_imageJournalUploadFileEntryHandler =
			new ImageJournalUploadFileEntryHandler();
	private final ImageJournalUploadResponseHandler
		_imageJournalUploadResponseHandler =
			new ImageJournalUploadResponseHandler();

	@Reference
	private ItemSelectorUploadResponseHandler
		_itemSelectorUploadResponseHandler;

	@Reference(
		target = "(model.class.name=com.liferay.journal.model.JournalArticle)"
	)
	private ModelResourcePermission<JournalArticle>
		_journalArticleModelResourcePermission;

	private volatile JournalFileUploadsConfiguration
		_journalFileUploadsConfiguration;

	@Reference(
		target = "(model.class.name=com.liferay.journal.model.JournalFolder)"
	)
	private ModelResourcePermission<JournalFolder>
		_journalFolderModelResourcePermission;

	@Reference(
		target = "(resource.name=" + JournalConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

	@Reference
	private UniqueFileNameProvider _uniqueFileNameProvider;

	@Reference
	private UploadHandler _uploadHandler;

	private class ImageJournalUploadFileEntryHandler
		implements UploadFileEntryHandler {

		@Override
		public FileEntry upload(UploadPortletRequest uploadPortletRequest)
			throws IOException, PortalException {

			ThemeDisplay themeDisplay =
				(ThemeDisplay)uploadPortletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			long resourcePrimKey = ParamUtil.getLong(
				uploadPortletRequest, "resourcePrimKey");

			long folderId = ParamUtil.getLong(uploadPortletRequest, "folderId");

			if (resourcePrimKey != 0) {
				_journalArticleModelResourcePermission.check(
					themeDisplay.getPermissionChecker(), resourcePrimKey,
					ActionKeys.UPDATE);
			}
			else if (folderId != 0) {
				_journalFolderModelResourcePermission.check(
					themeDisplay.getPermissionChecker(), folderId,
					ActionKeys.ADD_ARTICLE);
			}
			else {
				_portletResourcePermission.check(
					themeDisplay.getPermissionChecker(),
					themeDisplay.getScopeGroup(), ActionKeys.ADD_ARTICLE);
			}

			String fileName = uploadPortletRequest.getFileName(
				"imageSelectorFileName");

			if (Validator.isNotNull(fileName)) {
				try (InputStream inputStream =
						uploadPortletRequest.getFileAsStream(
							"imageSelectorFileName")) {

					return _addTempFileEntry(
						fileName, inputStream, "imageSelectorFileName",
						uploadPortletRequest, themeDisplay);
				}
			}

			return _editImageFileEntry(uploadPortletRequest, themeDisplay);
		}

		private FileEntry _addTempFileEntry(
				String fileName, InputStream inputStream, String parameterName,
				UploadPortletRequest uploadPortletRequest,
				ThemeDisplay themeDisplay)
			throws PortalException {

			_validateFile(
				themeDisplay.getScopeGroupId(), fileName,
				uploadPortletRequest.getContentType(parameterName),
				uploadPortletRequest.getSize(parameterName));

			String contentType = uploadPortletRequest.getContentType(
				parameterName);

			String uniqueFileName = _uniqueFileNameProvider.provide(
				fileName, curFileName -> _exists(themeDisplay, curFileName));

			return TempFileEntryUtil.addTempFileEntry(
				themeDisplay.getScopeGroupId(), themeDisplay.getUserId(),
				_tempFolderName, uniqueFileName, inputStream, contentType);
		}

		private FileEntry _editImageFileEntry(
				UploadPortletRequest uploadPortletRequest,
				ThemeDisplay themeDisplay)
			throws IOException, PortalException {

			try (InputStream inputStream = uploadPortletRequest.getFileAsStream(
					"imageBlob")) {

				long fileEntryId = ParamUtil.getLong(
					uploadPortletRequest, "fileEntryId");

				FileEntry fileEntry = _dlAppService.getFileEntry(fileEntryId);

				return _addTempFileEntry(
					fileEntry.getFileName(), inputStream, "imageBlob",
					uploadPortletRequest, themeDisplay);
			}
		}

		private boolean _exists(ThemeDisplay themeDisplay, String curFileName) {
			try {
				FileEntry tempFileEntry = TempFileEntryUtil.getTempFileEntry(
					themeDisplay.getScopeGroupId(), themeDisplay.getUserId(),
					_tempFolderName, curFileName);

				if (tempFileEntry != null) {
					return true;
				}

				return false;
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}

				return false;
			}
		}

		private void _validateFile(
				long groupId, String fileName, String mimeType, long size)
			throws PortalException {

			_dlValidator.validateFileSize(groupId, fileName, mimeType, size);

			String extension = FileUtil.getExtension(fileName);

			for (String imageExtension :
					_journalFileUploadsConfiguration.imageExtensions()) {

				if (StringPool.STAR.equals(imageExtension) ||
					imageExtension.equals(StringPool.PERIOD + extension)) {

					return;
				}
			}

			throw new ImageTypeException(
				"Invalid image type for file name " + fileName);
		}

		private final String _tempFolderName =
			ImageJournalUploadFileEntryHandler.class.getName();

	}

	private class ImageJournalUploadResponseHandler
		implements UploadResponseHandler {

		@Override
		public JSONObject onFailure(
				PortletRequest portletRequest, PortalException portalException)
			throws PortalException {

			return _itemSelectorUploadResponseHandler.onFailure(
				portletRequest, portalException
			).put(
				"error",
				() -> {
					if (!(portalException instanceof ImageTypeException)) {
						return null;
					}

					return JSONUtil.put(
						"errorType",
						ServletResponseConstants.SC_FILE_EXTENSION_EXCEPTION
					).put(
						"message", StringPool.BLANK
					);
				}
			);
		}

		@Override
		public JSONObject onSuccess(
				UploadPortletRequest uploadPortletRequest, FileEntry fileEntry)
			throws PortalException {

			JSONObject jsonObject =
				_itemSelectorUploadResponseHandler.onSuccess(
					uploadPortletRequest, fileEntry);

			JSONObject fileJSONObject = jsonObject.getJSONObject("file");

			fileJSONObject.put("type", "journal");

			return jsonObject;
		}

	}

}