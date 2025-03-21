/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.menu.web.internal.display.context;

import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.display.template.PortletDisplayTemplate;
import com.liferay.site.navigation.constants.SiteNavigationConstants;
import com.liferay.site.navigation.item.selector.SiteNavigationMenuItemItemSelectorCriterion;
import com.liferay.site.navigation.item.selector.SiteNavigationMenuItemSelectorCriterion;
import com.liferay.site.navigation.menu.web.internal.configuration.SiteNavigationMenuPortletInstanceConfiguration;
import com.liferay.site.navigation.menu.web.internal.constants.SiteNavigationMenuWebKeys;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.service.SiteNavigationMenuItemLocalServiceUtil;
import com.liferay.site.navigation.service.SiteNavigationMenuLocalServiceUtil;
import com.liferay.site.navigation.taglib.servlet.taglib.NavigationMenuMode;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Juergen Kappler
 */
public class SiteNavigationMenuDisplayContext {

	public SiteNavigationMenuDisplayContext(
			HttpServletRequest httpServletRequest)
		throws ConfigurationException {

		_httpServletRequest = httpServletRequest;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_siteNavigationMenuPortletInstanceConfiguration =
			ConfigurationProviderUtil.getPortletInstanceConfiguration(
				SiteNavigationMenuPortletInstanceConfiguration.class,
				_themeDisplay);
	}

	public String getAlertKey() {
		if (_alertKey != null) {
			return _alertKey;
		}

		if (!_isShowAlert()) {
			_alertKey = StringPool.BLANK;

			return _alertKey;
		}

		if (getSelectSiteNavigationMenuType() ==
				SiteNavigationConstants.TYPE_PRIVATE_PAGES_HIERARCHY) {

			_alertKey =
				"the-navigation-being-displayed-here-is-the-private-pages-" +
					"hierarchy";
		}
		else if (getSelectSiteNavigationMenuType() ==
					SiteNavigationConstants.TYPE_PUBLIC_PAGES_HIERARCHY) {

			_alertKey =
				"the-navigation-being-displayed-here-is-the-public-pages-" +
					"hierarchy";
		}
		else {
			_alertKey = StringPool.BLANK;
		}

		return _alertKey;
	}

	public String getDDMTemplateKey() {
		if (_ddmTemplateKey != null) {
			return _ddmTemplateKey;
		}

		String displayStyle = getDisplayStyle();

		if (displayStyle != null) {
			PortletDisplayTemplate portletDisplayTemplate =
				(PortletDisplayTemplate)_httpServletRequest.getAttribute(
					WebKeys.PORTLET_DISPLAY_TEMPLATE);

			_ddmTemplateKey = portletDisplayTemplate.getDDMTemplateKey(
				displayStyle);
		}

		return _ddmTemplateKey;
	}

	public int getDisplayDepth() {
		if (_displayDepth != -1) {
			return _displayDepth;
		}

		_displayDepth = ParamUtil.getInteger(
			_httpServletRequest, "displayDepth",
			_siteNavigationMenuPortletInstanceConfiguration.displayDepth());

		return _displayDepth;
	}

	public String getDisplayStyle() {
		if (_displayStyle != null) {
			return _displayStyle;
		}

		_displayStyle = ParamUtil.getString(
			_httpServletRequest, "displayStyle",
			_siteNavigationMenuPortletInstanceConfiguration.displayStyle());

		return _displayStyle;
	}

	public long getDisplayStyleGroupId() {
		if (_displayStyleGroupId != 0) {
			return _displayStyleGroupId;
		}

		_displayStyleGroupId = _getDisplayStyleGroupId();

		return _displayStyleGroupId;
	}

	public String getExpandedLevels() {
		if (_expandedLevels != null) {
			return _expandedLevels;
		}

		String defaultExpandedLevels =
			_siteNavigationMenuPortletInstanceConfiguration.expandedLevels();

		_expandedLevels = ParamUtil.getString(
			_httpServletRequest, "expandedLevels", defaultExpandedLevels);

		return _expandedLevels;
	}

	public NavigationMenuMode getNavigationMenuMode() {
		if (_navigationMenuMode != null) {
			return _navigationMenuMode;
		}

		int selectSiteNavigationMenuType = getSelectSiteNavigationMenuType();

		if (selectSiteNavigationMenuType ==
				SiteNavigationConstants.TYPE_PRIVATE_PAGES_HIERARCHY) {

			_navigationMenuMode = NavigationMenuMode.PRIVATE_PAGES;
		}
		else if (selectSiteNavigationMenuType ==
					SiteNavigationConstants.TYPE_PUBLIC_PAGES_HIERARCHY) {

			_navigationMenuMode = NavigationMenuMode.PUBLIC_PAGES;
		}
		else {
			_navigationMenuMode = NavigationMenuMode.DEFAULT;
		}

		return _navigationMenuMode;
	}

	public String getRootMenuItemEventName() {
		PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

		return portletDisplay.getNamespace() + "selectRootMenuItem";
	}

	public String getRootMenuItemId() {
		if (_rootMenuItemId != null) {
			return _rootMenuItemId;
		}

		String rootMenuItemExternalReferenceCode = ParamUtil.getString(
			_httpServletRequest, "rootMenuItemExternalReferenceCode",
			_siteNavigationMenuPortletInstanceConfiguration.
				rootMenuItemExternalReferenceCode());

		if (Validator.isNull(rootMenuItemExternalReferenceCode)) {
			return StringPool.BLANK;
		}

		if (isSiteNavigationMenuSelected()) {
			SiteNavigationMenuItem siteNavigationMenuItem =
				SiteNavigationMenuItemLocalServiceUtil.
					fetchSiteNavigationMenuItemByExternalReferenceCode(
						rootMenuItemExternalReferenceCode,
						_getSiteNavigationMenuGroupId());

			if (siteNavigationMenuItem == null) {
				return StringPool.BLANK;
			}

			_rootMenuItemId = String.valueOf(
				siteNavigationMenuItem.getSiteNavigationMenuItemId());
		}
		else {
			Layout rootLayout =
				LayoutLocalServiceUtil.fetchLayoutByUuidAndGroupId(
					rootMenuItemExternalReferenceCode,
					_themeDisplay.getScopeGroupId(), false);

			if (rootLayout == null) {
				rootLayout = LayoutLocalServiceUtil.fetchLayoutByUuidAndGroupId(
					rootMenuItemExternalReferenceCode,
					_themeDisplay.getScopeGroupId(), true);
			}

			if (rootLayout == null) {
				return StringPool.BLANK;
			}

			_rootMenuItemId = rootMenuItemExternalReferenceCode;
		}

		return _rootMenuItemId;
	}

	public int getRootMenuItemLevel() {
		if (_rootMenuItemLevel != null) {
			return _rootMenuItemLevel;
		}

		int defaultRootMenuItemLevel =
			_siteNavigationMenuPortletInstanceConfiguration.rootMenuItemLevel();

		_rootMenuItemLevel = ParamUtil.getInteger(
			_httpServletRequest, "rootMenuItemLevel", defaultRootMenuItemLevel);

		return _rootMenuItemLevel;
	}

	public String getRootMenuItemSelectorURL() {
		String eventName = getRootMenuItemEventName();

		ItemSelector itemSelector =
			(ItemSelector)_httpServletRequest.getAttribute(
				SiteNavigationMenuWebKeys.ITEM_SELECTOR);

		ItemSelectorCriterion itemSelectorCriterion =
			new SiteNavigationMenuItemItemSelectorCriterion();

		itemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new UUIDItemSelectorReturnType());

		return String.valueOf(
			itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(_httpServletRequest),
				eventName, itemSelectorCriterion));
	}

	public String getRootMenuItemType() {
		if (_rootMenuItemType != null) {
			return _rootMenuItemType;
		}

		String defaultRootMenuItemType =
			_siteNavigationMenuPortletInstanceConfiguration.rootMenuItemType();

		_rootMenuItemType = ParamUtil.getString(
			_httpServletRequest, "rootMenuItemType", defaultRootMenuItemType);

		return _rootMenuItemType;
	}

	public long getSelectSiteNavigationMenuId() {
		int siteNavigationMenuType = getSiteNavigationMenuType();

		long siteNavigationMenuId = getSiteNavigationMenuId();

		if ((siteNavigationMenuType == -1) && (siteNavigationMenuId <= 0)) {
			SiteNavigationMenu siteNavigationMenu =
				SiteNavigationMenuLocalServiceUtil.fetchSiteNavigationMenu(
					_themeDisplay.getScopeGroupId(),
					_getDefaultSelectSiteNavigationMenuType());

			if (siteNavigationMenu != null) {
				return siteNavigationMenu.getSiteNavigationMenuId();
			}

			return 0;
		}

		if (siteNavigationMenuType > 0) {
			SiteNavigationMenu siteNavigationMenu =
				SiteNavigationMenuLocalServiceUtil.fetchSiteNavigationMenu(
					_themeDisplay.getScopeGroupId(), siteNavigationMenuType);

			if (siteNavigationMenu != null) {
				return siteNavigationMenu.getSiteNavigationMenuId();
			}

			return 0;
		}

		return siteNavigationMenuId;
	}

	public int getSelectSiteNavigationMenuType() {
		int selectSiteNavigationMenuType = getSiteNavigationMenuType();

		if (selectSiteNavigationMenuType > 0) {
			return selectSiteNavigationMenuType;
		}

		return _getDefaultSelectSiteNavigationMenuType();
	}

	public String getSelectSiteNavigationMenuTypeLabel() {
		String typeKey = "select";

		int type = getSelectSiteNavigationMenuType();

		if (type == SiteNavigationConstants.TYPE_PRIMARY) {
			typeKey = "primary-navigation";
		}
		else if (type == SiteNavigationConstants.TYPE_PRIVATE_PAGES_HIERARCHY) {
			typeKey = "private-pages-hierarchy";
		}
		else if (type == SiteNavigationConstants.TYPE_PUBLIC_PAGES_HIERARCHY) {
			Group group = _themeDisplay.getScopeGroup();

			if (group.isPrivateLayoutsEnabled()) {
				typeKey = "public-pages-hierarchy";
			}
			else {
				typeKey = "pages-hierarchy";
			}
		}
		else if (type == SiteNavigationConstants.TYPE_SECONDARY) {
			typeKey = "secondary-navigation";
		}
		else if (type == SiteNavigationConstants.TYPE_SOCIAL) {
			typeKey = "social-navigation";
		}

		return LanguageUtil.get(_httpServletRequest, typeKey);
	}

	public SiteNavigationMenu getSiteNavigationMenu() {
		if (_siteNavigationMenu != null) {
			return _siteNavigationMenu;
		}

		String siteNavigationMenuExternalReferenceCode = ParamUtil.getString(
			_httpServletRequest, "siteNavigationMenuExternalReferenceCode",
			_siteNavigationMenuPortletInstanceConfiguration.
				siteNavigationMenuExternalReferenceCode());

		if (Validator.isNotNull(siteNavigationMenuExternalReferenceCode)) {
			_siteNavigationMenu =
				SiteNavigationMenuLocalServiceUtil.
					fetchSiteNavigationMenuByExternalReferenceCode(
						siteNavigationMenuExternalReferenceCode,
						_getSiteNavigationMenuGroupId());
		}

		return _siteNavigationMenu;
	}

	public String getSiteNavigationMenuEventName() {
		PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

		return portletDisplay.getNamespace() + "selectSiteNavigationMenu";
	}

	public long getSiteNavigationMenuId() {
		if (_siteNavigationMenuId != null) {
			return _siteNavigationMenuId;
		}

		_siteNavigationMenuId = _getSiteNavigationMenuId();

		return _siteNavigationMenuId;
	}

	public String getSiteNavigationMenuItemSelectorURL() {
		String eventName = getSiteNavigationMenuEventName();

		ItemSelector itemSelector =
			(ItemSelector)_httpServletRequest.getAttribute(
				SiteNavigationMenuWebKeys.ITEM_SELECTOR);

		ItemSelectorCriterion itemSelectorCriterion =
			new SiteNavigationMenuItemSelectorCriterion();

		itemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new UUIDItemSelectorReturnType());

		return String.valueOf(
			itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(_httpServletRequest),
				eventName, itemSelectorCriterion));
	}

	public String getSiteNavigationMenuName() {
		SiteNavigationMenu siteNavigationMenu = getSiteNavigationMenu();

		if (siteNavigationMenu != null) {
			return HtmlUtil.escape(siteNavigationMenu.getName());
		}

		Group group = _themeDisplay.getScopeGroup();

		if (!group.isPrivateLayoutsEnabled()) {
			return LanguageUtil.get(_httpServletRequest, "pages-hierarchy");
		}

		if (getSelectSiteNavigationMenuType() ==
				SiteNavigationConstants.TYPE_PRIVATE_PAGES_HIERARCHY) {

			return LanguageUtil.get(
				_httpServletRequest, "private-pages-hierarchy");
		}

		if (getSelectSiteNavigationMenuType() ==
				SiteNavigationConstants.TYPE_PUBLIC_PAGES_HIERARCHY) {

			return LanguageUtil.get(
				_httpServletRequest, "public-pages-hierarchy");
		}

		Layout layout = _themeDisplay.getLayout();

		return LanguageUtil.get(
			_httpServletRequest,
			layout.isPrivateLayout() ? "private-pages-hierarchy" :
				"public-pages-hierarchy");
	}

	public int getSiteNavigationMenuType() {
		if (_navigationMenuType != null) {
			return _navigationMenuType;
		}

		int siteNavigationMenuType =
			_siteNavigationMenuPortletInstanceConfiguration.
				siteNavigationMenuType();

		_navigationMenuType = ParamUtil.getInteger(
			_httpServletRequest, "siteNavigationMenuType",
			siteNavigationMenuType);

		return _navigationMenuType;
	}

	public boolean isPreview() {
		if (_preview != null) {
			return _preview;
		}

		_preview = ParamUtil.getBoolean(
			_httpServletRequest, "preview",
			_siteNavigationMenuPortletInstanceConfiguration.preview());

		return _preview;
	}

	public boolean isSiteNavigationMenuSelected() {
		long siteNavigationMenuId = getSiteNavigationMenuId();
		String siteNavigationMenuName =
			_siteNavigationMenuPortletInstanceConfiguration.
				siteNavigationMenuName();
		int siteNavigationMenuType =
			_siteNavigationMenuPortletInstanceConfiguration.
				siteNavigationMenuType();

		if (((siteNavigationMenuId > 0) ||
			 Validator.isNotNull(siteNavigationMenuName)) &&
			(siteNavigationMenuType == -1)) {

			return true;
		}

		return false;
	}

	private int _getDefaultSelectSiteNavigationMenuType() {
		Layout layout = _themeDisplay.getLayout();
		Group scopeGroup = _themeDisplay.getScopeGroup();

		if (_hasLayoutPageTemplateEntry(layout)) {
			if (scopeGroup.hasPublicLayouts()) {
				return SiteNavigationConstants.TYPE_PUBLIC_PAGES_HIERARCHY;
			}
			else if (scopeGroup.hasPrivateLayouts()) {
				return SiteNavigationConstants.TYPE_PRIVATE_PAGES_HIERARCHY;
			}

			return SiteNavigationConstants.TYPE_PRIMARY;
		}

		if (layout.isPrivateLayout() && scopeGroup.hasPrivateLayouts()) {
			return SiteNavigationConstants.TYPE_PRIVATE_PAGES_HIERARCHY;
		}

		if (layout.isPublicLayout() && scopeGroup.hasPublicLayouts()) {
			return SiteNavigationConstants.TYPE_PUBLIC_PAGES_HIERARCHY;
		}

		return SiteNavigationConstants.TYPE_PRIMARY;
	}

	private long _getDisplayStyleGroupId() {
		String displayStyleGroupExternalReferenceCode =
			_siteNavigationMenuPortletInstanceConfiguration.
				displayStyleGroupExternalReferenceCode();

		if (Validator.isNull(displayStyleGroupExternalReferenceCode)) {
			return _themeDisplay.getScopeGroupId();
		}

		Group group = GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
			displayStyleGroupExternalReferenceCode,
			_themeDisplay.getCompanyId());

		if (group != null) {
			return group.getGroupId();
		}

		return 0;
	}

	private long _getSiteNavigationMenuGroupId() {
		if (_siteNavigationMenuGroupId != null) {
			return _siteNavigationMenuGroupId;
		}

		String siteNavigationMenuGroupExternalReferenceCode =
			ParamUtil.getString(
				_httpServletRequest,
				"siteNavigationMenuGroupExternalReferenceCode",
				_siteNavigationMenuPortletInstanceConfiguration.
					siteNavigationMenuGroupExternalReferenceCode());

		long siteNavigationMenuGroupId = 0;

		if (Validator.isNull(siteNavigationMenuGroupExternalReferenceCode)) {
			siteNavigationMenuGroupId = _themeDisplay.getScopeGroupId();
		}
		else {
			Group group =
				GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
					siteNavigationMenuGroupExternalReferenceCode,
					_themeDisplay.getCompanyId());

			if (group != null) {
				siteNavigationMenuGroupId = group.getGroupId();
			}
		}

		_siteNavigationMenuGroupId = siteNavigationMenuGroupId;

		return _siteNavigationMenuGroupId;
	}

	private long _getSiteNavigationMenuId() {
		SiteNavigationMenu siteNavigationMenu = getSiteNavigationMenu();

		if (siteNavigationMenu == null) {
			return 0;
		}

		return siteNavigationMenu.getSiteNavigationMenuId();
	}

	private boolean _hasLayoutPageTemplateEntry(Layout layout) {
		long plid = layout.getPlid();

		if (layout.isDraftLayout()) {
			plid = layout.getClassPK();
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryLocalServiceUtil.
				fetchLayoutPageTemplateEntryByPlid(plid);

		if (layoutPageTemplateEntry != null) {
			return true;
		}

		return false;
	}

	private boolean _isShowAlert() {
		Group scopeGroup = _themeDisplay.getScopeGroup();

		if (!scopeGroup.isPrivateLayoutsEnabled()) {
			return false;
		}

		long plid = _themeDisplay.getPlid();

		Layout layout = _themeDisplay.getLayout();

		if (layout.isDraftLayout()) {
			plid = layout.getClassPK();
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryLocalServiceUtil.
				fetchLayoutPageTemplateEntryByPlid(plid);

		if ((layoutPageTemplateEntry == null) ||
			(!layout.isDraftLayout() &&
			 (layoutPageTemplateEntry.getType() ==
				 LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE))) {

			return false;
		}

		return true;
	}

	private String _alertKey;
	private String _ddmTemplateKey;
	private int _displayDepth = -1;
	private String _displayStyle;
	private long _displayStyleGroupId;
	private String _expandedLevels;
	private final HttpServletRequest _httpServletRequest;
	private NavigationMenuMode _navigationMenuMode;
	private Integer _navigationMenuType;
	private Boolean _preview;
	private String _rootMenuItemId;
	private Integer _rootMenuItemLevel;
	private String _rootMenuItemType;
	private SiteNavigationMenu _siteNavigationMenu;
	private Long _siteNavigationMenuGroupId;
	private Long _siteNavigationMenuId;
	private final SiteNavigationMenuPortletInstanceConfiguration
		_siteNavigationMenuPortletInstanceConfiguration;
	private final ThemeDisplay _themeDisplay;

}