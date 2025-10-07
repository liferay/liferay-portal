/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.item.selector.web.internal.display.context;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntry;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.navigation.constants.SiteNavigationConstants;
import com.liferay.site.navigation.item.selector.SiteNavigationMenuItemSelectorReturnType;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.service.SiteNavigationMenuItemLocalServiceUtil;
import com.liferay.site.navigation.service.SiteNavigationMenuItemServiceUtil;
import com.liferay.site.navigation.service.SiteNavigationMenuLocalServiceUtil;
import com.liferay.site.navigation.service.SiteNavigationMenuServiceUtil;
import com.liferay.site.navigation.taglib.servlet.taglib.util.BreadcrumbEntryBuilder;
import com.liferay.site.navigation.taglib.servlet.taglib.util.BreadcrumbEntryListBuilder;
import com.liferay.site.navigation.type.SiteNavigationMenuItemType;
import com.liferay.site.navigation.type.SiteNavigationMenuItemTypeRegistry;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Víctor Galán
 */
public class SelectSiteNavigationMenuDisplayContext {

	public SelectSiteNavigationMenuDisplayContext(
		HttpServletRequest httpServletRequest, String itemSelectedEventName,
		PortletURL portletURL,
		SiteNavigationMenuItemTypeRegistry siteNavigationMenuItemTypeRegistry) {

		_httpServletRequest = httpServletRequest;
		_itemSelectedEventName = itemSelectedEventName;
		_portletURL = portletURL;
		_siteNavigationMenuItemTypeRegistry =
			siteNavigationMenuItemTypeRegistry;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<BreadcrumbEntry> getBreadcrumbEntries() {
		return BreadcrumbEntryListBuilder.add(
			breadcrumbEntry -> {
				String backURL = ParamUtil.getString(
					_httpServletRequest, "backURL",
					PortalUtil.getCurrentURL(_httpServletRequest));

				breadcrumbEntry.setBrowsable(backURL != null);

				breadcrumbEntry.setTitle(
					LanguageUtil.get(_themeDisplay.getLocale(), "menus"));
				breadcrumbEntry.setURL(backURL);
			}
		).addAll(
			() -> getSiteNavigationMenuId() == 0,
			this::_getLayoutBreadcrumbEntries
		).addAll(
			() -> getSiteNavigationMenuId() > 0,
			this::_getSiteNavigationMenuBreadcrumbEntries
		).build();
	}

	public Map<String, Object> getContext(
		LiferayPortletResponse liferayPortletResponse) {

		return HashMapBuilder.<String, Object>put(
			"buttonClass", ".site-navigation-menu-selector"
		).put(
			"containerId",
			liferayPortletResponse.getNamespace() +
				"siteNavigationMenuLevelSelector"
		).put(
			"eventName", getItemSelectedEventName()
		).put(
			"returnType",
			SiteNavigationMenuItemSelectorReturnType.class.toString()
		).build();
	}

	public String getCurrentLevelTitle() {
		long siteNavigationMenuId = getSiteNavigationMenuId();
		long parentSiteNavigationMenuItemId =
			getParentSiteNavigationMenuItemId();

		if (siteNavigationMenuId == 0) {
			if (parentSiteNavigationMenuItemId == 0) {
				return LanguageUtil.get(_themeDisplay.getLocale(), _getKey());
			}

			Layout layout = LayoutLocalServiceUtil.fetchLayout(
				getParentSiteNavigationMenuItemId());

			return layout.getName(_themeDisplay.getLocale());
		}

		if (parentSiteNavigationMenuItemId == 0) {
			SiteNavigationMenu siteNavigationMenu =
				SiteNavigationMenuLocalServiceUtil.fetchSiteNavigationMenu(
					siteNavigationMenuId);

			return siteNavigationMenu.getName();
		}

		SiteNavigationMenuItem siteNavigationMenuItem =
			SiteNavigationMenuItemLocalServiceUtil.fetchSiteNavigationMenuItem(
				getParentSiteNavigationMenuItemId());

		return _getSiteNavigationMenuItemName(siteNavigationMenuItem);
	}

	public String getItemSelectedEventName() {
		return _itemSelectedEventName;
	}

	public long getParentSiteNavigationMenuItemId() {
		if (_parentSiteNavigationMenuItemId != null) {
			return _parentSiteNavigationMenuItemId;
		}

		_parentSiteNavigationMenuItemId = ParamUtil.getLong(
			_httpServletRequest, "parentSiteNavigationMenuItemId");

		return _parentSiteNavigationMenuItemId;
	}

	public String getSelectSiteNavigationMenuLevelURL(
			long siteNavigationMenuId, int type)
		throws PortletException {

		PortletURL portletURL = _getBasePortletURL(siteNavigationMenuId);

		if (type == SiteNavigationConstants.TYPE_PRIVATE_PAGES_HIERARCHY) {
			portletURL.setParameter("privateLayout", Boolean.TRUE.toString());
		}

		return portletURL.toString();
	}

	public String getSiteNavigationMenuExternalReferenceCode() {
		if (_siteNavigationMenuExternalReferenceCode != null) {
			return _siteNavigationMenuExternalReferenceCode;
		}

		SiteNavigationMenu siteNavigationMenu =
			SiteNavigationMenuLocalServiceUtil.fetchSiteNavigationMenu(
				getSiteNavigationMenuId());

		if (siteNavigationMenu != null) {
			_siteNavigationMenuExternalReferenceCode =
				siteNavigationMenu.getExternalReferenceCode();
		}

		return _siteNavigationMenuExternalReferenceCode;
	}

	public long getSiteNavigationMenuId() {
		if (_siteNavigationMenuId != null) {
			return _siteNavigationMenuId;
		}

		_siteNavigationMenuId = ParamUtil.getLong(
			_httpServletRequest, "siteNavigationMenuId", -1);

		return _siteNavigationMenuId;
	}

	public SearchContainer<SiteNavigationMenuEntry>
			getSiteNavigationMenuItemSearchContainer()
		throws PortalException, PortletException {

		SearchContainer<SiteNavigationMenuEntry> searchContainer =
			new SearchContainer<>(
				_getPortletRequest(), _portletURL, null,
				"there-are-no-items-to-display");

		List<SiteNavigationMenuEntry> siteNavigationMenuItems =
			_getSiteNavigationMenuItems();

		searchContainer.setResultsAndTotal(
			() -> siteNavigationMenuItems, siteNavigationMenuItems.size());

		return searchContainer;
	}

	public SearchContainer<SiteNavigationMenu>
		getSiteNavigationMenuSearchContainer() {

		SearchContainer<SiteNavigationMenu> searchContainer =
			new SearchContainer<>(
				_getPortletRequest(), _portletURL, null, null);

		long[] groupIds = {_themeDisplay.getScopeGroupId()};

		Group scopeGroup = _themeDisplay.getScopeGroup();

		if (!scopeGroup.isCompany()) {
			groupIds = ArrayUtil.append(
				groupIds, _themeDisplay.getCompanyGroupId());
		}

		List<SiteNavigationMenu> staticSiteNavigationMenus =
			_getStaticSiteNavigationMenus();

		int staticSiteNavigationMenusCount = staticSiteNavigationMenus.size();

		int siteNavigationMenusCount =
			SiteNavigationMenuServiceUtil.getSiteNavigationMenusCount(groupIds);

		long[] siteNavigationMenusGroupIds = groupIds;

		searchContainer.setResultsAndTotal(
			() -> {
				int start = searchContainer.getStart();

				if (start != 0) {
					start -= staticSiteNavigationMenusCount;
				}

				List<SiteNavigationMenu> siteNavigationMenus =
					SiteNavigationMenuServiceUtil.getSiteNavigationMenus(
						siteNavigationMenusGroupIds, start,
						searchContainer.getEnd(), null);

				if (start == 0) {
					siteNavigationMenus = ListUtil.concat(
						staticSiteNavigationMenus, siteNavigationMenus);
				}

				return siteNavigationMenus;
			},
			siteNavigationMenusCount + staticSiteNavigationMenusCount);

		return searchContainer;
	}

	public boolean isPrivateLayout() {
		if (_privateLayout != null) {
			return _privateLayout;
		}

		_privateLayout = ParamUtil.getBoolean(
			_httpServletRequest, "privateLayout");

		return _privateLayout;
	}

	private List<BreadcrumbEntry> _getAncestorsBreadcrumbEntries() {
		SiteNavigationMenuItem siteNavigationMenuItem =
			SiteNavigationMenuItemLocalServiceUtil.fetchSiteNavigationMenuItem(
				getParentSiteNavigationMenuItemId());

		return BreadcrumbEntryListBuilder.addAll(
			() -> {
				List<SiteNavigationMenuItem> ancestorsSiteNavigationMenuItems =
					siteNavigationMenuItem.getAncestors();

				Collections.reverse(ancestorsSiteNavigationMenuItems);

				return TransformUtil.transform(
					ancestorsSiteNavigationMenuItems,
					curSiteNavigationMenuItem ->
						BreadcrumbEntryBuilder.setTitle(
							_getSiteNavigationMenuItemName(
								curSiteNavigationMenuItem)
						).setURL(
							_getSelectSiteNavigationMenuLevelURL(
								getSiteNavigationMenuId(),
								curSiteNavigationMenuItem.
									getSiteNavigationMenuItemId())
						).build());
			}
		).add(
			breadcrumbEntry -> {
				String selectSiteNavigationMenuLevelURL =
					getSelectSiteNavigationMenuLevelURL(
						getSiteNavigationMenuId(),
						SiteNavigationConstants.TYPE_DEFAULT);

				breadcrumbEntry.setBrowsable(
					selectSiteNavigationMenuLevelURL != null);

				breadcrumbEntry.setTitle(
					_getSiteNavigationMenuItemName(siteNavigationMenuItem));
				breadcrumbEntry.setURL(selectSiteNavigationMenuLevelURL);
			}
		).build();
	}

	private PortletURL _getBasePortletURL(long siteNavigationMenuId)
		throws PortletException {

		PortletResponse portletResponse =
			(PortletResponse)_httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_RESPONSE);

		return PortletURLBuilder.create(
			PortletURLUtil.clone(
				_portletURL,
				PortalUtil.getLiferayPortletResponse(portletResponse))
		).setBackURL(
			ParamUtil.getString(
				_httpServletRequest, "backURL",
				PortalUtil.getCurrentURL(_httpServletRequest))
		).setParameter(
			"siteNavigationMenuId", siteNavigationMenuId
		).buildPortletURL();
	}

	private String _getKey() {
		Group group = _themeDisplay.getScopeGroup();

		if (!group.isPrivateLayoutsEnabled()) {
			return "pages-hierarchy";
		}

		if (isPrivateLayout()) {
			return "private-pages-hierarchy";
		}

		return "public-pages-hierarchy";
	}

	private List<BreadcrumbEntry> _getLayoutBreadcrumbEntries() {
		return BreadcrumbEntryListBuilder.add(
			breadcrumbEntry -> {
				String selectSiteNavigationMenuLevelURL =
					_getSelectSiteNavigationMenuLevelURL(
						getSiteNavigationMenuId(), 0);

				breadcrumbEntry.setBrowsable(
					selectSiteNavigationMenuLevelURL != null);

				breadcrumbEntry.setTitle(
					LanguageUtil.get(_themeDisplay.getLocale(), _getKey()));
				breadcrumbEntry.setURL(selectSiteNavigationMenuLevelURL);
			}
		).addAll(
			() -> getParentSiteNavigationMenuItemId() != 0,
			() -> {
				Layout layout = LayoutLocalServiceUtil.fetchLayout(
					getParentSiteNavigationMenuItemId());

				List<Layout> ancestors = layout.getAncestors();

				if (ListUtil.isEmpty(ancestors)) {
					ancestors = new ArrayList<>();
				}
				else {
					Collections.reverse(ancestors);
				}

				ancestors.add(layout);

				return TransformUtil.transform(
					ancestors,
					ancestor -> BreadcrumbEntryBuilder.setTitle(
						ancestor.getName(_themeDisplay.getLocale())
					).setURL(
						_getSelectSiteNavigationMenuLevelURL(
							getSiteNavigationMenuId(), ancestor.getPlid())
					).build());
			}
		).build();
	}

	private List<Layout> _getLayouts() {
		long parentSiteNavigationMenuItemId =
			getParentSiteNavigationMenuItemId();

		if (parentSiteNavigationMenuItemId > 0) {
			Layout layout = LayoutLocalServiceUtil.fetchLayout(
				getParentSiteNavigationMenuItemId());

			return layout.getChildren();
		}

		return LayoutLocalServiceUtil.getLayouts(
			_themeDisplay.getScopeGroupId(), isPrivateLayout(),
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID);
	}

	private SiteNavigationMenu _getPagesHierarchySiteNavigationMenu() {
		SiteNavigationMenu siteNavigationMenu =
			SiteNavigationMenuLocalServiceUtil.createSiteNavigationMenu(0);

		siteNavigationMenu.setGroupId(_themeDisplay.getScopeGroupId());
		siteNavigationMenu.setName(
			LanguageUtil.get(_themeDisplay.getLocale(), "pages-hierarchy"));
		siteNavigationMenu.setType(
			SiteNavigationConstants.TYPE_PUBLIC_PAGES_HIERARCHY);

		return siteNavigationMenu;
	}

	private PortletRequest _getPortletRequest() {
		return (PortletRequest)_httpServletRequest.getAttribute(
			JavaConstants.JAKARTA_PORTLET_REQUEST);
	}

	private SiteNavigationMenu _getPrivatePagesHierarchySiteNavigationMenu() {
		SiteNavigationMenu siteNavigationMenu =
			SiteNavigationMenuLocalServiceUtil.createSiteNavigationMenu(0);

		siteNavigationMenu.setGroupId(_themeDisplay.getScopeGroupId());
		siteNavigationMenu.setName(
			LanguageUtil.get(
				_themeDisplay.getLocale(), "private-pages-hierarchy"));
		siteNavigationMenu.setType(
			SiteNavigationConstants.TYPE_PRIVATE_PAGES_HIERARCHY);

		return siteNavigationMenu;
	}

	private SiteNavigationMenu _getPublicPagesHierarchySiteNavigationMenu() {
		SiteNavigationMenu siteNavigationMenu =
			SiteNavigationMenuLocalServiceUtil.createSiteNavigationMenu(0);

		siteNavigationMenu.setGroupId(_themeDisplay.getScopeGroupId());
		siteNavigationMenu.setName(
			LanguageUtil.get(
				_themeDisplay.getLocale(), "public-pages-hierarchy"));
		siteNavigationMenu.setType(
			SiteNavigationConstants.TYPE_PUBLIC_PAGES_HIERARCHY);

		return siteNavigationMenu;
	}

	private String _getSelectSiteNavigationMenuLevelURL(
			long siteNavigationMenuId, long parentSiteNavigationMenuItemId)
		throws PortletException {

		PortletURL portletURL = _getBasePortletURL(siteNavigationMenuId);

		if (parentSiteNavigationMenuItemId >= 0) {
			portletURL.setParameter(
				"parentSiteNavigationMenuItemId",
				String.valueOf(parentSiteNavigationMenuItemId));
		}

		if (isPrivateLayout()) {
			portletURL.setParameter("privateLayout", Boolean.TRUE.toString());
		}

		return portletURL.toString();
	}

	private List<BreadcrumbEntry> _getSiteNavigationMenuBreadcrumbEntries() {
		return BreadcrumbEntryListBuilder.add(
			breadcrumbEntry -> {
				SiteNavigationMenu siteNavigationMenu =
					SiteNavigationMenuServiceUtil.fetchSiteNavigationMenu(
						getSiteNavigationMenuId());

				String selectSiteNavigationMenuLevelURL =
					_getSelectSiteNavigationMenuLevelURL(
						getSiteNavigationMenuId(), 0);

				breadcrumbEntry.setBrowsable(
					selectSiteNavigationMenuLevelURL != null);

				breadcrumbEntry.setTitle(siteNavigationMenu.getName());
				breadcrumbEntry.setURL(siteNavigationMenu.getName());
			}
		).addAll(
			() -> getParentSiteNavigationMenuItemId() != 0,
			this::_getAncestorsBreadcrumbEntries
		).build();
	}

	private String _getSiteNavigationMenuItemName(
		SiteNavigationMenuItem siteNavigationMenuItem) {

		SiteNavigationMenuItemType siteNavigationMenuItemType =
			_siteNavigationMenuItemTypeRegistry.getSiteNavigationMenuItemType(
				siteNavigationMenuItem);

		return siteNavigationMenuItemType.getTitle(
			siteNavigationMenuItem, _themeDisplay.getLocale());
	}

	private List<SiteNavigationMenuEntry> _getSiteNavigationMenuItems()
		throws PortalException, PortletException {

		if (getSiteNavigationMenuId() > 0) {
			return TransformUtil.transform(
				SiteNavigationMenuItemServiceUtil.getSiteNavigationMenuItems(
					getSiteNavigationMenuId(),
					getParentSiteNavigationMenuItemId()),
				siteNavigationMenuItem -> SiteNavigationMenuEntry.of(
					_getSiteNavigationMenuItemName(siteNavigationMenuItem),
					_getSelectSiteNavigationMenuLevelURL(
						getSiteNavigationMenuId(),
						siteNavigationMenuItem.getSiteNavigationMenuItemId())));
		}

		return TransformUtil.transform(
			_getLayouts(),
			layout -> SiteNavigationMenuEntry.of(
				layout.getName(_themeDisplay.getLocale()),
				_getSelectSiteNavigationMenuLevelURL(
					getSiteNavigationMenuId(), layout.getPlid())));
	}

	private List<SiteNavigationMenu> _getStaticSiteNavigationMenus() {
		Group group = _themeDisplay.getScopeGroup();

		if (group.isPrivateLayoutsEnabled()) {
			return Arrays.asList(
				_getPublicPagesHierarchySiteNavigationMenu(),
				_getPrivatePagesHierarchySiteNavigationMenu());
		}

		return Collections.singletonList(
			_getPagesHierarchySiteNavigationMenu());
	}

	private final HttpServletRequest _httpServletRequest;
	private final String _itemSelectedEventName;
	private Long _parentSiteNavigationMenuItemId;
	private final PortletURL _portletURL;
	private Boolean _privateLayout;
	private String _siteNavigationMenuExternalReferenceCode;
	private Long _siteNavigationMenuId;
	private final SiteNavigationMenuItemTypeRegistry
		_siteNavigationMenuItemTypeRegistry;
	private final ThemeDisplay _themeDisplay;

}