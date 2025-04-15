/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.resource.v1_0;

import com.liferay.dynamic.data.mapping.constants.DDMConstants;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureService;
import com.liferay.headless.delivery.dto.v1_0.ContentStructure;
import com.liferay.headless.delivery.dto.v1_0.util.ContentStructureUtil;
import com.liferay.headless.delivery.internal.odata.entity.v1_0.ContentStructureEntityModel;
import com.liferay.headless.delivery.resource.v1_0.ContentStructureResource;
import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.Collections;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/content-structure.properties",
	scope = ServiceScope.PROTOTYPE, service = ContentStructureResource.class
)
public class ContentStructureResourceImpl
	extends BaseContentStructureResourceImpl {

	@Override
	public Page<ContentStructure> getAssetLibraryContentStructuresPage(
			Long assetLibraryId, String search, Aggregation aggregation,
			Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		return getSiteContentStructuresPage(
			assetLibraryId, search, aggregation, filter, pagination, sorts);
	}

	@Override
	public ContentStructure getContentStructure(Long contentStructureId)
		throws Exception {

		return _toContentStructure(
			_ddmStructureService.getStructure(contentStructureId));
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public Page<ContentStructure> getSiteContentStructuresPage(
			Long siteId, String search, Aggregation aggregation, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			Collections.emptyMap(),
			booleanQuery -> {
			},
			filter, DDMStructure.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.addVulcanAggregation(aggregation);
				searchContext.setAttribute(Field.DESCRIPTION, search);
				searchContext.setAttribute(Field.NAME, search);
				searchContext.setAttribute(
					"searchPermissionContext", StringPool.BLANK);
				searchContext.setCompanyId(contextCompany.getCompanyId());
				searchContext.setGroupIds(new long[] {siteId});
			},
			sorts,
			document -> _toContentStructure(
				_ddmStructureService.getStructure(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)))));
	}

	@Override
	protected Long getPermissionCheckerGroupId(Object id) throws Exception {
		DDMStructure ddmStructure = _ddmStructureService.getStructure((Long)id);

		return ddmStructure.getGroupId();
	}

	@Override
	protected String getPermissionCheckerPortletName(Object id) {
		return DDMConstants.RESOURCE_NAME;
	}

	@Override
	protected String getPermissionCheckerResourceName(Object id)
		throws Exception {

		return ResourceActionsUtil.getCompositeModelName(
			DDMStructure.class.getName(), JournalArticle.class.getName());
	}

	private ContentStructure _toContentStructure(DDMStructure ddmStructure) {
		return ContentStructureUtil.toContentStructure(
			contextAcceptLanguage.isAcceptAllLanguages(), groupLocalService,
			contextAcceptLanguage.getPreferredLocale(), _portal,
			_userLocalService, ddmStructure);
	}

	private static final EntityModel _entityModel =
		new ContentStructureEntityModel();

	@Reference
	private DDMStructureService _ddmStructureService;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}