/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.exportimport.constants.ExportImportConstants;
import com.liferay.exportimport.vulcan.batch.engine.ExportImportVulcanBatchEngineTaskItemDelegate;
import com.liferay.headless.admin.site.dto.v1_0.PageTemplateSet;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.DTOConverterContextUtil;
import com.liferay.headless.admin.site.internal.odata.entity.v1_0.PageTemplateSetEntityModel;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.PageTemplateSetActionUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.PageTemplateSetUtil;
import com.liferay.headless.admin.site.internal.util.EnabledUtil;
import com.liferay.headless.admin.site.resource.v1_0.PageTemplateSetResource;
import com.liferay.headless.common.spi.util.GroupUtil;
import com.liferay.layout.page.template.admin.constants.LayoutPageTemplateAdminPortletKeys;
import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionService;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.crud.VulcanCRUDItemDelegate;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.MultivaluedMap;

import java.util.Collections;
import java.util.function.Function;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Rubén Pulido
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/page-template-set.properties",
	property = {
		"crud.entity.class.name=com.liferay.headless.admin.site.dto.v1_0.PageTemplateSet",
		"crud.item.delegate=true",
		"export.import.vulcan.batch.engine.task.item.delegate=true"
	},
	scope = ServiceScope.PROTOTYPE, service = PageTemplateSetResource.class
)
public class PageTemplateSetResourceImpl
	extends BasePageTemplateSetResourceImpl
	implements ExportImportVulcanBatchEngineTaskItemDelegate<PageTemplateSet>,
			   VulcanCRUDItemDelegate<PageTemplateSet> {

	@Override
	public void deleteDesignLibraryPageTemplateSet(
			String designLibraryExternalReferenceCode,
			String pageTemplateSetExternalReferenceCode)
		throws Exception {

		EnabledUtil.checkDesignLibrariesEnabled(contextCompany);

		_layoutPageTemplateCollectionService.deleteLayoutPageTemplateCollection(
			pageTemplateSetExternalReferenceCode,
			_getDesignLibraryGroupId(designLibraryExternalReferenceCode));
	}

	@Override
	public void deleteSitePageTemplateSet(
			String siteExternalReferenceCode,
			String pageTemplateSetExternalReferenceCode)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		_layoutPageTemplateCollectionService.deleteLayoutPageTemplateCollection(
			pageTemplateSetExternalReferenceCode,
			GroupUtil.getStagingAwareGroupId(
				contextCompany.getCompanyId(), siteExternalReferenceCode));
	}

	@Override
	public PageTemplateSet getDesignLibraryPageTemplateSet(
			String designLibraryExternalReferenceCode,
			String pageTemplateSetExternalReferenceCode)
		throws Exception {

		EnabledUtil.checkDesignLibrariesEnabled(contextCompany);

		return _toDesignLibraryPageTemplateSet(
			designLibraryExternalReferenceCode,
			_layoutPageTemplateCollectionService.
				getLayoutPageTemplateCollection(
					pageTemplateSetExternalReferenceCode,
					_getDesignLibraryGroupId(
						designLibraryExternalReferenceCode)));
	}

	@Override
	public Page<PageTemplateSet> getDesignLibraryPageTemplateSetsPage(
			String designLibraryExternalReferenceCode, String search,
			Aggregation aggregation, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		EnabledUtil.checkDesignLibrariesEnabled(contextCompany);

		long groupId = _getDesignLibraryGroupId(
			designLibraryExternalReferenceCode);

		if (!_hasViewDepotEntryPermission(groupId)) {
			return Page.of(Collections.emptyList());
		}

		return _getPageTemplateSetsPage(
			aggregation, filter, groupId, pagination, search, sorts,
			layoutPageTemplateCollection -> _toDesignLibraryPageTemplateSet(
				designLibraryExternalReferenceCode,
				layoutPageTemplateCollection));
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public ExportImportDescriptor<LayoutPageTemplateCollection>
		getExportImportDescriptor() {

		return new ExportImportVulcanBatchEngineTaskItemDelegate.
			ExportImportDescriptor<>() {

			@Override
			public Function<LayoutPageTemplateCollection, Boolean>
				getApplicableModelFunction() {

				return layoutPageTemplateCollection ->
					layoutPageTemplateCollection.getType() ==
						LayoutPageTemplateCollectionTypeConstants.BASIC;
			}

			@Override
			public String getKey() {
				return LayoutPageTemplateCollection.class.getName() + "-" +
					LayoutPageTemplateCollectionTypeConstants.BASIC;
			}

			@Override
			public String getLabelLanguageKey() {
				return "page-template-sets";
			}

			@Override
			public Class<LayoutPageTemplateCollection> getModelClass() {
				return LayoutPageTemplateCollection.class;
			}

			@Override
			public String getPortletId() {
				return LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES;
			}

			@Override
			public Scope getScope() {
				return Scope.SITE;
			}

			@Override
			public String getSectionKey() {
				return ExportImportConstants.SECTION_KEY_DESIGN;
			}

			@Override
			public boolean isStagingSupported() {
				return true;
			}

		};
	}

	@Override
	public PageTemplateSet getItem(Long id) throws Exception {
		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionService.
				fetchLayoutPageTemplateCollection(id);

		if ((layoutPageTemplateCollection == null) ||
			(layoutPageTemplateCollection.getType() !=
				LayoutPageTemplateCollectionTypeConstants.BASIC)) {

			throw new NotFoundException(
				"No page template set exists with ID " + id);
		}

		Group group = _groupLocalService.getGroup(
			layoutPageTemplateCollection.getGroupId());

		if (group.isDepot()) {
			EnabledUtil.checkDesignLibrariesEnabled(contextCompany);

			return _toDesignLibraryPageTemplateSet(
				group.getExternalReferenceCode(), layoutPageTemplateCollection);
		}

		EnabledUtil.checkEnabled(contextCompany);

		return _toPageTemplateSet(layoutPageTemplateCollection);
	}

	@Override
	protected PageTemplateSet doGetSitePageTemplateSet(
			String siteExternalReferenceCode,
			String pageTemplateSetExternalReferenceCode)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

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

		EnabledUtil.checkEnabled(contextCompany);

		return _getPageTemplateSetsPage(
			aggregation, filter,
			GroupUtil.getGroupId(
				true, contextCompany.getCompanyId(), siteExternalReferenceCode),
			pagination, search, sorts, this::_toPageTemplateSet);
	}

	@Override
	protected PageTemplateSet doPostSitePageTemplateSet(
			String siteExternalReferenceCode, PageTemplateSet pageTemplateSet)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		return _toPageTemplateSet(
			PageTemplateSetUtil.addLayoutPageTemplateCollection(
				GroupUtil.getStagingAwareGroupId(
					contextCompany.getCompanyId(), siteExternalReferenceCode),
				contextHttpServletRequest, pageTemplateSet));
	}

	@Override
	protected PageTemplateSet doPutSitePageTemplateSet(
			String siteExternalReferenceCode,
			String pageTemplateSetExternalReferenceCode,
			PageTemplateSet pageTemplateSet)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		long groupId = GroupUtil.getStagingAwareGroupId(
			contextCompany.getCompanyId(), siteExternalReferenceCode);

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

	private long _getDesignLibraryGroupId(
			String designLibraryExternalReferenceCode)
		throws Exception {

		return GroupUtil.getDepotGroupId(
			contextCompany.getCompanyId(), designLibraryExternalReferenceCode,
			DepotConstants.TYPE_DESIGN_LIBRARY);
	}

	private Page<PageTemplateSet> _getPageTemplateSetsPage(
			Aggregation aggregation, Filter filter, long groupId,
			Pagination pagination, String search, Sort[] sorts,
			UnsafeFunction
				<LayoutPageTemplateCollection, PageTemplateSet, Exception>
					unsafeFunction)
		throws Exception {

		return SearchUtil.search(
			Collections.emptyMap(),
			booleanQuery -> {
			},
			filter, LayoutPageTemplateCollection.class.getName(), search,
			pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.addVulcanAggregation(aggregation);
				searchContext.setAttribute(
					Field.TYPE,
					String.valueOf(
						LayoutPageTemplateCollectionTypeConstants.BASIC));
				searchContext.setCompanyId(contextCompany.getCompanyId());
				searchContext.setGroupIds(new long[] {groupId});
			},
			sorts,
			document -> unsafeFunction.apply(
				_layoutPageTemplateCollectionService.
					fetchLayoutPageTemplateCollection(
						GetterUtil.getLong(
							document.get(Field.ENTRY_CLASS_PK)))));
	}

	private boolean _hasViewDepotEntryPermission(long groupId)
		throws Exception {

		return _depotEntryModelResourcePermission.contains(
			PermissionThreadLocal.getPermissionChecker(),
			_depotEntryLocalService.getGroupDepotEntry(groupId),
			ActionKeys.VIEW);
	}

	private PageTemplateSet _toDesignLibraryPageTemplateSet(
			String designLibraryExternalReferenceCode,
			LayoutPageTemplateCollection layoutPageTemplateCollection)
		throws Exception {

		return _pageTemplateSetDTOConverter.toDTO(
			DTOConverterContextUtil.getDTOConverterContext(
				contextAcceptLanguage,
				PageTemplateSetActionUtil.getDesignLibraryActions(
					contextScopeChecker, designLibraryExternalReferenceCode,
					layoutPageTemplateCollection,
					_layoutPageTemplateCollectionModelResourcePermission,
					contextUriInfo),
				Collections.emptyMap(), _dtoConverterRegistry,
				contextHttpServletRequest,
				layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				contextUriInfo, contextUser),
			layoutPageTemplateCollection);
	}

	private PageTemplateSet _toPageTemplateSet(
			LayoutPageTemplateCollection layoutPageTemplateCollection)
		throws Exception {

		return _pageTemplateSetDTOConverter.toDTO(
			DTOConverterContextUtil.getDTOConverterContext(
				contextAcceptLanguage, _dtoConverterRegistry,
				contextHttpServletRequest,
				layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				contextUriInfo, contextUser),
			layoutPageTemplateCollection);
	}

	private static final EntityModel _entityModel =
		new PageTemplateSetEntityModel();

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference(target = "(model.class.name=com.liferay.depot.model.DepotEntry)")
	private ModelResourcePermission<DepotEntry>
		_depotEntryModelResourcePermission;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.layout.page.template.model.LayoutPageTemplateCollection)"
	)
	private ModelResourcePermission<LayoutPageTemplateCollection>
		_layoutPageTemplateCollectionModelResourcePermission;

	@Reference
	private LayoutPageTemplateCollectionService
		_layoutPageTemplateCollectionService;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.PageTemplateSetDTOConverter)"
	)
	private DTOConverter<LayoutPageTemplateCollection, PageTemplateSet>
		_pageTemplateSetDTOConverter;

}