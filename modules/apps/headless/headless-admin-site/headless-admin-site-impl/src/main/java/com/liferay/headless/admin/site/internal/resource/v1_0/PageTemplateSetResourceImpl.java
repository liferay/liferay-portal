/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.vulcan.batch.engine.ExportImportVulcanBatchEngineTaskItemDelegate;
import com.liferay.headless.admin.site.dto.v1_0.PageTemplateSet;
import com.liferay.headless.admin.site.internal.odata.entity.v1_0.PageTemplateSetEntityModel;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.GroupUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.PageTemplateSetUtil;
import com.liferay.headless.admin.site.resource.v1_0.PageTemplateSetResource;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionService;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.Collections;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Rubén Pulido
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/page-template-set.properties",
	property = "export.import.vulcan.batch.engine.task.item.delegate=true",
	scope = ServiceScope.PROTOTYPE, service = PageTemplateSetResource.class
)
public class PageTemplateSetResourceImpl
	extends BasePageTemplateSetResourceImpl
	implements ExportImportVulcanBatchEngineTaskItemDelegate<PageTemplateSet> {

	@Override
	public void deleteSitePageTemplateSet(
			String siteExternalReferenceCode,
			String pageTemplateSetExternalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-35443")) {
			throw new UnsupportedOperationException();
		}

		_layoutPageTemplateCollectionService.deleteLayoutPageTemplateCollection(
			pageTemplateSetExternalReferenceCode,
			GroupUtil.getGroupId(
				false, contextCompany.getCompanyId(),
				siteExternalReferenceCode));
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public ExportImportDescriptor getExportImportDescriptor() {
		return new ExportImportVulcanBatchEngineTaskItemDelegate.
			ExportImportDescriptor() {

			@Override
			public String getItemClassName() {
				return LayoutPageTemplateCollection.class.getName();
			}

			@Override
			public String getLabel() {
				return "page-template-sets";
			}

			@Override
			public String getPortletId() {
				return LayoutAdminPortletKeys.GROUP_PAGES;
			}

			@Override
			public Scope getScope() {
				return Scope.SITE;
			}

			@Override
			public boolean isActive(PortletDataContext portletDataContext) {
				return FeatureFlagManagerUtil.isEnabled("LPD-35443");
			}

		};
	}

	@Override
	protected PageTemplateSet doGetSitePageTemplateSet(
			String siteExternalReferenceCode,
			String pageTemplateSetExternalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-35443")) {
			throw new UnsupportedOperationException();
		}

		return _toPageTemplateSet(
			_layoutPageTemplateCollectionService.
				getLayoutPageTemplateCollection(
					pageTemplateSetExternalReferenceCode,
					GroupUtil.getGroupId(
						true, contextCompany.getCompanyId(),
						siteExternalReferenceCode)));
	}

	@Override
	protected Page<PageTemplateSet> doGetSitePageTemplateSetsPage(
			String siteExternalReferenceCode, String search,
			Aggregation aggregation, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-35443")) {
			throw new UnsupportedOperationException();
		}

		long groupId = GroupUtil.getGroupId(
			true, contextCompany.getCompanyId(), siteExternalReferenceCode);

		return SearchUtil.search(
			Collections.emptyMap(),
			booleanQuery -> {
			},
			filter, LayoutPageTemplateCollection.class.getName(), search,
			pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.setAttribute(
					Field.TYPE,
					String.valueOf(
						LayoutPageTemplateCollectionTypeConstants.BASIC));
				searchContext.setCompanyId(contextCompany.getCompanyId());
				searchContext.setGroupIds(new long[] {groupId});
			},
			sorts,
			document -> _toPageTemplateSet(
				_layoutPageTemplateCollectionService.
					fetchLayoutPageTemplateCollection(
						GetterUtil.getLong(
							document.get(Field.ENTRY_CLASS_PK)))));
	}

	@Override
	protected PageTemplateSet doPostSitePageTemplateSet(
			String siteExternalReferenceCode, PageTemplateSet pageTemplateSet)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-35443")) {
			throw new UnsupportedOperationException();
		}

		return _toPageTemplateSet(
			PageTemplateSetUtil.addLayoutPageTemplateCollection(
				GroupUtil.getGroupId(
					false, contextCompany.getCompanyId(),
					siteExternalReferenceCode),
				contextHttpServletRequest, pageTemplateSet));
	}

	@Override
	protected PageTemplateSet doPutSitePageTemplateSet(
			String siteExternalReferenceCode,
			String pageTemplateSetExternalReferenceCode,
			PageTemplateSet pageTemplateSet)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-35443")) {
			throw new UnsupportedOperationException();
		}

		long groupId = GroupUtil.getGroupId(
			false, contextCompany.getCompanyId(), siteExternalReferenceCode);

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionService.
				fetchLayoutPageTemplateCollection(
					pageTemplateSetExternalReferenceCode, groupId);

		if (layoutPageTemplateCollection == null) {
			return _toPageTemplateSet(
				PageTemplateSetUtil.addLayoutPageTemplateCollection(
					groupId, contextHttpServletRequest, pageTemplateSet));
		}

		return _toPageTemplateSet(
			_layoutPageTemplateCollectionService.
				updateLayoutPageTemplateCollection(
					layoutPageTemplateCollection.
						getLayoutPageTemplateCollectionId(),
					pageTemplateSet.getName(),
					pageTemplateSet.getDescription()));
	}

	@Override
	protected Long getPermissionCheckerResourceId(
			String groupExternalReferenceCode, String externalReferenceCode)
		throws Exception {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionService.
				getLayoutPageTemplateCollection(
					externalReferenceCode,
					getPermissionCheckerGroupId(groupExternalReferenceCode));

		return layoutPageTemplateCollection.getPrimaryKey();
	}

	@Override
	protected String getPermissionCheckerResourceName(
			String groupExternalReferenceCode, String externalReferenceCode)
		throws Exception {

		return LayoutPageTemplateCollection.class.getName();
	}

	private PageTemplateSet _toPageTemplateSet(
			LayoutPageTemplateCollection layoutPageTemplateCollection)
		throws Exception {

		return _pageTemplateSetDTOConverter.toDTO(layoutPageTemplateCollection);
	}

	private static final EntityModel _entityModel =
		new PageTemplateSetEntityModel();

	@Reference
	private LayoutPageTemplateCollectionService
		_layoutPageTemplateCollectionService;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.PageTemplateSetDTOConverter)"
	)
	private DTOConverter<LayoutPageTemplateCollection, PageTemplateSet>
		_pageTemplateSetDTOConverter;

}