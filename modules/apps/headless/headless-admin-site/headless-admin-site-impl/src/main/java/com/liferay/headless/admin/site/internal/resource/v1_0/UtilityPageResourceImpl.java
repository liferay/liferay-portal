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
import com.liferay.headless.admin.site.dto.v1_0.PageSpecification;
import com.liferay.headless.admin.site.dto.v1_0.UtilityPage;
import com.liferay.headless.admin.site.dto.v1_0.UtilityPageSEOSettings;
import com.liferay.headless.admin.site.dto.v1_0.UtilityPageSettings;
import com.liferay.headless.admin.site.dto.v1_0.util.FileEntryUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.DTOConverterContextUtil;
import com.liferay.headless.admin.site.internal.odata.entity.v1_0.UtilityPageEntityModel;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.LayoutUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.ServiceContextUtil;
import com.liferay.headless.admin.site.internal.util.EnabledUtil;
import com.liferay.headless.admin.site.resource.v1_0.UtilityPageResource;
import com.liferay.headless.common.spi.util.GroupUtil;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.utility.page.kernel.constants.LayoutUtilityPageEntryConstants;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryService;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Rubén Pulido
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/utility-page.properties",
	property = "export.import.vulcan.batch.engine.task.item.delegate=true",
	scope = ServiceScope.PROTOTYPE, service = UtilityPageResource.class
)
public class UtilityPageResourceImpl
	extends BaseUtilityPageResourceImpl
	implements ExportImportVulcanBatchEngineTaskItemDelegate<UtilityPage> {

	@Override
	public void deleteSiteUtilityPage(
			String siteExternalReferenceCode,
			String utilityPageExternalReferenceCode)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		_layoutUtilityPageEntryService.deleteLayoutUtilityPageEntry(
			utilityPageExternalReferenceCode,
			GroupUtil.getStagingAwareGroupId(
				contextCompany.getCompanyId(), siteExternalReferenceCode));
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public ExportImportDescriptor<LayoutUtilityPageEntry>
		getExportImportDescriptor() {

		return new ExportImportDescriptor<>() {

			@Override
			public String getKey() {
				return UtilityPageResourceImpl.class.getName();
			}

			@Override
			public String getLabelLanguageKey() {
				return "utility-pages";
			}

			@Override
			public Class<LayoutUtilityPageEntry> getModelClass() {
				return LayoutUtilityPageEntry.class;
			}

			@Override
			public List<String> getNestedFields() {
				return List.of("pageSpecifications", "thumbnailURLReference");
			}

			@Override
			public String getPortletId() {
				return LayoutAdminPortletKeys.LAYOUT_UTILITY_PAGES;
			}

			@Override
			public Scope getScope() {
				return Scope.SITE;
			}

			@Override
			public String getSectionKey() {
				return ExportImportConstants.SECTION_KEY_SITE_BUILDER;
			}

			@Override
			public boolean isStagingSupported() {
				return true;
			}

		};
	}

	@Override
	public ContentPageSpecification postSiteUtilityPagePageSpecification(
			String siteExternalReferenceCode,
			String utilityPageExternalReferenceCode,
			ContentPageSpecification contentPageSpecification)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			_layoutUtilityPageEntryService.
				getLayoutUtilityPageEntryByExternalReferenceCode(
					utilityPageExternalReferenceCode,
					GroupUtil.getStagingAwareGroupId(
						contextCompany.getCompanyId(),
						siteExternalReferenceCode));

		return (ContentPageSpecification)_pageSpecificationDTOConverter.toDTO(
			DTOConverterContextUtil.getDTOConverterContext(
				contextAcceptLanguage, _dtoConverterRegistry,
				contextHttpServletRequest, layoutUtilityPageEntry.getPlid(),
				contextUriInfo, contextUser),
			LayoutUtil.addDraftToLayout(
				_cetManager, contentPageSpecification,
				_fragmentEntryProcessorRegistry, _infoItemServiceRegistry,
				_layoutLocalService.getLayout(layoutUtilityPageEntry.getPlid()),
				ServiceContextUtil.createServiceContext(
					layoutUtilityPageEntry.getGroupId(),
					contextHttpServletRequest, contextUser.getUserId())));
	}

	@Override
	protected UtilityPage doGetSiteUtilityPage(
			String siteExternalReferenceCode,
			String utilityPageExternalReferenceCode)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			_layoutUtilityPageEntryService.
				getLayoutUtilityPageEntryByExternalReferenceCode(
					utilityPageExternalReferenceCode,
					GroupUtil.getGroupId(
						true, contextCompany.getCompanyId(),
						siteExternalReferenceCode));

		return _utilityPageDTOConverter.toDTO(
			DTOConverterContextUtil.getDTOConverterContext(
				contextAcceptLanguage, _dtoConverterRegistry,
				contextHttpServletRequest,
				layoutUtilityPageEntry.getLayoutUtilityPageEntryId(),
				contextUriInfo, contextUser),
			layoutUtilityPageEntry);
	}

	@Override
	protected Page<UtilityPage> doGetSiteUtilityPagesPage(
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
			filter, LayoutUtilityPageEntry.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.setCompanyId(contextCompany.getCompanyId());
				searchContext.setGroupIds(new long[] {groupId});
			},
			sorts,
			document -> {
				long layoutUtilityPageEntryId = GetterUtil.getLong(
					document.get(Field.ENTRY_CLASS_PK));

				return _utilityPageDTOConverter.toDTO(
					DTOConverterContextUtil.getDTOConverterContext(
						contextAcceptLanguage, _dtoConverterRegistry,
						contextHttpServletRequest, layoutUtilityPageEntryId,
						contextUriInfo, contextUser),
					_layoutUtilityPageEntryService.fetchLayoutUtilityPageEntry(
						layoutUtilityPageEntryId));
			});
	}

	@Override
	protected UtilityPage doPostSiteUtilityPage(
			String siteExternalReferenceCode, UtilityPage utilityPage)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		return _addLayoutUtilityPageEntry(
			GroupUtil.getStagingAwareGroupId(
				contextCompany.getCompanyId(), siteExternalReferenceCode),
			utilityPage);
	}

	@Override
	protected UtilityPage doPutSiteUtilityPage(
			String siteExternalReferenceCode,
			String utilityPageExternalReferenceCode, UtilityPage utilityPage)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		long groupId = GroupUtil.getStagingAwareGroupId(
			contextCompany.getCompanyId(), siteExternalReferenceCode);

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			_layoutUtilityPageEntryService.
				fetchLayoutUtilityPageEntryByExternalReferenceCode(
					utilityPageExternalReferenceCode, groupId);

		if (layoutUtilityPageEntry == null) {
			return _addLayoutUtilityPageEntry(groupId, utilityPage);
		}

		_validateUtilityPage(utilityPage);

		Layout layout = _layoutLocalService.getLayout(
			layoutUtilityPageEntry.getPlid());

		Map<Locale, String> titleMap = Collections.singletonMap(
			_portal.getSiteDefaultLocale(groupId), utilityPage.getName());
		Map<Locale, String> descriptionMap = Collections.emptyMap();

		UtilityPageSettings utilityPageSettings =
			utilityPage.getUtilityPageSettings();

		if ((utilityPageSettings != null) &&
			(utilityPageSettings.getSeoSettings() != null)) {

			UtilityPageSEOSettings utilityPageSEOSettings =
				utilityPageSettings.getSeoSettings();

			titleMap = LocalizedMapUtil.getLocalizedMap(
				utilityPageSEOSettings.getHtmlTitle_i18n());
			descriptionMap = LocalizedMapUtil.getLocalizedMap(
				utilityPageSEOSettings.getDescription_i18n());
		}

		ServiceContext serviceContext = _getServiceContext(
			groupId, utilityPage);

		LayoutUtil.updateContentLayout(
			_cetManager, _fragmentEntryProcessorRegistry,
			_infoItemServiceRegistry, layout, layout.getNameMap(), titleMap,
			descriptionMap, layout.getKeywordsMap(), layout.getRobotsMap(),
			LocalizedMapUtil.getLocalizedMap(
				utilityPage.getFriendlyUrlPath_i18n()),
			layout.getTypeSettingsProperties(),
			utilityPage.getPageSpecifications(), serviceContext);

		if (GetterUtil.getBoolean(utilityPage.getMarkedAsDefault()) &&
			!layoutUtilityPageEntry.isDefaultLayoutUtilityPageEntry()) {

			layoutUtilityPageEntry =
				_layoutUtilityPageEntryService.setDefaultLayoutUtilityPageEntry(
					layoutUtilityPageEntry.getLayoutUtilityPageEntryId());
		}
		else if (!GetterUtil.getBoolean(utilityPage.getMarkedAsDefault()) &&
				 layoutUtilityPageEntry.isDefaultLayoutUtilityPageEntry()) {

			layoutUtilityPageEntry =
				_layoutUtilityPageEntryService.
					unsetDefaultLayoutUtilityPageEntry(
						layoutUtilityPageEntry.getLayoutUtilityPageEntryId());
		}

		long previewFileEntryId = FileEntryUtil.getPreviewFileEntryId(
			groupId, LayoutAdminPortletKeys.GROUP_PAGES,
			utilityPage.getThumbnailURLReference(), contextUser.getUserId());

		if (previewFileEntryId !=
				layoutUtilityPageEntry.getPreviewFileEntryId()) {

			layoutUtilityPageEntry =
				_layoutUtilityPageEntryService.updateLayoutUtilityPageEntry(
					layoutUtilityPageEntry.getLayoutUtilityPageEntryId(),
					previewFileEntryId, serviceContext);
		}

		return _utilityPageDTOConverter.toDTO(
			_layoutUtilityPageEntryService.updateLayoutUtilityPageEntry(
				layoutUtilityPageEntry.getLayoutUtilityPageEntryId(),
				utilityPage.getName(), serviceContext));
	}

	@Override
	protected Long getPermissionCheckerResourceId(
			String groupExternalReferenceCode, String externalReferenceCode)
		throws Exception {

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			_layoutUtilityPageEntryService.
				getLayoutUtilityPageEntryByExternalReferenceCode(
					externalReferenceCode,
					getPermissionCheckerGroupId(groupExternalReferenceCode));

		return layoutUtilityPageEntry.getPrimaryKey();
	}

	@Override
	protected String getPermissionCheckerResourceName(
			String groupExternalReferenceCode, String externalReferenceCode)
		throws Exception {

		return LayoutUtilityPageEntry.class.getName();
	}

	@Override
	protected void preparePatch(
		UtilityPage utilityPage, UtilityPage existingUtilityPage) {

		if (utilityPage.getPageSpecifications() != null) {
			existingUtilityPage.setPageSpecifications(
				utilityPage::getPageSpecifications);
		}

		if (utilityPage.getThumbnailURLReference() != null) {
			existingUtilityPage.setThumbnailURLReference(
				utilityPage::getThumbnailURLReference);
		}

		if (utilityPage.getUtilityPageSettings() != null) {
			existingUtilityPage.setUtilityPageSettings(
				utilityPage::getUtilityPageSettings);
		}
	}

	private UtilityPage _addLayoutUtilityPageEntry(
			long groupId, UtilityPage utilityPage)
		throws Exception {

		ServiceContext serviceContext = _getServiceContext(
			groupId, utilityPage);

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			_layoutUtilityPageEntryService.addLayoutUtilityPageEntry(
				utilityPage.getExternalReferenceCode(), groupId,
				_getLayoutPlid(groupId, utilityPage, serviceContext),
				FileEntryUtil.getPreviewFileEntryId(
					groupId, LayoutAdminPortletKeys.GROUP_PAGES,
					utilityPage.getThumbnailURLReference(),
					contextUser.getUserId()),
				utilityPage.getMarkedAsDefault(), utilityPage.getName(),
				_getType(utilityPage.getType()), null, serviceContext);

		return _utilityPageDTOConverter.toDTO(
			DTOConverterContextUtil.getDTOConverterContext(
				contextAcceptLanguage, _dtoConverterRegistry,
				contextHttpServletRequest,
				layoutUtilityPageEntry.getLayoutUtilityPageEntryId(),
				contextUriInfo, contextUser),
			layoutUtilityPageEntry);
	}

	private long _getLayoutPlid(
			long groupId, UtilityPage utilityPage,
			ServiceContext serviceContext)
		throws Exception {

		_validateUtilityPage(utilityPage);

		Map<Locale, String> nameMap = Collections.singletonMap(
			_portal.getSiteDefaultLocale(groupId), utilityPage.getName());

		Map<Locale, String> titleMap = nameMap;

		Map<Locale, String> descriptionMap = Collections.emptyMap();

		UtilityPageSettings utilityPageSettings =
			utilityPage.getUtilityPageSettings();

		if ((utilityPageSettings != null) &&
			(utilityPageSettings.getSeoSettings() != null)) {

			UtilityPageSEOSettings utilityPageSEOSettings =
				utilityPageSettings.getSeoSettings();

			titleMap = LocalizedMapUtil.getLocalizedMap(
				utilityPageSEOSettings.getHtmlTitle_i18n());
			descriptionMap = LocalizedMapUtil.getLocalizedMap(
				utilityPageSEOSettings.getDescription_i18n());
		}

		serviceContext.setAttribute(
			"layout.instanceable.allowed", Boolean.TRUE);

		Layout layout = LayoutUtil.addContentLayout(
			_cetManager, _fragmentEntryProcessorRegistry, groupId,
			_infoItemServiceRegistry, utilityPage.getPageSpecifications(),
			false, LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, nameMap, titleMap,
			descriptionMap, null, null, LayoutConstants.TYPE_UTILITY, null,
			true, true,
			LocalizedMapUtil.getLocalizedMap(
				utilityPage.getFriendlyUrlPath_i18n()),
			WorkflowConstants.STATUS_DRAFT, serviceContext);

		return layout.getPlid();
	}

	private ServiceContext _getServiceContext(
		long groupId, UtilityPage utilityPage) {

		ServiceContext serviceContext = ServiceContextUtil.createServiceContext(
			groupId, contextHttpServletRequest, contextUser.getUserId());

		serviceContext.setCreateDate(utilityPage.getDateCreated());
		serviceContext.setModifiedDate(utilityPage.getDateModified());
		serviceContext.setUuid(utilityPage.getUuid());

		return serviceContext;
	}

	private String _getType(UtilityPage.Type type) {
		String internalType = _externalToInternalValuesMap.get(type);

		if (internalType != null) {
			return internalType;
		}

		throw new IllegalArgumentException(
			"The page type does not match the expected utility page type");
	}

	private void _validateUtilityPage(UtilityPage utilityPage) {
		if (ArrayUtil.isEmpty(utilityPage.getPageSpecifications())) {
			return;
		}

		for (PageSpecification pageSpecification :
				utilityPage.getPageSpecifications()) {

			if (pageSpecification.getCustomFields() != null) {
				throw new IllegalArgumentException(
					"Utility pages do not support custom fields");
			}
		}
	}

	private static final EntityModel _entityModel =
		new UtilityPageEntityModel();
	private static final Map<UtilityPage.Type, String>
		_externalToInternalValuesMap = HashMapBuilder.put(
			UtilityPage.Type.COOKIE_POLICY,
			LayoutUtilityPageEntryConstants.TYPE_COOKIE_POLICY
		).put(
			UtilityPage.Type.CREATE_ACCOUNT,
			LayoutUtilityPageEntryConstants.TYPE_CREATE_ACCOUNT
		).put(
			UtilityPage.Type.ERROR, LayoutUtilityPageEntryConstants.TYPE_STATUS
		).put(
			UtilityPage.Type.ERROR_CODE404,
			LayoutUtilityPageEntryConstants.TYPE_SC_NOT_FOUND
		).put(
			UtilityPage.Type.ERROR_CODE500,
			LayoutUtilityPageEntryConstants.TYPE_SC_INTERNAL_SERVER_ERROR
		).put(
			UtilityPage.Type.FORGOT_PASSWORD,
			LayoutUtilityPageEntryConstants.TYPE_FORGOT_PASSWORD
		).put(
			UtilityPage.Type.LOGIN, LayoutUtilityPageEntryConstants.TYPE_LOGIN
		).put(
			UtilityPage.Type.TERMS_OF_USE,
			LayoutUtilityPageEntryConstants.TYPE_TERMS_OF_USE
		).build();

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
	private LayoutUtilityPageEntryService _layoutUtilityPageEntryService;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.PageSpecificationDTOConverter)"
	)
	private DTOConverter<Layout, PageSpecification>
		_pageSpecificationDTOConverter;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.UtilityPageDTOConverter)"
	)
	private DTOConverter<LayoutUtilityPageEntry, UtilityPage>
		_utilityPageDTOConverter;

}