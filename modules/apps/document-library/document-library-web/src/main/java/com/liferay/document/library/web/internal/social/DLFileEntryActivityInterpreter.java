/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.social;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.repository.capabilities.TrashCapability;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portlet.documentlibrary.social.DLActivityKeys;
import com.liferay.social.kernel.model.BaseSocialActivityInterpreter;
import com.liferay.social.kernel.model.SocialActivity;
import com.liferay.social.kernel.model.SocialActivityConstants;
import com.liferay.social.kernel.model.SocialActivityInterpreter;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Ryan Park
 * @author Zsolt Berentey
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY,
		"model.class.name=com.liferay.document.library.kernel.model.DLFileEntry"
	},
	service = SocialActivityInterpreter.class
)
public class DLFileEntryActivityInterpreter
	extends BaseSocialActivityInterpreter {

	@Override
	public String[] getClassNames() {
		return _CLASS_NAMES;
	}

	@Override
	protected String getBody(
			SocialActivity activity, ServiceContext serviceContext)
		throws Exception {

		FileEntry fileEntry = _dlAppLocalService.getFileEntry(
			activity.getClassPK());

		if (fileEntry.isRepositoryCapabilityProvided(TrashCapability.class)) {
			TrashCapability trashCapability = fileEntry.getRepositoryCapability(
				TrashCapability.class);

			if (trashCapability.isInTrash(fileEntry)) {
				return StringPool.BLANK;
			}
		}

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				DLFileEntry.class.getName());

		if (assetRendererFactory == null) {
			return StringPool.BLANK;
		}

		AssetRenderer<?> assetRenderer = assetRendererFactory.getAssetRenderer(
			fileEntry.getFileEntryId());

		if (assetRenderer == null) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(3);

		String fileEntryLink = assetRenderer.getURLDownload(
			serviceContext.getThemeDisplay());

		sb.append(wrapLink(fileEntryLink, "download-file", serviceContext));

		sb.append(StringPool.SPACE);

		String folderLink = _getFolderLink(fileEntry, serviceContext);

		folderLink = addNoSuchEntryRedirect(
			folderLink, DLFolder.class.getName(), fileEntry.getFolderId(),
			serviceContext);

		sb.append(wrapLink(folderLink, "go-to-folder", serviceContext));

		return sb.toString();
	}

	@Override
	protected String getPath(
		SocialActivity activity, ServiceContext serviceContext) {

		return "/document_library/find_file_entry?fileEntryId=" +
			activity.getClassPK();
	}

	@Override
	protected Object[] getTitleArguments(
			String groupName, SocialActivity activity, String link,
			String title, ServiceContext serviceContext)
		throws Exception {

		if (activity.getType() == SocialActivityConstants.TYPE_ADD_COMMENT) {
			String creatorUserName = getUserName(
				activity.getUserId(), serviceContext);
			String receiverUserName = getUserName(
				activity.getReceiverUserId(), serviceContext);

			return new Object[] {
				groupName, creatorUserName, receiverUserName,
				wrapLink(link, title)
			};
		}

		return super.getTitleArguments(
			groupName, activity, link, title, serviceContext);
	}

	@Override
	protected String getTitlePattern(
		String groupName, SocialActivity activity) {

		int activityType = activity.getType();

		if (activityType == DLActivityKeys.ADD_FILE_ENTRY) {
			if (Validator.isNull(groupName)) {
				return "activity-document-library-file-add-file";
			}

			return "activity-document-library-file-add-file-in";
		}
		else if (activityType == DLActivityKeys.UPDATE_FILE_ENTRY) {
			if (Validator.isNull(groupName)) {
				return "activity-document-library-file-update-file";
			}

			return "activity-document-library-file-update-file-in";
		}
		else if (activityType == SocialActivityConstants.TYPE_ADD_COMMENT) {
			if (Validator.isNull(groupName)) {
				return "activity-document-library-file-add-comment";
			}

			return "activity-document-library-file-add-comment-in";
		}
		else if (activityType == SocialActivityConstants.TYPE_MOVE_TO_TRASH) {
			if (Validator.isNull(groupName)) {
				return "activity-document-library-file-move-to-trash";
			}

			return "activity-document-library-file-move-to-trash-in";
		}
		else if (activityType ==
					SocialActivityConstants.TYPE_RESTORE_FROM_TRASH) {

			if (Validator.isNull(groupName)) {
				return "activity-document-library-file-restore-from-trash";
			}

			return "activity-document-library-file-restore-from-trash-in";
		}

		return null;
	}

	@Override
	protected String getViewEntryURL(
			String className, long classPK, ServiceContext serviceContext)
		throws Exception {

		return StringPool.BLANK;
	}

	@Override
	protected boolean hasPermissions(
			PermissionChecker permissionChecker, SocialActivity activity,
			String actionId, ServiceContext serviceContext)
		throws Exception {

		return _fileEntryModelResourcePermission.contains(
			permissionChecker, activity.getClassPK(), actionId);
	}

	private String _getFolderLink(
		FileEntry fileEntry, ServiceContext serviceContext) {

		return StringBundler.concat(
			serviceContext.getPortalURL(), serviceContext.getPathMain(),
			"/document_library/find_folder?groupId=",
			fileEntry.getRepositoryId(), "&folderId=", fileEntry.getFolderId());
	}

	private static final String[] _CLASS_NAMES = {DLFileEntry.class.getName()};

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.repository.model.FileEntry)"
	)
	private ModelResourcePermission<FileEntry>
		_fileEntryModelResourcePermission;

}