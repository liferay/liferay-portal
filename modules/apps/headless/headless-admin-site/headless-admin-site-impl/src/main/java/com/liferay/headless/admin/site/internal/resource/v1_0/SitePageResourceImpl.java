/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0;

import com.liferay.headless.admin.site.dto.v1_0.ContentPageSettings;
import com.liferay.headless.admin.site.dto.v1_0.ContentPageSpecification;
import com.liferay.headless.admin.site.dto.v1_0.PageSettings;
import com.liferay.headless.admin.site.dto.v1_0.PageSpecification;
import com.liferay.headless.admin.site.dto.v1_0.SitePage;
import com.liferay.headless.admin.site.dto.v1_0.WidgetPageSettings;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.SitePageTypeUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.GroupUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.LayoutUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.ServiceContextUtil;
import com.liferay.headless.admin.site.resource.v1_0.SitePageResource;
import com.liferay.headless.common.spi.service.context.ServiceContextBuilder;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTypePortletConstants;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import jakarta.ws.rs.NotSupportedException;

import java.io.Serializable;

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
	properties = "OSGI-INF/liferay/rest/v1_0/site-page.properties",
	scope = ServiceScope.PROTOTYPE, service = SitePageResource.class
)
public class SitePageResourceImpl extends BaseSitePageResourceImpl {

	@Override
	public void deleteSiteSiteByExternalReferenceCodeSitePage(
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
	public SitePage getSiteSiteByExternalReferenceCodeSitePage(
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
	public Page<SitePage> getSiteSiteByExternalReferenceCodeSitePagesPage(
			String siteExternalReferenceCode, String search,
			Aggregation aggregation, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-35443")) {
			throw new UnsupportedOperationException();
		}

		return Page.of(
			transform(
				_layoutService.getLayouts(
					GroupUtil.getGroupId(
						true, contextCompany.getCompanyId(),
						siteExternalReferenceCode),
					false, search,
					new String[] {
						LayoutConstants.TYPE_CONTENT,
						LayoutConstants.TYPE_PORTLET
					},
					null, pagination.getStartPosition(),
					pagination.getEndPosition(), null),
				layout -> _toSitePage(layout)),
			pagination,
			_layoutService.getLayoutsCount(
				GroupUtil.getGroupId(
					true, contextCompany.getCompanyId(),
					siteExternalReferenceCode),
				false, search,
				new String[] {
					LayoutConstants.TYPE_CONTENT, LayoutConstants.TYPE_PORTLET
				},
				null));
	}

	@Override
	public SitePage postByExternalReferenceCodeSitePage(
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
	public ContentPageSpecification
			postSiteSiteByExternalReferenceCodeSitePagePageSpecification(
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
				contentPageSpecification, layout,
				ServiceContextUtil.createServiceContext(
					layout.getGroupId(), contextHttpServletRequest,
					contextUser.getUserId())));
	}

	@Override
	public SitePage putSiteSiteByExternalReferenceCodeSitePage(
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
	public Page<SitePage> read(
			Filter filter, Pagination pagination, Sort[] sorts,
			Map<String, Serializable> parameters, String search)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-35443")) {
			throw new UnsupportedOperationException();
		}

		if (parameters.containsKey("siteId")) {
			Group group = _groupLocalService.getGroup(
				(Long)parameters.get("siteId"));

			return getSiteSiteByExternalReferenceCodeSitePagesPage(
				group.getExternalReferenceCode(), search, null, filter,
				pagination, sorts);
		}

		throw new NotSupportedException(
			"One of the following parameters must be specified: [siteId]");
	}

	@Override
	protected void preparePatch(SitePage sitePage, SitePage existingSitePage) {
		if (sitePage.getPageSettings() != null) {
			existingSitePage.setPageSettings(sitePage::getPageSettings);
		}
	}

	private Layout _addLayout(
			String externalReferenceCode, long groupId, SitePage sitePage)
		throws Exception {

		ServiceContext serviceContext = ServiceContextBuilder.create(
			groupId, contextHttpServletRequest, sitePage.getViewableByAsString()
		).build();

		serviceContext.setUuid(sitePage.getUuid());

		return _layoutService.addLayout(
			externalReferenceCode, groupId, false,
			_getParentLayoutId(
				LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, groupId,
				sitePage.getParentSitePageExternalReferenceCode()),
			LocalizedMapUtil.getLocalizedMap(sitePage.getName_i18n()), null,
			null, null, null,
			SitePageTypeUtil.toInternalType(sitePage.getType()),
			_getTypeSettings(sitePage),
			_isHiddenFromNavigation(false, sitePage.getPageSettings()),
			LocalizedMapUtil.getLocalizedMap(
				sitePage.getFriendlyUrlPath_i18n()),
			0, serviceContext);
	}

	private long _getParentLayoutId(
			long defaultParentLayoutId, long groupId,
			String parentSitePageExternalReferenceCode)
		throws Exception {

		if (parentSitePageExternalReferenceCode == null) {
			return defaultParentLayoutId;
		}

		if (Validator.isNull(parentSitePageExternalReferenceCode)) {
			return LayoutConstants.DEFAULT_PARENT_LAYOUT_ID;
		}

		Layout layout = _layoutService.fetchLayoutByExternalReferenceCode(
			parentSitePageExternalReferenceCode, groupId);

		if (layout == null) {
			throw new UnsupportedOperationException();
		}

		return layout.getLayoutId();
	}

	private String _getTypeSettings(SitePage sitePage) {
		PageSettings pageSettings = sitePage.getPageSettings();

		if (pageSettings == null) {
			return null;
		}

		if (sitePage.getType() == SitePage.Type.CONTENT_PAGE) {
			if (!(pageSettings instanceof ContentPageSettings)) {
				throw new UnsupportedOperationException();
			}

			return null;
		}

		if ((sitePage.getType() != SitePage.Type.WIDGET_PAGE) ||
			!(pageSettings instanceof WidgetPageSettings)) {

			throw new UnsupportedOperationException();
		}

		WidgetPageSettings widgetPageSettings =
			(WidgetPageSettings)pageSettings;

		return UnicodePropertiesBuilder.create(
			true
		).setProperty(
			LayoutTypePortletConstants.LAYOUT_TEMPLATE_ID,
			widgetPageSettings.getLayoutTemplateId()
		).buildString();
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

		Map<Locale, String> friendlyURLMap = layout.getFriendlyURLMap();

		if (sitePage.getFriendlyUrlPath_i18n() != null) {
			friendlyURLMap = LocalizedMapUtil.getLocalizedMap(
				sitePage.getFriendlyUrlPath_i18n());
		}

		layout = _layoutService.updateLayout(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			_getParentLayoutId(
				layout.getParentLayoutId(), layout.getGroupId(),
				sitePage.getParentSitePageExternalReferenceCode()),
			nameMap, layout.getTitleMap(), layout.getDescriptionMap(),
			layout.getKeywordsMap(), layout.getRobotsMap(), layout.getType(),
			_isHiddenFromNavigation(
				layout.isHidden(), sitePage.getPageSettings()),
			friendlyURLMap, layout.isIconImage(), null,
			layout.getStyleBookEntryId(), layout.getFaviconFileEntryId(),
			layout.getMasterLayoutPlid(),
			ServiceContextUtil.createServiceContext(
				layout.getGroupId(), contextHttpServletRequest,
				contextUser.getUserId()));

		String typeSettings = _getTypeSettings(sitePage);

		if (typeSettings == null) {
			return layout;
		}

		return _layoutService.updateLayout(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			typeSettings);
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

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

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