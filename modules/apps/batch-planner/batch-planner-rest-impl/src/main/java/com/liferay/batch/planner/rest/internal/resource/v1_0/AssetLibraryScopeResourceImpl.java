/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.planner.rest.internal.resource.v1_0;

import com.liferay.batch.planner.rest.dto.v1_0.AssetLibraryScope;
import com.liferay.batch.planner.rest.internal.vulcan.batch.engine.util.EntityScopesUtil;
import com.liferay.batch.planner.rest.internal.vulcan.yaml.openapi.OpenAPIYAMLProvider;
import com.liferay.batch.planner.rest.resource.v1_0.AssetLibraryScopeResource;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.service.DepotEntryService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.vulcan.pagination.Page;

import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Matija Petanjek
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/asset-library-scope.properties",
	scope = ServiceScope.PROTOTYPE, service = AssetLibraryScopeResource.class
)
public class AssetLibraryScopeResourceImpl
	extends BaseAssetLibraryScopeResourceImpl {

	@Override
	public Page<AssetLibraryScope>
			getPlanInternalClassNameKeyAssetLibraryScopesPage(
				String internalClassNameKey, Boolean export)
		throws Exception {

		List<String> entityScopes = EntityScopesUtil.getEntityScopes(
			contextCompany.getCompanyId(), GetterUtil.getBoolean(export),
			internalClassNameKey, _objectDefinitionLocalService,
			_openAPIYAMLProvider);

		if (!entityScopes.contains("depot")) {
			return Page.of(Collections.emptyList());
		}

		return Page.of(
			transform(
				transform(
					ListUtil.concat(
						_depotEntryService.getDepotEntryGroupIds(
							contextCompany.getCompanyId(),
							contextUser.getUserId(),
							DepotConstants.TYPE_ASSET_LIBRARY),
						_depotEntryService.getDepotEntryGroupIds(
							contextCompany.getCompanyId(),
							contextUser.getUserId(),
							DepotConstants.TYPE_SPACE)),
					_groupService::getGroup),
				group -> new AssetLibraryScope() {
					{
						setLabel(group::getDescriptiveName);
						setValue(group::getGroupId);
					}
				}));
	}

	@Reference
	private DepotEntryService _depotEntryService;

	@Reference
	private GroupService _groupService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private OpenAPIYAMLProvider _openAPIYAMLProvider;

}