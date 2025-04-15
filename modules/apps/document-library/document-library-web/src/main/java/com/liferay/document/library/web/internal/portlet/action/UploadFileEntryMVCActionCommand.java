/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.action;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.util.DLValidator;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.item.selector.ItemSelectorUploadResponseHandler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.service.permission.ModelPermissionsFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.GetterUtil;
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
 * @author Roberto Díaz
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY,
		"jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY_ADMIN,
		"jakarta.portlet.name=" + DLPortletKeys.MEDIA_GALLERY_DISPLAY,
		"mvc.command.name=/document_library/image_editor",
		"mvc.command.name=/document_library/upload_file_entry"
	},
	service = MVCActionCommand.class
)
public class UploadFileEntryMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws PortalException {

		_uploadHandler.upload(
			_dlUploadFileEntryHandler, _dlUploadResponseHandler, actionRequest,
			actionResponse);
	}

	@Reference
	private DLAppService _dlAppService;

	private final DLUploadFileEntryHandler _dlUploadFileEntryHandler =
		new DLUploadFileEntryHandler();
	private final DLUploadResponseHandler _dlUploadResponseHandler =
		new DLUploadResponseHandler();

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
	private UniqueFileNameProvider _uniqueFileNameProvider;

	@Reference
	private UploadHandler _uploadHandler;

	private class DLUploadFileEntryHandler implements UploadFileEntryHandler {

		@Override
		public FileEntry upload(UploadPortletRequest uploadPortletRequest)
			throws IOException, PortalException {

			ThemeDisplay themeDisplay =
				(ThemeDisplay)uploadPortletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			Group group = _getGroup(themeDisplay);

			long folderId = ParamUtil.getLong(uploadPortletRequest, "folderId");

			ModelResourcePermissionUtil.check(
				_folderModelResourcePermission,
				themeDisplay.getPermissionChecker(), group.getGroupId(),
				folderId, ActionKeys.ADD_DOCUMENT);

			String fileName = uploadPortletRequest.getFileName(
				"imageSelectorFileName");

			if (Validator.isNotNull(fileName)) {
				try (InputStream inputStream =
						uploadPortletRequest.getFileAsStream(
							"imageSelectorFileName")) {

					return _addFileEntry(
						fileName, folderId, inputStream,
						"imageSelectorFileName", uploadPortletRequest,
						themeDisplay);
				}
			}

			return _editImageFileEntry(
				uploadPortletRequest, themeDisplay, folderId);
		}

		private FileEntry _addFileEntry(
				String fileName, long folderId, InputStream inputStream,
				String parameterName, UploadPortletRequest uploadPortletRequest,
				ThemeDisplay themeDisplay)
			throws PortalException {

			Group group = _getGroup(themeDisplay);

			_dlValidator.validateFileSize(
				group.getGroupId(), fileName,
				uploadPortletRequest.getContentType(parameterName),
				uploadPortletRequest.getSize(parameterName));

			String uniqueFileName = _uniqueFileNameProvider.provide(
				fileName,
				curFileName -> _exists(
					group.getGroupId(), folderId, curFileName));

			return _dlAppService.addFileEntry(
				null, group.getGroupId(), folderId, uniqueFileName,
				uploadPortletRequest.getContentType(parameterName),
				uniqueFileName, uniqueFileName,
				_getDescription(uploadPortletRequest), StringPool.BLANK,
				inputStream, uploadPortletRequest.getSize(parameterName), null,
				null, null, _getServiceContext(uploadPortletRequest));
		}

		private FileEntry _editImageFileEntry(
				UploadPortletRequest uploadPortletRequest,
				ThemeDisplay themeDisplay, long folderId)
			throws IOException, PortalException {

			try (InputStream inputStream = uploadPortletRequest.getFileAsStream(
					"imageBlob")) {

				long fileEntryId = ParamUtil.getLong(
					uploadPortletRequest, "fileEntryId");

				FileEntry fileEntry = _dlAppService.getFileEntry(fileEntryId);

				return _addFileEntry(
					fileEntry.getFileName(), folderId, inputStream, "imageBlob",
					uploadPortletRequest, themeDisplay);
			}
		}

		private boolean _exists(long groupId, long folderId, String fileName) {
			try {
				FileEntry fileEntry = _dlAppService.getFileEntryByFileName(
					groupId, folderId, fileName);

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

		private FileEntry _fetchFileEntry(
				UploadPortletRequest uploadPortletRequest)
			throws PortalException {

			try {
				long fileEntryId = GetterUtil.getLong(
					uploadPortletRequest.getParameter("fileEntryId"));

				if (fileEntryId == 0) {
					return null;
				}

				return _dlAppService.getFileEntry(fileEntryId);
			}
			catch (NoSuchFileEntryException noSuchFileEntryException) {
				if (_log.isDebugEnabled()) {
					_log.debug(noSuchFileEntryException);
				}

				return null;
			}
		}

		private String _getDescription(
				UploadPortletRequest uploadPortletRequest)
			throws PortalException {

			FileEntry fileEntry = _fetchFileEntry(uploadPortletRequest);

			if (fileEntry == null) {
				return StringPool.BLANK;
			}

			return fileEntry.getDescription();
		}

		private Group _getGroup(ThemeDisplay themeDisplay)
			throws PortalException {

			Group group = themeDisplay.getScopeGroup();

			if (group.isControlPanel()) {
				Company company = themeDisplay.getCompany();

				return company.getGroup();
			}

			return group;
		}

		private ServiceContext _getServiceContext(
				UploadPortletRequest uploadPortletRequest)
			throws PortalException {

			FileEntry fileEntry = _fetchFileEntry(uploadPortletRequest);

			if ((fileEntry == null) ||
				!(fileEntry.getModel() instanceof DLFileEntry)) {

				return _getServiceContextWithRestrictedGuestPermissions(
					uploadPortletRequest);
			}

			ServiceContext serviceContext =
				_getServiceContextWithRestrictedGuestPermissions(
					uploadPortletRequest);

			ExpandoBridge expandoBridge = fileEntry.getExpandoBridge();

			serviceContext.setExpandoBridgeAttributes(
				expandoBridge.getAttributes());

			return serviceContext;
		}

		private ServiceContext _getServiceContextWithRestrictedGuestPermissions(
				UploadPortletRequest uploadPortletRequest)
			throws PortalException {

			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				DLFileEntry.class.getName(), uploadPortletRequest);

			ThemeDisplay themeDisplay =
				(ThemeDisplay)uploadPortletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			Layout layout = themeDisplay.getLayout();
			Group group = _getGroup(themeDisplay);

			if (layout.isPublicLayout() ||
				(layout.isTypeControlPanel() && !group.hasPrivateLayouts())) {

				return serviceContext;
			}

			ModelPermissions modelPermissions =
				serviceContext.getModelPermissions();

			serviceContext.setModelPermissions(
				ModelPermissionsFactory.create(
					modelPermissions.getActionIds(
						RoleConstants.PLACEHOLDER_DEFAULT_GROUP_ROLE),
					_restrictedGuestPermissions, DLFileEntry.class.getName()));

			return serviceContext;
		}

		private final Log _log = LogFactoryUtil.getLog(
			DLUploadFileEntryHandler.class);
		private final String[] _restrictedGuestPermissions = {};

	}

	private class DLUploadResponseHandler implements UploadResponseHandler {

		@Override
		public JSONObject onFailure(
				PortletRequest portletRequest, PortalException portalException)
			throws PortalException {

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
			DLUploadResponseHandler.class);

	}

}