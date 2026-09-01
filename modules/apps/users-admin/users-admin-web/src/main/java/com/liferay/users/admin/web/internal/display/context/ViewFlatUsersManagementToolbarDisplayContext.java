/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.display.context;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalServiceUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItemList;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.OrganizationLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.PortalPermissionUtil;
import com.liferay.portal.kernel.service.permission.UserPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.users.admin.management.toolbar.FilterContributor;
import com.liferay.users.admin.search.UserSearchTerms;
import com.liferay.users.admin.web.internal.util.DisplayStyleUtil;
import com.liferay.users.admin.web.internal.util.UsersAdminPortletURLUtil;

import jakarta.portlet.PortletURL;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.LongFunction;
import java.util.function.ToLongFunction;

/**
 * @author Pei-Jung Lan
 */
public class ViewFlatUsersManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public ViewFlatUsersManagementToolbarDisplayContext(
		FilterContributor[] filterContributors,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		SearchContainer<User> searchContainer, boolean showDeleteButton,
		boolean showRestoreButton) {

		super(
			liferayPortletRequest.getHttpServletRequest(),
			liferayPortletRequest, liferayPortletResponse, searchContainer);

		_filterContributors = filterContributors;
		_showDeleteButton = showDeleteButton;
		_showRestoreButton = showRestoreButton;

		_navigation = ParamUtil.getString(
			liferayPortletRequest, "navigation", "active");
		_selection = _getSelection(liferayPortletRequest);
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		UserSearchTerms userSearchTerms =
			(UserSearchTerms)searchContainer.getSearchTerms();

		List<DropdownItem> dropdownItems = DropdownItemListBuilder.add(
			() ->
				_showRestoreButton &&
				UserPermissionUtil.contains(
					permissionChecker, ResourceConstants.PRIMKEY_DNE,
					ActionKeys.ACTIVATE),
			dropdownItem -> {
				dropdownItem.putData("action", "activateUsers");
				dropdownItem.putData(
					"activateUsersURL",
					PortletURLBuilder.createActionURL(
						liferayPortletResponse
					).setActionName(
						"/users_admin/edit_user"
					).setCMD(
						Constants.RESTORE
					).setNavigation(
						getNavigation()
					).buildString());
				dropdownItem.setIcon("undo");
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "activate"));
				dropdownItem.setQuickAction(true);
			}
		).add(
			() ->
				_showDeleteButton &&
				_hasDeletePermission(permissionChecker, userSearchTerms),
			dropdownItem -> {
				String action = "deleteUsers";
				String cmd = Constants.DELETE;

				if (userSearchTerms.isActive()) {
					action = "deactivateUsers";
					cmd = Constants.DEACTIVATE;
				}

				dropdownItem.putData("action", action);
				dropdownItem.putData(
					"editUsersURL",
					PortletURLBuilder.createActionURL(
						liferayPortletResponse
					).setActionName(
						"/users_admin/edit_user"
					).setCMD(
						cmd
					).setNavigation(
						getNavigation()
					).buildString());

				String icon = "times-circle";

				if (userSearchTerms.isActive()) {
					icon = "hidden";
				}

				dropdownItem.setIcon(icon);

				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, cmd));
				dropdownItem.setQuickAction(true);
			}
		).build();

		if (dropdownItems.isEmpty()) {
			return null;
		}

		return dropdownItems;
	}

	@Override
	public String getClearResultsURL() {
		return PortletURLBuilder.create(
			UsersAdminPortletURLUtil.removeSelectionParameters(getPortletURL())
		).setKeywords(
			StringPool.BLANK
		).setNavigation(
			(String)null
		).buildString();
	}

	@Override
	public CreationMenu getCreationMenu() {
		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					liferayPortletResponse.createRenderURL(),
					"mvcRenderCommandName", "/users_admin/edit_user");
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "add-user"));
			}
		).build();
	}

	@Override
	public List<DropdownItem> getFilterDropdownItems() {
		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					super.getFilterDropdownItems());
				dropdownGroupItem.setLabel(
					getFilterNavigationDropdownItemsLabel());
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					_getFilterNavigationDropdownItems());
				dropdownGroupItem.setLabel(
					LanguageUtil.get(httpServletRequest, "filter-by"));
			}
		).build();
	}

	@Override
	public List<LabelItem> getFilterLabelItems() {
		return new LabelItemList() {
			{
				if (Objects.equals(_selection, "selected-account-users")) {
					_addSelectedEntityFilterLabelItems(
						"no-accounts-were-found",
						ParamUtil.getLongValues(
							httpServletRequest, "accountEntryIds"),
						AccountEntryLocalServiceUtil::fetchAccountEntry,
						AccountEntry::getAccountEntryId, this,
						AccountEntry::getName, "accountEntryIds");
				}
				else if (Objects.equals(
							_selection, "selected-organization-users")) {

					_addSelectedEntityFilterLabelItems(
						"no-organizations-were-found",
						ParamUtil.getLongValues(
							httpServletRequest, "organizationIds"),
						OrganizationLocalServiceUtil::fetchOrganization,
						Organization::getOrganizationId, this,
						Organization::getName, "organizationIds");
				}

				if (!Objects.equals(_navigation, "active")) {
					add(
						labelItem -> {
							labelItem.putData(
								"removeLabelURL",
								PortletURLBuilder.create(
									getPortletURL()
								).setNavigation(
									(String)null
								).buildString());
							labelItem.setDismissible(true);
							labelItem.setLabel(
								String.format(
									"%s: %s",
									LanguageUtil.get(
										httpServletRequest, "status"),
									LanguageUtil.get(
										httpServletRequest, getNavigation())));
						});
				}
			}
		};
	}

	@Override
	public String getSearchFormName() {
		return "searchFm";
	}

	@Override
	public Boolean isSelectable() {
		return _showDeleteButton || _showRestoreButton;
	}

	@Override
	public Boolean isShowCreationMenu() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return PortalPermissionUtil.contains(
			themeDisplay.getPermissionChecker(), ActionKeys.ADD_USER);
	}

	@Override
	protected String getDisplayStyle() {
		return DisplayStyleUtil.getDisplayStyle(
			liferayPortletRequest, getDefaultDisplayStyle());
	}

	@Override
	protected String[] getDisplayViews() {
		return new String[] {"icon", "descriptive", "list"};
	}

	@Override
	protected String getFilterNavigationDropdownItemsLabel() {
		return LanguageUtil.get(httpServletRequest, "filter-by-status");
	}

	@Override
	protected String getNavigation() {
		return _navigation;
	}

	@Override
	protected String[] getNavigationKeys() {
		return new String[] {"all", "active", "inactive"};
	}

	@Override
	protected String[] getOrderByKeys() {
		String[] orderColumns = {
			"first-name", "last-login-date", "last-name", "screen-name"
		};

		if (searchContainer.isSearch()) {
			orderColumns = ArrayUtil.append(orderColumns, "relevance");
		}

		return orderColumns;
	}

	@Override
	protected PortletURL getPortletURL() {
		return PortletURLBuilder.create(
			super.getPortletURL()
		).setParameter(
			"orderByCol", getOrderByCol()
		).setParameter(
			"orderByType", getOrderByType()
		).buildPortletURL();
	}

	private <T> void _addSelectedEntityFilterLabelItems(
		String emptyLabelKey, long[] entityIds, LongFunction<T> fetchFunction,
		ToLongFunction<T> idFunction, LabelItemList labelItemList,
		Function<T, String> nameFunction, String parameterName) {

		List<T> entities = TransformUtil.transformToList(
			entityIds, fetchFunction::apply);

		if (entities.isEmpty()) {
			labelItemList.add(
				labelItem -> {
					labelItem.putData(
						"removeLabelURL",
						PortletURLBuilder.create(
							getPortletURL()
						).setParameter(
							parameterName, (String)null
						).setParameter(
							"selection", "all"
						).buildString());
					labelItem.setDismissible(true);
					labelItem.setLabel(
						LanguageUtil.get(httpServletRequest, emptyLabelKey));
				});

			return;
		}

		PortletURL portletURL = getPortletURL();
		long[] resolvedEntityIds = ListUtil.toLongArray(entities, idFunction);

		for (T entity : entities) {
			if (resolvedEntityIds.length == 1) {
				portletURL.setParameter(parameterName, (String)null);
				portletURL.setParameter("selection", "all");
			}
			else {
				portletURL.setParameter(
					parameterName,
					StringUtil.merge(
						ArrayUtil.remove(
							resolvedEntityIds, idFunction.applyAsLong(entity)),
						StringPool.COMMA));
			}

			labelItemList.add(
				labelItem -> {
					labelItem.putData("removeLabelURL", portletURL.toString());
					labelItem.setDismissible(true);
					labelItem.setLabel(
						HtmlUtil.escape(nameFunction.apply(entity)));
				});
		}
	}

	private List<DropdownItem> _getFilterNavigationDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.setActive(Objects.equals(_selection, "all"));
				dropdownItem.setHref(
					_removeFilterContributorParameters(
						UsersAdminPortletURLUtil.removeSelectionParameters(
							getPortletURL()
						).toString()));
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "all"));
			}
		).add(
			dropdownItem -> {
				dropdownItem.putData(
					"accountEntriesSelectorURL",
					PortletURLBuilder.createRenderURL(
						liferayPortletResponse
					).setMVCPath(
						"/select_account_entries.jsp"
					).setParameter(
						"selection", "selected-account-users"
					).setWindowState(
						LiferayWindowState.POP_UP
					).buildString());
				dropdownItem.putData("action", "selectAccountEntries");
				dropdownItem.putData(
					"dialogTitle",
					LanguageUtil.get(httpServletRequest, "select-accounts"));
				dropdownItem.putData(
					"redirectURL",
					_removeFilterContributorParameters(
						currentURLObj.toString()));
				dropdownItem.setActive(
					Objects.equals(_selection, "selected-account-users"));
				dropdownItem.setLabel(
					LanguageUtil.get(
						httpServletRequest, "selected-account-users"));
			}
		).add(
			dropdownItem -> {
				dropdownItem.putData("action", "selectOrganizations");
				dropdownItem.putData(
					"dialogTitle",
					LanguageUtil.get(
						httpServletRequest, "select-organizations"));
				dropdownItem.putData(
					"organizationsSelectorURL",
					PortletURLBuilder.createRenderURL(
						liferayPortletResponse
					).setMVCPath(
						"/select_organizations.jsp"
					).setParameter(
						"selection", "selected-organization-users"
					).setWindowState(
						LiferayWindowState.POP_UP
					).buildString());
				dropdownItem.putData(
					"redirectURL",
					_removeFilterContributorParameters(
						currentURLObj.toString()));
				dropdownItem.setActive(
					Objects.equals(_selection, "selected-organization-users"));
				dropdownItem.setLabel(
					LanguageUtil.get(
						httpServletRequest, "selected-organization-users"));
			}
		).build();
	}

	private String _getSelection(LiferayPortletRequest liferayPortletRequest) {
		String selection = ParamUtil.getString(
			liferayPortletRequest, "selection", "all");

		if (Objects.equals(selection, "selected-account-users") &&
			(ParamUtil.getLongValues(
				liferayPortletRequest, "accountEntryIds").length == 0)) {

			return "all";
		}

		if (Objects.equals(selection, "selected-organization-users") &&
			(ParamUtil.getLongValues(
				liferayPortletRequest, "organizationIds").length == 0)) {

			return "all";
		}

		return selection;
	}

	private boolean _hasDeletePermission(
		PermissionChecker permissionChecker, UserSearchTerms userSearchTerms) {

		if (userSearchTerms.isActive()) {
			return UserPermissionUtil.contains(
				permissionChecker, ResourceConstants.PRIMKEY_DNE,
				ActionKeys.DEACTIVATE);
		}

		return UserPermissionUtil.contains(
			permissionChecker, ResourceConstants.PRIMKEY_DNE,
			ActionKeys.DELETE);
	}

	private String _removeFilterContributorParameters(String url) {
		if (_filterContributors == null) {
			return url;
		}

		for (FilterContributor filterContributor : _filterContributors) {
			url = HttpComponentsUtil.removeParameter(
				url,
				liferayPortletResponse.getNamespace() +
					filterContributor.getParameter());
		}

		return url;
	}

	private final FilterContributor[] _filterContributors;
	private final String _navigation;
	private final String _selection;
	private final boolean _showDeleteButton;
	private final boolean _showRestoreButton;

}