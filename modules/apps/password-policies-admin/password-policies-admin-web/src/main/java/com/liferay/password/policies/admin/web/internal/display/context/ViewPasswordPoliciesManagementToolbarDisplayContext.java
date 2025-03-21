/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.password.policies.admin.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.ViewTypeItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.ViewTypeItemList;
import com.liferay.password.policies.admin.constants.PasswordPoliciesAdminPortletKeys;
import com.liferay.password.policies.admin.web.internal.search.PasswordPolicyChecker;
import com.liferay.password.policies.admin.web.internal.search.PasswordPolicyDisplayTerms;
import com.liferay.password.policies.admin.web.internal.search.PasswordPolicySearch;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.PasswordPolicy;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.PasswordPolicyServiceUtil;
import com.liferay.portal.kernel.service.permission.PortalPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;

/**
 * @author Pei-Jung Lan
 */
public class ViewPasswordPoliciesManagementToolbarDisplayContext {

	public ViewPasswordPoliciesManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		RenderResponse renderResponse, String displayStyle) {

		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
		_displayStyle = displayStyle;
	}

	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.putData("action", "deletePasswordPolicies");
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

	public CreationMenu getCreationMenu() throws PortalException {
		return CreationMenuBuilder.addDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					_renderResponse.createRenderURL(), "mvcPath",
					"/edit_password_policy.jsp", "redirect",
					_renderResponse.createRenderURL());
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "add"));
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
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_httpServletRequest,
			PasswordPoliciesAdminPortletKeys.PASSWORD_POLICIES_ADMIN,
			"view-password-policies-order-by-col", "name");

		return _orderByCol;
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_httpServletRequest,
			PasswordPoliciesAdminPortletKeys.PASSWORD_POLICIES_ADMIN,
			"view-password-policies-order-by-type", "asc");

		return _orderByType;
	}

	public PortletURL getPortletURL() {
		PortletURL portletURL = PortletURLBuilder.createRenderURL(
			_renderResponse
		).setKeywords(
			() -> {
				if (Validator.isNotNull(getKeywords())) {
					return getKeywords();
				}

				return null;
			}
		).setParameter(
			"displayStyle", _displayStyle
		).setParameter(
			"orderByCol", getOrderByCol()
		).setParameter(
			"orderByType", getOrderByType()
		).buildPortletURL();

		if (_passwordPolicySearch != null) {
			portletURL.setParameter(
				_passwordPolicySearch.getCurParam(),
				String.valueOf(_passwordPolicySearch.getCur()));
			portletURL.setParameter(
				_passwordPolicySearch.getDeltaParam(),
				String.valueOf(_passwordPolicySearch.getDelta()));
		}

		return portletURL;
	}

	public String getSearchActionURL() {
		PortletURL searchActionURL = getPortletURL();

		return searchActionURL.toString();
	}

	public SearchContainer<PasswordPolicy> getSearchContainer()
		throws Exception {

		if (_passwordPolicySearch != null) {
			return _passwordPolicySearch;
		}

		PasswordPolicySearch passwordPolicySearch = new PasswordPolicySearch(
			_renderRequest, getPortletURL());

		passwordPolicySearch.setId("passwordPolicy");

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PasswordPolicyDisplayTerms searchTerms =
			(PasswordPolicyDisplayTerms)passwordPolicySearch.getSearchTerms();

		passwordPolicySearch.setResultsAndTotal(
			() -> PasswordPolicyServiceUtil.search(
				themeDisplay.getCompanyId(), searchTerms.getKeywords(),
				passwordPolicySearch.getStart(), passwordPolicySearch.getEnd(),
				passwordPolicySearch.getOrderByComparator()),
			PasswordPolicyServiceUtil.searchCount(
				themeDisplay.getCompanyId(), searchTerms.getKeywords()));

		passwordPolicySearch.setRowChecker(
			new PasswordPolicyChecker(_renderResponse));

		_passwordPolicySearch = passwordPolicySearch;

		return _passwordPolicySearch;
	}

	public String getSortingURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setParameter(
			"orderByType",
			Objects.equals(getOrderByType(), "asc") ? "desc" : "asc"
		).buildString();
	}

	public List<ViewTypeItem> getViewTypeItems() {
		return new ViewTypeItemList(getPortletURL(), _displayStyle) {
			{
				addListViewTypeItem();
				addTableViewTypeItem();
			}
		};
	}

	public boolean showCreationMenu() throws PortalException {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return PortalPermissionUtil.contains(
			themeDisplay.getPermissionChecker(),
			ActionKeys.ADD_PASSWORD_POLICY);
	}

	private final String _displayStyle;
	private final HttpServletRequest _httpServletRequest;
	private String _keywords;
	private String _orderByCol;
	private String _orderByType;
	private PasswordPolicySearch _passwordPolicySearch;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;

}