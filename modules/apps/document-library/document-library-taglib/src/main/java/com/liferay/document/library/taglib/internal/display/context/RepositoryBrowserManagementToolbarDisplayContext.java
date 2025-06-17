/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.taglib.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Set;

/**
 * @author Adolfo Pérez
 */
public class RepositoryBrowserManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public RepositoryBrowserManagementToolbarDisplayContext(
		Set<String> actions, long folderId,
		ModelResourcePermission<Folder> folderModelResourcePermission,
		HttpServletRequest httpServletRequest, boolean includeExtension,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse, long repositoryId,
		SearchContainer<Object> searchContainer, boolean viewableByGuest) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			searchContainer);

		_actions = actions;
		_folderId = folderId;
		_folderModelResourcePermission = folderModelResourcePermission;
		_includeExtension = includeExtension;
		_repositoryId = repositoryId;
		_viewableByGuest = viewableByGuest;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.add(
			() -> {
				if (!_actions.contains("delete")) {
					return false;
				}

				User user = _themeDisplay.getUser();

				return !user.isGuestUser();
			},
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
	public String getClearResultsURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setKeywords(
			StringPool.BLANK
		).buildString();
	}

	@Override
	public CreationMenu getCreationMenu() {
		if (!_actions.contains("add-folder") && !_actions.contains("upload")) {
			return null;
		}

		return CreationMenuBuilder.addDropdownItem(
			() ->
				_actions.contains("upload") &&
				ModelResourcePermissionUtil.contains(
					_folderModelResourcePermission,
					_themeDisplay.getPermissionChecker(),
					_themeDisplay.getScopeGroupId(), _folderId,
					ActionKeys.ADD_DOCUMENT),
			dropdownItem -> {
				dropdownItem.putData("action", "uploadFile");
				dropdownItem.putData(
					"includeExtension", String.valueOf(_includeExtension));
				dropdownItem.setIcon("upload");
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "file-upload"));
			}
		).addDropdownItem(
			() ->
				_actions.contains("add-folder") &&
				ModelResourcePermissionUtil.contains(
					_folderModelResourcePermission,
					_themeDisplay.getPermissionChecker(),
					_themeDisplay.getScopeGroupId(), _folderId,
					ActionKeys.ADD_FOLDER),
			dropdownItem -> {
				dropdownItem.putData("action", "addFolder");
				dropdownItem.putData(
					"parentFolderId", String.valueOf(_folderId));
				dropdownItem.putData(
					"repositoryId", String.valueOf(_repositoryId));
				dropdownItem.putData(
					"viewableByGuest", String.valueOf(_viewableByGuest));
				dropdownItem.setIcon("folder");
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "folder"));
			}
		).build();
	}

	@Override
	public String[] getDisplayViews() {
		return new String[] {"icon", "descriptive", "list"};
	}

	@Override
	public String getSearchActionURL() {
		return String.valueOf(getPortletURL());
	}

	@Override
	public String getSearchContainerId() {
		return "repositoryEntries";
	}

	@Override
	public Boolean isSelectable() {
		return !_actions.isEmpty();
	}

	@Override
	protected String getDefaultDisplayStyle() {
		return "icon";
	}

	@Override
	protected String[] getOrderByKeys() {
		if (Validator.isNotNull(
				ParamUtil.getString(httpServletRequest, "keywords"))) {

			return null;
		}

		return new String[] {"modified-date", "size", "title"};
	}

	private final Set<String> _actions;
	private final long _folderId;
	private final ModelResourcePermission<Folder>
		_folderModelResourcePermission;
	private final boolean _includeExtension;
	private final long _repositoryId;
	private final ThemeDisplay _themeDisplay;
	private final boolean _viewableByGuest;

}