/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.internal.resource.v1_0;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.rest.dto.v1_0.PreviewAssetLibrary;
import com.liferay.exportimport.rest.internal.odata.entity.v1_0.PreviewAssetLibraryEntityModel;
import com.liferay.exportimport.rest.internal.util.GroupUtil;
import com.liferay.exportimport.rest.internal.util.PermissionUtil;
import com.liferay.exportimport.rest.internal.util.PreviewAssetLibraryUtil;
import com.liferay.exportimport.rest.resource.v1_0.PreviewAssetLibraryResource;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.SortFactoryUtil;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.Collections;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
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
			String search, Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		FeatureFlagManagerUtil.checkEnabled(
			contextCompany.getCompanyId(), "LPD-41307");

		Group companyGroup = GroupUtil.getCompanyGroup(
			contextCompany.getCompanyId());

		PermissionUtil.checkExportPermission(
			contextCompany.getCompanyId(), companyGroup.getGroupId(),
			ActionKeys.EXPORT_IMPORT_LAYOUTS);

		if (ArrayUtil.isEmpty(sorts)) {
			sorts = new Sort[] {
				SortFactoryUtil.create(
					Field.getSortableFieldName(Field.NAME), Sort.STRING_TYPE,
					false)
			};
		}

		return SearchUtil.search(
			Collections.emptyMap(),
			booleanQuery -> {
				long[] unsupportedGroupIds = transformToLongArray(
					ListUtil.filter(
						_groupLocalService.getGroups(
							contextCompany.getCompanyId(),
							DepotEntry.class.getName(), 0),
						group -> !_exportImportHelper.isGroupSupported(group)),
					Group::getGroupId);

				if (ArrayUtil.isEmpty(unsupportedGroupIds)) {
					return;
				}

				TermsFilter termsFilter = new TermsFilter(Field.GROUP_ID);

				termsFilter.addValues(
					ArrayUtil.toStringArray(unsupportedGroupIds));

				BooleanFilter booleanFilter =
					booleanQuery.getPreBooleanFilter();

				booleanFilter.add(termsFilter, BooleanClauseOccur.MUST_NOT);
			},
			filter, DepotEntry.class.getName(), search, pagination,
			queryConfig -> {
			},
			searchContext -> searchContext.setCompanyId(
				contextCompany.getCompanyId()),
			sorts,
			document -> _toPreviewAssetLibrary(
				_depotEntryLocalService.getDepotEntry(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)))));
	}

	private PreviewAssetLibrary _toPreviewAssetLibrary(DepotEntry depotEntry)
		throws Exception {

		Group group = depotEntry.getGroup();

		return new PreviewAssetLibrary() {
			{
				setDescriptiveName(
					() -> group.getDescriptiveName(
						contextAcceptLanguage.getPreferredLocale()));
				setExternalReferenceCode(group::getExternalReferenceCode);
				setType(
					() -> PreviewAssetLibraryUtil.toType(depotEntry.getType()));
			}
		};
	}

	private static final EntityModel _entityModel =
		new PreviewAssetLibraryEntityModel();

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private ExportImportHelper _exportImportHelper;

	@Reference
	private GroupLocalService _groupLocalService;

}