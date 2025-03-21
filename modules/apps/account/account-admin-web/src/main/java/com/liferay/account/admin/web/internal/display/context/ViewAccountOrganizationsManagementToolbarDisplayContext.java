/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.display.context;

import com.liferay.account.admin.web.internal.security.permission.resource.AccountEntryPermission;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalServiceUtil;
import com.liferay.account.service.AccountEntryOrganizationRelLocalServiceUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Pei-Jung Lan
 */
public class ViewAccountOrganizationsManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public ViewAccountOrganizationsManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		SearchContainer<Organization> searchContainer) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			searchContainer);
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		if (!_hasEditOrManageOrganizationsPermission()) {
			return null;
		}

		return DropdownItemList.of(
			DropdownItemBuilder.putData(
				"action", "removeOrganizations"
			).putData(
				"removeOrganizationsURL",
				PortletURLBuilder.createActionURL(
					liferayPortletResponse
				).setActionName(
					"/account_admin/remove_account_organizations"
				).setRedirect(
					currentURLObj
				).buildString()
			).setIcon(
				"times-circle"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "remove")
			).setQuickAction(
				true
			).build());
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
		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.putData("action", "selectAccountOrganizations");

				AccountEntry accountEntry =
					AccountEntryLocalServiceUtil.fetchAccountEntry(
						_getAccountEntryId());

				if (accountEntry != null) {
					dropdownItem.putData(
						"accountEntryName", accountEntry.getName());
				}

				dropdownItem.putData(
					"assignAccountOrganizationsURL",
					PortletURLBuilder.createActionURL(
						liferayPortletResponse
					).setActionName(
						"/account_admin/assign_account_organizations"
					).setRedirect(
						currentURLObj
					).buildString());

				dropdownItem.putData(
					"selectAccountOrganizationsURL",
					PortletURLBuilder.createRenderURL(
						liferayPortletResponse
					).setMVCPath(
						"/account_entries_admin" +
							"/select_account_organizations.jsp"
					).setParameter(
						"accountEntryId", accountEntry.getAccountEntryId()
					).setWindowState(
						LiferayWindowState.POP_UP
					).buildString());

				dropdownItem.setLabel(
					LanguageUtil.get(
						httpServletRequest, "assign-organizations"));
			}
		).build();
	}

	@Override
	public Boolean isDisabled() {
		long count =
			AccountEntryOrganizationRelLocalServiceUtil.
				getAccountEntryOrganizationRelsCount(_getAccountEntryId());

		if (count > 0) {
			return false;
		}

		return true;
	}

	@Override
	public Boolean isSelectable() {
		return _hasEditOrManageOrganizationsPermission();
	}

	@Override
	public Boolean isShowCreationMenu() {
		return _hasEditOrManageOrganizationsPermission();
	}

	@Override
	protected String getOrderByCol() {
		return ParamUtil.getString(
			liferayPortletRequest, getOrderByColParam(), "name");
	}

	@Override
	protected String[] getOrderByKeys() {
		return new String[] {"name"};
	}

	private long _getAccountEntryId() {
		return ParamUtil.getLong(liferayPortletRequest, "accountEntryId");
	}

	private boolean _hasEditOrManageOrganizationsPermission() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return AccountEntryPermission.hasEditOrManageOrganizationsPermission(
			themeDisplay.getPermissionChecker(), _getAccountEntryId());
	}

}