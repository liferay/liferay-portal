/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.password.policies.admin.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.ViewTypeItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.ViewTypeItemList;
import com.liferay.organizations.search.OrganizationSearch;
import com.liferay.organizations.search.OrganizationSearchTerms;
import com.liferay.password.policies.admin.constants.PasswordPoliciesAdminPortletKeys;
import com.liferay.password.policies.admin.web.internal.search.AddOrganizationPasswordPolicyChecker;
import com.liferay.password.policies.admin.web.internal.search.AddUserPasswordPolicyChecker;
import com.liferay.password.policies.admin.web.internal.search.DeleteOrganizationPasswordPolicyChecker;
import com.liferay.password.policies.admin.web.internal.search.DeleteUserPasswordPolicyChecker;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.model.PasswordPolicy;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.ldap.LDAPSettingsUtil;
import com.liferay.portal.kernel.service.OrganizationLocalServiceUtil;
import com.liferay.portal.kernel.service.PasswordPolicyLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.users.admin.search.UserSearch;
import com.liferay.users.admin.search.UserSearchTerms;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

/**
 * @author Pei-Jung Lan
 */
public class EditPasswordPolicyAssignmentsManagementToolbarDisplayContext {

	public EditPasswordPolicyAssignmentsManagementToolbarDisplayContext(
			HttpServletRequest httpServletRequest, RenderRequest renderRequest,
			RenderResponse renderResponse, String displayStyle, String mvcPath)
		throws PortalException {

		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
		_displayStyle = displayStyle;
		_mvcPath = mvcPath;

		_passwordPolicy = PasswordPolicyLocalServiceUtil.fetchPasswordPolicy(
			ParamUtil.getLong(httpServletRequest, "passwordPolicyId"));
	}

	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				String action = StringPool.BLANK;

				if (_tabs2.equals("users")) {
					action = "deleteUsers";
				}
				else if (_tabs2.equals("organizations")) {
					action = "deleteOrganizations";
				}

				dropdownItem.putData("action", action);
				dropdownItem.setIcon("trash");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "delete"));
				dropdownItem.setQuickAction(true);
			}
		).build();
	}

	public String getClearResultsURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setKeywords(
			StringPool.BLANK
		).buildString();
	}

	public CreationMenu getCreationMenu() {
		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
			}
		).build();
	}

	public String getKeywords() {
		if (Validator.isNull(_keywords)) {
			_keywords = ParamUtil.getString(_httpServletRequest, "keywords");
		}

		return _keywords;
	}

	public String getOrderByCol() {
		if (Validator.isNull(_orderByCol)) {
			_orderByCol = ParamUtil.getString(
				_httpServletRequest, "orderByCol", _getDefaultOrderByCol());
		}

		if (!ArrayUtil.contains(_getOrderColumns(), _orderByCol)) {
			_orderByCol = _getDefaultOrderByCol();
		}

		return _orderByCol;
	}

	public List<DropdownItem> getOrderByDropdownItems() {
		return new DropdownItemList() {
			{
				for (String orderColumn : _getOrderColumns()) {
					add(
						dropdownItem -> {
							dropdownItem.setActive(
								Objects.equals(getOrderByCol(), orderColumn));
							dropdownItem.setHref(
								getPortletURL(), "orderByCol", orderColumn);
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, orderColumn));
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
			_httpServletRequest,
			PasswordPoliciesAdminPortletKeys.PASSWORD_POLICIES_ADMIN,
			"edit-password-policy-order-by-type", "asc");

		return _orderByType;
	}

	public SearchContainer<Organization> getOrganizationSearchContainer()
		throws PortalException {

		OrganizationSearch organizationSearch = new OrganizationSearch(
			_renderRequest, getPortletURL());

		RowChecker rowChecker = new AddOrganizationPasswordPolicyChecker(
			_renderResponse, _passwordPolicy);

		LinkedHashMap<String, Object> organizationParams =
			new LinkedHashMap<>();

		if (_mvcPath.equals("/edit_password_policy_assignments.jsp")) {
			rowChecker = new DeleteOrganizationPasswordPolicyChecker(
				_renderResponse, _passwordPolicy);

			organizationParams.put(
				"organizationsPasswordPolicies",
				Long.valueOf(_passwordPolicy.getPasswordPolicyId()));
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		OrganizationSearchTerms searchTerms =
			(OrganizationSearchTerms)organizationSearch.getSearchTerms();

		organizationSearch.setResultsAndTotal(
			() -> OrganizationLocalServiceUtil.search(
				themeDisplay.getCompanyId(),
				OrganizationConstants.ANY_PARENT_ORGANIZATION_ID, getKeywords(),
				searchTerms.getType(), searchTerms.getRegionIdObj(),
				searchTerms.getCountryIdObj(), organizationParams,
				organizationSearch.getStart(), organizationSearch.getEnd(),
				organizationSearch.getOrderByComparator()),
			OrganizationLocalServiceUtil.searchCount(
				themeDisplay.getCompanyId(),
				OrganizationConstants.ANY_PARENT_ORGANIZATION_ID,
				searchTerms.getKeywords(), searchTerms.getType(),
				searchTerms.getRegionIdObj(), searchTerms.getCountryIdObj(),
				organizationParams));

		organizationSearch.setRowChecker(rowChecker);

		return organizationSearch;
	}

	public PortletURL getPortletURL() {
		PortletURL portletURL = PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCPath(
			_mvcPath
		).setRedirect(
			ParamUtil.getString(_httpServletRequest, "redirect")
		).setKeywords(
			() -> {
				if (Validator.isNotNull(getKeywords())) {
					return getKeywords();
				}

				return null;
			}
		).setTabs1(
			"assignees"
		).setTabs2(
			getTabs2()
		).setParameter(
			"displayStyle", _displayStyle
		).setParameter(
			"orderByCol", getOrderByCol()
		).setParameter(
			"orderByType", getOrderByType()
		).setParameter(
			"passwordPolicyId", _passwordPolicy.getPasswordPolicyId()
		).buildPortletURL();

		if (_searchContainer != null) {
			portletURL.setParameter(
				_searchContainer.getCurParam(),
				String.valueOf(_searchContainer.getCur()));
			portletURL.setParameter(
				_searchContainer.getDeltaParam(),
				String.valueOf(_searchContainer.getDelta()));
		}

		return portletURL;
	}

	public String getSearchActionURL() {
		PortletURL searchActionURL = getPortletURL();

		return searchActionURL.toString();
	}

	public SearchContainer<?> getSearchContainer() throws Exception {
		if (Objects.equals(getTabs2(), "organizations")) {
			_searchContainer = getOrganizationSearchContainer();
		}
		else {
			_searchContainer = getUserSearchContainer();
		}

		return _searchContainer;
	}

	public String getSortingURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setParameter(
			"orderByType",
			Objects.equals(getOrderByType(), "asc") ? "desc" : "asc"
		).buildString();
	}

	public String getTabs2() {
		if (Validator.isNull(_tabs2)) {
			_tabs2 = ParamUtil.getString(_httpServletRequest, "tabs2", "users");
		}

		return _tabs2;
	}

	public SearchContainer<User> getUserSearchContainer() {
		UserSearch userSearch = new UserSearch(_renderRequest, getPortletURL());

		RowChecker rowChecker = new AddUserPasswordPolicyChecker(
			_renderResponse, _passwordPolicy);

		LinkedHashMap<String, Object> userParams = new LinkedHashMap<>();

		if (_mvcPath.equals("/edit_password_policy_assignments.jsp")) {
			rowChecker = new DeleteUserPasswordPolicyChecker(
				_renderResponse, _passwordPolicy);

			userParams.put(
				"usersPasswordPolicies",
				Long.valueOf(_passwordPolicy.getPasswordPolicyId()));
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (LDAPSettingsUtil.isPasswordPolicyEnabled(
				themeDisplay.getCompanyId())) {

			userParams.put("noLDAPUsers", true);
		}

		UserSearchTerms searchTerms =
			(UserSearchTerms)userSearch.getSearchTerms();

		userSearch.setResultsAndTotal(
			() -> UserLocalServiceUtil.search(
				themeDisplay.getCompanyId(), searchTerms.getKeywords(),
				searchTerms.getStatus(), userParams, userSearch.getStart(),
				userSearch.getEnd(), userSearch.getOrderByComparator()),
			UserLocalServiceUtil.searchCount(
				themeDisplay.getCompanyId(), searchTerms.getKeywords(),
				searchTerms.getStatus(), userParams));

		userSearch.setRowChecker(rowChecker);

		return userSearch;
	}

	public List<ViewTypeItem> getViewTypeItems() {
		return new ViewTypeItemList(getPortletURL(), _displayStyle) {
			{
				addCardViewTypeItem();
				addListViewTypeItem();
				addTableViewTypeItem();
			}
		};
	}

	private String _getDefaultOrderByCol() {
		if (_tabs2.equals("users")) {
			return "last-name";
		}

		return "name";
	}

	private String[] _getOrderColumns() {
		if (_tabs2.equals("users")) {
			return new String[] {"first-name", "last-name", "screen-name"};
		}

		if (_tabs2.equals("organizations")) {
			return new String[] {"name", "type"};
		}

		return new String[0];
	}

	private final String _displayStyle;
	private final HttpServletRequest _httpServletRequest;
	private String _keywords;
	private final String _mvcPath;
	private String _orderByCol;
	private String _orderByType;
	private final PasswordPolicy _passwordPolicy;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private SearchContainer<?> _searchContainer;
	private String _tabs2;

}