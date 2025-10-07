/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.security.permission.resource;

import com.liferay.exportimport.kernel.staging.permission.StagingPermission;
import com.liferay.journal.constants.JournalConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalFolderLocalService;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.BaseModelResourcePermissionWrapper;
import com.liferay.portal.kernel.security.permission.resource.DynamicInheritancePermissionLogic;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionFactory;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.StagedModelPermissionLogic;
import com.liferay.portal.kernel.util.PropsValues;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Preston Crary
 */
@Component(
	property = "model.class.name=com.liferay.journal.model.JournalFolder",
	service = ModelResourcePermission.class
)
public class JournalFolderModelResourcePermissionWrapper
	extends BaseModelResourcePermissionWrapper<JournalFolder> {

	@Override
	protected ModelResourcePermission<JournalFolder>
		doGetModelResourcePermission() {

		return ModelResourcePermissionFactory.create(
			JournalFolder.class, JournalFolder::getFolderId,
			_journalFolderLocalService::getFolder, _portletResourcePermission,
			(modelResourcePermission, consumer) -> {
				consumer.accept(
					new StagedModelPermissionLogic<JournalFolder>(
						_stagingPermission, JournalPortletKeys.JOURNAL,
						JournalFolder::getFolderId) {

						@Override
						public Boolean contains(
							PermissionChecker permissionChecker, String name,
							JournalFolder journalFolder, String actionId) {

							if (actionId.equals(ActionKeys.SUBSCRIBE)) {
								return null;
							}

							return super.contains(
								permissionChecker, name, journalFolder,
								actionId);
						}

					});

				if (PropsValues.PERMISSIONS_VIEW_DYNAMIC_INHERITANCE) {
					consumer.accept(
						new DynamicInheritancePermissionLogic<>(
							modelResourcePermission, _getFetchParentFunction(),
							false));
				}
			},
			actionId -> {
				if (ActionKeys.ADD_FOLDER.equals(actionId)) {
					return ActionKeys.ADD_SUBFOLDER;
				}

				return actionId;
			});
	}

	private UnsafeFunction<JournalFolder, JournalFolder, PortalException>
		_getFetchParentFunction() {

		return folder -> {
			long folderId = folder.getParentFolderId();

			if (JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID == folderId) {
				return null;
			}

			if (folder.isInTrash()) {
				return _journalFolderLocalService.fetchJournalFolder(folderId);
			}

			return _journalFolderLocalService.getFolder(folderId);
		};
	}

	@Reference
	private JournalFolderLocalService _journalFolderLocalService;

	@Reference(
		target = "(resource.name=" + JournalConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

	@Reference
	private StagingPermission _stagingPermission;

}