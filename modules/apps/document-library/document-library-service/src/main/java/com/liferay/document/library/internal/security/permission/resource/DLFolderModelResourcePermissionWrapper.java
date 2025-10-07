/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.security.permission.resource;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.exportimport.kernel.staging.permission.StagingPermission;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.BaseModelResourcePermissionWrapper;
import com.liferay.portal.kernel.security.permission.resource.DynamicInheritancePermissionLogic;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionFactory;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.StagedModelPermissionLogic;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portlet.documentlibrary.constants.DLConstants;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Preston Crary
 */
@Component(
	property = "model.class.name=com.liferay.document.library.kernel.model.DLFolder",
	service = ModelResourcePermission.class
)
public class DLFolderModelResourcePermissionWrapper
	extends BaseModelResourcePermissionWrapper<DLFolder> {

	@Override
	protected ModelResourcePermission<DLFolder> doGetModelResourcePermission() {
		return ModelResourcePermissionFactory.create(
			DLFolder.class, DLFolder::getFolderId,
			_dlFolderLocalService::getDLFolder, _portletResourcePermission,
			(modelResourcePermission, consumer) -> {
				consumer.accept(
					new StagedModelPermissionLogic<>(
						_stagingPermission, DLPortletKeys.DOCUMENT_LIBRARY,
						DLFolder::getFolderId));

				if (PropsValues.PERMISSIONS_VIEW_DYNAMIC_INHERITANCE) {
					consumer.accept(
						new DynamicInheritancePermissionLogic<>(
							modelResourcePermission, _getFetchParentFunction(),
							false));
				}
			},
			actionId -> {
				if (actionId.equals(ActionKeys.ADD_FOLDER)) {
					return ActionKeys.ADD_SUBFOLDER;
				}

				return actionId;
			});
	}

	private UnsafeFunction<DLFolder, DLFolder, PortalException>
		_getFetchParentFunction() {

		return folder -> {
			long folderId = folder.getParentFolderId();

			if (DLFolderConstants.DEFAULT_PARENT_FOLDER_ID == folderId) {
				return null;
			}

			if (folder.isInTrash()) {
				return _dlFolderLocalService.fetchFolder(folderId);
			}

			return _dlFolderLocalService.getFolder(folderId);
		};
	}

	@Reference
	private DLFolderLocalService _dlFolderLocalService;

	@Reference(target = "(resource.name=" + DLConstants.RESOURCE_NAME + ")")
	private PortletResourcePermission _portletResourcePermission;

	@Reference
	private StagingPermission _stagingPermission;

}