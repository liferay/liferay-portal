/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.admin.web.internal.display.context;

import com.liferay.dynamic.data.mapping.constants.DDMTemplateConstants;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalServiceUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.SearchDisplayStyleUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.theme.NavItem;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.display.template.PortletDisplayTemplate;
import com.liferay.site.navigation.admin.constants.SiteNavigationAdminPortletKeys;
import com.liferay.site.navigation.admin.web.internal.security.permission.resource.DDMTemplatePermission;
import com.liferay.site.navigation.admin.web.internal.security.permission.resource.SiteNavigationMenuPermission;
import com.liferay.site.navigation.admin.web.internal.util.SiteNavigationMenuPortletUtil;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.service.SiteNavigationMenuLocalService;
import com.liferay.site.navigation.service.SiteNavigationMenuService;
import com.liferay.site.navigation.type.DefaultSiteNavigationMenuItemTypeContext;
import com.liferay.site.navigation.type.SiteNavigationMenuItemType;
import com.liferay.site.navigation.type.SiteNavigationMenuItemTypeContext;
import com.liferay.site.navigation.type.SiteNavigationMenuItemTypeRegistry;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.staging.StagingGroupHelperUtil;
import com.liferay.template.constants.TemplatePortletKeys;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Pavel Savinov
 */
public class SiteNavigationAdminDisplayContext {

	public SiteNavigationAdminDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		PortletDisplayTemplate portletDisplayTemplate,
		SiteNavigationMenuItemTypeRegistry siteNavigationMenuItemTypeRegistry,
		SiteNavigationMenuLocalService siteNavigationMenuLocalService,
		SiteNavigationMenuService siteNavigationMenuService) {

		_httpServletRequest = httpServletRequest;
		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_portletDisplayTemplate = portletDisplayTemplate;
		_siteNavigationMenuItemTypeRegistry =
			siteNavigationMenuItemTypeRegistry;
		_siteNavigationMenuLocalService = siteNavigationMenuLocalService;
		_siteNavigationMenuService = siteNavigationMenuService;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getAddSiteNavigationMenuItemDropdownItems() {
		List<SiteNavigationMenuItemType> siteNavigationMenuItemTypes =
			_siteNavigationMenuItemTypeRegistry.
				getSiteNavigationMenuItemTypes();

		SiteNavigationMenuItemTypeContext siteNavigationMenuItemTypeContext =
			new DefaultSiteNavigationMenuItemTypeContext(
				_themeDisplay.getScopeGroup());

		siteNavigationMenuItemTypes = ListUtil.filter(
			siteNavigationMenuItemTypes,
			siteNavigationMenuItemType ->
				siteNavigationMenuItemType.isAvailable(
					siteNavigationMenuItemTypeContext));

		ListUtil.sort(
			siteNavigationMenuItemTypes,
			Comparator.comparing(
				siteNavigationMenuItemType ->
					siteNavigationMenuItemType.getLabel(
						_themeDisplay.getLocale())));

		return DropdownItemListBuilder.addAll(
			TransformUtil.transform(
				siteNavigationMenuItemTypes,
				siteNavigationMenuItemType -> _getDropdownItem(
					siteNavigationMenuItemType, _themeDisplay))
		).build();
	}

	public String getDisplayStyle() {
		if (_displayStyle != null) {
			return _displayStyle;
		}

		_displayStyle = SearchDisplayStyleUtil.getDisplayStyle(
			_httpServletRequest,
			SiteNavigationAdminPortletKeys.SITE_NAVIGATION_ADMIN, "list");

		return _displayStyle;
	}

	public String getKeywords() {
		if (Validator.isNotNull(_keywords)) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		return _keywords;
	}

	public String getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_httpServletRequest,
			SiteNavigationAdminPortletKeys.SITE_NAVIGATION_ADMIN,
			"create-date");

		return _orderByCol;
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_httpServletRequest,
			SiteNavigationAdminPortletKeys.SITE_NAVIGATION_ADMIN, "asc");

		return _orderByType;
	}

	public PortletURL getPortletURL() {
		PortletURL portletURL = _liferayPortletResponse.createRenderURL();

		String displayStyle = ParamUtil.getString(
			_httpServletRequest, "displayStyle");

		if (Validator.isNotNull(displayStyle)) {
			portletURL.setParameter("displayStyle", getDisplayStyle());
		}

		String orderByCol = getOrderByCol();

		if (Validator.isNotNull(orderByCol)) {
			portletURL.setParameter("orderByCol", orderByCol);
		}

		String orderByType = getOrderByType();

		if (Validator.isNotNull(orderByType)) {
			portletURL.setParameter("orderByType", orderByType);
		}

		return portletURL;
	}

	public SiteNavigationMenu getPrimarySiteNavigationMenu() {
		return _siteNavigationMenuLocalService.fetchPrimarySiteNavigationMenu(
			_themeDisplay.getScopeGroupId());
	}

	public SearchContainer<SiteNavigationMenu> getSearchContainer() {
		if (_searchContainer != null) {
			return _searchContainer;
		}

		SearchContainer<SiteNavigationMenu> searchContainer =
			new SearchContainer(
				_liferayPortletRequest, getPortletURL(), null,
				"there-are-no-navigation-menus");

		searchContainer.setOrderByCol(getOrderByCol());
		searchContainer.setOrderByComparator(
			SiteNavigationMenuPortletUtil.getOrderByComparator(
				getOrderByCol(), getOrderByType()));
		searchContainer.setOrderByType(getOrderByType());

		if (Validator.isNotNull(getKeywords())) {
			searchContainer.setResultsAndTotal(
				() -> _siteNavigationMenuService.getSiteNavigationMenus(
					_themeDisplay.getScopeGroupId(), getKeywords(),
					searchContainer.getStart(), searchContainer.getEnd(),
					searchContainer.getOrderByComparator()),
				_siteNavigationMenuService.getSiteNavigationMenusCount(
					_themeDisplay.getScopeGroupId(), getKeywords()));
		}
		else {
			searchContainer.setResultsAndTotal(
				() -> _siteNavigationMenuService.getSiteNavigationMenus(
					_themeDisplay.getScopeGroupId(), searchContainer.getStart(),
					searchContainer.getEnd(),
					searchContainer.getOrderByComparator()),
				_siteNavigationMenuService.getSiteNavigationMenusCount(
					_themeDisplay.getScopeGroupId()));
		}

		searchContainer.setRowChecker(
			new EmptyOnClickRowChecker(_liferayPortletResponse));

		_searchContainer = searchContainer;

		return _searchContainer;
	}

	public Map<String, Object> getSiteNavigationContext() throws Exception {
		return HashMapBuilder.<String, Object>put(
			"addSiteNavigationMenuItemOptions",
			getAddSiteNavigationMenuItemDropdownItems()
		).put(
			"deleteSiteNavigationMenuItemURL",
			() -> PortletURLBuilder.createActionURL(
				_liferayPortletResponse
			).setActionName(
				"/site_navigation_admin/delete_site_navigation_menu_item"
			).buildString()
		).put(
			"displayTemplateOptions", _getDDMTemplatesJSONArray()
		).put(
			"editSiteNavigationMenuItemParentURL",
			() -> PortletURLBuilder.createActionURL(
				_liferayPortletResponse
			).setActionName(
				"/site_navigation_admin/edit_site_navigation_menu_item_parent"
			).setRedirect(
				PortalUtil.getCurrentURL(_liferayPortletRequest)
			).buildString()
		).put(
			"editSiteNavigationMenuItemURL",
			() -> PortletURLBuilder.createRenderURL(
				_liferayPortletResponse
			).setMVCPath(
				"/edit_site_navigation_menu_item.jsp"
			).setWindowState(
				LiferayWindowState.EXCLUSIVE
			).buildString()
		).put(
			"editSiteNavigationMenuSettingsURL",
			() -> PortletURLBuilder.createRenderURL(
				_liferayPortletResponse
			).setMVCPath(
				"/site_navigation_menu_settings.jsp"
			).setWindowState(
				LiferayWindowState.EXCLUSIVE
			).buildString()
		).put(
			"id", _liferayPortletResponse.getNamespace() + "sidebar"
		).put(
			"languageId", _themeDisplay.getLanguageId()
		).put(
			"previewSiteNavigationMenuURL", _getPreviewSiteNavigationMenuURL()
		).put(
			"redirect", PortalUtil.getCurrentURL(_liferayPortletRequest)
		).put(
			"siteNavigationMenuId", getSiteNavigationMenuId()
		).put(
			"siteNavigationMenuItems",
			SiteNavigationMenuPortletUtil.getSiteNavigationMenuItemsJSONArray(
				0, getSiteNavigationMenuId(),
				_siteNavigationMenuItemTypeRegistry, _themeDisplay)
		).put(
			"siteNavigationMenuName", getSiteNavigationMenuName()
		).build();
	}

	public SiteNavigationMenu getSiteNavigationMenu() throws PortalException {
		if (getSiteNavigationMenuId() == 0) {
			return null;
		}

		return _siteNavigationMenuService.fetchSiteNavigationMenu(
			getSiteNavigationMenuId());
	}

	public long getSiteNavigationMenuId() {
		if (_siteNavigationMenuId != null) {
			return _siteNavigationMenuId;
		}

		_siteNavigationMenuId = ParamUtil.getLong(
			_httpServletRequest, "siteNavigationMenuId");

		return _siteNavigationMenuId;
	}

	public SiteNavigationMenuItemTypeRegistry
		getSiteNavigationMenuItemTypeRegistry() {

		return _siteNavigationMenuItemTypeRegistry;
	}

	public String getSiteNavigationMenuName() throws PortalException {
		if (_siteNavigationMenuName != null) {
			return _siteNavigationMenuName;
		}

		SiteNavigationMenu siteNavigationMenu = getSiteNavigationMenu();

		_siteNavigationMenuName = siteNavigationMenu.getName();

		return _siteNavigationMenuName;
	}

	public boolean hasEditPermission() {
		Group group = _themeDisplay.getScopeGroup();

		StagingGroupHelper stagingGroupHelper =
			StagingGroupHelperUtil.getStagingGroupHelper();

		if ((stagingGroupHelper.isLocalLiveGroup(group) ||
			 stagingGroupHelper.isRemoteLiveGroup(group)) &&
			group.isStagedPortlet(
				SiteNavigationAdminPortletKeys.SITE_NAVIGATION_ADMIN)) {

			return false;
		}

		return true;
	}

	public boolean hasUpdatePermission() throws PortalException {
		if (_updatePermission != null) {
			return _updatePermission;
		}

		_updatePermission = SiteNavigationMenuPermission.contains(
			_themeDisplay.getPermissionChecker(), getSiteNavigationMenuId(),
			ActionKeys.UPDATE);

		return _updatePermission;
	}

	private String _getAddURL(
		SiteNavigationMenuItemType siteNavigationMenuItemType) {

		if (siteNavigationMenuItemType.isItemSelector()) {
			String itemSelectorURL =
				siteNavigationMenuItemType.getItemSelectorURL(
					_httpServletRequest);

			if (Validator.isNotNull(itemSelectorURL)) {
				return itemSelectorURL;
			}
		}

		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCPath(
			"/add_site_navigation_menu_item.jsp"
		).setRedirect(
			PortletURLBuilder.createRenderURL(
				_liferayPortletResponse
			).setMVCPath(
				"/add_site_navigation_menu_item_redirect.jsp"
			).setPortletResource(
				() -> {
					PortletDisplay portletDisplay =
						_themeDisplay.getPortletDisplay();

					return portletDisplay.getId();
				}
			).buildString()
		).setParameter(
			"siteNavigationMenuId", getSiteNavigationMenuId()
		).setParameter(
			"type", siteNavigationMenuItemType.getType()
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	private JSONArray _getDDMTemplatesJSONArray() {
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		List<DDMTemplate> ddmTemplates =
			DDMTemplateLocalServiceUtil.getTemplates(
				_getGroupIds(_themeDisplay.getScopeGroup()),
				PortalUtil.getClassNameId(NavItem.class.getName()), 0L);

		for (DDMTemplate ddmTemplate : ddmTemplates) {
			try {
				if (!DDMTemplatePermission.contains(
						_themeDisplay.getPermissionChecker(),
						ddmTemplate.getTemplateId(), ActionKeys.VIEW) ||
					!DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY.equals(
						ddmTemplate.getType())) {

					continue;
				}
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}

			jsonArray.put(
				JSONUtil.put(
					"label",
					HtmlUtil.escape(
						ddmTemplate.getName(_themeDisplay.getLocale()))
				).put(
					"selected",
					() -> Objects.equals(
						ddmTemplate.getTemplateKey(),
						_getDefaultDDMTemplateKey())
				).put(
					"value", HtmlUtil.escape(ddmTemplate.getTemplateKey())
				));
		}

		return jsonArray;
	}

	private String _getDefaultDDMTemplateKey() {
		if (_ddmTemplateKey != null) {
			return _ddmTemplateKey;
		}

		DDMTemplate portletDisplayDDMTemplate =
			_portletDisplayTemplate.getDefaultPortletDisplayTemplateDDMTemplate(
				_themeDisplay.getScopeGroupId(),
				PortalUtil.getClassNameId(NavItem.class));

		_ddmTemplateKey = portletDisplayDDMTemplate.getTemplateKey();

		return _ddmTemplateKey;
	}

	private DropdownItem _getDropdownItem(
		SiteNavigationMenuItemType siteNavigationMenuItemType,
		ThemeDisplay themeDisplay) {

		return DropdownItemBuilder.setData(
			HashMapBuilder.<String, Object>put(
				"addItemURL",
				() -> {
					if (!siteNavigationMenuItemType.isItemSelector()) {
						return null;
					}

					RenderRequest renderRequest =
						(RenderRequest)_httpServletRequest.getAttribute(
							JavaConstants.JAVAX_PORTLET_REQUEST);
					RenderResponse renderResponse =
						(RenderResponse)_httpServletRequest.getAttribute(
							JavaConstants.JAVAX_PORTLET_RESPONSE);

					return String.valueOf(
						siteNavigationMenuItemType.getAddURL(
							renderRequest, renderResponse));
				}
			).put(
				"addTitle",
				siteNavigationMenuItemType.getAddTitle(themeDisplay.getLocale())
			).put(
				"href", _getAddURL(siteNavigationMenuItemType)
			).put(
				"itemSelector", siteNavigationMenuItemType.isItemSelector()
			).put(
				"multiSelection", siteNavigationMenuItemType.isMultiSelection()
			).put(
				"siteNavigationMenuId", getSiteNavigationMenuId()
			).put(
				"type", siteNavigationMenuItemType.getType()
			).build()
		).setLabel(
			siteNavigationMenuItemType.getLabel(themeDisplay.getLocale())
		).build();
	}

	private long[] _getGroupIds(Group group) {
		if (group.isLayout()) {
			group = group.getParentGroup();
		}

		long groupId = group.getGroupId();

		if (group.isStagingGroup()) {
			Group liveGroup = group.getLiveGroup();

			if (!liveGroup.isStagedPortlet(TemplatePortletKeys.TEMPLATE)) {
				groupId = liveGroup.getGroupId();
			}
		}

		return PortalUtil.getCurrentAndAncestorSiteGroupIds(groupId);
	}

	private String _getPreviewSiteNavigationMenuURL() {
		LiferayPortletURL liferayPortletURL =
			(LiferayPortletURL)ResourceURLBuilder.createResourceURL(
				_liferayPortletResponse
			).setParameter(
				"siteNavigationMenuId",
				String.valueOf(getSiteNavigationMenuId())
			).setResourceID(
				"/site_navigation_admin/get_site_navigation_menu_preview"
			).buildResourceURL();

		liferayPortletURL.setCopyCurrentRenderParameters(false);

		return liferayPortletURL.toString();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SiteNavigationAdminDisplayContext.class);

	private String _ddmTemplateKey;
	private String _displayStyle;
	private final HttpServletRequest _httpServletRequest;
	private String _keywords;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private String _orderByCol;
	private String _orderByType;
	private final PortletDisplayTemplate _portletDisplayTemplate;
	private SearchContainer<SiteNavigationMenu> _searchContainer;
	private Long _siteNavigationMenuId;
	private final SiteNavigationMenuItemTypeRegistry
		_siteNavigationMenuItemTypeRegistry;
	private final SiteNavigationMenuLocalService
		_siteNavigationMenuLocalService;
	private String _siteNavigationMenuName;
	private final SiteNavigationMenuService _siteNavigationMenuService;
	private final ThemeDisplay _themeDisplay;
	private Boolean _updatePermission;

}