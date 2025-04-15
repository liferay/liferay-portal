/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.web.internal.display.context;

import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.web.internal.security.permission.resource.BlogsPermission;
import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItemListBuilder;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.trash.TrashHelper;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Sergio González
 */
public class BlogsManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public BlogsManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		SearchContainer<BlogsEntry> searchContainer, TrashHelper trashHelper) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			searchContainer);

		_trashHelper = trashHelper;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.putData("action", "deleteEntries");
				dropdownItem.setIcon("trash");
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "delete"));
				dropdownItem.setQuickAction(true);
			}
		).build();
	}

	@Override
	public Map<String, Object> getAdditionalProps() {
		return HashMapBuilder.<String, Object>put(
			"deleteEntriesCmd",
			() -> {
				if (_isTrashEnabled()) {
					return Constants.MOVE_TO_TRASH;
				}

				return Constants.DELETE;
			}
		).put(
			"deleteEntriesURL",
			() -> PortletURLBuilder.createActionURL(
				liferayPortletResponse
			).setActionName(
				"/blogs/edit_entry"
			).buildString()
		).put(
			"trashEnabled", _isTrashEnabled()
		).build();
	}

	@Override
	public String getClearResultsURL() {
		return PortletURLBuilder.createRenderURL(
			liferayPortletResponse
		).setMVCRenderCommandName(
			"/blogs/view"
		).buildString();
	}

	@Override
	public CreationMenu getCreationMenu() {
		if (!BlogsPermission.contains(
				_themeDisplay.getPermissionChecker(),
				_themeDisplay.getScopeGroupId(), ActionKeys.ADD_ENTRY)) {

			return null;
		}

		return CreationMenuBuilder.addDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					liferayPortletResponse.createRenderURL(),
					"mvcRenderCommandName", "/blogs/edit_entry", "redirect",
					currentURLObj.toString());
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "add-blog-entry"));
			}
		).build();
	}

	@Override
	public List<LabelItem> getFilterLabelItems() {
		if (!Objects.equals(getNavigation(), "mine")) {
			return null;
		}

		return LabelItemListBuilder.add(
			labelItem -> {
				labelItem.putData(
					"removeLabelURL",
					PortletURLBuilder.create(
						getPortletURL()
					).setParameter(
						"entriesNavigation", (String)null
					).buildString());

				labelItem.setCloseable(true);

				User user = _themeDisplay.getUser();

				labelItem.setLabel(
					String.format(
						"%s: %s", LanguageUtil.get(httpServletRequest, "owner"),
						user.getFullName()));
			}
		).build();
	}

	@Override
	public String getSearchActionURL() {
		return PortletURLBuilder.createRenderURL(
			liferayPortletResponse
		).setMVCRenderCommandName(
			"/blogs/search"
		).setNavigation(
			ParamUtil.getString(httpServletRequest, "navigation", "entries")
		).setParameter(
			"orderByCol", "relevance"
		).buildString();
	}

	@Override
	public String getSearchContainerId() {
		return "blogEntries";
	}

	@Override
	public String getSortingURL() {
		PortletURL sortingURL = getPortletURL();

		String orderByCol = getOrderByCol();

		if (Validator.isNotNull(orderByCol)) {
			sortingURL.setParameter(getOrderByColParam(), orderByCol);
		}

		if (!Objects.equals(orderByCol, "relevance")) {
			sortingURL.setParameter(
				getOrderByTypeParam(),
				Objects.equals(getOrderByType(), "asc") ? "desc" : "asc");
		}

		return sortingURL.toString();
	}

	@Override
	protected String getDefaultDisplayStyle() {
		return "icon";
	}

	@Override
	protected String[] getDisplayViews() {
		return new String[] {"list", "descriptive", "icon"};
	}

	@Override
	protected String[] getNavigationKeys() {
		return new String[] {"all", "mine"};
	}

	@Override
	protected String getNavigationParam() {
		return "entriesNavigation";
	}

	@Override
	protected String[] getOrderByKeys() {
		if (_isSearch()) {
			return new String[] {"title", "display-date", "relevance"};
		}

		return new String[] {"title", "display-date"};
	}

	private String _getKeywords() {
		if (Validator.isNotNull(_keywords)) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(httpServletRequest, "keywords");

		return _keywords;
	}

	private boolean _isSearch() {
		return Validator.isNotNull(_getKeywords());
	}

	private boolean _isTrashEnabled() {
		try {
			return _trashHelper.isTrashEnabled(
				PortalUtil.getScopeGroupId(httpServletRequest));
		}
		catch (PortalException portalException) {
			return ReflectionUtil.throwException(portalException);
		}
	}

	private String _keywords;
	private final ThemeDisplay _themeDisplay;
	private final TrashHelper _trashHelper;

}