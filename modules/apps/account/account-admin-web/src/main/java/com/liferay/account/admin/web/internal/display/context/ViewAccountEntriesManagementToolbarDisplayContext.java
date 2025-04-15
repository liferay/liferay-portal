/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.display.context;

import com.liferay.account.admin.web.internal.constants.AccountWebKeys;
import com.liferay.account.admin.web.internal.display.AccountEntryDisplay;
import com.liferay.account.admin.web.internal.security.permission.resource.AccountEntryPermission;
import com.liferay.account.constants.AccountActionKeys;
import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItemListBuilder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.PortalPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowThreadLocal;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author Pei-Jung Lan
 */
public class ViewAccountEntriesManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public ViewAccountEntriesManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		SearchContainer<AccountEntryDisplay> searchContainer) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			searchContainer);

		_workflowEnabled = _isWorkflowEnabled(
			PortalUtil.getCompanyId(httpServletRequest));
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.add(
			() -> Objects.equals(getNavigation(), "active"),
			dropdownItem -> {
				dropdownItem.putData("action", "deactivateAccountEntries");
				dropdownItem.putData(
					"deactivateAccountEntriesURL",
					PortletURLBuilder.createActionURL(
						liferayPortletResponse
					).setActionName(
						"/account_admin/update_account_entry_status"
					).setCMD(
						Constants.DEACTIVATE
					).setNavigation(
						getNavigation()
					).buildString());
				dropdownItem.setIcon("hidden");
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "deactivate"));
				dropdownItem.setQuickAction(true);
			}
		).add(
			() -> Objects.equals(getNavigation(), "inactive"),
			dropdownItem -> {
				dropdownItem.putData("action", "activateAccountEntries");
				dropdownItem.putData(
					"activateAccountEntriesURL",
					PortletURLBuilder.createActionURL(
						liferayPortletResponse
					).setActionName(
						"/account_admin/update_account_entry_status"
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
			dropdownItem -> {
				dropdownItem.putData("action", "deleteAccountEntries");
				dropdownItem.putData(
					"deleteAccountEntriesURL",
					PortletURLBuilder.createActionURL(
						liferayPortletResponse
					).setActionName(
						"/account_admin/delete_account_entry"
					).setNavigation(
						getNavigation()
					).buildString());
				dropdownItem.setIcon("times-circle");
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "delete"));
				dropdownItem.setQuickAction(true);
			}
		).build();
	}

	public List<String> getAvailableActions(
			AccountEntryDisplay accountEntryDisplay)
		throws PortalException {

		List<String> availableActions = new ArrayList<>();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (AccountEntryPermission.contains(
				themeDisplay.getPermissionChecker(),
				accountEntryDisplay.getAccountEntryId(),
				ActionKeys.DEACTIVATE)) {

			if (accountEntryDisplay.isApproved()) {
				availableActions.add("deactivateAccountEntries");
			}
			else if (accountEntryDisplay.isInactive()) {
				availableActions.add("activateAccountEntries");
			}
		}

		if (AccountEntryPermission.contains(
				themeDisplay.getPermissionChecker(),
				accountEntryDisplay.getAccountEntryId(), ActionKeys.DELETE)) {

			availableActions.add("deleteAccountEntries");
		}

		return availableActions;
	}

	@Override
	public String getClearResultsURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setKeywords(
			StringPool.BLANK
		).setNavigation(
			(String)null
		).setParameter(
			"type", (String)null
		).buildString();
	}

	@Override
	public String getComponentId() {
		return "accountEntriesManagementToolbar";
	}

	@Override
	public CreationMenu getCreationMenu() {
		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					liferayPortletResponse.createRenderURL(),
					"mvcRenderCommandName", "/account_admin/edit_account_entry",
					"backURL", currentURLObj.toString());
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "add-account"));
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
					super.getFilterNavigationDropdownItemsLabel());
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					getDropdownItems(
						getDefaultEntriesMap(_getFilterByTypeKeys()),
						getPortletURL(), "type", _getType()));
				dropdownGroupItem.setLabel(
					LanguageUtil.get(httpServletRequest, "filter-by-type"));
			}
		).build();
	}

	@Override
	public List<LabelItem> getFilterLabelItems() {
		return LabelItemListBuilder.add(
			() -> !Objects.equals(getNavigation(), "active"),
			labelItem -> {
				labelItem.putData(
					"removeLabelURL",
					PortletURLBuilder.create(
						getPortletURL()
					).setNavigation(
						(String)null
					).buildString());
				labelItem.setCloseable(true);
				labelItem.setLabel(
					String.format(
						"%s: %s",
						LanguageUtil.get(httpServletRequest, "status"),
						LanguageUtil.get(httpServletRequest, getNavigation())));
			}
		).add(
			() -> !Objects.equals(_getType(), "all"),
			labelItem -> {
				labelItem.putData(
					"removeLabelURL",
					PortletURLBuilder.create(
						getPortletURL()
					).setParameter(
						"type", (String)null
					).buildString());
				labelItem.setCloseable(true);
				labelItem.setLabel(
					String.format(
						"%s: %s", LanguageUtil.get(httpServletRequest, "type"),
						LanguageUtil.get(httpServletRequest, _getType())));
			}
		).build();
	}

	@Override
	public String getFilterNavigationDropdownItemsLabel() {
		return LanguageUtil.get(httpServletRequest, "filter-by-status");
	}

	@Override
	public PortletURL getPortletURL() {
		try {
			return PortletURLUtil.clone(currentURLObj, liferayPortletResponse);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			return liferayPortletResponse.createRenderURL();
		}
	}

	@Override
	public Boolean isShowCreationMenu() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return PortalPermissionUtil.contains(
			themeDisplay.getPermissionChecker(),
			AccountActionKeys.ADD_ACCOUNT_ENTRY);
	}

	@Override
	protected String getNavigation() {
		return ParamUtil.getString(
			liferayPortletRequest, getNavigationParam(), "active");
	}

	@Override
	protected String[] getNavigationKeys() {
		String[] navigationKeys = {"all", "active", "inactive"};

		if (_workflowEnabled) {
			navigationKeys = ArrayUtil.append(
				navigationKeys, new String[] {"pending", "draft"});
		}

		return navigationKeys;
	}

	@Override
	protected String[] getOrderByKeys() {
		return new String[] {"name"};
	}

	private String[] _getFilterByTypeKeys() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)liferayPortletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return GetterUtil.getStringValues(
			liferayPortletRequest.getAttribute(
				AccountWebKeys.ACCOUNT_ENTRY_ALLOWED_TYPES),
			AccountConstants.getAccountEntryTypes(themeDisplay.getCompanyId()));
	}

	private String _getType() {
		return ParamUtil.getString(liferayPortletRequest, "type", "all");
	}

	private boolean _isWorkflowEnabled(long companyId) {
		Supplier<WorkflowDefinitionLink> workflowDefinitionLinkSupplier = () ->
			WorkflowDefinitionLinkLocalServiceUtil.fetchWorkflowDefinitionLink(
				companyId, GroupConstants.DEFAULT_LIVE_GROUP_ID,
				AccountEntry.class.getName(), 0, 0);

		if (WorkflowThreadLocal.isEnabled() &&
			(workflowDefinitionLinkSupplier.get() != null)) {

			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ViewAccountEntriesManagementToolbarDisplayContext.class);

	private final boolean _workflowEnabled;

}