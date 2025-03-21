/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.action;

import com.liferay.depot.group.provider.SiteConnectedGroupGroupProvider;
import com.liferay.document.library.configuration.DLSizeLimitConfigurationProvider;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.exception.FileSizeException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFileShortcut;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeService;
import com.liferay.document.library.kernel.service.DLFileShortcutLocalService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.document.library.web.internal.exception.DLObjectSizeLimitExceededException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RepositoryLocalService;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.FileItem;
import com.liferay.portal.kernel.upload.UploadException;
import com.liferay.portal.kernel.upload.configuration.UploadServletRequestConfigurationProvider;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sam Ziemer
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY,
		"jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY_ADMIN,
		"jakarta.portlet.name=" + DLPortletKeys.MEDIA_GALLERY_DISPLAY,
		"mvc.command.name=/document_library/copy_dl_objects"
	},
	service = MVCActionCommand.class
)
public class CopyDLObjectsMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		List<String> errorMessages = new ArrayList<>();

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		UploadException uploadException =
			(UploadException)actionRequest.getAttribute(
				WebKeys.UPLOAD_EXCEPTION);

		if (uploadException != null) {
			errorMessages.add(
				_getUploadExceptionErrorMessage(uploadException, themeDisplay));
		}
		else {
			try {
				_copyDLObjects(actionRequest, errorMessages, themeDisplay);
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}

				errorMessages.add(
					themeDisplay.translate(portalException.getMessage()));
			}
		}

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		if (!errorMessages.isEmpty()) {
			int failedItems = errorMessages.size();

			if (failedItems <= 10) {
				jsonObject.put(
					"errorMessages",
					JSONUtil.toJSONArray(
						errorMessages, errorMessage -> errorMessage));
			}

			jsonObject.put("failedItems", failedItems);
		}

		JSONPortletResponseUtil.writeJSON(
			actionRequest, actionResponse,
			jsonObject.put(
				"successItems", _getItemsCopied(actionRequest, errorMessages)));

		hideDefaultSuccessMessage(actionRequest);
	}

	private void _checkDestinationCopyToSizeLimit(
			Group group, long size, ThemeDisplay themeDisplay)
		throws Exception {

		if (!DLCopyValidationUtil.isCopyToAllowed(
				_dlSizeLimitConfigurationProvider.getCompanyMaxSizeToCopy(
					group.getCompanyId()),
				_dlSizeLimitConfigurationProvider.getGroupMaxSizeToCopy(
					group.getGroupId()),
				_dlSizeLimitConfigurationProvider.getSystemMaxSizeToCopy(),
				size)) {

			throw new DLObjectSizeLimitExceededException(
				DLCopyValidationUtil.getCopyToValidationMessage(
					_dlSizeLimitConfigurationProvider.getCompanyMaxSizeToCopy(
						group.getCompanyId()),
					_dlSizeLimitConfigurationProvider.getGroupMaxSizeToCopy(
						group.getGroupId()),
					_dlSizeLimitConfigurationProvider.getSystemMaxSizeToCopy(),
					size, themeDisplay.getLocale(), true));
		}
	}

	private void _checkDestinationGroup(
			Group group, long[] groupIds, long sourceGroupId)
		throws Exception {

		if (group.isStaged() && !group.isStagingGroup()) {
			throw new PortalException(
				"cannot-copy-to-the-live-version-of-a-site");
		}

		Group sourceGroup = _groupLocalService.getGroup(sourceGroupId);

		if (group.isDepot() ^ sourceGroup.isDepot()) {
			long[] connectedGroupIds = groupIds;

			if (group.isDepot()) {
				connectedGroupIds =
					_siteConnectedGroupGroupProvider.
						getCurrentAndAncestorSiteAndDepotGroupIds(
							sourceGroup.getGroupId());
			}

			if (ArrayUtil.isEmpty(connectedGroupIds) ||
				!ArrayUtil.contains(connectedGroupIds, sourceGroupId)) {

				throw new PortalException(
					"the-item-is-not-copied-because-the-site-and-asset-" +
						"library-are-not-connected");
			}
		}
	}

	private void _copyDLObjects(
			ActionRequest actionRequest, List<String> errorMessages,
			ThemeDisplay themeDisplay)
		throws Exception {

		long destinationFolderId = ParamUtil.getLong(
			actionRequest, "destinationParentFolderId");
		long destinationRepositoryId = ParamUtil.getLong(
			actionRequest, "destinationRepositoryId");
		long sourceRepositoryId = ParamUtil.getLong(
			actionRequest, "sourceRepositoryId");

		Group group = _getRepositoryGroup(destinationRepositoryId);

		long[] groupIds =
			_siteConnectedGroupGroupProvider.
				getCurrentAndAncestorSiteAndDepotGroupIds(group.getGroupId());

		Group sourceGroup = _getRepositoryGroup(sourceRepositoryId);

		_checkDestinationGroup(group, groupIds, sourceGroup.getGroupId());

		_checkDestinationCopyToSizeLimit(
			group, ParamUtil.getLong(actionRequest, "size"),
			(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY));

		long[] dlObjectIds = ParamUtil.getLongValues(
			actionRequest, "dlObjectIds");

		for (long dlObjectId : dlObjectIds) {
			try {
				DLFileEntry dlFileEntry =
					_dlFileEntryLocalService.fetchDLFileEntry(dlObjectId);

				if (dlFileEntry != null) {
					_dlAppService.copyFileEntry(
						dlFileEntry.getFileEntryId(), destinationFolderId,
						destinationRepositoryId,
						_getFileEntryTypeId(
							destinationRepositoryId,
							dlFileEntry.getFileEntryId()),
						groupIds,
						ServiceContextFactory.getInstance(
							DLFileEntry.class.getName(), actionRequest));

					continue;
				}

				DLFileShortcut dlFileShortcut =
					_dlFileShortcutLocalService.fetchDLFileShortcut(dlObjectId);

				if (dlFileShortcut != null) {
					_dlAppService.copyFileShortcut(
						dlFileShortcut.getFileShortcutId(), destinationFolderId,
						destinationRepositoryId,
						ServiceContextFactory.getInstance(
							DLFileShortcut.class.getName(), actionRequest));

					continue;
				}

				DLFolder dlFolder = _dlFolderLocalService.fetchDLFolder(
					dlObjectId);

				if (dlFolder != null) {
					_dlAppService.copyFolder(
						dlFolder.getRepositoryId(), dlFolder.getFolderId(),
						destinationRepositoryId, destinationFolderId,
						_getFileEntryTypeIds(
							group.getGroupId(), dlFolder.getFolderId()),
						groupIds,
						ServiceContextFactory.getInstance(
							DLFolder.class.getName(), actionRequest));
				}
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}

				errorMessages.add(
					themeDisplay.translate(portalException.getMessage()));
			}
		}
	}

	private long _getFileEntryTypeId(long groupId, long fileEntryId)
		throws PortalException {

		long[] groupIds =
			_siteConnectedGroupGroupProvider.
				getCurrentAndAncestorSiteAndDepotGroupIds(groupId, false, true);

		if (ArrayUtil.isEmpty(groupIds)) {
			return DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT;
		}

		FileEntry fileEntry = _dlAppService.getFileEntry(fileEntryId);

		DLFileEntry dlFileEntry = (DLFileEntry)fileEntry.getModel();

		if (ArrayUtil.contains(groupIds, fileEntry.getGroupId())) {
			return dlFileEntry.getFileEntryTypeId();
		}

		DLFileEntryType fileEntryType =
			_dlFileEntryTypeService.getFileEntryType(
				dlFileEntry.getFileEntryTypeId());

		if (ArrayUtil.contains(groupIds, fileEntryType.getGroupId())) {
			return dlFileEntry.getFileEntryTypeId();
		}

		return DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT;
	}

	private Map<Long, Long> _getFileEntryTypeIds(long groupId, long folderId)
		throws PortalException {

		DLFolder folder = _dlFolderLocalService.getFolder(folderId);

		long[] groupIds =
			_siteConnectedGroupGroupProvider.
				getCurrentAndAncestorSiteAndDepotGroupIds(groupId, false, true);

		if (ArrayUtil.isEmpty(groupIds) ||
			!ArrayUtil.contains(groupIds, folder.getGroupId())) {

			return new HashMap<>();
		}

		return _dlFileEntryLocalService.getFileEntryTypeIds(
			folder.getCompanyId(), groupIds, folder.getTreePath());
	}

	private int _getItemsCopied(
		ActionRequest actionRequest, List<String> errorMessages) {

		long[] dlObjectIds = ParamUtil.getLongValues(
			actionRequest, "dlObjectIds");

		return dlObjectIds.length - errorMessages.size();
	}

	private Group _getRepositoryGroup(long repositoryId) throws Exception {
		Group group = _groupLocalService.fetchGroup(repositoryId);

		if (group != null) {
			return group;
		}

		Repository repository = _repositoryLocalService.getRepository(
			repositoryId);

		return _groupLocalService.getGroup(repository.getGroupId());
	}

	private String _getUploadExceptionErrorMessage(
		UploadException uploadException, ThemeDisplay themeDisplay) {

		if (uploadException.isExceededFileSizeLimit()) {
			Throwable throwable = uploadException.getCause();

			FileSizeException fileSizeException = new FileSizeException(
				throwable);

			return themeDisplay.translate(
				"please-enter-a-file-with-a-valid-file-size-no-larger-than-x",
				_language.formatStorageSize(
					fileSizeException.getMaxSize(), themeDisplay.getLocale()));
		}

		if (uploadException.isExceededLiferayFileItemSizeLimit()) {
			return themeDisplay.translate(
				"please-enter-valid-content-with-valid-content-size-no-" +
					"larger-than-x",
				_language.formatStorageSize(
					FileItem.THRESHOLD_SIZE, themeDisplay.getLocale()));
		}

		if (uploadException.isExceededUploadRequestSizeLimit()) {
			return themeDisplay.translate(
				"please-enter-a-file-with-a-valid-file-size-no-larger-than-x",
				_language.formatStorageSize(
					_uploadServletRequestConfigurationProvider.getMaxSize(),
					themeDisplay.getLocale()));
		}

		return themeDisplay.translate(
			"an-unexpected-error-occurred-while-saving-your-document");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CopyDLObjectsMVCActionCommand.class);

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Reference
	private DLFileEntryTypeService _dlFileEntryTypeService;

	@Reference
	private DLFileShortcutLocalService _dlFileShortcutLocalService;

	@Reference
	private DLFolderLocalService _dlFolderLocalService;

	@Reference
	private DLSizeLimitConfigurationProvider _dlSizeLimitConfigurationProvider;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private RepositoryLocalService _repositoryLocalService;

	@Reference
	private SiteConnectedGroupGroupProvider _siteConnectedGroupGroupProvider;

	@Reference
	private UploadServletRequestConfigurationProvider
		_uploadServletRequestConfigurationProvider;

}