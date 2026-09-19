/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.security.permission;

import com.liferay.document.library.internal.util.DLFileEntryTypePermissionUtil;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.dynamic.data.mapping.security.permission.DDMPermissionSupport;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.security.permission.PermissionUpdateHandler;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gergely Mathe
 */
@Component(
	property = "model.class.name=com.liferay.document.library.kernel.model.DLFileEntryType",
	service = PermissionUpdateHandler.class
)
public class DLFileEntryTypePermissionUpdateHandler
	implements PermissionUpdateHandler {

	@Override
	public void updatedPermission(String primKey) {
		try {
			DLFileEntryType dlFileEntryType =
				_dLFileEntryTypeLocalService.fetchDLFileEntryType(
					GetterUtil.getLong(primKey));

			if (dlFileEntryType == null) {
				return;
			}

			dlFileEntryType.setModifiedDate(new Date());

			dlFileEntryType =
				_dLFileEntryTypeLocalService.updateDLFileEntryType(
					dlFileEntryType);

			String dlFileEntryMetadataResourceName =
				_ddmPermissionSupport.getStructureModelResourceName(
					DLFileEntryMetadata.class.getName());

			List<ResourceAction> dlFileEntryMetadataResourceActions =
				_resourceActionLocalService.getResourceActions(
					dlFileEntryMetadataResourceName);

			Set<String> dlFileEntryMetadataActionIds = new HashSet<>();

			for (ResourceAction resourceAction :
					dlFileEntryMetadataResourceActions) {

				dlFileEntryMetadataActionIds.add(resourceAction.getActionId());
			}

			List<ResourcePermission> resourcePermissions =
				_resourcePermissionLocalService.getResourcePermissions(
					dlFileEntryType.getCompanyId(),
					DLFileEntryType.class.getName(),
					ResourceConstants.SCOPE_INDIVIDUAL,
					String.valueOf(dlFileEntryType.getFileEntryTypeId()));

			_resourcePermissionLocalService.setResourcePermissions(
				dlFileEntryType.getCompanyId(), dlFileEntryMetadataResourceName,
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(dlFileEntryType.getDataDefinitionId()),
				DLFileEntryTypePermissionUtil.getRoleIdsToActionIds(
					_resourceActionLocalService.getResourceActions(
						DLFileEntryType.class.getName()),
					resourcePermissions,
					dlFileEntryMetadataActionIds::contains));
		}
		catch (PortalException portalException) {
			ReflectionUtil.throwException(portalException);
		}
	}

	@Reference
	private DLFileEntryTypeLocalService _dLFileEntryTypeLocalService;

	@Reference
	private DDMPermissionSupport _ddmPermissionSupport;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

}