/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.internal.resource.v1_0;

import com.liferay.headless.cms.dto.v1_0.AssetPermissionAction;
import com.liferay.headless.cms.resource.v1_0.AssetPermissionActionResource;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.site.cms.site.initializer.util.ResetAssetPermissionUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Stefano Motta
 * @author Balazs Breier
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/asset-permission-action.properties",
	scope = ServiceScope.PROTOTYPE,
	service = AssetPermissionActionResource.class
)
public class AssetPermissionActionResourceImpl
	extends BaseAssetPermissionActionResourceImpl {

	@Override
	public AssetPermissionAction postAssetPermission(
			AssetPermissionAction assetPermissionAction)
		throws Exception {

		if (AssetPermissionAction.Type.RESET_ASSET_PERMISSION_ACTION.equals(
				assetPermissionAction.getType())) {

			ResetAssetPermissionUtil.executeResetAssetPermission(
				assetPermissionAction.getClassName(),
				assetPermissionAction.getClassPK(), _filterFactory,
				_groupLocalService, _objectDefinitionLocalService,
				_objectEntryFolderLocalService,
				_objectEntryFolderModelResourcePermission,
				_objectEntryLocalService, _resourcePermissionLocalService,
				_roleLocalService);
		}

		return assetPermissionAction;
	}

	@Reference(
		target = "(filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT + ")"
	)
	private FilterFactory<Predicate> _filterFactory;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.object.model.ObjectEntryFolder)"
	)
	private ModelResourcePermission<ObjectEntryFolder>
		_objectEntryFolderModelResourcePermission;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

}