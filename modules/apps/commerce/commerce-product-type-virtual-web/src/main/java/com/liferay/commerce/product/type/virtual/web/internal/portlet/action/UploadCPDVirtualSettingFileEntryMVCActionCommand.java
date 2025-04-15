/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.web.internal.portlet.action;

import com.liferay.commerce.product.constants.CPConstants;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.exception.CPAttachmentFileEntryNameException;
import com.liferay.commerce.product.exception.CPAttachmentFileEntrySizeException;
import com.liferay.commerce.product.type.virtual.service.CPDVirtualSettingFileEntryService;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.util.DLValidator;
import com.liferay.item.selector.ItemSelectorUploadResponseHandler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.RepositoryLocalService;
import com.liferay.portal.kernel.servlet.ServletResponseConstants;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ParamUtil;
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
 * @author Andrea Sbarra
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.CP_DEFINITIONS,
		"mvc.command.name=/cp_definitions/upload_cpd_virtual_setting_file_entry"
	},
	service = MVCActionCommand.class
)
public class UploadCPDVirtualSettingFileEntryMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		_uploadHandler.upload(
			_cpdVirtualSettingFileEntryUploadHandler,
			_cpdVirtualSettingFileEntryUploadResponseHandler, actionRequest,
			actionResponse);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UploadCPDVirtualSettingFileEntryMVCActionCommand.class);

	@Reference
	private CPDVirtualSettingFileEntryService
		_cpdVirtualSettingFileEntryService;

	private final CPDVirtualSettingFileEntryUploadHandler
		_cpdVirtualSettingFileEntryUploadHandler =
			new CPDVirtualSettingFileEntryUploadHandler();
	private final CPDVirtualSettingFileEntryUploadResponseHandler
		_cpdVirtualSettingFileEntryUploadResponseHandler =
			new CPDVirtualSettingFileEntryUploadResponseHandler();

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private DLValidator _dlValidator;

	@Reference
	private ItemSelectorUploadResponseHandler
		_itemSelectorUploadResponseHandler;

	@Reference
	private RepositoryLocalService _repositoryService;

	@Reference
	private UniqueFileNameProvider _uniqueFileNameProvider;

	@Reference
	private UploadHandler _uploadHandler;

	private class CPDVirtualSettingFileEntryUploadHandler
		implements UploadFileEntryHandler {

		@Override
		public FileEntry upload(UploadPortletRequest uploadPortletRequest)
			throws IOException, PortalException {

			String fileName = uploadPortletRequest.getFileName(
				"imageSelectorFileName");

			if (Validator.isNotNull(fileName)) {
				try (InputStream inputStream =
						uploadPortletRequest.getFileAsStream(
							"imageSelectorFileName")) {

					return _addFileEntry(
						fileName, inputStream, "imageSelectorFileName",
						uploadPortletRequest);
				}
			}

			throw new PortalException();
		}

		private FileEntry _addFileEntry(
				String fileName, InputStream inputStream, String parameterName,
				UploadPortletRequest uploadPortletRequest)
			throws PortalException {

			ThemeDisplay themeDisplay =
				(ThemeDisplay)uploadPortletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			_dlValidator.validateFileSize(
				themeDisplay.getScopeGroupId(), fileName,
				uploadPortletRequest.getContentType(parameterName),
				uploadPortletRequest.getSize(parameterName));

			long catalogGroupId = ParamUtil.getLong(
				uploadPortletRequest, "catalogGroupId");

			Repository repository = _repositoryService.fetchRepository(
				catalogGroupId, CPConstants.SERVICE_NAME_PRODUCT);

			String uniqueFileName = _uniqueFileNameProvider.provide(
				fileName,
				curFileName -> _exists(
					catalogGroupId, repository, curFileName));

			return _cpdVirtualSettingFileEntryService.addFileEntry(
				catalogGroupId,
				(repository != null) ? repository.getDlFolderId() : 0,
				inputStream, uniqueFileName,
				uploadPortletRequest.getContentType(parameterName),
				CPConstants.SERVICE_NAME_PRODUCT);
		}

		private boolean _exists(
			long groupId, Repository repository, String fileName) {

			try {
				if (repository == null) {
					return false;
				}

				FileEntry fileEntry = _dlAppService.getFileEntryByFileName(
					groupId, repository.getDlFolderId(), fileName);

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

	}

	private class CPDVirtualSettingFileEntryUploadResponseHandler
		implements UploadResponseHandler {

		@Override
		public JSONObject onFailure(
				PortletRequest portletRequest, PortalException portalException)
			throws PortalException {

			JSONObject jsonObject =
				_itemSelectorUploadResponseHandler.onFailure(
					portletRequest, portalException);

			if (portalException instanceof CPAttachmentFileEntryNameException ||
				portalException instanceof CPAttachmentFileEntrySizeException) {

				String errorMessage = StringPool.BLANK;

				int errorType = 0;

				if (portalException instanceof
						CPAttachmentFileEntryNameException) {

					errorType =
						ServletResponseConstants.SC_FILE_EXTENSION_EXCEPTION;
				}
				else if (portalException instanceof
							CPAttachmentFileEntrySizeException) {

					errorType = ServletResponseConstants.SC_FILE_SIZE_EXCEPTION;
				}

				jsonObject.put(
					"error",
					JSONUtil.put(
						"errorType", errorType
					).put(
						"message", errorMessage
					));
			}
			else {
				throw portalException;
			}

			return jsonObject;
		}

		@Override
		public JSONObject onSuccess(
				UploadPortletRequest uploadPortletRequest, FileEntry fileEntry)
			throws PortalException {

			return _itemSelectorUploadResponseHandler.onSuccess(
				uploadPortletRequest, fileEntry);
		}

	}

}