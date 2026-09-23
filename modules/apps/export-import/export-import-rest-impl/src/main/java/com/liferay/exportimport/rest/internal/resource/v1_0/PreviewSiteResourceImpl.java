/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.internal.resource.v1_0;

import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.rest.dto.v1_0.PreviewSite;
import com.liferay.exportimport.rest.internal.odata.entity.v1_0.PreviewSiteEntityModel;
import com.liferay.exportimport.rest.internal.util.PermissionUtil;
import com.liferay.exportimport.rest.resource.v1_0.PreviewSiteResource;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.comparator.GroupDescriptiveNameComparator;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.staging.StagingGroupHelper;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.MultivaluedMap;

import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Petteri Karttunen
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/preview-site.properties",
	scope = ServiceScope.PROTOTYPE, service = PreviewSiteResource.class
)
public class PreviewSiteResourceImpl extends BasePreviewSiteResourceImpl {

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public Page<PreviewSite> getExportPreviewPreviewSitesPage(
			String search, Pagination pagination, Sort[] sorts)
		throws Exception {

		FeatureFlagManagerUtil.checkEnabled(
			contextCompany.getCompanyId(), "LPD-85946");

		Group companyGroup = _stagingGroupHelper.fetchCompanyGroup(
			contextCompany.getCompanyId());

		if (companyGroup == null) {
			throw new NotFoundException();
		}

		PermissionUtil.checkExportPermission(
			contextCompany.getCompanyId(), companyGroup.getGroupId(),
			ActionKeys.EXPORT_IMPORT_LAYOUTS);

		List<Group> groups = _exportImportHelper.getSupportedGroups(
			contextCompany.getCompanyId(), search,
			_getOrderByComparator(sorts));

		return Page.of(
			transform(
				ListUtil.subList(
					groups, pagination.getStartPosition(),
					pagination.getEndPosition()),
				group -> new PreviewSite() {
					{
						setChildSitesCount(
							() -> _exportImportHelper.getChildGroupsCount(
								group));
						setDescriptiveName(
							() -> group.getDescriptiveName(
								contextAcceptLanguage.getPreferredLocale()));
						setExternalReferenceCode(
							group::getExternalReferenceCode);
						setPath(
							() -> _exportImportHelper.getGroupPath(
								group,
								contextAcceptLanguage.getPreferredLocale()));
					}
				}),
			pagination, groups.size());
	}

	private OrderByComparator<Group> _getOrderByComparator(Sort[] sorts) {
		if (ArrayUtil.isEmpty(sorts)) {
			return new GroupDescriptiveNameComparator(
				true, contextAcceptLanguage.getPreferredLocale());
		}

		Sort sort = sorts[0];

		if (!Objects.equals(sort.getFieldName(), "descriptiveName")) {
			throw new UnsupportedOperationException();
		}

		return new GroupDescriptiveNameComparator(
			!sort.isReverse(), contextAcceptLanguage.getPreferredLocale());
	}

	private static final EntityModel _entityModel =
		new PreviewSiteEntityModel();

	@Reference
	private ExportImportHelper _exportImportHelper;

	@Reference
	private StagingGroupHelper _stagingGroupHelper;

}