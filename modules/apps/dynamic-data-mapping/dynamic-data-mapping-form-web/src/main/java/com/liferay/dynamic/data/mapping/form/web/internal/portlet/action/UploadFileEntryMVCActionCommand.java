/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.portlet.action;

import com.liferay.document.library.configuration.DLFileEntryMimeTypeConfiguration;
import com.liferay.document.library.kernel.exception.FileExtensionException;
import com.liferay.document.library.kernel.exception.FileMimeTypeException;
import com.liferay.document.library.kernel.exception.FileNameException;
import com.liferay.document.library.kernel.exception.FileSizeException;
import com.liferay.document.library.kernel.exception.InvalidFileException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.util.DLValidator;
import com.liferay.dynamic.data.mapping.constants.DDMActionKeys;
import com.liferay.dynamic.data.mapping.constants.DDMFormConstants;
import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.form.web.internal.portlet.action.util.FileEntryMVCActionCommandUtil;
import com.liferay.dynamic.data.mapping.form.web.internal.security.permission.resource.DDMFormInstancePermission;
import com.liferay.dynamic.data.mapping.form.web.internal.upload.DDMFormUploadValidator;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.util.DDMFormUtil;
import com.liferay.object.exception.ObjectEntryValuesException;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.upload.UploadFileEntryHandler;
import com.liferay.upload.UploadHandler;
import com.liferay.upload.UploadResponseHandler;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletRequest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM,
		"jakarta.portlet.name=" + DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM_ADMIN,
		"mvc.command.name=/dynamic_data_mapping_form/upload_file_entry"
	},
	service = MVCActionCommand.class
)
public class UploadFileEntryMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		FileEntryMVCActionCommandUtil.deleteFileEntry(
			ParamUtil.getLong(actionRequest, "oldFileEntryId"),
			(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY));

		_uploadHandler.upload(
			_ddmFormUploadFileEntryHandler, _ddmFormUploadResponseHandler,
			actionRequest, actionResponse);

		hideDefaultSuccessMessage(actionRequest);
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	private final DDMFormUploadFileEntryHandler _ddmFormUploadFileEntryHandler =
		new DDMFormUploadFileEntryHandler();
	private final DDMFormUploadResponseHandler _ddmFormUploadResponseHandler =
		new DDMFormUploadResponseHandler();

	@Reference(target = "(upload.response.handler.system.default=true)")
	private UploadResponseHandler _defaultUploadResponseHandler;

	@Reference
	private DLValidator _dlValidator;

	@Reference
	private Language _language;

	@Reference
	private ObjectFieldSettingLocalService _objectFieldSettingLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UploadHandler _uploadHandler;

	private class DDMFormUploadFileEntryHandler
		implements UploadFileEntryHandler {

		@Override
		public FileEntry upload(UploadPortletRequest uploadPortletRequest)
			throws IOException, PortalException {

			File file = null;

			try {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)uploadPortletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				InputStream inputStream = uploadPortletRequest.getFileAsStream(
					"file");

				if (inputStream == null) {
					inputStream = new UnsyncByteArrayInputStream(new byte[0]);
				}

				file = FileUtil.createTempFile(inputStream);

				String fileName = uploadPortletRequest.getFileName("file");

				String mimeType = MimeTypesUtil.getContentType(file, fileName);

				_dlValidator.validateFileMimeType(
					themeDisplay.getCompanyId(), mimeType);

				DDMFormUploadValidator.validateFileSize(file, fileName);

				long objectFieldId = ParamUtil.getLong(
					uploadPortletRequest, "objectFieldId");

				if (objectFieldId > 0) {
					_validateAttachmentObjectField(fileName, objectFieldId);
				}

				DDMFormUploadValidator.validateFileExtension(fileName);

				return addFileEntry(
					ParamUtil.getLong(uploadPortletRequest, "formInstanceId"),
					ParamUtil.getLong(uploadPortletRequest, "groupId"),
					ParamUtil.getLong(uploadPortletRequest, "folderId"), file,
					fileName, mimeType, themeDisplay);
			}
			finally {
				FileUtil.delete(file);
			}
		}

		protected FileEntry addFileEntry(
				long formInstanceId, long groupId, long folderId, File file,
				String fileName, String mimeType, ThemeDisplay themeDisplay)
			throws PortalException {

			if (!DDMFormInstancePermission.contains(
					themeDisplay.getPermissionChecker(), formInstanceId,
					DDMActionKeys.ADD_FORM_INSTANCE_RECORD)) {

				throw new PrincipalException.MustHavePermission(
					themeDisplay.getPermissionChecker(),
					DDMFormInstance.class.getName(), formInstanceId,
					DDMActionKeys.ADD_FORM_INSTANCE_RECORD);
			}

			User user = DDMFormUtil.getDDMFormDefaultUser(
				themeDisplay.getCompanyId());

			String uniqueFileName = PortletFileRepositoryUtil.getUniqueFileName(
				groupId, folderId, fileName);

			FileEntry fileEntry = PortletFileRepositoryUtil.addPortletFileEntry(
				null, groupId, user.getUserId(),
				DDMFormInstance.class.getName(), 0,
				DDMFormConstants.SERVICE_NAME, folderId, file, uniqueFileName,
				mimeType, true);

			_resourcePermissionLocalService.removeResourcePermission(
				themeDisplay.getCompanyId(), DLFileEntry.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(fileEntry.getFileEntryId()),
				_roleLocalService.getRole(
					themeDisplay.getCompanyId(), RoleConstants.GUEST
				).getRoleId(),
				ActionKeys.VIEW);

			return fileEntry;
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

	}

	private class DDMFormUploadResponseHandler
		implements UploadResponseHandler {

		@Override
		public JSONObject onFailure(
				PortletRequest portletRequest, PortalException portalException)
			throws PortalException {

			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			JSONObject jsonObject = _defaultUploadResponseHandler.onFailure(
				portletRequest, portalException);

			String errorMessage = StringPool.BLANK;

			ThemeDisplay themeDisplay =
				(ThemeDisplay)portletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			if (portalException instanceof FileExtensionException) {
				errorMessage = themeDisplay.translate(
					"please-enter-a-file-with-a-valid-extension-x",
					StringUtil.merge(
						DDMFormUploadValidator.getGuestUploadFileExtensions(),
						StringPool.COMMA_AND_SPACE));
			}
			else if (portalException instanceof FileMimeTypeException) {
				DLFileEntryMimeTypeConfiguration
					dlFileEntryMimeTypeConfiguration =
						_configurationProvider.getCompanyConfiguration(
							DLFileEntryMimeTypeConfiguration.class,
							themeDisplay.getCompanyId());

				errorMessage = themeDisplay.translate(
					"please-enter-a-file-with-a-valid-mime-type-x",
					StringUtil.merge(
						dlFileEntryMimeTypeConfiguration.fileMimeTypes(),
						StringPool.COMMA_AND_SPACE));
			}
			else if (portalException instanceof FileNameException) {
				errorMessage = themeDisplay.translate(
					"please-enter-a-file-with-a-valid-file-name");
			}
			else if (portalException instanceof FileSizeException) {
				errorMessage = themeDisplay.translate(
					"please-enter-a-file-with-a-valid-file-size-no-larger-" +
						"than-x",
					_language.formatStorageSize(
						DDMFormUploadValidator.getGuestUploadMaximumFileSize(),
						themeDisplay.getLocale()));
			}
			else if (portalException instanceof InvalidFileException) {
				errorMessage = themeDisplay.translate(
					"please-enter-a-valid-file");
			}
			else if (portalException instanceof
						ObjectEntryValuesException.InvalidFileExtension) {

				ObjectFieldSetting objectFieldSetting =
					_objectFieldSettingLocalService.fetchObjectFieldSetting(
						ParamUtil.getLong(portletRequest, "objectFieldId"),
						"acceptedFileExtensions");

				errorMessage = themeDisplay.translate(
					"please-enter-a-file-with-a-valid-extension-x",
					objectFieldSetting.getValue());
			}
			else {
				errorMessage = themeDisplay.translate(
					"an-unexpected-error-occurred-while-uploading-your-file");
			}

			return jsonObject.put(
				"error", JSONUtil.put("message", errorMessage));
		}

		@Override
		public JSONObject onSuccess(
				UploadPortletRequest uploadPortletRequest, FileEntry fileEntry)
			throws PortalException {

			return _defaultUploadResponseHandler.onSuccess(
				uploadPortletRequest, fileEntry);
		}

		private final Log _log = LogFactoryUtil.getLog(
			DDMFormUploadResponseHandler.class);

	}

}