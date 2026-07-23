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
import com.liferay.headless.admin.site.dto.v1_0.ContentPageTemplate;
import com.liferay.headless.admin.site.dto.v1_0.NavigationSettings;
import com.liferay.headless.admin.site.dto.v1_0.PageSpecification;
import com.liferay.headless.admin.site.dto.v1_0.PageTemplate;
import com.liferay.headless.admin.site.dto.v1_0.PageTemplateSet;
import com.liferay.headless.admin.site.dto.v1_0.PageTemplateSettings;
import com.liferay.headless.admin.site.dto.v1_0.WidgetPageSpecification;
import com.liferay.headless.admin.site.dto.v1_0.WidgetPageTemplate;
import com.liferay.headless.admin.site.dto.v1_0.WidgetPageTemplateSettings;
import com.liferay.headless.admin.site.dto.v1_0.util.FileEntryUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.DTOConverterContextUtil;
import com.liferay.headless.admin.site.internal.odata.entity.v1_0.PageTemplateEntityModel;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.LayoutUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.PageSpecificationUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.PageTemplateSetUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.ServiceContextUtil;
import com.liferay.headless.admin.site.internal.util.EnabledUtil;
import com.liferay.headless.admin.site.resource.v1_0.PageTemplateResource;
import com.liferay.headless.common.spi.util.GroupUtil;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.page.template.admin.constants.LayoutPageTemplateAdminPortletKeys;
import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.exception.LayoutPageTemplateEntryLayoutPageTemplateCollectionIdException;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.model.LayoutTypePortletConstants;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutPrototypeService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
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
import java.util.Objects;
import java.util.function.Function;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Rubén Pulido
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/page-template.properties",
	property = "export.import.vulcan.batch.engine.task.item.delegate=true",
	scope = ServiceScope.PROTOTYPE, service = PageTemplateResource.class
)
public class PageTemplateResourceImpl
	extends BasePageTemplateResourceImpl
	implements ExportImportVulcanBatchEngineTaskItemDelegate<PageTemplate> {

	@Override
	public void deleteSitePageTemplate(
			String siteExternalReferenceCode,
			String pageTemplateExternalReferenceCode)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		_layoutPageTemplateEntryService.deleteLayoutPageTemplateEntry(
			pageTemplateExternalReferenceCode,
			GroupUtil.getGroupId(
				true, false, contextCompany.getCompanyId(),
				siteExternalReferenceCode));
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
						LayoutPageTemplateEntryTypeConstants.BASIC;
			}

			@Override
			public String getKey() {
				return LayoutPageTemplateEntry.class.getName() + "-" +
					LayoutPageTemplateEntryTypeConstants.BASIC;
			}

			@Override
			public String getLabelLanguageKey() {
				return "page-templates";
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
	public Page<PageTemplate> getSitePageTemplateSetPageTemplatesPage(
			String siteExternalReferenceCode,
			String pageTemplateSetExternalReferenceCode, Boolean flatten)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		long groupId = GroupUtil.getGroupId(
			true, contextCompany.getCompanyId(), siteExternalReferenceCode);

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionService.
				getLayoutPageTemplateCollection(
					pageTemplateSetExternalReferenceCode, groupId);

		if (!Objects.equals(
				LayoutPageTemplateCollectionTypeConstants.BASIC,
				layoutPageTemplateCollection.getType())) {

			throw new IllegalArgumentException(
				"The page template set must be of type basic");
		}

		return Page.of(
			transform(
				_layoutPageTemplateEntryService.getLayoutPageTemplateEntries(
					groupId,
					layoutPageTemplateCollection.
						getLayoutPageTemplateCollectionId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null),
				this::_toPageTemplate));
	}

	@Override
	public ContentPageSpecification postSitePageTemplatePageSpecification(
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
				LayoutPageTemplateEntryTypeConstants.BASIC,
				layoutPageTemplateEntry.getType())) {

			throw new IllegalArgumentException(
				"The page template must be of type basic");
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
	public PageTemplate postSitePageTemplateSetPageTemplate(
			String siteExternalReferenceCode,
			String pageTemplateSetExternalReferenceCode,
			PageTemplate pageTemplate)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionService.
				getLayoutPageTemplateCollection(
					pageTemplateSetExternalReferenceCode,
					GroupUtil.getStagingAwareGroupId(
						contextCompany.getCompanyId(),
						siteExternalReferenceCode));

		if (!Objects.equals(
				LayoutPageTemplateCollectionTypeConstants.BASIC,
				layoutPageTemplateCollection.getType())) {

			throw new IllegalArgumentException(
				"The page template set must be of type basic");
		}

		return _addPageTemplate(
			layoutPageTemplateCollection.getGroupId(),
			layoutPageTemplateCollection.getLayoutPageTemplateCollectionId(),
			pageTemplate);
	}

	@Override
	protected PageTemplate doGetSitePageTemplate(
			String siteExternalReferenceCode,
			String pageTemplateExternalReferenceCode)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

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

			throw new IllegalArgumentException(
				"The page template must be of type basic or widget");
		}

		return _toPageTemplate(layoutPageTemplateEntry);
	}

	@Override
	protected Page<PageTemplate> doGetSitePageTemplatesPage(
			String siteExternalReferenceCode, String search,
			Aggregation aggregation, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		long groupId = GroupUtil.getGroupId(
			true, true, contextCompany.getCompanyId(),
			siteExternalReferenceCode);

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
							LayoutPageTemplateEntryTypeConstants.BASIC),
						String.valueOf(
							LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE)
					});
				searchContext.setCompanyId(contextCompany.getCompanyId());
				searchContext.setGroupIds(new long[] {groupId});
			},
			sorts,
			document -> _toPageTemplate(
				_layoutPageTemplateEntryService.fetchLayoutPageTemplateEntry(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)))));
	}

	@Override
	protected PageTemplate doPostSitePageTemplate(
			String siteExternalReferenceCode, PageTemplate pageTemplate)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		return _addPageTemplate(
			GroupUtil.getStagingAwareGroupId(
				_isTypeWidgetPageTemplate(pageTemplate),
				contextCompany.getCompanyId(), siteExternalReferenceCode),
			pageTemplate);
	}

	@Override
	protected PageTemplate doPutSitePageTemplate(
			String siteExternalReferenceCode,
			String pageTemplateExternalReferenceCode, PageTemplate pageTemplate)
		throws Exception {

		EnabledUtil.checkEnabled(contextCompany);

		long groupId = GroupUtil.getStagingAwareGroupId(
			_isTypeWidgetPageTemplate(pageTemplate),
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

			throw new IllegalArgumentException(
				"The page template must be of type basic or widget");
		}

		long layoutPageTemplateCollectionId =
			LayoutPageTemplateConstants.
				PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT;

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_getOrAddLayoutPageTemplateCollection(groupId, pageTemplate);

		if (layoutPageTemplateCollection != null) {
			layoutPageTemplateCollectionId =
				layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId();
		}

		if (!Objects.equals(
				layoutPageTemplateEntry.getLayoutPageTemplateCollectionId(),
				layoutPageTemplateCollectionId)) {

			layoutPageTemplateEntry =
				_layoutPageTemplateEntryService.moveLayoutPageTemplateEntry(
					layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
					layoutPageTemplateCollectionId);
		}

		long previewFileEntryId = FileEntryUtil.getPreviewFileEntryId(
			groupId, LayoutAdminPortletKeys.GROUP_PAGES,
			pageTemplate.getThumbnailURLReference(), contextUser.getUserId());

		if (previewFileEntryId !=
				layoutPageTemplateEntry.getPreviewFileEntryId()) {

			layoutPageTemplateEntry =
				_layoutPageTemplateEntryService.updateLayoutPageTemplateEntry(
					layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
					previewFileEntryId);
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
		PageTemplate pageTemplate, PageTemplate existingPageTemplate) {

		if (pageTemplate.getKeywords() != null) {
			existingPageTemplate.setKeywords(pageTemplate::getKeywords);
		}

		if (pageTemplate.getPageSpecifications() != null) {
			existingPageTemplate.setPageSpecifications(
				pageTemplate::getPageSpecifications);
		}

		if (pageTemplate.getPageTemplateSet() != null) {
			existingPageTemplate.setPageTemplateSet(
				pageTemplate::getPageTemplateSet);
		}

		if (pageTemplate.getTaxonomyCategoryBriefs() != null) {
			existingPageTemplate.setTaxonomyCategoryBriefs(
				pageTemplate::getTaxonomyCategoryBriefs);
		}

		if (pageTemplate.getThumbnailURLReference() != null) {
			existingPageTemplate.setThumbnailURLReference(
				pageTemplate::getThumbnailURLReference);
		}

		if (Objects.equals(
				existingPageTemplate.getType(),
				PageTemplate.Type.CONTENT_PAGE_TEMPLATE)) {

			return;
		}

		if (!(existingPageTemplate instanceof WidgetPageTemplate) ||
			!(pageTemplate instanceof WidgetPageTemplate)) {

			throw new IllegalArgumentException(
				"The existing page template must be a widget template");
		}

		_preparePatch(
			(WidgetPageTemplate)existingPageTemplate,
			(WidgetPageTemplate)pageTemplate);
	}

	private PageTemplate _addPageTemplate(
			ContentPageTemplate contentPageTemplate, long groupId,
			long layoutPageTemplateCollectionId)
		throws Exception {

		ServiceContext serviceContext = _getServiceContext(
			groupId, contentPageTemplate);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryService.addLayoutPageTemplateEntry(
				contentPageTemplate.getExternalReferenceCode(), groupId,
				layoutPageTemplateCollectionId, contentPageTemplate.getKey(), 0,
				null, contentPageTemplate.getName(),
				LayoutPageTemplateEntryTypeConstants.BASIC,
				FileEntryUtil.getPreviewFileEntryId(
					groupId, LayoutAdminPortletKeys.GROUP_PAGES,
					contentPageTemplate.getThumbnailURLReference(),
					contextUser.getUserId()),
				false, 0,
				_getLayoutPlid(contentPageTemplate, groupId, serviceContext), 0,
				PageSpecificationUtil.getPublishedStatus(
					contentPageTemplate.getPageSpecifications()),
				serviceContext);

		return _toPageTemplate(layoutPageTemplateEntry);
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

		EnabledUtil.checkAddWidgetPageEnabled(contextCompany);

		if (!((layoutPageTemplateCollectionId ==
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT) ^
			  (groupId != contextCompany.getGroupId()))) {

			throw new LayoutPageTemplateEntryLayoutPageTemplateCollectionIdException();
		}

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

		WidgetPageSpecification widgetPageSpecification =
			PageSpecificationUtil.getWidgetPageSpecification(
				widgetPageTemplate.getPageSpecifications());

		if (widgetPageSpecification != null) {
			if ((widgetPageSpecification.getExternalReferenceCode() != null) &&
				!Objects.equals(
					widgetPageTemplate.getExternalReferenceCode(),
					widgetPageSpecification.getExternalReferenceCode())) {

				throw new IllegalArgumentException(
					"The provided external reference code does not point to " +
						"a widget page specification");
			}

			ServiceContextUtil.setLayoutSetPrototypeLayoutERC(
				serviceContext.getScopeGroupId(), widgetPageSpecification,
				serviceContext,
				widgetPageSpecification.
					getSiteTemplatePageSpecificationExternalReferenceCode());
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

		if (widgetPageTemplate.getUuid() != null) {
			layoutPageTemplateEntry.setUuid(widgetPageTemplate.getUuid());
		}

		if (widgetPageTemplate.getExternalReferenceCode() != null) {
			layoutPageTemplateEntry.setExternalReferenceCode(
				widgetPageTemplate.getExternalReferenceCode());
		}

		layoutPageTemplateEntry.setGroupId(groupId);
		layoutPageTemplateEntry.setLayoutPageTemplateCollectionId(
			layoutPageTemplateCollectionId);

		layoutPageTemplateEntry.setPreviewFileEntryId(
			FileEntryUtil.getPreviewFileEntryId(
				groupId, LayoutAdminPortletKeys.GROUP_PAGES,
				widgetPageTemplate.getThumbnailURLReference(),
				contextUser.getUserId()));

		layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.updateLayoutPageTemplateEntry(
				layoutPageTemplateEntry);

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		LayoutUtil.updatePortletLayout(
			_cetManager, layout, nameMap, layout.getTitleMap(),
			layout.getDescriptionMap(), layout.getKeywordsMap(),
			layout.getRobotsMap(), layout.getFriendlyURLMap(),
			_getWidgetPageTemplateTypeSettingsUnicodeProperties(
				widgetPageTemplate.getPageTemplateSettings()),
			serviceContext, widgetPageSpecification);

		return _toPageTemplate(layoutPageTemplateEntry);
	}

	private PageTemplate _addPageTemplate(
			long groupId, PageTemplate pageTemplate)
		throws Exception {

		long layoutPageTemplateCollectionId =
			LayoutPageTemplateConstants.
				PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT;

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_getOrAddLayoutPageTemplateCollection(groupId, pageTemplate);

		if (layoutPageTemplateCollection != null) {
			layoutPageTemplateCollectionId =
				layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId();
		}

		return _addPageTemplate(
			groupId, layoutPageTemplateCollectionId, pageTemplate);
	}

	private long _getLayoutPlid(
			ContentPageTemplate contentPageTemplate, long groupId,
			ServiceContext serviceContext)
		throws Exception {

		Map<Locale, String> nameMap = Collections.singletonMap(
			_portal.getSiteDefaultLocale(groupId),
			contentPageTemplate.getName());

		serviceContext.setAttribute(
			"layout.instanceable.allowed", Boolean.TRUE);
		serviceContext.setAttribute(
			"layout.page.template.entry.type",
			LayoutPageTemplateEntryTypeConstants.BASIC);

		Layout layout = LayoutUtil.addContentLayout(
			_cetManager, _fragmentEntryProcessorRegistry, groupId,
			_infoItemServiceRegistry,
			contentPageTemplate.getPageSpecifications(), true,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, nameMap, null, null, null,
			null, LayoutConstants.TYPE_CONTENT, null, true, true,
			Collections.emptyMap(), WorkflowConstants.STATUS_APPROVED,
			serviceContext);

		return layout.getPlid();
	}

	private LayoutPageTemplateCollection _getOrAddLayoutPageTemplateCollection(
			long groupId, PageTemplate pageTemplate)
		throws Exception {

		PageTemplateSet pageTemplateSet = pageTemplate.getPageTemplateSet();

		if ((pageTemplateSet == null) ||
			Validator.isNull(pageTemplateSet.getExternalReferenceCode())) {

			return null;
		}

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionService.
				fetchLayoutPageTemplateCollection(
					pageTemplateSet.getExternalReferenceCode(), groupId);

		if (layoutPageTemplateCollection == null) {
			if (!LazyReferencingThreadLocal.isEnabled()) {
				throw new IllegalArgumentException(
					"The provided external reference code does not point to " +
						"a display page template folder");
			}

			layoutPageTemplateCollection =
				PageTemplateSetUtil.addLayoutPageTemplateCollection(
					groupId, contextHttpServletRequest, pageTemplateSet);
		}
		else if (!Objects.equals(
					LayoutPageTemplateCollectionTypeConstants.BASIC,
					layoutPageTemplateCollection.getType())) {

			throw new IllegalArgumentException(
				"The display page template folder must be of type basic");
		}

		return layoutPageTemplateCollection;
	}

	private ServiceContext _getServiceContext(
			long groupId, PageTemplate pageTemplate)
		throws Exception {

		String uuid = null;

		if (Objects.equals(
				pageTemplate.getType(),
				PageTemplate.Type.CONTENT_PAGE_TEMPLATE)) {

			uuid = pageTemplate.getUuid();
		}

		return ServiceContextUtil.createServiceContext(
			contextCompany.getCompanyId(), pageTemplate.getDateCreated(),
			groupId, contextHttpServletRequest, pageTemplate.getKeywords(),
			pageTemplate.getDateModified(),
			pageTemplate.getTaxonomyCategoryBriefs(), contextUser.getUserId(),
			uuid);
	}

	private UnicodeProperties
		_getWidgetPageTemplateTypeSettingsUnicodeProperties(
			PageTemplateSettings pageTemplateSettings) {

		UnicodeProperties unicodeProperties = new UnicodeProperties(true);

		if (pageTemplateSettings == null) {
			unicodeProperties.setProperty(
				LayoutTypePortletConstants.LAYOUT_TEMPLATE_ID,
				PropsValues.DEFAULT_LAYOUT_TEMPLATE_ID);
			unicodeProperties.setProperty("target", StringPool.BLANK);
			unicodeProperties.setProperty("targetType", StringPool.BLANK);

			return unicodeProperties;
		}

		if (!(pageTemplateSettings instanceof
				WidgetPageTemplateSettings widgetPageTemplateSettings)) {

			throw new IllegalArgumentException(
				"The page template settings must be of type widget");
		}

		unicodeProperties.setProperty(
			LayoutTypePortletConstants.LAYOUT_TEMPLATE_ID,
			GetterUtil.getString(
				widgetPageTemplateSettings.getLayoutTemplateId(),
				PropsValues.DEFAULT_LAYOUT_TEMPLATE_ID));

		NavigationSettings navigationSettings =
			widgetPageTemplateSettings.getNavigationSettings();

		if (navigationSettings != null) {
			unicodeProperties.setProperty(
				"target", navigationSettings.getTarget());

			if (Objects.equals(
					navigationSettings.getTargetType(),
					NavigationSettings.TargetType.NEW_TAB)) {

				unicodeProperties.setProperty("targetType", "useNewTab");
			}
			else {
				unicodeProperties.setProperty("targetType", StringPool.BLANK);
			}
		}
		else {
			unicodeProperties.setProperty("target", StringPool.BLANK);
			unicodeProperties.setProperty("targetType", StringPool.BLANK);
		}

		return unicodeProperties;
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

		if (widgetPageTemplate.getPageTemplateSettings() != null) {
			existingWidgetPageTemplate.setPageTemplateSettings(
				widgetPageTemplate::getPageTemplateSettings);
		}
	}

	private PageTemplate _toPageTemplate(
			LayoutPageTemplateEntry layoutPageTemplateEntry)
		throws Exception {

		return _pageTemplateDTOConverter.toDTO(
			DTOConverterContextUtil.getDTOConverterContext(
				contextAcceptLanguage, _dtoConverterRegistry,
				contextHttpServletRequest,
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
				contextUriInfo, contextUser),
			layoutPageTemplateEntry);
	}

	private PageTemplate _updatePageTemplate(
			ContentPageTemplate contentPageTemplate,
			LayoutPageTemplateEntry layoutPageTemplateEntry)
		throws Exception {

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		ServiceContext serviceContext = _getServiceContext(
			layoutPageTemplateEntry.getGroupId(), contentPageTemplate);

		layout = LayoutUtil.updateContentLayout(
			_cetManager, _fragmentEntryProcessorRegistry,
			_infoItemServiceRegistry, layout, layout.getNameMap(),
			layout.getTitleMap(), layout.getDescriptionMap(),
			layout.getKeywordsMap(), layout.getRobotsMap(),
			layout.getFriendlyURLMap(), layout.getTypeSettingsProperties(),
			contentPageTemplate.getPageSpecifications(), serviceContext);

		if (layout.isPublished() && !layoutPageTemplateEntry.isApproved()) {
			_layoutPageTemplateEntryService.updateStatus(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
				WorkflowConstants.STATUS_APPROVED);
		}

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		try {
			return _toPageTemplate(
				_layoutPageTemplateEntryService.updateLayoutPageTemplateEntry(
					layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
					contentPageTemplate.getName()));
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
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

		ServiceContext serviceContext = _getServiceContext(
			layoutPageTemplateEntry.getGroupId(), widgetPageTemplate);

		_layoutPrototypeService.updateLayoutPrototype(
			layoutPrototype.getLayoutPrototypeId(), nameMap, descriptionMap,
			active, serviceContext);

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		LayoutUtil.updatePortletLayout(
			_cetManager, layout, nameMap, layout.getTitleMap(),
			layout.getDescriptionMap(), layout.getKeywordsMap(),
			layout.getRobotsMap(), layout.getFriendlyURLMap(),
			_getWidgetPageTemplateTypeSettingsUnicodeProperties(
				widgetPageTemplate.getPageTemplateSettings()),
			serviceContext,
			PageSpecificationUtil.getWidgetPageSpecification(
				widgetPageTemplate.getPageSpecifications()));

		return _toPageTemplate(
			_layoutPageTemplateEntryService.getLayoutPageTemplateEntry(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId()));
	}

	private static final EntityModel _entityModel =
		new PageTemplateEntityModel();

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

	@Reference
	private Portal _portal;

}