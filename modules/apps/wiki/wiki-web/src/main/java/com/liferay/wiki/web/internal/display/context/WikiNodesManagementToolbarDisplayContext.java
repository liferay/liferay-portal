/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.ViewTypeItemList;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.SearchDisplayStyleUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.trash.TrashHelper;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.service.WikiNodeServiceUtil;
import com.liferay.wiki.web.internal.search.NodesChecker;
import com.liferay.wiki.web.internal.security.permission.resource.WikiNodePermission;
import com.liferay.wiki.web.internal.security.permission.resource.WikiResourcePermission;
import com.liferay.wiki.web.internal.util.WikiPortletUtil;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Alejandro Tardín
 */
public class WikiNodesManagementToolbarDisplayContext {

	public WikiNodesManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		RenderRequest renderRequest, TrashHelper trashHelper) {

		_httpServletRequest = httpServletRequest;
		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_renderRequest = renderRequest;
		_trashHelper = trashHelper;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.putData("action", "deleteNodes");
				dropdownItem.setIcon("trash");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "delete"));
				dropdownItem.setQuickAction(true);
			}
		).build();
	}

	public Map<String, Object> getAdditionalProps() {
		return HashMapBuilder.<String, Object>put(
			"deleteNodesCmd",
			() -> {
				if (_isTrashEnabled()) {
					return Constants.MOVE_TO_TRASH;
				}

				return Constants.DELETE;
			}
		).put(
			"deleteNodesURL",
			() -> PortletURLBuilder.createActionURL(
				_liferayPortletResponse
			).setActionName(
				"/wiki/edit_node"
			).buildString()
		).put(
			"trashEnabled", _isTrashEnabled()
		).build();
	}

	public List<String> getAvailableActions(WikiNode wikiNode)
		throws PortalException {

		List<String> availableActions = new ArrayList<>();

		if (WikiNodePermission.contains(
				_themeDisplay.getPermissionChecker(), wikiNode,
				ActionKeys.DELETE)) {

			availableActions.add("deleteNodes");
		}

		return availableActions;
	}

	public CreationMenu getCreationMenu() {
		if (!WikiResourcePermission.contains(
				_themeDisplay.getPermissionChecker(),
				_themeDisplay.getScopeGroupId(), ActionKeys.ADD_NODE)) {

			return null;
		}

		return CreationMenuBuilder.addDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					_liferayPortletResponse.createRenderURL(),
					"mvcRenderCommandName", "/wiki/edit_node", "redirect",
					PortletURLBuilder.createRenderURL(
						_liferayPortletResponse
					).setMVCRenderCommandName(
						"/wiki_admin/view"
					).buildString());
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "add-wiki"));
			}
		).build();
	}

	public String getDisplayStyle() {
		if (Validator.isNotNull(_displayStyle)) {
			return _displayStyle;
		}

		_displayStyle = SearchDisplayStyleUtil.getDisplayStyle(
			_httpServletRequest, WikiPortletKeys.WIKI_ADMIN,
			"nodes-display-style", "descriptive", true);

		return _displayStyle;
	}

	public String getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_httpServletRequest, WikiPortletKeys.WIKI_ADMIN,
			"nodes-order-by-col", "lastPostDate");

		return _orderByCol;
	}

	public List<DropdownItem> getOrderByDropdownItems() {
		return new DropdownItemList() {
			{
				final Map<String, String> orderColumns = HashMapBuilder.put(
					"lastPostDate", "last-post-date"
				).put(
					"name", "name"
				).build();

				for (Map.Entry<String, String> orderByColEntry :
						orderColumns.entrySet()) {

					String orderByCol = orderByColEntry.getKey();

					add(
						dropdownItem -> {
							dropdownItem.setActive(
								orderByCol.equals(getOrderByCol()));
							dropdownItem.setHref(
								getPortletURL(), "orderByCol", orderByCol);
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest,
									orderByColEntry.getValue()));
						});
				}
			}
		};
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_httpServletRequest, WikiPortletKeys.WIKI_ADMIN,
			"nodes-order-by-type", "desc");

		return _orderByType;
	}

	public PortletURL getPortletURL() throws PortletException {
		if (_portletURL != null) {
			return _portletURL;
		}

		_portletURL = PortletURLBuilder.create(
			PortletURLUtil.clone(
				PortletURLUtil.getCurrent(
					_liferayPortletRequest, _liferayPortletResponse),
				_liferayPortletResponse)
		).setMVCRenderCommandName(
			"/wiki_admin/view"
		).buildPortletURL();

		return _portletURL;
	}

	public SearchContainer<WikiNode> getSearchContainer() throws Exception {
		if (_searchContainer != null) {
			return _searchContainer;
		}

		_searchContainer = new SearchContainer(
			_renderRequest, getPortletURL(), null, "there-are-no-wikis");

		_searchContainer.setOrderByCol(getOrderByCol());
		_searchContainer.setOrderByComparator(
			WikiPortletUtil.getNodeOrderByComparator(
				getOrderByCol(), getOrderByType()));
		_searchContainer.setOrderByType(getOrderByType());
		_searchContainer.setResultsAndTotal(
			() -> WikiNodeServiceUtil.getNodes(
				_themeDisplay.getScopeGroupId(),
				WorkflowConstants.STATUS_APPROVED, _searchContainer.getStart(),
				_searchContainer.getEnd(),
				_searchContainer.getOrderByComparator()),
			WikiNodeServiceUtil.getNodesCount(_themeDisplay.getScopeGroupId()));
		_searchContainer.setRowChecker(
			new NodesChecker(_liferayPortletRequest, _liferayPortletResponse));

		return _searchContainer;
	}

	public int getSearchContainerTotal() throws Exception {
		SearchContainer<WikiNode> searchContainer = getSearchContainer();

		return searchContainer.getTotal();
	}

	public String getSortingOrder() {
		return getOrderByType();
	}

	public PortletURL getSortingURL() throws PortletException {
		return PortletURLBuilder.create(
			getPortletURL()
		).setParameter(
			"orderByCol", getOrderByCol()
		).setParameter(
			"orderByType",
			Objects.equals(getOrderByType(), "asc") ? "desc" : "asc"
		).buildPortletURL();
	}

	public int getTotalItems() throws Exception {
		SearchContainer<WikiNode> searchContainer = getSearchContainer();

		return searchContainer.getTotal();
	}

	public ViewTypeItemList getViewTypes() throws PortletException {
		return new ViewTypeItemList(getPortletURL(), getDisplayStyle()) {
			{
				addListViewTypeItem();
				addTableViewTypeItem();
			}
		};
	}

	public boolean isDisabled() throws Exception {
		SearchContainer<WikiNode> searchContainer = getSearchContainer();

		return !searchContainer.hasResults();
	}

	public boolean isSelectable() {
		return true;
	}

	public boolean isShowSearch() {
		return false;
	}

	private boolean _isTrashEnabled() {
		try {
			return _trashHelper.isTrashEnabled(
				PortalUtil.getScopeGroupId(_httpServletRequest));
		}
		catch (PortalException portalException) {
			return ReflectionUtil.throwException(portalException);
		}
	}

	private String _displayStyle;
	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private String _orderByCol;
	private String _orderByType;
	private PortletURL _portletURL;
	private final RenderRequest _renderRequest;
	private SearchContainer<WikiNode> _searchContainer;
	private final ThemeDisplay _themeDisplay;
	private final TrashHelper _trashHelper;

}