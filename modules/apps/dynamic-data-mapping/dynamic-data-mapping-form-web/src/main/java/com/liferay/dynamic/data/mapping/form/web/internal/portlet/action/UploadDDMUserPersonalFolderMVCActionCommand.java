/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.portlet.action;

import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.util.DLValidator;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.item.selector.ItemSelectorUploadResponseHandler;
import com.liferay.object.exception.ObjectEntryValuesException;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.ServletResponseConstants;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
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

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM,
		"mvc.command.name=/dynamic_data_mapping_form/upload_ddm_user_personal_folder"
	},
	service = MVCActionCommand.class
)
public class UploadDDMUserPersonalFolderMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		_uploadHandler.upload(
			_ddmUserPersonalFolderUploadFileEntryHandler,
			_ddmUserPersonalFolderUploadResponseHandler, actionRequest,
			actionResponse);
	}

	private final DDMUserPersonalFolderUploadFileEntryHandler
		_ddmUserPersonalFolderUploadFileEntryHandler =
			new DDMUserPersonalFolderUploadFileEntryHandler();
	private final DDMUserPersonalFolderUploadResponseHandler
		_ddmUserPersonalFolderUploadResponseHandler =
			new DDMUserPersonalFolderUploadResponseHandler();

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private DLURLHelper _dlURLHelper;

	@Reference
	private DLValidator _dlValidator;

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.repository.model.Folder)"
	)
	private ModelResourcePermission<Folder> _folderModelResourcePermission;

	@Reference
	private ItemSelectorUploadResponseHandler
		_itemSelectorUploadResponseHandler;

	@Reference
	private ObjectFieldSettingLocalService _objectFieldSettingLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private UniqueFileNameProvider _uniqueFileNameProvider;

	@Reference
	private UploadHandler _uploadHandler;

	private class DDMUserPersonalFolderUploadFileEntryHandler
		implements UploadFileEntryHandler {

		@Override
		public FileEntry upload(UploadPortletRequest uploadPortletRequest)
			throws IOException, PortalException {

			ThemeDisplay themeDisplay =
				(ThemeDisplay)uploadPortletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			long folderId = ParamUtil.getLong(uploadPortletRequest, "folderId");

			ModelResourcePermissionUtil.check(
				_folderModelResourcePermission,
				themeDisplay.getPermissionChecker(),
				themeDisplay.getScopeGroupId(), folderId,
				ActionKeys.ADD_DOCUMENT);

			FileEntry fileEntry = null;

			long fileEntryId = GetterUtil.getLong(
				uploadPortletRequest.getParameter("fileEntryId"));

			if (fileEntryId > 0) {
				try {
					fileEntry = _dlAppService.getFileEntry(fileEntryId);
				}
				catch (NoSuchFileEntryException noSuchFileEntryException) {
					if (_log.isDebugEnabled()) {
						_log.debug(noSuchFileEntryException);
					}
				}
			}

			String fileName = uploadPortletRequest.getFileName(
				"imageSelectorFileName");

			if (Validator.isNotNull(fileName)) {
				try (InputStream inputStream =
						uploadPortletRequest.getFileAsStream(
							"imageSelectorFileName")) {

					return _addFileEntry(
						fileEntry, fileName, folderId, inputStream,
						"imageSelectorFileName", themeDisplay,
						uploadPortletRequest);
				}
			}

			try (InputStream inputStream = uploadPortletRequest.getFileAsStream(
					"imageBlob")) {

				return _addFileEntry(
					fileEntry, fileEntry.getFileName(), folderId, inputStream,
					"imageBlob", themeDisplay, uploadPortletRequest);
			}
		}

		private FileEntry _addFileEntry(
				FileEntry fileEntry, String fileName, long folderId,
				InputStream inputStream, String parameterName,
				ThemeDisplay themeDisplay,
				UploadPortletRequest uploadPortletRequest)
			throws PortalException {

			long size = uploadPortletRequest.getSize(parameterName);

			_dlValidator.validateFileSize(
				themeDisplay.getScopeGroupId(), fileName,
				uploadPortletRequest.getContentType(parameterName), size);

			long objectFieldId = ParamUtil.getLong(
				uploadPortletRequest, "objectFieldId");

			if (objectFieldId > 0) {
				_validateAttachmentObjectField(fileName, objectFieldId);
			}

			long repositoryId = ParamUtil.getLong(
				uploadPortletRequest, "repositoryId");

			String uniqueFileName = _uniqueFileNameProvider.provide(
				fileName,
				curFileName -> _exists(repositoryId, folderId, curFileName));

			String description = StringPool.BLANK;

			if (fileEntry != null) {
				description = fileEntry.getDescription();
			}

			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				DLFileEntry.class.getName(), uploadPortletRequest);

			if ((fileEntry != null) &&
				(fileEntry.getModel() instanceof DLFileEntry)) {

				ExpandoBridge expandoBridge = fileEntry.getExpandoBridge();

				serviceContext.setExpandoBridgeAttributes(
					expandoBridge.getAttributes());
			}

			return _dlAppService.addFileEntry(
				null, repositoryId, folderId, uniqueFileName,
				uploadPortletRequest.getContentType(parameterName),
				uniqueFileName, uniqueFileName, description, StringPool.BLANK,
				inputStream, size, null, null, null, serviceContext);
		}

		private boolean _exists(
			long repositoryId, long folderId, String fileName) {

			try {
				FileEntry fileEntry = _dlAppService.getFileEntry(
					repositoryId, folderId, fileName);

				if (fileEntry != null) {
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

		private void _validateAttachmentObjectField(
				String fileName, long objectFieldId)
			throws PortalException {

			ObjectFieldSetting objectFieldSetting =
				_objectFieldSettingLocalService.fetchObjectFieldSetting(
					objectFieldId, "acceptedFileExtensions");

			String value = objectFieldSetting.getValue();

			if (!ArrayUtil.contains(
					value.split("\\s*,\\s*"), FileUtil.getExtension(fileName),
					true)) {

				throw new ObjectEntryValuesException.InvalidFileExtension(
					FileUtil.getExtension(fileName), fileName);
			}
		}

		private final Log _log = LogFactoryUtil.getLog(
			DDMUserPersonalFolderUploadFileEntryHandler.class);

	}

	private class DDMUserPersonalFolderUploadResponseHandler
		implements UploadResponseHandler {

		@Override
		public JSONObject onFailure(
				PortletRequest portletRequest, PortalException portalException)
			throws PortalException {

			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			if (portalException instanceof
					ObjectEntryValuesException.InvalidFileExtension) {

				return JSONUtil.put(
					"error",
					() -> {
						ObjectFieldSetting objectFieldSetting =
							_objectFieldSettingLocalService.
								fetchObjectFieldSetting(
									ParamUtil.getLong(
										portletRequest, "objectFieldId"),
									"acceptedFileExtensions");

						return JSONUtil.put(
							"errorType",
							ServletResponseConstants.SC_FILE_EXTENSION_EXCEPTION
						).put(
							"message", objectFieldSetting.getValue()
						);
					}
				).put(
					"success", Boolean.FALSE
				);
			}

			return _itemSelectorUploadResponseHandler.onFailure(
				portletRequest, portalException);
		}

		@Override
		public JSONObject onSuccess(
				UploadPortletRequest uploadPortletRequest, FileEntry fileEntry)
			throws PortalException {

			JSONObject jsonObject =
				_itemSelectorUploadResponseHandler.onSuccess(
					uploadPortletRequest, fileEntry);

			JSONObject fileJSONObject = jsonObject.getJSONObject("file");

			fileJSONObject.put("url", _getURL(uploadPortletRequest, fileEntry));

			if (SessionMessages.contains(
					uploadPortletRequest,
					_portal.getPortletId(uploadPortletRequest) +
						SessionMessages.
							KEY_SUFFIX_HIDE_DEFAULT_SUCCESS_MESSAGE)) {

				SessionMessages.clear(uploadPortletRequest);
			}

			return jsonObject;
		}

		private String _getURL(
			UploadPortletRequest uploadPortletRequest, FileEntry fileEntry) {

			try {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)uploadPortletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				return _dlURLHelper.getPreviewURL(
					fileEntry, fileEntry.getLatestFileVersion(), themeDisplay,
					StringPool.BLANK);
			}
			catch (PortalException portalException) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to get URL for file entry " +
							fileEntry.getFileEntryId(),
						portalException);
				}
			}

			return StringPool.BLANK;
		}

		private final Log _log = LogFactoryUtil.getLog(
			DDMUserPersonalFolderUploadResponseHandler.class);

	}

}