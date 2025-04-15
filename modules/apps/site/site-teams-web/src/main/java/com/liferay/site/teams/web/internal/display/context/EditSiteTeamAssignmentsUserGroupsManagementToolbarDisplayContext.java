/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.teams.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.user.groups.admin.item.selector.UserGroupSiteTeamItemSelectorCriterion;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class EditSiteTeamAssignmentsUserGroupsManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public EditSiteTeamAssignmentsUserGroupsManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		EditSiteTeamAssignmentsUserGroupsDisplayContext
			editSiteTeamAssignmentsUserGroupsDisplayContext) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			editSiteTeamAssignmentsUserGroupsDisplayContext.
				getUserGroupSearchContainer());

		_editSiteTeamAssignmentsUserGroupsDisplayContext =
			editSiteTeamAssignmentsUserGroupsDisplayContext;
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.putData("action", "deleteUserGroups");
				dropdownItem.setIcon("times-circle");
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
	public String getComponentId() {
		return "editTeamAssignemntsUserGroupsWebManagementToolbar";
	}

	@Override
	public CreationMenu getCreationMenu() {
		try {
			return CreationMenuBuilder.addDropdownItem(
				dropdownItem -> {
					dropdownItem.putData("action", "selectUserGroup");
					dropdownItem.putData(
						"selectUserGroupURL", _getSelectUserGroupURL());

					String title = LanguageUtil.format(
						httpServletRequest, "add-new-user-group-to-x",
						_editSiteTeamAssignmentsUserGroupsDisplayContext.
							getTeamName());

					dropdownItem.putData("title", title);

					dropdownItem.setLabel(
						LanguageUtil.get(httpServletRequest, "add"));
				}
			).build();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return null;
		}
	}

	@Override
	public String getSearchContainerId() {
		return "userGroups";
	}

	@Override
	public Boolean isShowCreationMenu() {
		return true;
	}

	@Override
	protected String getDisplayStyle() {
		return _editSiteTeamAssignmentsUserGroupsDisplayContext.
			getDisplayStyle();
	}

	@Override
	protected String[] getDisplayViews() {
		return new String[] {"list", "descriptive"};
	}

	@Override
	protected String[] getOrderByKeys() {
		return new String[] {"name", "description"};
	}

	private String _getSelectUserGroupURL() {
		ItemSelector itemSelector =
			(ItemSelector)httpServletRequest.getAttribute(
				ItemSelector.class.getName());

		UserGroupSiteTeamItemSelectorCriterion
			userGroupSiteTeamItemSelectorCriterion =
				new UserGroupSiteTeamItemSelectorCriterion();

		userGroupSiteTeamItemSelectorCriterion.
			setDesiredItemSelectorReturnTypes(new UUIDItemSelectorReturnType());
		userGroupSiteTeamItemSelectorCriterion.setTeamId(
			_editSiteTeamAssignmentsUserGroupsDisplayContext.getTeamId());

		return String.valueOf(
			itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(httpServletRequest),
				liferayPortletResponse.getNamespace() + "selectUserGroup",
				userGroupSiteTeamItemSelectorCriterion));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditSiteTeamAssignmentsUserGroupsManagementToolbarDisplayContext.class);

	private final EditSiteTeamAssignmentsUserGroupsDisplayContext
		_editSiteTeamAssignmentsUserGroupsDisplayContext;

}