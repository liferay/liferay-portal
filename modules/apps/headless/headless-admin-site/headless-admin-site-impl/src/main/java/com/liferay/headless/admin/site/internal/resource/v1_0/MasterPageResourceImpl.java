/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0;

import com.liferay.client.extension.type.manager.CETManager;
import com.liferay.exportimport.constants.ExportImportConstants;
import com.liferay.exportimport.vulcan.batch.engine.ExportImportVulcanBatchEngineTaskItemDelegate;
import com.liferay.fragment.processor.FragmentEntryProcessorRegistry;
import com.liferay.headless.admin.site.dto.v1_0.ContentPageSpecification;
import com.liferay.headless.admin.site.dto.v1_0.MasterPage;
import com.liferay.headless.admin.site.dto.v1_0.PageSpecification;
import com.liferay.headless.admin.site.dto.v1_0.util.FileEntryUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.DTOConverterContextUtil;
import com.liferay.headless.admin.site.internal.odata.entity.v1_0.MasterPageEntityModel;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.LayoutUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.PageSpecificationUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.ServiceContextUtil;
import com.liferay.headless.admin.site.internal.util.EnabledUtil;
import com.liferay.headless.admin.site.resource.v1_0.MasterPageResource;
import com.liferay.headless.common.spi.util.GroupUtil;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.page.template.admin.constants.LayoutPageTemplateAdminPortletKeys;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Rubén Pulido
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/master-page.properties",
	property = "export.import.vulcan.batch.engine.task.item.delegate=true",
	scope = ServiceScope.PROTOTYPE, service = MasterPageResource.class
)
public class MasterPageResourceImpl
	extends BaseMasterPageResourceImpl
	implements ExportImportVulcanBatchEngineTaskItemDelegate<MasterPage> {

	@Override
	public void deleteSiteMasterPage(
			String siteExternalReferenceCode,
			String masterPageExternalReferenceCode)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		_layoutPageTemplateEntryService.deleteLayoutPageTemplateEntry(
			masterPageExternalReferenceCode,
			GroupUtil.getStagingAwareGroupId(
				contextCompany.getCompanyId(), siteExternalReferenceCode));
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public ExportImportDescriptor<LayoutPageTemplateEntry>
		getExportImportDescriptor() {

		return new ExportImportDescriptor<>() {

			@Override
			public Function<LayoutPageTemplateEntry, Boolean>
				getApplicableModelFunction() {

				return layoutPageTemplateEntry ->
					layoutPageTemplateEntry.getType() ==
						LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT;
			}

			@Override
			public String getKey() {
				return LayoutPageTemplateEntry.class.getName() + "-" +
					LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT;
			}

			@Override
			public String getLabelLanguageKey() {
				return "master-pages";
			}

			@Override
			public Class<LayoutPageTemplateEntry> getModelClass() {
				return LayoutPageTemplateEntry.class;
			}

			@Override
			public List<String> getNestedFields() {
				return List.of("pageSpecifications", "thumbnailURLReference");
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
	public ContentPageSpecification postSiteMasterPagePageSpecification(
			String siteExternalReferenceCode,
			String pageTemplateExternalReferenceCode,
			ContentPageSpecification contentPageSpecification)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryService.
				fetchLayoutPageTemplateEntryByExternalReferenceCode(
					pageTemplateExternalReferenceCode,
					GroupUtil.getStagingAwareGroupId(
						contextCompany.getCompanyId(),
						siteExternalReferenceCode));

		if (!Objects.equals(
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT,
				layoutPageTemplateEntry.getType())) {

			throw new IllegalArgumentException(
				"The external reference code does not point to a master page");
		}

		return (ContentPageSpecification)_pageSpecificationDTOConverter.toDTO(
			DTOConverterContextUtil.getDTOConverterContext(
				contextAcceptLanguage, _dtoConverterRegistry,
				contextHttpServletRequest, layoutPageTemplateEntry.getPlid(),
				contextUriInfo, contextUser),
			LayoutUtil.addDraftToLayout(
				_cetManager, contentPageSpecification,
				_fragmentEntryProcessorRegistry, _infoItemServiceRegistry,
				_layoutLocalService.getLayout(
					layoutPageTemplateEntry.getPlid()),
				ServiceContextUtil.createServiceContext(
					layoutPageTemplateEntry.getGroupId(),
					contextHttpServletRequest, contextUser.getUserId())));
	}

	@Override
	protected MasterPage doGetSiteMasterPage(
			String siteExternalReferenceCode,
			String masterPageExternalReferenceCode)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryService.
				getLayoutPageTemplateEntryByExternalReferenceCode(
					masterPageExternalReferenceCode,
					GroupUtil.getGroupId(
						true, contextCompany.getCompanyId(),
						siteExternalReferenceCode));

		if (!Objects.equals(
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT,
				layoutPageTemplateEntry.getType())) {

			throw new IllegalArgumentException(
				"The external reference code does not point to a master page");
		}

		return _masterPageDTOConverter.toDTO(
			DTOConverterContextUtil.getDTOConverterContext(
				contextAcceptLanguage, _dtoConverterRegistry,
				contextHttpServletRequest,
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
				contextUriInfo, contextUser),
			layoutPageTemplateEntry);
	}

	@Override
	protected Page<MasterPage> doGetSiteMasterPagesPage(
			String siteExternalReferenceCode, String search,
			Aggregation aggregation, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		long groupId = GroupUtil.getGroupId(
			true, contextCompany.getCompanyId(), siteExternalReferenceCode);

		return SearchUtil.search(
			Collections.emptyMap(),
			booleanQuery -> {
			},
			filter, LayoutPageTemplateEntry.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.setAttribute(
					"types",
					new String[] {
						String.valueOf(
							LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT)
					});
				searchContext.setCompanyId(contextCompany.getCompanyId());
				searchContext.setGroupIds(new long[] {groupId});
			},
			sorts,
			document -> {
				long layoutPageTemplateEntryId = GetterUtil.getLong(
					document.get(Field.ENTRY_CLASS_PK));

				return _masterPageDTOConverter.toDTO(
					DTOConverterContextUtil.getDTOConverterContext(
						contextAcceptLanguage, _dtoConverterRegistry,
						contextHttpServletRequest, layoutPageTemplateEntryId,
						contextUriInfo, contextUser),
					_layoutPageTemplateEntryService.
						fetchLayoutPageTemplateEntry(
							layoutPageTemplateEntryId));
			});
	}

	@Override
	protected MasterPage doPostSiteMasterPage(
			String siteExternalReferenceCode, MasterPage masterPage)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		return _addMasterPage(
			GroupUtil.getStagingAwareGroupId(
				contextCompany.getCompanyId(), siteExternalReferenceCode),
			masterPage);
	}

	@Override
	protected MasterPage doPutSiteMasterPage(
			String siteExternalReferenceCode,
			String masterPageExternalReferenceCode, MasterPage masterPage)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		long groupId = GroupUtil.getStagingAwareGroupId(
			contextCompany.getCompanyId(), siteExternalReferenceCode);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryService.
				fetchLayoutPageTemplateEntryByExternalReferenceCode(
					masterPageExternalReferenceCode, groupId);

		if (layoutPageTemplateEntry == null) {
			return _addMasterPage(groupId, masterPage);
		}

		ServiceContext serviceContext = _getServiceContext(groupId, masterPage);

		long previewFileEntryId = FileEntryUtil.getPreviewFileEntryId(
			groupId, LayoutAdminPortletKeys.GROUP_PAGES,
			masterPage.getThumbnailURLReference(), contextUser.getUserId());

		if (previewFileEntryId !=
				layoutPageTemplateEntry.getPreviewFileEntryId()) {

			layoutPageTemplateEntry =
				_layoutPageTemplateEntryService.updateLayoutPageTemplateEntry(
					layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
					previewFileEntryId);
		}

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		layout = LayoutUtil.updateContentLayout(
			_cetManager, _fragmentEntryProcessorRegistry,
			_infoItemServiceRegistry, layout, layout.getNameMap(),
			layout.getTitleMap(), layout.getDescriptionMap(),
			layout.getKeywordsMap(), layout.getRobotsMap(),
			layout.getFriendlyURLMap(), layout.getTypeSettingsProperties(),
			masterPage.getPageSpecifications(), serviceContext);

		if (!layoutPageTemplateEntry.isApproved() && layout.isPublished()) {
			layoutPageTemplateEntry =
				_layoutPageTemplateEntryService.updateStatus(
					layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
					WorkflowConstants.STATUS_APPROVED);
		}

		if (!Objects.equals(
				GetterUtil.getBoolean(masterPage.getMarkedAsDefault()),
				layoutPageTemplateEntry.isDefaultTemplate())) {

			layoutPageTemplateEntry =
				_layoutPageTemplateEntryService.updateLayoutPageTemplateEntry(
					layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
					GetterUtil.getBoolean(masterPage.getMarkedAsDefault()));
		}

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		try {
			return _masterPageDTOConverter.toDTO(
				DTOConverterContextUtil.getDTOConverterContext(
					contextAcceptLanguage, _dtoConverterRegistry,
					contextHttpServletRequest,
					layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
					contextUriInfo, contextUser),
				_layoutPageTemplateEntryService.updateLayoutPageTemplateEntry(
					layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
					masterPage.getName()));
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Override
	protected Long getPermissionCheckerResourceId(
			String groupExternalReferenceCode, String externalReferenceCode)
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryService.
				getLayoutPageTemplateEntryByExternalReferenceCode(
					externalReferenceCode,
					getPermissionCheckerGroupId(groupExternalReferenceCode));

		return layoutPageTemplateEntry.getPrimaryKey();
	}

	@Override
	protected String getPermissionCheckerResourceName(
			String groupExternalReferenceCode, String externalReferenceCode)
		throws Exception {

		return LayoutPageTemplateEntry.class.getName();
	}

	@Override
	protected void preparePatch(
		MasterPage masterPage, MasterPage existingMasterPage) {

		if (masterPage.getKeywords() != null) {
			existingMasterPage.setKeywords(masterPage::getKeywords);
		}

		if (masterPage.getPageSpecifications() != null) {
			existingMasterPage.setPageSpecifications(
				masterPage::getPageSpecifications);
		}

		if (masterPage.getTaxonomyCategoryBriefs() != null) {
			existingMasterPage.setTaxonomyCategoryBriefs(
				masterPage::getTaxonomyCategoryBriefs);
		}

		if (masterPage.getThumbnailURLReference() != null) {
			existingMasterPage.setThumbnailURLReference(
				masterPage::getThumbnailURLReference);
		}
	}

	private MasterPage _addMasterPage(long groupId, MasterPage masterPage)
		throws Exception {

		boolean defaultTemplate = false;

		if (GetterUtil.getBoolean(masterPage.getMarkedAsDefault())) {
			defaultTemplate = true;
		}

		ServiceContext serviceContext = _getServiceContext(groupId, masterPage);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryService.addLayoutPageTemplateEntry(
				masterPage.getExternalReferenceCode(), groupId,
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				masterPage.getKey(), 0, null, masterPage.getName(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT,
				FileEntryUtil.getPreviewFileEntryId(
					groupId, LayoutAdminPortletKeys.GROUP_PAGES,
					masterPage.getThumbnailURLReference(),
					contextUser.getUserId()),
				defaultTemplate, 0,
				_getLayoutPlid(groupId, masterPage, serviceContext), 0,
				PageSpecificationUtil.getPublishedStatus(
					masterPage.getPageSpecifications()),
				serviceContext);

		return _masterPageDTOConverter.toDTO(
			DTOConverterContextUtil.getDTOConverterContext(
				contextAcceptLanguage, _dtoConverterRegistry,
				contextHttpServletRequest,
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
				contextUriInfo, contextUser),
			layoutPageTemplateEntry);
	}

	private long _getLayoutPlid(
			long groupId, MasterPage masterPage, ServiceContext serviceContext)
		throws Exception {

		Map<Locale, String> nameMap = Collections.singletonMap(
			_portal.getSiteDefaultLocale(groupId), masterPage.getName());

		serviceContext.setAttribute(
			"layout.instanceable.allowed", Boolean.TRUE);
		serviceContext.setAttribute(
			"layout.page.template.entry.type",
			LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT);

		Layout layout = LayoutUtil.addContentLayout(
			_cetManager, _fragmentEntryProcessorRegistry, groupId,
			_infoItemServiceRegistry, masterPage.getPageSpecifications(), true,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, nameMap, null, null, null,
			null, LayoutConstants.TYPE_CONTENT, null, true, true,
			Collections.emptyMap(), WorkflowConstants.STATUS_APPROVED,
			serviceContext);

		return layout.getPlid();
	}

	private ServiceContext _getServiceContext(
			long groupId, MasterPage masterPage)
		throws Exception {

		return ServiceContextUtil.createServiceContext(
			contextCompany.getCompanyId(), masterPage.getDateCreated(), groupId,
			contextHttpServletRequest, masterPage.getKeywords(),
			masterPage.getDateModified(),
			masterPage.getTaxonomyCategoryBriefs(), contextUser.getUserId(),
			masterPage.getUuid());
	}

	private static final EntityModel _entityModel = new MasterPageEntityModel();

	@Reference
	private CETManager _cetManager;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private FragmentEntryProcessorRegistry _fragmentEntryProcessorRegistry;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateEntryService _layoutPageTemplateEntryService;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.MasterPageDTOConverter)"
	)
	private DTOConverter<LayoutPageTemplateEntry, MasterPage>
		_masterPageDTOConverter;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.PageSpecificationDTOConverter)"
	)
	private DTOConverter<Layout, PageSpecification>
		_pageSpecificationDTOConverter;

	@Reference
	private Portal _portal;

}