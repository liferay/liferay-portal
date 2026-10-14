/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.internal.resource.v1_0;

import com.liferay.exportimport.rest.dto.v1_0.PreviewAssetLibrary;
import com.liferay.exportimport.rest.internal.odata.entity.v1_0.PreviewAssetLibraryEntityModel;
import com.liferay.exportimport.rest.internal.util.GroupUtil;
import com.liferay.exportimport.rest.resource.v1_0.PreviewAssetLibraryResource;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Petteri Karttunen
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/preview-asset-library.properties",
	scope = ServiceScope.PROTOTYPE, service = PreviewAssetLibraryResource.class
)
public class PreviewAssetLibraryResourceImpl
	extends BasePreviewAssetLibraryResourceImpl {

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public Page<PreviewAssetLibrary> getExportPreviewPreviewAssetLibrariesPage(
			String search, Pagination pagination, Sort[] sorts)
		throws Exception {

		FeatureFlagManagerUtil.checkEnabled(
			contextCompany.getCompanyId(), "LPD-41307");

		List<Group> groups = GroupUtil.getExportableGroups(
			contextCompany.getCompanyId(),
			contextAcceptLanguage.getPreferredLocale(), Group::isDepot, search,
			sorts);

		return Page.of(
			transform(
				ListUtil.subList(
					groups, pagination.getStartPosition(),
					pagination.getEndPosition()),
				group -> new PreviewAssetLibrary() {
					{
						setDescriptiveName(
							() -> group.getDescriptiveName(
								contextAcceptLanguage.getPreferredLocale()));
						setExternalReferenceCode(
							group::getExternalReferenceCode);
					}
				}),
			pagination, groups.size());
	}

	private static final EntityModel _entityModel =
		new PreviewAssetLibraryEntityModel();

}