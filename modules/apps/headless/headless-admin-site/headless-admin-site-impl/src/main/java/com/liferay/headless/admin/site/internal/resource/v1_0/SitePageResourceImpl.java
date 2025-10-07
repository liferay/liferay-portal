/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0;

import com.liferay.client.extension.type.manager.CETManager;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLFileEntryService;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.vulcan.batch.engine.ExportImportVulcanBatchEngineTaskItemDelegate;
import com.liferay.headless.admin.site.dto.v1_0.ContentPageSettings;
import com.liferay.headless.admin.site.dto.v1_0.ContentPageSpecification;
import com.liferay.headless.admin.site.dto.v1_0.CustomMetaTag;
import com.liferay.headless.admin.site.dto.v1_0.ItemExternalReference;
import com.liferay.headless.admin.site.dto.v1_0.NavigationSettings;
import com.liferay.headless.admin.site.dto.v1_0.OpenGraphSettings;
import com.liferay.headless.admin.site.dto.v1_0.PageSettings;
import com.liferay.headless.admin.site.dto.v1_0.PageSpecification;
import com.liferay.headless.admin.site.dto.v1_0.SEOSettings;
import com.liferay.headless.admin.site.dto.v1_0.SitePage;
import com.liferay.headless.admin.site.dto.v1_0.SitemapSettings;
import com.liferay.headless.admin.site.dto.v1_0.WidgetPageSettings;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.SitePageTypeUtil;
import com.liferay.headless.admin.site.internal.odata.entity.v1_0.SitePageEntityModel;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.GroupUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.LayoutUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.PageSpecificationUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.ServiceContextUtil;
import com.liferay.headless.admin.site.resource.v1_0.SitePageResource;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.admin.kernel.model.LayoutTypePortletConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.seo.model.LayoutSEOEntryCustomMetaTagProperty;
import com.liferay.layout.seo.service.LayoutSEOEntryService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CustomizedPages;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.TermFilter;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.validation.ValidationException;

import jakarta.ws.rs.core.MultivaluedMap;

import java.io.Serializable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Rubén Pulido
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/site-page.properties",
	property = "export.import.vulcan.batch.engine.task.item.delegate=true",
	scope = ServiceScope.PROTOTYPE, service = SitePageResource.class
)
public class SitePageResourceImpl
	extends BaseSitePageResourceImpl
	implements ExportImportVulcanBatchEngineTaskItemDelegate<SitePage> {

	@Override
	public void deleteSiteSitePage(
			String siteExternalReferenceCode,
			String sitePageExternalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-35443")) {
			throw new UnsupportedOperationException();
		}

		Layout layout = _layoutService.getLayoutByExternalReferenceCode(
			sitePageExternalReferenceCode,
			GroupUtil.getGroupId(
				false, contextCompany.getCompanyId(),
				siteExternalReferenceCode));

		_validateSitePageLayout(layout);

		_layoutService.deleteLayout(
			layout.getPlid(),
			ServiceContextUtil.createServiceContext(
				layout.getGroupId(), contextHttpServletRequest,
				contextUser.getUserId()));
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	public ExportImportDescriptor getExportImportDescriptor() {
		return new ExportImportDescriptor() {

			@Override
			public String getItemClassName() {
				return Layout.class.getName();
			}

			@Override
			public Map<String, Serializable> getParameters(
				PortletDataContext portletDataContext) {

				return HashMapBuilder.<String, Serializable>put(
					"filter",
					() -> {
						if ((portletDataContext.getLayoutIds() == null) ||
							(portletDataContext.getLayoutIds().length == 0) ||
							((portletDataContext.getLayoutIds().length == 1) &&
							 (portletDataContext.getLayoutIds()[0] == 0))) {

							return null;
						}

						Set<String> layoutExternalReferenceCodes =
							new HashSet<>();

						for (long layoutId :
								portletDataContext.getLayoutIds()) {

							Layout layout = null;

							try {
								layout = _layoutService.fetchLayout(
									portletDataContext.getScopeGroupId(),
									portletDataContext.isPrivateLayout(),
									layoutId);
							}
							catch (PortalException portalException) {
								if (_log.isWarnEnabled()) {
									_log.warn(portalException);
								}
							}

							if (layout != null) {
								layoutExternalReferenceCodes.add(
									layout.getExternalReferenceCode());
							}
						}

						StringBundler sb = new StringBundler(3);

						sb.append("externalReferenceCode in ('");

						sb.append(
							ListUtil.toString(
								ListUtil.fromCollection(
									layoutExternalReferenceCodes),
								StringPool.BLANK, "', '"));

						sb.append("')");

						return sb.toString();
					}
				).build();
			}

			@Override
			public String getPortletId() {
				return LayoutAdminPortletKeys.LAYOUT_SET_LAYOUTS;
			}

			@Override
			public Scope getScope() {
				return Scope.SITE;
			}

			@Override
			public boolean isActive(PortletDataContext portletDataContext) {
				if (!portletDataContext.isPrivateLayout() &&
					FeatureFlagManagerUtil.isEnabled("LPD-35443")) {

					return true;
				}

				return false;
			}

		};
	}

	@Override
	public ContentPageSpecification postSiteSitePagePageSpecification(
			String siteExternalReferenceCode,
			String sitePageExternalReferenceCode,
			ContentPageSpecification contentPageSpecification)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-35443")) {
			throw new UnsupportedOperationException();
		}

		Layout layout = _layoutService.getLayoutByExternalReferenceCode(
			sitePageExternalReferenceCode,
			GroupUtil.getGroupId(
				false, contextCompany.getCompanyId(),
				siteExternalReferenceCode));

		if (!layout.isTypeContent()) {
			throw new UnsupportedOperationException();
		}

		return (ContentPageSpecification)_pageSpecificationDTOConverter.toDTO(
			LayoutUtil.addDraftToLayout(
				_cetManager, contentPageSpecification, _infoItemServiceRegistry,
				layout,
				ServiceContextUtil.createServiceContext(
					layout.getGroupId(), contextHttpServletRequest,
					contextUser.getUserId())));
	}

	@Override
	public Page<SitePage> read(
			Filter filter, Pagination pagination, Sort[] sorts,
			Map<String, Serializable> parameters, String search)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-35443")) {
			throw new UnsupportedOperationException();
		}

		return super.read(filter, pagination, sorts, parameters, search);
	}

	@Override
	protected SitePage doGetSiteSitePage(
			String siteExternalReferenceCode,
			String sitePageExternalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-35443")) {
			throw new UnsupportedOperationException();
		}

		Layout layout = _layoutService.getLayoutByExternalReferenceCode(
			sitePageExternalReferenceCode,
			GroupUtil.getGroupId(
				true, contextCompany.getCompanyId(),
				siteExternalReferenceCode));

		_validateSitePageLayout(layout);

		return _toSitePage(layout);
	}

	@Override
	protected Page<SitePage> doGetSiteSitePagesPage(
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
			null,
			booleanQuery -> {
				BooleanFilter booleanFilter =
					booleanQuery.getPreBooleanFilter();

				booleanFilter.add(
					new TermFilter(Field.GROUP_ID, String.valueOf(groupId)),
					BooleanClauseOccur.MUST);
			},
			filter, Layout.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.addVulcanAggregation(aggregation);
				searchContext.setAttribute(Field.TITLE, search);
				searchContext.setAttribute(
					Field.TYPE,
					new String[] {
						LayoutConstants.TYPE_CONTENT,
						LayoutConstants.TYPE_PORTLET
					});
				searchContext.setAttribute(
					"privateLayout", Boolean.FALSE.toString());
				searchContext.setAttribute(
					"status", WorkflowConstants.STATUS_ANY);
				searchContext.setAttribute(
					"systemLayout", Boolean.FALSE.toString());
				searchContext.setCompanyId(contextCompany.getCompanyId());
				searchContext.setGroupIds(new long[] {groupId});
				searchContext.setKeywords(search);
			},
			sorts,
			document -> {
				long plid = GetterUtil.getLong(
					document.get(Field.ENTRY_CLASS_PK));

				return _toSitePage(_layoutLocalService.getLayout(plid));
			});
	}

	@Override
	protected SitePage doPostSiteSitePage(
			String siteExternalReferenceCode, SitePage sitePage)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-35443")) {
			throw new UnsupportedOperationException();
		}

		return _toSitePage(
			_addLayout(
				sitePage.getExternalReferenceCode(),
				GroupUtil.getGroupId(
					false, contextCompany.getCompanyId(),
					siteExternalReferenceCode),
				sitePage));
	}

	@Override
	protected SitePage doPutSiteSitePage(
			String siteExternalReferenceCode,
			String sitePageExternalReferenceCode, SitePage sitePage)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-35443")) {
			throw new UnsupportedOperationException();
		}

		long groupId = GroupUtil.getGroupId(
			false, contextCompany.getCompanyId(), siteExternalReferenceCode);

		Layout layout = _layoutService.fetchLayoutByExternalReferenceCode(
			sitePageExternalReferenceCode, groupId);

		if (layout == null) {
			return _toSitePage(
				_addLayout(sitePageExternalReferenceCode, groupId, sitePage));
		}

		_validateSitePageLayout(layout);

		if ((sitePage.getType() != null) &&
			!Objects.equals(
				layout.getType(),
				SitePageTypeUtil.toInternalType(sitePage.getType()))) {

			throw new UnsupportedOperationException();
		}

		return _toSitePage(_updateLayout(layout, sitePage));
	}

	@Override
	protected Long getPermissionCheckerResourceId(
			String groupExternalReferenceCode, String externalReferenceCode)
		throws Exception {

		Layout layout = _layoutService.getLayoutByExternalReferenceCode(
			externalReferenceCode,
			getPermissionCheckerGroupId(groupExternalReferenceCode));

		return layout.getPrimaryKey();
	}

	@Override
	protected String getPermissionCheckerResourceName(
			String groupExternalReferenceCode, String externalReferenceCode)
		throws Exception {

		return Layout.class.getName();
	}

	@Override
	protected void preparePatch(SitePage sitePage, SitePage existingSitePage) {
		if (sitePage.getPageSettings() != null) {
			existingSitePage.setPageSettings(sitePage::getPageSettings);
		}

		if (sitePage.getPageSpecifications() != null) {
			existingSitePage.setPageSpecifications(
				sitePage::getPageSpecifications);
		}
	}

	private Layout _addLayout(
			String externalReferenceCode, long groupId, SitePage sitePage)
		throws Exception {

		if (sitePage.getExternalReferenceCode() == null) {
			sitePage.setExternalReferenceCode(() -> externalReferenceCode);
		}

		if (!Objects.equals(
				externalReferenceCode, sitePage.getExternalReferenceCode())) {

			throw new UnsupportedOperationException();
		}

		ServiceContext serviceContext = ServiceContextUtil.createServiceContext(
			sitePage.getTaxonomyCategoryItemExternalReferences(),
			contextCompany.getCompanyId(), sitePage.getDateCreated(), groupId,
			contextHttpServletRequest, sitePage.getKeywords(),
			sitePage.getDateModified(), contextUser.getUserId(),
			sitePage.getUuid(), sitePage.getPageSettings());

		_validatePageSpecificationExternalReferenceCode(
			serviceContext, sitePage);

		Map<Locale, String> nameMap = LocalizedMapUtil.getLocalizedMap(
			sitePage.getName_i18n());

		Map<Locale, String> titleMap = new HashMap<>();
		Map<Locale, String> descriptionMap = new HashMap<>();
		Map<Locale, String> keywordsMap = new HashMap<>();
		Map<Locale, String> robotsMap = new HashMap<>();

		if (sitePage.getPageSettings() != null) {
			PageSettings pageSettings = sitePage.getPageSettings();

			if (pageSettings.getSeoSettings() != null) {
				SEOSettings seoSettings = pageSettings.getSeoSettings();

				titleMap = LocalizedMapUtil.getLocalizedMap(
					seoSettings.getHtmlTitle_i18n());
				descriptionMap = LocalizedMapUtil.getLocalizedMap(
					seoSettings.getDescription_i18n());
				keywordsMap = LocalizedMapUtil.getLocalizedMap(
					seoSettings.getSeoKeywords_i18n());
				robotsMap = LocalizedMapUtil.getLocalizedMap(
					seoSettings.getRobots_i18n());
			}
		}

		UnicodeProperties typeSettingsUnicodeProperties =
			_getTypeSettingsUnicodeProperties(sitePage);

		Layout layout = null;

		if (Objects.equals(sitePage.getType(), SitePage.Type.CONTENT_PAGE)) {
			layout = LayoutUtil.addContentLayout(
				_cetManager, groupId, _infoItemServiceRegistry,
				sitePage.getPageSpecifications(),
				_getParentLayoutId(
					LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, groupId,
					sitePage.getParentSitePageExternalReferenceCode(),
					serviceContext),
				false, nameMap, titleMap, descriptionMap, keywordsMap,
				robotsMap, SitePageTypeUtil.toInternalType(sitePage.getType()),
				typeSettingsUnicodeProperties,
				_isHiddenFromNavigation(false, sitePage.getPageSettings()),
				false,
				LocalizedMapUtil.getLocalizedMap(
					sitePage.getFriendlyUrlPath_i18n()),
				WorkflowConstants.STATUS_APPROVED, serviceContext);
		}
		else {
			layout = LayoutUtil.addPortletLayout(
				_cetManager, sitePage.getExternalReferenceCode(),
				_infoItemServiceRegistry, groupId,
				_getParentLayoutId(
					LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, groupId,
					sitePage.getParentSitePageExternalReferenceCode(),
					serviceContext),
				nameMap, titleMap, descriptionMap, keywordsMap, robotsMap,
				typeSettingsUnicodeProperties,
				_isHiddenFromNavigation(false, sitePage.getPageSettings()),
				LocalizedMapUtil.getLocalizedMap(
					sitePage.getFriendlyUrlPath_i18n()),
				serviceContext,
				PageSpecificationUtil.getWidgetPageSpecification(
					sitePage.getPageSpecifications()));
		}

		PageSettings pageSettings = sitePage.getPageSettings();

		_updateSEOEntry(
			layout.getGroupId(), layout.getLayoutId(), pageSettings,
			serviceContext);

		if ((pageSettings != null) && (pageSettings.getPriority() != null)) {
			layout = _layoutService.updatePriority(
				layout.getPlid(), pageSettings.getPriority());
		}

		return layout;
	}

	private long _getFileEntryId(
			ItemExternalReference itemExternalReference,
			ServiceContext serviceContext)
		throws Exception {

		long groupId = serviceContext.getScopeGroupId();

		com.liferay.headless.admin.site.dto.v1_0.Scope scope =
			itemExternalReference.getScope();

		if (scope != null) {
			groupId = GroupUtil.getGroupId(
				true, true, serviceContext.getCompanyId(),
				scope.getExternalReferenceCode());
		}

		DLFileEntry dlFileEntry =
			_dlFileEntryService.fetchFileEntryByExternalReferenceCode(
				groupId, itemExternalReference.getExternalReferenceCode());

		if (dlFileEntry == null) {
			throw new UnsupportedOperationException();
		}

		return dlFileEntry.getFileEntryId();
	}

	private long _getParentLayoutId(
			long defaultParentLayoutId, long groupId,
			String parentSitePageExternalReferenceCode,
			ServiceContext serviceContext)
		throws Exception {

		if (parentSitePageExternalReferenceCode == null) {
			return defaultParentLayoutId;
		}

		if (Validator.isNull(parentSitePageExternalReferenceCode)) {
			return LayoutConstants.DEFAULT_PARENT_LAYOUT_ID;
		}

		Layout layout = _layoutService.getOrAddEmptyLayout(
			parentSitePageExternalReferenceCode, groupId, serviceContext);

		return layout.getLayoutId();
	}

	private UnicodeProperties _getTypeSettingsUnicodeProperties(
		SitePage sitePage) {

		PageSettings pageSettings = sitePage.getPageSettings();

		if (pageSettings == null) {
			return null;
		}

		if ((sitePage.getType() == SitePage.Type.CONTENT_PAGE) &&
			!(pageSettings instanceof ContentPageSettings)) {

			throw new UnsupportedOperationException();
		}

		if ((sitePage.getType() == SitePage.Type.WIDGET_PAGE) &&
			!(pageSettings instanceof WidgetPageSettings)) {

			throw new UnsupportedOperationException();
		}

		String target = StringPool.BLANK;
		String targetTypeString = StringPool.BLANK;
		NavigationSettings navigationSettings =
			pageSettings.getNavigationSettings();

		if (navigationSettings != null) {
			target = navigationSettings.getTarget();

			NavigationSettings.TargetType targetType =
				navigationSettings.getTargetType();

			if (targetType == NavigationSettings.TargetType.NEW_TAB) {
				targetTypeString = "useNewTab";
			}
		}

		SitemapSettings.ChangeFrequency changeFrequency =
			SitemapSettings.ChangeFrequency.DAILY;
		String sitemapInclude = "1";
		String sitemapIncludeChildLayouts = "true";
		String sitemapPagePriority = "0.0";
		SEOSettings seoSettings = pageSettings.getSeoSettings();

		if (seoSettings != null) {
			SitemapSettings sitemapSettings = seoSettings.getSitemapSettings();

			if (sitemapSettings != null) {
				if (sitemapSettings.getChangeFrequency() != null) {
					changeFrequency = sitemapSettings.getChangeFrequency();
				}

				if (Boolean.FALSE.equals(sitemapSettings.getInclude())) {
					sitemapInclude = "0";
				}

				if (Boolean.FALSE.equals(
						sitemapSettings.getIncludeChildSitePages())) {

					sitemapIncludeChildLayouts = "false";
				}

				if (sitemapSettings.getPagePriority() != null) {
					sitemapPagePriority = String.valueOf(
						sitemapSettings.getPagePriority());
				}
			}
		}

		UnicodePropertiesBuilder.UnicodePropertiesWrapper
			unicodePropertiesWrapper = UnicodePropertiesBuilder.create(
				true
			).setProperty(
				LayoutTypePortletConstants.QUERY_STRING,
				GetterUtil.getString(pageSettings.getQueryString())
			).setProperty(
				LayoutTypePortletConstants.SITEMAP_CHANGEFREQ,
				StringUtil.toLowerCase(changeFrequency.getValue())
			).setProperty(
				LayoutTypePortletConstants.SITEMAP_INCLUDE, sitemapInclude
			).setProperty(
				LayoutTypePortletConstants.SITEMAP_PRIORITY, sitemapPagePriority
			).setProperty(
				LayoutTypePortletConstants.TARGET, target
			).setProperty(
				"sitemap-include-child-layouts", sitemapIncludeChildLayouts
			).setProperty(
				"targetType", targetTypeString
			);

		if (sitePage.getType() == SitePage.Type.CONTENT_PAGE) {
			return unicodePropertiesWrapper.build();
		}

		WidgetPageSettings widgetPageSettings =
			(WidgetPageSettings)pageSettings;

		unicodePropertiesWrapper.setProperty(
			LayoutConstants.CUSTOMIZABLE_LAYOUT,
			String.valueOf(
				GetterUtil.getBoolean(widgetPageSettings.getCustomizable())));

		if (widgetPageSettings.getLayoutTemplateId() != null) {
			unicodePropertiesWrapper.setProperty(
				LayoutTypePortletConstants.LAYOUT_TEMPLATE_ID,
				widgetPageSettings.getLayoutTemplateId());
		}

		String[] customizableSectionIds =
			widgetPageSettings.getCustomizableSectionIds();

		if (ArrayUtil.isEmpty(customizableSectionIds)) {
			return unicodePropertiesWrapper.build();
		}

		for (String customizableSectionId : customizableSectionIds) {
			unicodePropertiesWrapper.setProperty(
				CustomizedPages.namespaceColumnId(customizableSectionId),
				"true");
		}

		return unicodePropertiesWrapper.build();
	}

	private boolean _isHiddenFromNavigation(
		boolean defaultValue, PageSettings pageSettings) {

		if (pageSettings == null) {
			return defaultValue;
		}

		return GetterUtil.getBoolean(
			pageSettings.getHiddenFromNavigation(), defaultValue);
	}

	private SitePage _toSitePage(Layout layout) throws Exception {
		return _sitePageDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(), null,
				_dtoConverterRegistry, contextHttpServletRequest,
				layout.getPlid(), contextAcceptLanguage.getPreferredLocale(),
				contextUriInfo, contextUser),
			layout);
	}

	private Layout _updateLayout(Layout layout, SitePage sitePage)
		throws Exception {

		Map<Locale, String> nameMap = layout.getNameMap();

		if (sitePage.getName_i18n() != null) {
			nameMap = LocalizedMapUtil.getLocalizedMap(sitePage.getName_i18n());
		}

		Map<Locale, String> titleMap = new HashMap<>();
		Map<Locale, String> descriptionMap = new HashMap<>();
		Map<Locale, String> keywordsMap = new HashMap<>();
		Map<Locale, String> robotsMap = new HashMap<>();

		if (sitePage.getPageSettings() != null) {
			PageSettings pageSettings = sitePage.getPageSettings();

			if (pageSettings.getSeoSettings() != null) {
				SEOSettings seoSettings = pageSettings.getSeoSettings();

				titleMap = LocalizedMapUtil.getLocalizedMap(
					seoSettings.getHtmlTitle_i18n());
				descriptionMap = LocalizedMapUtil.getLocalizedMap(
					seoSettings.getDescription_i18n());
				keywordsMap = LocalizedMapUtil.getLocalizedMap(
					seoSettings.getSeoKeywords_i18n());
				robotsMap = LocalizedMapUtil.getLocalizedMap(
					seoSettings.getRobots_i18n());
			}
		}

		Map<Locale, String> friendlyURLMap = layout.getFriendlyURLMap();

		if (sitePage.getFriendlyUrlPath_i18n() != null) {
			friendlyURLMap = LocalizedMapUtil.getLocalizedMap(
				sitePage.getFriendlyUrlPath_i18n());
		}

		ServiceContext serviceContext = ServiceContextUtil.createServiceContext(
			sitePage.getTaxonomyCategoryItemExternalReferences(),
			contextCompany.getCompanyId(), sitePage.getDateCreated(),
			layout.getGroupId(), contextHttpServletRequest,
			sitePage.getKeywords(), sitePage.getDateModified(),
			contextUser.getUserId(), sitePage.getUuid(),
			sitePage.getPageSettings());

		serviceContext.setAttribute(
			"hidden",
			_isHiddenFromNavigation(
				layout.isHidden(), sitePage.getPageSettings()));
		serviceContext.setAttribute(
			"parentLayoutId",
			_getParentLayoutId(
				layout.getParentLayoutId(), layout.getGroupId(),
				sitePage.getParentSitePageExternalReferenceCode(),
				serviceContext));

		if (Objects.equals(sitePage.getType(), SitePage.Type.CONTENT_PAGE)) {
			layout = LayoutUtil.updateContentLayout(
				_cetManager, _infoItemServiceRegistry, layout, nameMap,
				titleMap, descriptionMap, keywordsMap, robotsMap,
				friendlyURLMap, _getTypeSettingsUnicodeProperties(sitePage),
				sitePage.getPageSpecifications(), serviceContext);
		}
		else {
			layout = LayoutUtil.updatePortletLayout(
				_cetManager, layout, nameMap, titleMap, descriptionMap,
				keywordsMap, robotsMap, friendlyURLMap,
				_getTypeSettingsUnicodeProperties(sitePage), serviceContext,
				PageSpecificationUtil.getWidgetPageSpecification(
					sitePage.getPageSpecifications()));
		}

		PageSettings pageSettings = sitePage.getPageSettings();

		_updateSEOEntry(
			layout.getGroupId(), layout.getLayoutId(), pageSettings,
			serviceContext);

		int priority = Integer.MAX_VALUE;

		if ((pageSettings != null) && (pageSettings.getPriority() != null)) {
			priority = pageSettings.getPriority();
		}

		if (layout.getPriority() == priority) {
			return layout;
		}

		return _layoutService.updatePriority(layout.getPlid(), priority);
	}

	private void _updateSEOEntry(
			long groupId, long layoutId, PageSettings pageSettings,
			ServiceContext serviceContext)
		throws Exception {

		boolean canonicalURLEnabled = false;

		Map<Locale, String> canonicalURLMap = new HashMap<>();

		if ((pageSettings != null) && (pageSettings.getSeoSettings() != null)) {
			SEOSettings seoSettings = pageSettings.getSeoSettings();

			canonicalURLMap = LocalizedMapUtil.getLocalizedMap(
				seoSettings.getCustomCanonicalURL_i18n());

			if (MapUtil.isNotEmpty(canonicalURLMap)) {
				canonicalURLEnabled = true;
			}
		}

		boolean openGraphDescriptionEnabled = false;
		Map<Locale, String> openGraphDescriptionMap = new HashMap<>();
		Map<Locale, String> openGraphImageAltMap = new HashMap<>();
		long openGraphImageFileEntryId = 0;
		boolean openGraphTitleEnabled = false;
		Map<Locale, String> openGraphTitleMap = new HashMap<>();

		if ((pageSettings != null) &&
			(pageSettings.getOpenGraphSettings() != null)) {

			OpenGraphSettings openGraphSettings =
				pageSettings.getOpenGraphSettings();

			openGraphDescriptionMap = LocalizedMapUtil.getLocalizedMap(
				openGraphSettings.getDescription_i18n());

			if (MapUtil.isNotEmpty(openGraphDescriptionMap)) {
				openGraphDescriptionEnabled = true;
			}

			openGraphImageAltMap = LocalizedMapUtil.getLocalizedMap(
				openGraphSettings.getImageAlt_i18n());

			ItemExternalReference itemExternalReference =
				openGraphSettings.getImage();

			if (itemExternalReference != null) {
				openGraphImageFileEntryId = _getFileEntryId(
					itemExternalReference, serviceContext);
			}

			openGraphTitleMap = LocalizedMapUtil.getLocalizedMap(
				openGraphSettings.getTitle_i18n());

			if (MapUtil.isNotEmpty(openGraphTitleMap)) {
				openGraphTitleEnabled = true;
			}
		}

		_layoutSEOEntryService.updateLayoutSEOEntry(
			groupId, false, layoutId, canonicalURLEnabled, canonicalURLMap,
			openGraphDescriptionEnabled, openGraphDescriptionMap,
			openGraphImageAltMap, openGraphImageFileEntryId,
			openGraphTitleEnabled, openGraphTitleMap, serviceContext);

		CustomMetaTag[] customMetaTags = new CustomMetaTag[0];

		if (pageSettings != null) {
			customMetaTags = pageSettings.getCustomMetaTags();
		}

		_layoutSEOEntryService.updateCustomMetaTags(
			groupId, false, layoutId,
			transformToList(
				customMetaTags,
				customMetaTag -> new LayoutSEOEntryCustomMetaTagProperty(
					LocalizedMapUtil.getLocalizedMap(
						customMetaTag.getValue_i18n()),
					customMetaTag.getKey())),
			serviceContext);
	}

	private void _validatePageSpecificationExternalReferenceCode(
		ServiceContext serviceContext, SitePage sitePage) {

		PageSpecification[] pageSpecifications =
			sitePage.getPageSpecifications();

		if (ArrayUtil.isEmpty(pageSpecifications)) {
			serviceContext.setAttribute(
				"layoutExternalReferenceCode",
				sitePage.getExternalReferenceCode());

			return;
		}

		PageSpecification publishedPageSpecification = null;

		if (Objects.equals(sitePage.getType(), SitePage.Type.CONTENT_PAGE) &&
			(pageSpecifications.length == 2)) {

			ContentPageSpecification publishedContentPageSpecification =
				(ContentPageSpecification)pageSpecifications[0];

			if (Validator.isNull(
					publishedContentPageSpecification.
						getDraftContentPageSpecificationExternalReferenceCode())) {

				publishedContentPageSpecification =
					(ContentPageSpecification)pageSpecifications[1];
			}

			publishedPageSpecification = publishedContentPageSpecification;
		}
		else if (Objects.equals(
					sitePage.getType(), SitePage.Type.WIDGET_PAGE) &&
				 (pageSpecifications.length == 1)) {

			publishedPageSpecification = pageSpecifications[0];
		}
		else {
			throw new UnsupportedOperationException();
		}

		if ((publishedPageSpecification.getExternalReferenceCode() != null) &&
			!Objects.equals(
				sitePage.getExternalReferenceCode(),
				publishedPageSpecification.getExternalReferenceCode())) {

			throw new ValidationException(
				StringBundler.concat(
					"Site page external reference code ",
					sitePage.getExternalReferenceCode(),
					" does not match published page specification external ",
					"reference code ",
					publishedPageSpecification.getExternalReferenceCode()));
		}

		publishedPageSpecification.setExternalReferenceCode(
			sitePage::getExternalReferenceCode);
	}

	private void _validateSitePageLayout(Layout layout) {
		if (layout.isDraftLayout() || layout.isTypeAssetDisplay() ||
			layout.isTypeUtility()) {

			throw new UnsupportedOperationException();
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByPlid(layout.getPlid());

		if (layoutPageTemplateEntry != null) {
			throw new UnsupportedOperationException();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SitePageResourceImpl.class);

	private static final EntityModel _entityModel = new SitePageEntityModel();

	@Reference
	private CETManager _cetManager;

	@Reference
	private DLFileEntryService _dlFileEntryService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private LayoutSEOEntryService _layoutSEOEntryService;

	@Reference
	private LayoutService _layoutService;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.PageSpecificationDTOConverter)"
	)
	private DTOConverter<Layout, PageSpecification>
		_pageSpecificationDTOConverter;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.SitePageDTOConverter)"
	)
	private DTOConverter<Layout, SitePage> _sitePageDTOConverter;

}