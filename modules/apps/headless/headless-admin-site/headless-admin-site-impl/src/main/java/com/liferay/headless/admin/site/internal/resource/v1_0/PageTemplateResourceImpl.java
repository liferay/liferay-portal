/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0;

import com.liferay.headless.admin.site.dto.v1_0.ContentPageSpecification;
import com.liferay.headless.admin.site.dto.v1_0.ContentPageTemplate;
import com.liferay.headless.admin.site.dto.v1_0.PageSpecification;
import com.liferay.headless.admin.site.dto.v1_0.PageTemplate;
import com.liferay.headless.admin.site.dto.v1_0.PageTemplateSet;
import com.liferay.headless.admin.site.dto.v1_0.WidgetPageTemplate;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.GroupUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.LayoutUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.ServiceContextUtil;
import com.liferay.headless.admin.site.resource.v1_0.PageTemplateResource;
import com.liferay.headless.common.spi.service.context.ServiceContextBuilder;
import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutPrototypeService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Rubén Pulido
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/page-template.properties",
	scope = ServiceScope.PROTOTYPE, service = PageTemplateResource.class
)
public class PageTemplateResourceImpl extends BasePageTemplateResourceImpl {

	@Override
	public void deleteSiteSiteByExternalReferenceCodePageTemplate(
			String siteExternalReferenceCode,
			String pageTemplateExternalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-35443")) {
			throw new UnsupportedOperationException();
		}

		_layoutPageTemplateEntryService.deleteLayoutPageTemplateEntry(
			pageTemplateExternalReferenceCode,
			GroupUtil.getGroupId(
				true, false, contextCompany.getCompanyId(),
				siteExternalReferenceCode));
	}

	@Override
	public PageTemplate getSiteSiteByExternalReferenceCodePageTemplate(
			String siteExternalReferenceCode,
			String pageTemplateExternalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-35443")) {
			throw new UnsupportedOperationException();
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryService.
				getLayoutPageTemplateEntryByExternalReferenceCode(
					pageTemplateExternalReferenceCode,
					GroupUtil.getGroupId(
						true, true, contextCompany.getCompanyId(),
						siteExternalReferenceCode));

		if (!Objects.equals(
				LayoutPageTemplateEntryTypeConstants.BASIC,
				layoutPageTemplateEntry.getType()) &&
			!Objects.equals(
				LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE,
				layoutPageTemplateEntry.getType())) {

			throw new UnsupportedOperationException();
		}

		return _pageTemplateDTOConverter.toDTO(layoutPageTemplateEntry);
	}

	@Override
	public Page<PageTemplate>
			getSiteSiteByExternalReferenceCodePageTemplateSetPageTemplatesPage(
				String siteExternalReferenceCode,
				String pageTemplateSetExternalReferenceCode, Boolean flatten)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-35443")) {
			throw new UnsupportedOperationException();
		}

		long groupId = GroupUtil.getGroupId(
			true, contextCompany.getCompanyId(), siteExternalReferenceCode);

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionService.
				getLayoutPageTemplateCollection(
					pageTemplateSetExternalReferenceCode, groupId);

		if (!Objects.equals(
				LayoutPageTemplateCollectionTypeConstants.BASIC,
				layoutPageTemplateCollection.getType())) {

			throw new UnsupportedOperationException();
		}

		return Page.of(
			transform(
				_layoutPageTemplateEntryService.getLayoutPageTemplateEntries(
					groupId,
					layoutPageTemplateCollection.
						getLayoutPageTemplateCollectionId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null),
				layoutPageTemplateEntry -> _pageTemplateDTOConverter.toDTO(
					layoutPageTemplateEntry)));
	}

	@Override
	public Page<PageTemplate>
			getSiteSiteByExternalReferenceCodePageTemplatesPage(
				String siteExternalReferenceCode, String search,
				Aggregation aggregation, Filter filter, Pagination pagination,
				Sort[] sorts)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-35443")) {
			throw new UnsupportedOperationException();
		}

		long groupId = GroupUtil.getGroupId(
			true, true, contextCompany.getCompanyId(),
			siteExternalReferenceCode);

		return Page.of(
			transform(
				_layoutPageTemplateEntryService.getLayoutPageTemplateEntries(
					groupId,
					new int[] {
						LayoutPageTemplateEntryTypeConstants.BASIC,
						LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE
					},
					pagination.getStartPosition(), pagination.getEndPosition(),
					null),
				layoutPageTemplateEntry -> _pageTemplateDTOConverter.toDTO(
					layoutPageTemplateEntry)),
			pagination,
			_layoutPageTemplateEntryService.getLayoutPageTemplateEntriesCount(
				groupId,
				new int[] {
					LayoutPageTemplateEntryTypeConstants.BASIC,
					LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE
				}));
	}

	@Override
	public PageTemplate postSiteSiteByExternalReferenceCodePageTemplate(
			String siteExternalReferenceCode, PageTemplate pageTemplate)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-35443")) {
			throw new UnsupportedOperationException();
		}

		return _addPageTemplate(
			GroupUtil.getGroupId(
				_isTypeWidgetPageTemplate(pageTemplate), false,
				contextCompany.getCompanyId(), siteExternalReferenceCode),
			pageTemplate);
	}

	@Override
	public ContentPageSpecification
			postSiteSiteByExternalReferenceCodePageTemplatePageSpecification(
				String siteExternalReferenceCode,
				String pageTemplateExternalReferenceCode,
				ContentPageSpecification contentPageSpecification)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-35443")) {
			throw new UnsupportedOperationException();
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryService.
				fetchLayoutPageTemplateEntryByExternalReferenceCode(
					pageTemplateExternalReferenceCode,
					GroupUtil.getGroupId(
						false, contextCompany.getCompanyId(),
						siteExternalReferenceCode));

		if (!Objects.equals(
				LayoutPageTemplateEntryTypeConstants.BASIC,
				layoutPageTemplateEntry.getType())) {

			throw new UnsupportedOperationException();
		}

		return (ContentPageSpecification)_pageSpecificationDTOConverter.toDTO(
			LayoutUtil.addDraftToLayout(
				contentPageSpecification,
				_layoutLocalService.getLayout(
					layoutPageTemplateEntry.getPlid()),
				ServiceContextUtil.createServiceContext(
					layoutPageTemplateEntry.getGroupId(),
					contextHttpServletRequest, contextUser.getUserId())));
	}

	@Override
	public PageTemplate
			postSiteSiteByExternalReferenceCodePageTemplateSetPageTemplate(
				String siteExternalReferenceCode,
				String pageTemplateSetExternalReferenceCode,
				PageTemplate pageTemplate)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-35443")) {
			throw new UnsupportedOperationException();
		}

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionService.
				getLayoutPageTemplateCollection(
					pageTemplateSetExternalReferenceCode,
					GroupUtil.getGroupId(
						false, contextCompany.getCompanyId(),
						siteExternalReferenceCode));

		if (!Objects.equals(
				LayoutPageTemplateCollectionTypeConstants.BASIC,
				layoutPageTemplateCollection.getType())) {

			throw new UnsupportedOperationException();
		}

		return _addPageTemplate(
			layoutPageTemplateCollection.getGroupId(),
			layoutPageTemplateCollection.getLayoutPageTemplateCollectionId(),
			pageTemplate);
	}

	@Override
	public PageTemplate putSiteSiteByExternalReferenceCodePageTemplate(
			String siteExternalReferenceCode,
			String pageTemplateExternalReferenceCode, PageTemplate pageTemplate)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-35443")) {
			throw new UnsupportedOperationException();
		}

		long groupId = GroupUtil.getGroupId(
			_isTypeWidgetPageTemplate(pageTemplate), false,
			contextCompany.getCompanyId(), siteExternalReferenceCode);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryService.
				fetchLayoutPageTemplateEntryByExternalReferenceCode(
					pageTemplateExternalReferenceCode, groupId);

		if (layoutPageTemplateEntry == null) {
			return _addPageTemplate(groupId, pageTemplate);
		}

		if ((Objects.equals(
				layoutPageTemplateEntry.getType(),
				LayoutPageTemplateEntryTypeConstants.BASIC) &&
			 !(pageTemplate instanceof ContentPageTemplate)) ||
			(Objects.equals(
				layoutPageTemplateEntry.getType(),
				LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE) &&
			 !(pageTemplate instanceof WidgetPageTemplate))) {

			throw new UnsupportedOperationException();
		}

		long layoutPageTemplateCollectionId =
			_getLayoutPageTemplateCollectionId(groupId, pageTemplate);

		if (Validator.isNotNull(pageTemplate.getPageTemplateSet()) &&
			!Objects.equals(
				layoutPageTemplateEntry.getLayoutPageTemplateCollectionId(),
				layoutPageTemplateCollectionId)) {

			layoutPageTemplateEntry =
				_layoutPageTemplateEntryService.moveLayoutPageTemplateEntry(
					layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
					layoutPageTemplateCollectionId);
		}

		if (Objects.equals(
				layoutPageTemplateEntry.getType(),
				LayoutPageTemplateEntryTypeConstants.BASIC)) {

			return _updatePageTemplate(
				(ContentPageTemplate)pageTemplate, layoutPageTemplateEntry);
		}

		return _updatePageTemplate(
			layoutPageTemplateEntry, (WidgetPageTemplate)pageTemplate);
	}

	@Override
	protected void preparePatch(
		PageTemplate pageTemplate, PageTemplate existingPageTemplate) {

		if (pageTemplate.getPageTemplateSet() != null) {
			existingPageTemplate.setPageTemplateSet(
				pageTemplate::getPageTemplateSet);
		}

		if (Objects.equals(
				existingPageTemplate.getType(),
				PageTemplate.Type.CONTENT_PAGE_TEMPLATE)) {

			return;
		}

		if (!(existingPageTemplate instanceof WidgetPageTemplate) ||
			!(pageTemplate instanceof WidgetPageTemplate)) {

			throw new UnsupportedOperationException();
		}

		_preparePatch(
			(WidgetPageTemplate)existingPageTemplate,
			(WidgetPageTemplate)pageTemplate);
	}

	private PageTemplate _addPageTemplate(
			ContentPageTemplate contentPageTemplate, long groupId,
			long layoutPageTemplateCollectionId)
		throws Exception {

		return _pageTemplateDTOConverter.toDTO(
			_layoutPageTemplateEntryService.addLayoutPageTemplateEntry(
				contentPageTemplate.getExternalReferenceCode(), groupId,
				layoutPageTemplateCollectionId, contentPageTemplate.getKey(),
				contentPageTemplate.getName(),
				LayoutPageTemplateEntryTypeConstants.BASIC, 0L,
				WorkflowConstants.STATUS_DRAFT,
				_getServiceContext(groupId, contentPageTemplate)));
	}

	private PageTemplate _addPageTemplate(
			long groupId, long layoutPageTemplateCollectionId,
			PageTemplate pageTemplate)
		throws Exception {

		if (Objects.equals(
				pageTemplate.getType(),
				PageTemplate.Type.CONTENT_PAGE_TEMPLATE)) {

			return _addPageTemplate(
				(ContentPageTemplate)pageTemplate, groupId,
				layoutPageTemplateCollectionId);
		}

		return _addPageTemplate(
			groupId, layoutPageTemplateCollectionId,
			(WidgetPageTemplate)pageTemplate);
	}

	private PageTemplate _addPageTemplate(
			long groupId, long layoutPageTemplateCollectionId,
			WidgetPageTemplate widgetPageTemplate)
		throws Exception {

		ServiceContext serviceContext = _getServiceContext(
			groupId, widgetPageTemplate);

		Map<Locale, String> nameMap = HashMapBuilder.put(
			serviceContext.getLocale(), widgetPageTemplate.getName()
		).build();

		if (widgetPageTemplate.getName_i18n() != null) {
			nameMap = LocalizedMapUtil.getLocalizedMap(
				widgetPageTemplate.getName_i18n());
		}

		Map<Locale, String> descriptionMap = Collections.emptyMap();

		if (widgetPageTemplate.getDescription_i18n() != null) {
			descriptionMap = LocalizedMapUtil.getLocalizedMap(
				widgetPageTemplate.getDescription_i18n());
		}

		LayoutPrototype layoutPrototype =
			_layoutPrototypeService.addLayoutPrototype(
				nameMap, descriptionMap,
				GetterUtil.getBoolean(widgetPageTemplate.getActive()),
				serviceContext);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				getFirstLayoutPageTemplateEntry(
					layoutPrototype.getLayoutPrototypeId());

		if (widgetPageTemplate.getExternalReferenceCode() != null) {
			layoutPageTemplateEntry.setExternalReferenceCode(
				widgetPageTemplate.getExternalReferenceCode());
		}

		layoutPageTemplateEntry.setGroupId(groupId);
		layoutPageTemplateEntry.setLayoutPageTemplateCollectionId(
			layoutPageTemplateCollectionId);

		if (widgetPageTemplate.getUuid() != null) {
			layoutPageTemplateEntry.setUuid(widgetPageTemplate.getUuid());
		}

		return _pageTemplateDTOConverter.toDTO(
			_layoutPageTemplateEntryLocalService.updateLayoutPageTemplateEntry(
				layoutPageTemplateEntry));
	}

	private PageTemplate _addPageTemplate(
			long groupId, PageTemplate pageTemplate)
		throws Exception {

		return _addPageTemplate(
			groupId, _getLayoutPageTemplateCollectionId(groupId, pageTemplate),
			pageTemplate);
	}

	private long _getLayoutPageTemplateCollectionId(
			long groupId, PageTemplate pageTemplate)
		throws Exception {

		PageTemplateSet pageTemplateSet = pageTemplate.getPageTemplateSet();

		if (pageTemplateSet == null) {
			return LayoutPageTemplateConstants.
				PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT;
		}

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionService.
				fetchLayoutPageTemplateCollection(
					pageTemplateSet.getExternalReferenceCode(), groupId);

		if (layoutPageTemplateCollection == null) {
			return LayoutPageTemplateConstants.
				PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT;
		}

		if (!Objects.equals(
				LayoutPageTemplateCollectionTypeConstants.BASIC,
				layoutPageTemplateCollection.getType())) {

			throw new UnsupportedOperationException();
		}

		return layoutPageTemplateCollection.getLayoutPageTemplateCollectionId();
	}

	private ServiceContext _getServiceContext(
		long groupId, PageTemplate pageTemplate) {

		ServiceContext serviceContext = ServiceContextBuilder.create(
			groupId, contextHttpServletRequest, null
		).build();

		serviceContext.setCreateDate(pageTemplate.getDateCreated());
		serviceContext.setModifiedDate(pageTemplate.getDateModified());

		if (Objects.equals(
				pageTemplate.getType(),
				PageTemplate.Type.CONTENT_PAGE_TEMPLATE)) {

			serviceContext.setUuid(pageTemplate.getUuid());
		}

		return serviceContext;
	}

	private boolean _isTypeWidgetPageTemplate(PageTemplate pageTemplate) {
		return Objects.equals(
			pageTemplate.getType(), PageTemplate.Type.WIDGET_PAGE_TEMPLATE);
	}

	private void _preparePatch(
		WidgetPageTemplate existingWidgetPageTemplate,
		WidgetPageTemplate widgetPageTemplate) {

		if (widgetPageTemplate.getActive() != null) {
			existingWidgetPageTemplate.setActive(widgetPageTemplate::getActive);
		}

		if (widgetPageTemplate.getDescription_i18n() != null) {
			existingWidgetPageTemplate.setDescription_i18n(
				widgetPageTemplate::getDescription_i18n);
		}

		if (widgetPageTemplate.getName_i18n() != null) {
			existingWidgetPageTemplate.setName_i18n(
				widgetPageTemplate::getName_i18n);
		}
	}

	private PageTemplate _updatePageTemplate(
			ContentPageTemplate contentPageTemplate,
			LayoutPageTemplateEntry layoutPageTemplateEntry)
		throws Exception {

		return _pageTemplateDTOConverter.toDTO(
			_layoutPageTemplateEntryService.updateLayoutPageTemplateEntry(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
				contentPageTemplate.getName()));
	}

	private PageTemplate _updatePageTemplate(
			LayoutPageTemplateEntry layoutPageTemplateEntry,
			WidgetPageTemplate widgetPageTemplate)
		throws Exception {

		LayoutPrototype layoutPrototype =
			_layoutPrototypeService.getLayoutPrototype(
				layoutPageTemplateEntry.getLayoutPrototypeId());

		Map<Locale, String> nameMap = layoutPrototype.getNameMap();

		if (widgetPageTemplate.getName_i18n() != null) {
			nameMap = LocalizedMapUtil.getLocalizedMap(
				widgetPageTemplate.getName_i18n());
		}

		Map<Locale, String> descriptionMap =
			layoutPrototype.getDescriptionMap();

		if (widgetPageTemplate.getDescription_i18n() != null) {
			descriptionMap = LocalizedMapUtil.getLocalizedMap(
				widgetPageTemplate.getDescription_i18n());
		}

		boolean active = layoutPrototype.isActive();

		if (widgetPageTemplate.getActive() != null) {
			active = widgetPageTemplate.getActive();
		}

		_layoutPrototypeService.updateLayoutPrototype(
			layoutPrototype.getLayoutPrototypeId(), nameMap, descriptionMap,
			active,
			_getServiceContext(
				layoutPageTemplateEntry.getGroupId(), widgetPageTemplate));

		return _pageTemplateDTOConverter.toDTO(
			_layoutPageTemplateEntryLocalService.getLayoutPageTemplateEntry(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId()));
	}

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateCollectionService
		_layoutPageTemplateCollectionService;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private LayoutPageTemplateEntryService _layoutPageTemplateEntryService;

	@Reference
	private LayoutPrototypeService _layoutPrototypeService;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.PageSpecificationDTOConverter)"
	)
	private DTOConverter<Layout, PageSpecification>
		_pageSpecificationDTOConverter;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.PageTemplateDTOConverter)"
	)
	private DTOConverter<LayoutPageTemplateEntry, PageTemplate>
		_pageTemplateDTOConverter;

}