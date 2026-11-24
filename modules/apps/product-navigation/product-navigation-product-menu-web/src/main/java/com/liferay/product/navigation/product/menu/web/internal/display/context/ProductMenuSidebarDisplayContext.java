/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.product.menu.web.internal.display.context;

import com.liferay.application.list.GroupProvider;
import com.liferay.application.list.PanelAppRegistry;
import com.liferay.application.list.PanelCategory;
import com.liferay.application.list.constants.ApplicationListWebKeys;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.application.list.display.context.logic.PanelCategoryHelper;
import com.liferay.application.list.util.PanelCategoryRegistryUtil;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.URLItemSelectorReturnType;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.portlet.ControlPanelEntry;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.SessionClicks;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.product.navigation.product.menu.constants.ProductNavigationProductMenuPortletKeys;
import com.liferay.site.item.selector.SiteItemSelectorCriterion;
import com.liferay.site.manager.RecentGroupManager;

import jakarta.portlet.ResourceURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Mario Leandro
 */
public class ProductMenuSidebarDisplayContext {

	public ProductMenuSidebarDisplayContext(
		HttpServletRequest httpServletRequest) {

		_httpServletRequest = httpServletRequest;

		_panelAppRegistry = (PanelAppRegistry)httpServletRequest.getAttribute(
			ApplicationListWebKeys.PANEL_APP_REGISTRY);
		_panelCategory = PanelCategoryRegistryUtil.getPanelCategory(
			PanelCategoryKeys.SITE_ADMINISTRATION);

		_panelCategoryHelper = new PanelCategoryHelper(_panelAppRegistry);

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_updateLatentGroup();
	}

	public Map<String, Object> getProps() throws Exception {
		if (_panelCategory == null) {
			return Collections.emptyMap();
		}

		String itemSelectedEventName = String.format(
			"_%s_selectSite",
			ProductNavigationProductMenuPortletKeys.
				PRODUCT_NAVIGATION_PRODUCT_MENU);

		return HashMapBuilder.<String, Object>put(
			"canonicalName", _panelCategory.getLabel(LocaleUtil.ENGLISH)
		).put(
			"expandedKeys", _getExpandedKeys()
		).put(
			"expandedKeysSessionKey", _SESSION_KEY_EXPANDED_KEYS
		).put(
			"imageURL", _getImageURL()
		).put(
			"items", _getPropsItems()
		).put(
			"label", _getLabel()
		).put(
			"navigationItemsURL",
			() -> {
				RequestBackedPortletURLFactory requestBackedPortletURLFactory =
					RequestBackedPortletURLFactoryUtil.create(
						_httpServletRequest);

				LiferayPortletURL navigationItemsURL =
					(LiferayPortletURL)
						requestBackedPortletURLFactory.createResourceURL(
							ProductNavigationProductMenuPortletKeys.
								PRODUCT_NAVIGATION_PRODUCT_MENU);

				navigationItemsURL.setCopyCurrentRenderParameters(false);
				navigationItemsURL.setParameter(
					"p_v_l_s_g_id",
					String.valueOf(_themeDisplay.getScopeGroupId()));
				navigationItemsURL.setParameter(
					"rootPanelCategoryKey", _panelCategory.getKey());
				navigationItemsURL.setResourceID(
					"/product_navigation_product_menu/get_navigation_items");

				return navigationItemsURL.toString();
			}
		).put(
			"pageTree", this::_getPageTree
		).put(
			"selectedPortletId", _getSelectedPortletId()
		).put(
			"sidebarStateSessionKey", _SESSION_KEY_SIDEBAR_STATE
		).put(
			"siteItemSelectedEventName", itemSelectedEventName
		).put(
			"siteItemSelectorURL",
			() -> {
				ItemSelector itemSelector = _itemSelectorSnapshot.get();

				SiteItemSelectorCriterion siteItemSelectorCriterion =
					new SiteItemSelectorCriterion();

				siteItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
					new URLItemSelectorReturnType());

				return String.valueOf(
					itemSelector.getItemSelectorURL(
						RequestBackedPortletURLFactoryUtil.create(
							_httpServletRequest),
						itemSelectedEventName, siteItemSelectorCriterion));
			}
		).put(
			"staging",
			() -> {
				ProductMenuSidebarStagingDisplayContext
					productMenuSidebarStagingDisplayContext =
						new ProductMenuSidebarStagingDisplayContext(
							_httpServletRequest);

				return productMenuSidebarStagingDisplayContext.getProps();
			}
		).put(
			"visible", isVisible()
		).build();
	}

	public boolean isVisible() {
		String state = SessionClicks.get(
			_httpServletRequest, _SESSION_KEY_SIDEBAR_STATE, "closed");

		return Objects.equals(state, "open");
	}

	private List<String> _getExpandedKeys() {
		String expandedKeysString = SessionClicks.get(
			_httpServletRequest, _SESSION_KEY_EXPANDED_KEYS, StringPool.BLANK);

		if (!expandedKeysString.isEmpty()) {
			return ListUtil.fromArray(StringUtil.split(expandedKeysString));
		}

		return TransformUtil.transform(
			_getRootChildPanelCategories(), PanelCategory::getKey);
	}

	private String _getGroupDisplayURL(Group group) {
		String groupDisplayURL = group.getDisplayURL(_themeDisplay, false);

		if (Validator.isNull(groupDisplayURL)) {
			groupDisplayURL = group.getDisplayURL(_themeDisplay, true);
		}

		if (Validator.isNull(groupDisplayURL)) {
			return StringPool.BLANK;
		}

		return HttpComponentsUtil.removeParameter(groupDisplayURL, "p_p_id");
	}

	private Map<String, Object> _getHomePropsItem() {
		String groupDisplayURL = _getGroupDisplayURL(
			_themeDisplay.getScopeGroup());

		if (Validator.isNull(groupDisplayURL)) {
			return null;
		}

		return HashMapBuilder.<String, Object>put(
			"href", groupDisplayURL
		).put(
			"id", "home"
		).put(
			"label", LanguageUtil.get(_httpServletRequest, "home[page]")
		).put(
			"leadingIcon", "home"
		).build();
	}

	private String _getImageURL() {
		Company company = _themeDisplay.getCompany();

		if (!company.isSiteLogo()) {
			return _themeDisplay.getCompanyLogo();
		}

		Group group = _themeDisplay.getScopeGroup();

		return group.getLogoURL(_themeDisplay, true);
	}

	private String _getLabel() throws Exception {
		Group group = _themeDisplay.getScopeGroup();

		if (!group.isUser()) {
			return group.getDescriptiveName(_themeDisplay.getLocale());
		}

		if (group.getClassPK() == _themeDisplay.getUserId()) {
			return LanguageUtil.get(_httpServletRequest, "my-site");
		}

		User user = UserLocalServiceUtil.getUser(group.getClassPK());

		return LanguageUtil.format(
			_themeDisplay.getLocale(), "x-site", user.getFullName());
	}

	private Map<String, Object> _getPageTree() throws Exception {
		if (!_isShowPageTree()) {
			return null;
		}

		return HashMapBuilder.<String, Object>put(
			"stateSessionKey", _SESSION_KEY_PAGE_TREE_GROUP_ID
		).put(
			"url",
			() -> {
				RequestBackedPortletURLFactory requestBackedPortletURLFactory =
					RequestBackedPortletURLFactoryUtil.create(
						_httpServletRequest);

				return ResourceURLBuilder.createResourceURL(
					(ResourceURL)
						requestBackedPortletURLFactory.createResourceURL(
							ProductNavigationProductMenuPortletKeys.
								PRODUCT_NAVIGATION_PRODUCT_MENU)
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setBackURL(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"p_v_l_s_g_id", _themeDisplay.getScopeGroupId()
				).setParameter(
					"selPpid", _themeDisplay.getPpid()
				).setResourceID(
					"/product_navigation_product_menu/get_page_tree"
				).buildString();
			}
		).put(
			"visible", _isPageTreeVisible()
		).build();
	}

	private Map<String, Object> _getPageTreePropsItem() throws Exception {
		if (!_isShowPageTree()) {
			return null;
		}

		return HashMapBuilder.<String, Object>put(
			"disabled", _isPageTreeDisabled()
		).put(
			"id", "pageTree"
		).put(
			"label", LanguageUtil.get(_httpServletRequest, "page-tree")
		).put(
			"leadingIcon", "pages-tree"
		).put(
			"pageTree", true
		).build();
	}

	private List<Map<String, Object>> _getPanelAppPropsItems(
			PanelCategory panelCategory)
		throws Exception {

		return TransformUtil.transform(
			_panelAppRegistry.getPanelApps(
				panelCategory.getKey(), _themeDisplay.getPermissionChecker(),
				_themeDisplay.getScopeGroup()),
			panelApp -> HashMapBuilder.<String, Object>put(
				"canonicalName", panelApp.getLabel(LocaleUtil.ENGLISH)
			).put(
				"href",
				panelApp.getPortletURL(
					_httpServletRequest
				).toString()
			).put(
				"id", panelApp.getPortletId()
			).put(
				"label", panelApp.getLabel(_themeDisplay.getLocale())
			).put(
				"leadingIcon", panelApp.getIcon()
			).build());
	}

	private List<Map<String, Object>> _getPropsItems() throws Exception {
		List<Map<String, Object>> propsItems = new ArrayList<>();

		Map<String, Object> homePropsItem = _getHomePropsItem();

		if (homePropsItem != null) {
			propsItems.add(homePropsItem);
		}

		Map<String, Object> pageTreePropsItem = _getPageTreePropsItem();

		if (pageTreePropsItem != null) {
			propsItems.add(pageTreePropsItem);
		}

		propsItems.addAll(_getPanelAppPropsItems(_panelCategory));

		propsItems.addAll(
			TransformUtil.transform(
				_getRootChildPanelCategories(),
				childPanelCategory -> {
					List<Map<String, Object>> panelAppPropsItems =
						_getPanelAppPropsItems(childPanelCategory);

					if (panelAppPropsItems.isEmpty()) {
						return null;
					}

					return HashMapBuilder.<String, Object>put(
						"id", childPanelCategory.getKey()
					).put(
						"items", panelAppPropsItems
					).put(
						"label",
						childPanelCategory.getLabel(_themeDisplay.getLocale())
					).build();
				}));

		return propsItems;
	}

	private List<PanelCategory> _getRootChildPanelCategories() {
		if (_rootChildPanelCategories != null) {
			return _rootChildPanelCategories;
		}

		_rootChildPanelCategories =
			_panelCategoryHelper.getChildPanelCategories(
				_panelCategory.getKey(), _themeDisplay);

		return _rootChildPanelCategories;
	}

	private String _getSelectedPortletId() {
		Layout layout = _themeDisplay.getLayout();

		if ((layout == null) || layout.isTypeControlPanel()) {
			return _themeDisplay.getPpid();
		}

		Group group = _themeDisplay.getScopeGroup();

		boolean homePrivateLayout = Validator.isNull(
			group.getDisplayURL(_themeDisplay, false));

		if (layout.isPrivateLayout() != homePrivateLayout) {
			return _themeDisplay.getPpid();
		}

		Layout firstLayout = LayoutLocalServiceUtil.fetchFirstLayout(
			layout.getGroupId(), homePrivateLayout,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, false);

		if ((firstLayout != null) &&
			(firstLayout.getPlid() == layout.getPlid())) {

			return "home";
		}

		return _themeDisplay.getPpid();
	}

	private boolean _isPageTreeDisabled() {
		if (!Objects.equals(
				_themeDisplay.getPpid(), LayoutAdminPortletKeys.GROUP_PAGES)) {

			return false;
		}

		String groupPagesPortletNamespace = PortalUtil.getPortletNamespace(
			LayoutAdminPortletKeys.GROUP_PAGES);
		HttpServletRequest originalHttpServletRequest =
			PortalUtil.getOriginalServletRequest(_httpServletRequest);

		if (Validator.isNotNull(
				ParamUtil.getString(
					originalHttpServletRequest,
					groupPagesPortletNamespace + "mvcRenderCommandName")) ||
			Validator.isNotNull(
				ParamUtil.getString(originalHttpServletRequest, "mvcPath"))) {

			return false;
		}

		return true;
	}

	private boolean _isPageTreeVisible() {
		if (_isPageTreeDisabled()) {
			return false;
		}

		String pageTreeGroupId = SessionClicks.get(
			_httpServletRequest, _SESSION_KEY_PAGE_TREE_GROUP_ID,
			StringPool.BLANK);

		return pageTreeGroupId.equals(
			String.valueOf(_themeDisplay.getScopeGroupId()));
	}

	private boolean _isShowPageTree() throws Exception {
		if (_showPageTree != null) {
			return _showPageTree;
		}

		_showPageTree = false;

		Group group = _themeDisplay.getScopeGroup();

		if ((group == null) || group.isCompany() || group.isDepot()) {
			return _showPageTree;
		}

		Portlet portlet = PortletLocalServiceUtil.getPortletById(
			_themeDisplay.getCompanyId(), LayoutAdminPortletKeys.GROUP_PAGES);

		if (portlet == null) {
			return _showPageTree;
		}

		ControlPanelEntry controlPanelEntry =
			portlet.getControlPanelEntryInstance();

		_showPageTree = controlPanelEntry.hasAccessPermission(
			_themeDisplay.getPermissionChecker(), group, portlet);

		return _showPageTree;
	}

	private void _updateLatentGroup() {
		GroupProvider groupProvider = _groupProviderSnapshot.get();

		Group group = groupProvider.getGroup(_httpServletRequest);

		if (group == null) {
			return;
		}

		RecentGroupManager recentGroupManager =
			_recentGroupManagerSnapshot.get();

		recentGroupManager.addRecentGroup(
			_httpServletRequest, group.getGroupId());

		groupProvider.setGroup(_httpServletRequest, group);
	}

	private static final String _SESSION_KEY_EXPANDED_KEYS =
		"com.liferay.product.navigation.product.menu.web_sidebarExpandedKeys";

	private static final String _SESSION_KEY_PAGE_TREE_GROUP_ID =
		"com.liferay.product.navigation.product.menu.web_sidebarPageTree";

	private static final String _SESSION_KEY_SIDEBAR_STATE =
		"com.liferay.application.list_sidebarState";

	private static final Snapshot<GroupProvider> _groupProviderSnapshot =
		new Snapshot<>(
			ProductMenuSidebarDisplayContext.class, GroupProvider.class);
	private static final Snapshot<ItemSelector> _itemSelectorSnapshot =
		new Snapshot<>(
			ProductMenuSidebarDisplayContext.class, ItemSelector.class);
	private static final Snapshot<RecentGroupManager>
		_recentGroupManagerSnapshot = new Snapshot<>(
			ProductMenuSidebarDisplayContext.class, RecentGroupManager.class);

	private final HttpServletRequest _httpServletRequest;
	private final PanelAppRegistry _panelAppRegistry;
	private final PanelCategory _panelCategory;
	private final PanelCategoryHelper _panelCategoryHelper;
	private List<PanelCategory> _rootChildPanelCategories;
	private Boolean _showPageTree;
	private final ThemeDisplay _themeDisplay;

}