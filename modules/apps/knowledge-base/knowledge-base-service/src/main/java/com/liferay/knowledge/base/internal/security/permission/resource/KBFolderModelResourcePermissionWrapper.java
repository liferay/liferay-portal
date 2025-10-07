/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.internal.security.permission.resource;

import com.liferay.knowledge.base.constants.KBConstants;
import com.liferay.knowledge.base.constants.KBFolderConstants;
import com.liferay.knowledge.base.model.KBFolder;
import com.liferay.knowledge.base.service.KBFolderLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.BaseModelResourcePermissionWrapper;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionFactory;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionLogic;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.util.PropsValues;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Preston Crary
 */
@Component(
	property = "model.class.name=com.liferay.knowledge.base.model.KBFolder",
	service = ModelResourcePermission.class
)
public class KBFolderModelResourcePermissionWrapper
	extends BaseModelResourcePermissionWrapper<KBFolder> {

	@Override
	protected ModelResourcePermission<KBFolder> doGetModelResourcePermission() {
		return ModelResourcePermissionFactory.create(
			KBFolder.class, KBFolder::getKbFolderId,
			_kbFolderLocalService::getKBFolder, _portletResourcePermission,
			(modelResourcePermission, consumer) -> {
				if (PropsValues.PERMISSIONS_VIEW_DYNAMIC_INHERITANCE) {
					consumer.accept(
						new KBFolderDynamicInheritanceModelResourcePermissionLogic(
							modelResourcePermission));
				}
			});
	}

	@Reference
	private KBFolderLocalService _kbFolderLocalService;

	@Reference(
		target = "(resource.name=" + KBConstants.RESOURCE_NAME_ADMIN + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

	private class KBFolderDynamicInheritanceModelResourcePermissionLogic
		implements ModelResourcePermissionLogic<KBFolder> {

		@Override
		public Boolean contains(
				PermissionChecker permissionChecker, String name,
				KBFolder kbFolder, String actionId)
			throws PortalException {

			if (!ActionKeys.VIEW.equals(actionId)) {
				return null;
			}

			long parentKBFolderId = kbFolder.getParentKBFolderId();

			if (parentKBFolderId ==
					KBFolderConstants.DEFAULT_PARENT_FOLDER_ID) {

				return null;
			}

			kbFolder = _kbFolderLocalService.fetchKBFolder(parentKBFolderId);

			if ((kbFolder != null) &&
				!_kbFolderModelResourcePermission.contains(
					permissionChecker, kbFolder, actionId)) {

				return false;
			}

			return null;
		}

		private KBFolderDynamicInheritanceModelResourcePermissionLogic(
			ModelResourcePermission<KBFolder> kbFolderModelResourcePermission) {

			_kbFolderModelResourcePermission = kbFolderModelResourcePermission;
		}

		private final ModelResourcePermission<KBFolder>
			_kbFolderModelResourcePermission;

	}

}