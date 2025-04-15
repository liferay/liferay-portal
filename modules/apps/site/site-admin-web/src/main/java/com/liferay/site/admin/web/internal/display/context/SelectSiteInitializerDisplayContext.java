/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.admin.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.VerticalNavItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.VerticalNavItemListBuilder;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.LayoutSetPrototypeServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.admin.web.internal.display.context.comparator.SiteInitializerNameComparator;
import com.liferay.site.admin.web.internal.util.SiteInitializerItem;
import com.liferay.site.constants.SiteWebKeys;
import com.liferay.site.initializer.SiteInitializerRegistry;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class SelectSiteInitializerDisplayContext {

	public SelectSiteInitializerDisplayContext(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_siteInitializerRegistry =
			(SiteInitializerRegistry)httpServletRequest.getAttribute(
				SiteWebKeys.SITE_INITIALIZER_REGISTRY);
	}

	public String getBackURL() {
		if (_backURL != null) {
			return _backURL;
		}

		String redirect = ParamUtil.getString(_httpServletRequest, "redirect");

		_backURL = ParamUtil.getString(
			_httpServletRequest, "backURL", redirect);

		return _backURL;
	}

	public long getParentGroupId() {
		if (_parentGroupId != null) {
			return _parentGroupId;
		}

		_parentGroupId = ParamUtil.getLong(
			_httpServletRequest, "parentGroupId");

		return _parentGroupId;
	}

	public SearchContainer<SiteInitializerItem> getSearchContainer()
		throws PortalException {

		SearchContainer<SiteInitializerItem>
			siteInitializerItemSearchContainer = new SearchContainer<>(
				_renderRequest, _getPortletURL(), null,
				"there-are-no-site-templates");

		siteInitializerItemSearchContainer.setResultsAndTotal(
			_getSiteInitializerItems());

		return siteInitializerItemSearchContainer;
	}

	public String getTitle() {
		return LanguageUtil.get(_httpServletRequest, _getTabs1());
	}

	public VerticalNavItemList getVerticalNavItemList() {
		return VerticalNavItemListBuilder.add(
			verticalNavItem -> {
				verticalNavItem.setActive(
					Objects.equals(_getTabs1(), "provided-by-liferay"));
				verticalNavItem.setHref(
					_getPortletURL(), "tabs1", "provided-by-liferay");

				String name = LanguageUtil.get(
					_httpServletRequest, "provided-by-liferay");

				verticalNavItem.setId(name);
				verticalNavItem.setLabel(name);
			}
		).add(
			verticalNavItem -> {
				verticalNavItem.setActive(
					Objects.equals(_getTabs1(), "custom-site-templates"));
				verticalNavItem.setHref(
					_getPortletURL(), "tabs1", "custom-site-templates");

				String name = LanguageUtil.get(
					_httpServletRequest, "custom-site-templates");

				verticalNavItem.setId(name);
				verticalNavItem.setLabel(name);
			}
		).build();
	}

	private PortletURL _getPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCRenderCommandName(
			"/site_admin/select_site_initializer"
		).setRedirect(
			getBackURL()
		).setTabs1(
			_getTabs1()
		).setParameter(
			"backURLTitle",
			ParamUtil.getString(_httpServletRequest, "backURLTitle")
		).setParameter(
			"parentGroupId", getParentGroupId()
		).buildPortletURL();
	}

	private List<SiteInitializerItem> _getSiteInitializerItems()
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (Objects.equals(_getTabs1(), "custom-site-templates")) {
			return ListUtil.sort(
				TransformUtil.transform(
					LayoutSetPrototypeServiceUtil.search(
						themeDisplay.getCompanyId(), Boolean.TRUE, null),
					layoutSetPrototype -> new SiteInitializerItem(
						layoutSetPrototype, themeDisplay.getLocale())),
				new SiteInitializerNameComparator(true));
		}

		return ListUtil.sort(
			TransformUtil.transform(
				_siteInitializerRegistry.getSiteInitializers(
					themeDisplay.getCompanyId(), true),
				siteInitializer -> {
					if (siteInitializer.isActive(themeDisplay.getCompanyId())) {
						return new SiteInitializerItem(
							siteInitializer, themeDisplay.getLocale());
					}

					return null;
				}),
			new SiteInitializerNameComparator(true));
	}

	private String _getTabs1() {
		if (_tabs1 != null) {
			return _tabs1;
		}

		_tabs1 = ParamUtil.getString(
			_httpServletRequest, "tabs1", "provided-by-liferay");

		return _tabs1;
	}

	private String _backURL;
	private final HttpServletRequest _httpServletRequest;
	private Long _parentGroupId;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final SiteInitializerRegistry _siteInitializerRegistry;
	private String _tabs1;

}