/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.resource.v1_0;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.friendly.url.model.FriendlyURLEntryLocalization;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.headless.common.spi.service.context.ServiceContextBuilder;
import com.liferay.headless.delivery.dto.v1_0.ContentDocument;
import com.liferay.headless.delivery.dto.v1_0.CustomMetaTag;
import com.liferay.headless.delivery.dto.v1_0.OpenGraphSettings;
import com.liferay.headless.delivery.dto.v1_0.PageDefinition;
import com.liferay.headless.delivery.dto.v1_0.PageElement;
import com.liferay.headless.delivery.dto.v1_0.PagePermission;
import com.liferay.headless.delivery.dto.v1_0.PageSettings;
import com.liferay.headless.delivery.dto.v1_0.ParentSitePage;
import com.liferay.headless.delivery.dto.v1_0.SEOSettings;
import com.liferay.headless.delivery.dto.v1_0.Settings;
import com.liferay.headless.delivery.dto.v1_0.SiteMapSettings;
import com.liferay.headless.delivery.dto.v1_0.SitePage;
import com.liferay.headless.delivery.dto.v1_0.SitePageNavigationMenuSettings;
import com.liferay.headless.delivery.dto.v1_0.TaxonomyCategoryBrief;
import com.liferay.headless.delivery.dto.v1_0.TaxonomyCategoryReference;
import com.liferay.headless.delivery.internal.odata.entity.v1_0.SitePageEntityModel;
import com.liferay.headless.delivery.resource.v1_0.SitePageResource;
import com.liferay.layout.admin.kernel.model.LayoutTypePortletConstants;
import com.liferay.layout.constants.LayoutTypeSettingsConstants;
import com.liferay.layout.importer.LayoutsImporter;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.seo.model.LayoutSEOEntryCustomMetaTagProperty;
import com.liferay.layout.seo.service.LayoutSEOEntryService;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.events.ServicePreAction;
import com.liferay.portal.events.ThemeServicePreAction;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Team;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.TermFilter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.TeamLocalService;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.service.permission.ModelPermissionsFactory;
import com.liferay.portal.kernel.servlet.DummyHttpServletResponse;
import com.liferay.portal.kernel.servlet.DynamicServletRequest;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.theme.ThemeUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.JaxRsLinkUtil;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.portal.vulcan.util.SearchUtil;
import com.liferay.segments.SegmentsEntryRetriever;
import com.liferay.segments.constants.SegmentsExperienceConstants;
import com.liferay.segments.constants.SegmentsWebKeys;
import com.liferay.segments.context.RequestContextMapper;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.processor.SegmentsExperienceRequestProcessorRegistry;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.segments.service.SegmentsExperienceService;

import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/site-page.properties",
	scope = ServiceScope.PROTOTYPE, service = SitePageResource.class
)
public class SitePageResourceImpl extends BaseSitePageResourceImpl {

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public SitePage getSiteSitePage(Long siteId, String friendlyUrlPath)
		throws Exception {

		return _toSitePage(true, _getLayout(siteId, friendlyUrlPath), null);
	}

	@Override
	public SitePage getSiteSitePageExperienceExperienceKey(
			Long siteId, String friendlyUrlPath, String experienceKey)
		throws Exception {

		return _toSitePage(
			true, _getLayout(siteId, friendlyUrlPath), experienceKey);
	}

	@Override
	public String getSiteSitePageExperienceExperienceKeyRenderedPage(
			Long siteId, String friendlyUrlPath, String experienceKey)
		throws Exception {

		return _toHTML(friendlyUrlPath, siteId, experienceKey);
	}

	@Override
	public String getSiteSitePageRenderedPage(
			Long siteId, String friendlyUrlPath)
		throws Exception {

		return _toHTML(friendlyUrlPath, siteId, null);
	}

	@Override
	public Page<SitePage> getSiteSitePagesExperiencesPage(
			Long siteId, String friendlyUrlPath)
		throws Exception {

		Layout layout = _getLayout(siteId, friendlyUrlPath);

		return Page.of(
			transform(
				_getSegmentsExperiences(layout),
				segmentsExperience -> _toSitePage(
					_isEmbeddedPageDefinition(), layout,
					segmentsExperience.getSegmentsExperienceKey())));
	}

	@Override
	public Page<SitePage> getSiteSitePagesPage(
			Long siteId, String search, Aggregation aggregation, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			HashMapBuilder.<String, Map<String, String>>put(
				"get",
				HashMapBuilder.put(
					"href",
					JaxRsLinkUtil.getJaxRsLink(
						"headless-delivery", BaseSitePageResourceImpl.class,
						"getSiteSitePagesPage", contextUriInfo, siteId)
				).put(
					"method", "GET"
				).build()
			).build(),
			booleanQuery -> {
				BooleanFilter booleanFilter =
					booleanQuery.getPreBooleanFilter();

				booleanFilter.add(
					new TermFilter(Field.GROUP_ID, String.valueOf(siteId)),
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
						LayoutConstants.TYPE_EMBEDDED,
						LayoutConstants.TYPE_LINK_TO_LAYOUT,
						LayoutConstants.TYPE_NODE,
						LayoutConstants.TYPE_FULL_PAGE_APPLICATION,
						LayoutConstants.TYPE_PANEL,
						LayoutConstants.TYPE_PORTLET, LayoutConstants.TYPE_URL,
						LayoutConstants.TYPE_UTILITY
					});
				searchContext.setAttribute(
					"privateLayout", Boolean.FALSE.toString());
				searchContext.setCompanyId(contextCompany.getCompanyId());
				searchContext.setGroupIds(new long[] {siteId});
				searchContext.setKeywords(search);
			},
			sorts,
			document -> {
				long plid = GetterUtil.getLong(
					document.get(Field.ENTRY_CLASS_PK));

				return _toSitePage(
					_isEmbeddedPageDefinition(),
					_layoutLocalService.getLayout(plid), null);
			});
	}

	@Override
	public SitePage postSiteSitePage(Long siteId, SitePage sitePage)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPS-178052")) {
			throw new UnsupportedOperationException();
		}

		Map<Locale, String> titleMap = LocalizedMapUtil.getLocalizedMap(
			contextAcceptLanguage.getPreferredLocale(), sitePage.getTitle(),
			sitePage.getTitle_i18n());

		Layout layout = _addLayout(
			siteId, sitePage, titleMap,
			LocalizedMapUtil.getLocalizedMap(
				contextAcceptLanguage.getPreferredLocale(),
				sitePage.getFriendlyUrlPath(),
				sitePage.getFriendlyUrlPath_i18n(), titleMap));

		DefaultDTOConverterContext dtoConverterContext =
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				_getBasicActions(layout), _dtoConverterRegistry,
				contextHttpServletRequest, layout.getPlid(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser);

		dtoConverterContext.setAttribute(
			"embeddedPageDefinition", Boolean.TRUE);
		dtoConverterContext.setAttribute("groupId", layout.getGroupId());

		return _sitePageDTOConverter.toDTO(dtoConverterContext, layout);
	}

	private Layout _addLayout(
			Long siteId, SitePage sitePage, Map<Locale, String> nameMap,
			Map<Locale, String> friendlyUrlMap)
		throws Exception {

		long parentLayoutId = 0;

		ParentSitePage parentSitePage = sitePage.getParentSitePage();

		if (parentSitePage != null) {
			Layout layout = _layoutLocalService.fetchLayoutByFriendlyURL(
				siteId, false, parentSitePage.getFriendlyUrlPath());

			if (layout == null) {
				if (_log.isWarnEnabled()) {
					_log.warn("Unable to get parent layout");
				}
			}
			else {
				parentLayoutId = layout.getLayoutId();
			}
		}

		Map<Locale, String> titleMap = new HashMap<>();
		Map<Locale, String> descriptionMap = new HashMap<>();
		Map<Locale, String> keywordsMap = new HashMap<>();
		Map<Locale, String> robotsMap = new HashMap<>();
		UnicodeProperties typeSettingsUnicodeProperties =
			new UnicodeProperties();
		boolean hidden = false;

		PageSettings pageSettings = sitePage.getPageSettings();

		if (pageSettings != null) {
			SEOSettings seoSettings = pageSettings.getSeoSettings();

			if (seoSettings != null) {
				titleMap = LocalizedMapUtil.getLocalizedMap(
					contextAcceptLanguage.getPreferredLocale(),
					seoSettings.getHtmlTitle(),
					seoSettings.getHtmlTitle_i18n());
				descriptionMap = LocalizedMapUtil.getLocalizedMap(
					contextAcceptLanguage.getPreferredLocale(),
					seoSettings.getDescription(),
					seoSettings.getDescription_i18n());
				keywordsMap = LocalizedMapUtil.getLocalizedMap(
					contextAcceptLanguage.getPreferredLocale(),
					seoSettings.getSeoKeywords(),
					seoSettings.getSeoKeywords_i18n());
				robotsMap = LocalizedMapUtil.getLocalizedMap(
					contextAcceptLanguage.getPreferredLocale(),
					seoSettings.getRobots(), seoSettings.getRobots_i18n());

				SiteMapSettings siteMapSettings =
					seoSettings.getSiteMapSettings();

				if (siteMapSettings != null) {
					SiteMapSettings.ChangeFrequency changeFrequency =
						siteMapSettings.getChangeFrequency();

					if (changeFrequency != null) {
						typeSettingsUnicodeProperties.setProperty(
							LayoutTypePortletConstants.SITEMAP_CHANGEFREQ,
							StringUtil.toLowerCase(changeFrequency.getValue()));
					}

					Boolean include = siteMapSettings.getInclude();

					if (include != null) {
						String siteMapInclude = "0";

						if (include) {
							siteMapInclude = "1";
						}

						typeSettingsUnicodeProperties.setProperty(
							LayoutTypePortletConstants.SITEMAP_INCLUDE,
							siteMapInclude);
					}

					Boolean includeChildSitePages =
						siteMapSettings.getIncludeChildSitePages();

					if (includeChildSitePages != null) {
						String siteMapIncludeChildLayouts = "false";

						if (includeChildSitePages) {
							siteMapIncludeChildLayouts = "true";
						}

						typeSettingsUnicodeProperties.setProperty(
							"sitemap-include-child-layouts",
							siteMapIncludeChildLayouts);
					}

					Double pagePriority = siteMapSettings.getPagePriority();

					if (pagePriority != null) {
						typeSettingsUnicodeProperties.setProperty(
							LayoutTypePortletConstants.SITEMAP_PRIORITY,
							String.valueOf(pagePriority));
					}
				}
			}

			SitePageNavigationMenuSettings sitePageNavigationMenuSettings =
				pageSettings.getSitePageNavigationMenuSettings();

			if (sitePageNavigationMenuSettings != null) {
				String queryString =
					sitePageNavigationMenuSettings.getQueryString();

				if (Validator.isNotNull(queryString)) {
					typeSettingsUnicodeProperties.setProperty(
						LayoutTypePortletConstants.QUERY_STRING, queryString);
				}

				String target = sitePageNavigationMenuSettings.getTarget();
				SitePageNavigationMenuSettings.TargetType targetType =
					sitePageNavigationMenuSettings.getTargetType();

				if (Validator.isNotNull(target)) {
					typeSettingsUnicodeProperties.setProperty(
						LayoutTypePortletConstants.TARGET, target);
				}

				if ((targetType != null) &&
					(targetType ==
						SitePageNavigationMenuSettings.TargetType.NEW_TAB)) {

					typeSettingsUnicodeProperties.setProperty(
						"targetType", "useNewTab");
				}
			}

			hidden = GetterUtil.getBoolean(
				pageSettings.getHiddenFromNavigation());
		}

		ServiceContext serviceContext = _createServiceContext(siteId, sitePage);

		Layout layout = _layoutService.addLayout(
			null, siteId, false, parentLayoutId, nameMap, titleMap,
			descriptionMap, keywordsMap, robotsMap,
			LayoutConstants.TYPE_CONTENT,
			typeSettingsUnicodeProperties.toString(), hidden, friendlyUrlMap, 0,
			serviceContext);

		_importPageDefinition(
			layout, sitePage.getPageDefinition(), serviceContext);

		layout = _layoutLocalService.getLayout(layout.getPlid());

		PageDefinition pageDefinition = sitePage.getPageDefinition();

		if (pageDefinition != null) {
			Settings settings = pageDefinition.getSettings();

			if (settings != null) {
				ServiceContextThreadLocal.pushServiceContext(serviceContext);

				try {
					layout = _layoutsImporter.importLayoutSettings(
						contextUser.getUserId(), layout, settings.toString());
				}
				finally {
					ServiceContextThreadLocal.popServiceContext();
				}
			}
		}

		Layout draftLayout = _updateDraftLayout(layout);

		layout.setModifiedDate(draftLayout.getModifiedDate());

		layout.setStatus(WorkflowConstants.STATUS_APPROVED);

		layout = _layoutLocalService.updateLayout(layout);

		_updateModelResourcePermissions(
			layout.getCompanyId(), siteId, layout.getPlid(), sitePage);

		_updateSEOEntry(siteId, layout.getLayoutId(), sitePage);

		return layout;
	}

	private ServiceContext _createServiceContext(
		long groupId, SitePage sitePage) {

		Long[] assetCategoryIds = null;

		if (sitePage.getTaxonomyCategoryBriefs() != null) {
			assetCategoryIds = transformToArray(
				Arrays.asList(sitePage.getTaxonomyCategoryBriefs()),
				taxonomyCategoryBrief -> _toAssetCategoryId(
					groupId, taxonomyCategoryBrief),
				Long.class);
		}

		String[] assetTagNames = new String[0];

		if (sitePage.getKeywords() != null) {
			assetTagNames = sitePage.getKeywords();
		}

		ServiceContext serviceContext = ServiceContextBuilder.create(
			groupId, contextHttpServletRequest, null
		).assetCategoryIds(
			assetCategoryIds
		).assetTagNames(
			assetTagNames
		).expandoBridgeAttributes(
			CustomFieldsUtil.toMap(
				Layout.class.getName(), contextCompany.getCompanyId(),
				sitePage.getCustomFields(),
				contextAcceptLanguage.getPreferredLocale())
		).build();

		serviceContext.setUserId(contextUser.getUserId());

		return serviceContext;
	}

	private Map<String, Map<String, String>> _getBasicActions(Layout layout) {
		return HashMapBuilder.<String, Map<String, String>>put(
			"get",
			addAction(
				ActionKeys.VIEW, layout.getPlid(), "getSiteSitePage", null,
				Layout.class.getName(), layout.getGroupId())
		).put(
			"get-experiences",
			() -> {
				if (!layout.isTypeContent()) {
					return null;
				}

				return addAction(
					ActionKeys.VIEW, "getSiteSitePagesExperiencesPage",
					Group.class.getName(), layout.getGroupId());
			}
		).put(
			"get-rendered-page",
			addAction(
				ActionKeys.VIEW, layout.getPlid(),
				"getSiteSitePageRenderedPage", null, Layout.class.getName(),
				layout.getGroupId())
		).build();
	}

	private Map<String, Map<String, String>> _getExperienceActions(
		Layout layout) {

		return HashMapBuilder.<String, Map<String, String>>put(
			"get",
			addAction(
				ActionKeys.VIEW, "getSiteSitePageExperienceExperienceKey",
				Group.class.getName(), layout.getGroupId())
		).put(
			"get-rendered-page",
			addAction(
				ActionKeys.VIEW,
				"getSiteSitePageExperienceExperienceKeyRenderedPage",
				Group.class.getName(), layout.getGroupId())
		).build();
	}

	private long _getFileEntryId(long contentDocumentId) {
		try {
			FileEntry fileEntry = _dlAppService.getFileEntry(contentDocumentId);

			return fileEntry.getFileEntryId();
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}
		}

		return 0;
	}

	private Layout _getLayout(long groupId, String friendlyUrlPath)
		throws Exception {

		String resourceName = ResourceActionsUtil.getCompositeModelName(
			Layout.class.getName(), "false");

		if (!StringUtil.startsWith(friendlyUrlPath, StringPool.FORWARD_SLASH)) {
			friendlyUrlPath = StringPool.FORWARD_SLASH + friendlyUrlPath;
		}

		FriendlyURLEntryLocalization friendlyURLEntryLocalization =
			_friendlyURLEntryLocalService.getFriendlyURLEntryLocalization(
				groupId, _portal.getClassNameId(resourceName), friendlyUrlPath);

		return _layoutLocalService.getLayout(
			friendlyURLEntryLocalization.getClassPK());
	}

	private SegmentsExperience _getSegmentsExperience(
			Layout layout, String segmentsExperienceKey)
		throws Exception {

		if (Validator.isNull(segmentsExperienceKey)) {
			return _getUserSegmentsExperience(layout);
		}

		return _segmentsExperienceService.fetchSegmentsExperience(
			layout.getGroupId(), segmentsExperienceKey, layout.getPlid());
	}

	private List<SegmentsExperience> _getSegmentsExperiences(Layout layout)
		throws Exception {

		if (!layout.isTypeContent()) {
			return Collections.emptyList();
		}

		return _segmentsExperienceLocalService.getSegmentsExperiences(
			layout.getGroupId(), layout.getPlid(), true);
	}

	private ThemeDisplay _getThemeDisplay(Layout layout) throws Exception {
		ServicePreAction servicePreAction = new ServicePreAction();

		HttpServletResponse httpServletResponse =
			new DummyHttpServletResponse();

		servicePreAction.servicePre(
			contextHttpServletRequest, httpServletResponse, false);

		ThemeServicePreAction themeServicePreAction =
			new ThemeServicePreAction();

		themeServicePreAction.run(
			contextHttpServletRequest, httpServletResponse);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)contextHttpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		themeDisplay.setLayout(layout);
		themeDisplay.setResponse(httpServletResponse);
		themeDisplay.setScopeGroupId(layout.getGroupId());
		themeDisplay.setSiteGroupId(layout.getGroupId());

		return themeDisplay;
	}

	private SegmentsExperience _getUserSegmentsExperience(Layout layout)
		throws Exception {

		contextHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(layout));

		long[] segmentsEntryIds = _segmentsEntryRetriever.getSegmentsEntryIds(
			layout.getGroupId(), contextUser.getUserId(),
			_requestContextMapper.map(contextHttpServletRequest), new long[0]);

		long[] segmentsExperienceIds =
			_segmentsExperienceRequestProcessorRegistry.
				getSegmentsExperienceIds(
					contextHttpServletRequest, null, layout.getGroupId(),
					layout.getPlid(), segmentsEntryIds);

		if (ArrayUtil.isEmpty(segmentsExperienceIds)) {
			return _segmentsExperienceLocalService.fetchSegmentsExperience(
				layout.getGroupId(), SegmentsExperienceConstants.KEY_DEFAULT,
				layout.getPlid());
		}

		return _segmentsExperienceLocalService.getSegmentsExperience(
			segmentsExperienceIds[0]);
	}

	private void _importPageDefinition(
			Layout layout, PageDefinition pageDefinition,
			ServiceContext serviceContext)
		throws Exception {

		if (pageDefinition == null) {
			return;
		}

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					layout.getGroupId(), layout.getPlid());

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructure.getDefaultSegmentsExperienceData());

		PageElement pageElement = pageDefinition.getPageElement();

		if ((layoutStructure == null) || (pageElement == null)) {
			return;
		}

		contextHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(layout));

		serviceContext.setRequest(contextHttpServletRequest);

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		try {
			_layoutsImporter.importPageElement(
				layout, layoutStructure, layoutStructure.getMainItemId(),
				pageElement.toString(), 0, true);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	private boolean _isEmbeddedPageDefinition() {
		MultivaluedMap<String, String> queryParameters =
			contextUriInfo.getQueryParameters();

		String nestedFields = queryParameters.getFirst("nestedFields");

		if (nestedFields == null) {
			return false;
		}

		return nestedFields.contains("pageDefinition");
	}

	private Long _toAssetCategoryId(
		long groupId, TaxonomyCategoryBrief taxonomyCategoryBrief) {

		TaxonomyCategoryReference taxonomyCategoryReference =
			taxonomyCategoryBrief.getTaxonomyCategoryReference();

		if (taxonomyCategoryReference == null) {
			return null;
		}

		long assetCategoryGroupId = groupId;

		String siteKey = taxonomyCategoryReference.getSiteKey();

		if (siteKey != null) {
			Group group = _groupLocalService.fetchGroup(
				contextCompany.getCompanyId(), siteKey);

			if (group == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"No group exists with company ID ",
							contextCompany.getCompanyId(), " and site key ",
							siteKey));
				}

				return null;
			}

			assetCategoryGroupId = group.getGroupId();
		}

		AssetCategory assetCategory =
			_assetCategoryLocalService.
				fetchAssetCategoryByExternalReferenceCode(
					taxonomyCategoryReference.getExternalReferenceCode(),
					assetCategoryGroupId);

		if (assetCategory == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"No asset category exists with external reference ",
						"code ",
						taxonomyCategoryReference.getExternalReferenceCode(),
						" and group ID ", assetCategoryGroupId));
			}

			return null;
		}

		return assetCategory.getCategoryId();
	}

	private String _toHTML(
			String friendlyUrlPath, long groupId, String segmentsExperienceKey)
		throws Exception {

		Layout layout = _getLayout(groupId, friendlyUrlPath);

		contextHttpServletRequest = DynamicServletRequest.addQueryString(
			contextHttpServletRequest, "p_l_id=" + layout.getPlid(), false);

		SegmentsExperience segmentsExperience = _getSegmentsExperience(
			layout, segmentsExperienceKey);

		if (segmentsExperience != null) {
			contextHttpServletRequest.setAttribute(
				SegmentsWebKeys.SEGMENTS_EXPERIENCE_IDS,
				new long[] {segmentsExperience.getSegmentsExperienceId()});
		}

		contextHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(layout));

		layout.includeLayoutContent(
			contextHttpServletRequest, contextHttpServletResponse);

		StringBundler sb =
			(StringBundler)contextHttpServletRequest.getAttribute(
				WebKeys.LAYOUT_CONTENT);

		LayoutSet layoutSet = layout.getLayoutSet();

		Document document = Jsoup.parse(
			ThemeUtil.include(
				ServletContextPool.get(StringPool.BLANK),
				contextHttpServletRequest, contextHttpServletResponse,
				"portal_normal.ftl", layoutSet.getTheme(), false));

		Element bodyElement = document.body();

		bodyElement.html(sb.toString());

		return document.html();
	}

	private SitePage _toSitePage(
			boolean embeddedPageDefinition, Layout layout,
			String segmentsExperienceKey)
		throws Exception {

		Map<String, Map<String, String>> actions = null;

		if (Validator.isNotNull(segmentsExperienceKey)) {
			actions = _getExperienceActions(layout);
		}
		else {
			actions = _getBasicActions(layout);
		}

		DefaultDTOConverterContext dtoConverterContext =
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(), actions,
				_dtoConverterRegistry, contextHttpServletRequest,
				layout.getPlid(), contextAcceptLanguage.getPreferredLocale(),
				contextUriInfo, contextUser);

		dtoConverterContext.setAttribute(
			"embeddedPageDefinition", embeddedPageDefinition);
		dtoConverterContext.setAttribute("groupId", layout.getGroupId());

		if (Validator.isNotNull(segmentsExperienceKey)) {
			dtoConverterContext.setAttribute(
				"segmentsExperience",
				_getSegmentsExperience(layout, segmentsExperienceKey));
			dtoConverterContext.setAttribute("showExperience", Boolean.TRUE);
		}
		else {
			dtoConverterContext.setAttribute(
				"segmentsExperience", _getUserSegmentsExperience(layout));
		}

		return _sitePageDTOConverter.toDTO(dtoConverterContext, layout);
	}

	private Layout _updateDraftLayout(Layout layout) throws Exception {
		Layout draftLayout = layout.fetchDraftLayout();

		draftLayout = _layoutLocalService.copyLayoutContent(
			layout, draftLayout);

		draftLayout.setStatus(WorkflowConstants.STATUS_APPROVED);

		UnicodeProperties typeSettingsUnicodeProperties =
			draftLayout.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.put(
			LayoutTypeSettingsConstants.KEY_PUBLISHED, Boolean.TRUE.toString());

		draftLayout.setTypeSettingsProperties(typeSettingsUnicodeProperties);

		return _layoutLocalService.updateLayout(draftLayout);
	}

	private void _updateModelResourcePermissions(
			long companyId, long groupId, long plid, SitePage sitePage)
		throws Exception {

		PagePermission[] pagePermissions = sitePage.getPagePermissions();

		if (pagePermissions == null) {
			return;
		}

		Map<String, String[]> modelPermissionsParameterMap = new HashMap<>(
			pagePermissions.length);

		for (PagePermission pagePermission : pagePermissions) {
			String roleKey = pagePermission.getRoleKey();

			Team team = _teamLocalService.fetchTeam(groupId, roleKey);

			if (team != null) {
				roleKey = String.valueOf(team.getTeamId());
			}

			modelPermissionsParameterMap.put(
				roleKey, pagePermission.getActionKeys());
		}

		ModelPermissions modelPermissions = ModelPermissionsFactory.create(
			modelPermissionsParameterMap, null);

		_resourcePermissionLocalService.deleteResourcePermissions(
			companyId, Layout.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL, String.valueOf(plid));

		_resourcePermissionLocalService.addModelResourcePermissions(
			companyId, groupId, contextUser.getUserId(), Layout.class.getName(),
			String.valueOf(plid), modelPermissions);
	}

	private void _updateSEOEntry(long groupId, long layoutId, SitePage sitePage)
		throws Exception {

		PageSettings pageSettings = sitePage.getPageSettings();

		if (pageSettings == null) {
			return;
		}

		SEOSettings seoSettings = pageSettings.getSeoSettings();

		boolean canonicalURLEnabled = false;
		Map<Locale, String> canonicalURLMap = new HashMap<>();

		if (seoSettings != null) {
			canonicalURLMap = LocalizedMapUtil.getLocalizedMap(
				contextAcceptLanguage.getPreferredLocale(),
				seoSettings.getCustomCanonicalURL(),
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

		OpenGraphSettings openGraphSettings =
			pageSettings.getOpenGraphSettings();

		if (openGraphSettings != null) {
			openGraphDescriptionMap = LocalizedMapUtil.getLocalizedMap(
				contextAcceptLanguage.getPreferredLocale(),
				openGraphSettings.getDescription(),
				openGraphSettings.getDescription_i18n());

			if (MapUtil.isNotEmpty(openGraphDescriptionMap)) {
				openGraphDescriptionEnabled = true;
			}

			openGraphImageAltMap = LocalizedMapUtil.getLocalizedMap(
				contextAcceptLanguage.getPreferredLocale(),
				openGraphSettings.getImageAlt(),
				openGraphSettings.getImageAlt_i18n());

			ContentDocument contentDocument = openGraphSettings.getImage();

			if (contentDocument != null) {
				openGraphImageFileEntryId = _getFileEntryId(
					contentDocument.getId());
			}

			openGraphTitleMap = LocalizedMapUtil.getLocalizedMap(
				contextAcceptLanguage.getPreferredLocale(),
				openGraphSettings.getTitle(),
				openGraphSettings.getTitle_i18n());

			if (MapUtil.isNotEmpty(openGraphTitleMap)) {
				openGraphTitleEnabled = true;
			}
		}

		ServiceContext serviceContext = ServiceContextBuilder.create(
			groupId, contextHttpServletRequest, null
		).build();

		_layoutSEOEntryService.updateLayoutSEOEntry(
			groupId, false, layoutId, canonicalURLEnabled, canonicalURLMap,
			openGraphDescriptionEnabled, openGraphDescriptionMap,
			openGraphImageAltMap, openGraphImageFileEntryId,
			openGraphTitleEnabled, openGraphTitleMap, serviceContext);

		CustomMetaTag[] customMetaTags = pageSettings.getCustomMetaTags();

		if (ArrayUtil.isEmpty(customMetaTags)) {
			return;
		}

		_layoutSEOEntryService.updateCustomMetaTags(
			groupId, false, layoutId,
			transformToList(
				customMetaTags,
				customMetaTag -> new LayoutSEOEntryCustomMetaTagProperty(
					LocalizedMapUtil.getLocalizedMap(
						contextAcceptLanguage.getPreferredLocale(),
						customMetaTag.getValue(),
						customMetaTag.getValue_i18n()),
					customMetaTag.getKey())),
			serviceContext);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SitePageResourceImpl.class);

	private static final EntityModel _entityModel = new SitePageEntityModel();

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Reference
	private LayoutSEOEntryService _layoutSEOEntryService;

	@Reference
	private LayoutService _layoutService;

	@Reference
	private LayoutsImporter _layoutsImporter;

	@Reference
	private Portal _portal;

	@Reference
	private RequestContextMapper _requestContextMapper;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private SegmentsEntryRetriever _segmentsEntryRetriever;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Reference
	private SegmentsExperienceRequestProcessorRegistry
		_segmentsExperienceRequestProcessorRegistry;

	@Reference
	private SegmentsExperienceService _segmentsExperienceService;

	@Reference(
		target = "(component.name=com.liferay.headless.delivery.internal.dto.v1_0.converter.SitePageDTOConverter)"
	)
	private DTOConverter<Layout, SitePage> _sitePageDTOConverter;

	@Reference
	private TeamLocalService _teamLocalService;

}