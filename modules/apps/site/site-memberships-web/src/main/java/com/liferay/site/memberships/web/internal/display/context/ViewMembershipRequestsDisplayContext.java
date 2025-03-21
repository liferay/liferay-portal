/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.memberships.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemListBuilder;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.MembershipRequest;
import com.liferay.portal.kernel.model.MembershipRequestConstants;
import com.liferay.portal.kernel.portlet.SearchDisplayStyleUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.MembershipRequestLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.memberships.constants.SiteMembershipsPortletKeys;
import com.liferay.site.memberships.web.internal.servlet.taglib.util.ViewMembershipRequetsPendingActionDropdownItemsProvider;
import com.liferay.site.memberships.web.internal.util.comparator.MembershipRequestCreateDateComparator;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class ViewMembershipRequestsDisplayContext {

	public ViewMembershipRequestsDisplayContext(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
	}

	public List<DropdownItem> getActionDropdownItems(
			MembershipRequest membershipRequest)
		throws Exception {

		ViewMembershipRequetsPendingActionDropdownItemsProvider
			viewMembershipRequetsPendingActionDropdownItemsProvider =
				new ViewMembershipRequetsPendingActionDropdownItemsProvider(
					membershipRequest, _renderRequest, _renderResponse);

		return viewMembershipRequetsPendingActionDropdownItemsProvider.
			getActionDropdownItems();
	}

	public String getDisplayStyle() {
		if (Validator.isNotNull(_displayStyle)) {
			return _displayStyle;
		}

		_displayStyle = SearchDisplayStyleUtil.getDisplayStyle(
			_httpServletRequest,
			SiteMembershipsPortletKeys.SITE_MEMBERSHIPS_ADMIN, "icon");

		return _displayStyle;
	}

	public List<NavigationItem> getNavigationItems() {
		return NavigationItemListBuilder.add(
			navigationItem -> {
				navigationItem.setActive(Objects.equals(getTabs1(), "pending"));
				navigationItem.setHref(getPortletURL(), "tabs1", "pending");
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "pending"));
			}
		).add(
			navigationItem -> {
				navigationItem.setActive(
					Objects.equals(getTabs1(), "approved"));
				navigationItem.setHref(getPortletURL(), "tabs1", "approved");
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "approved"));
			}
		).add(
			navigationItem -> {
				navigationItem.setActive(Objects.equals(getTabs1(), "denied"));
				navigationItem.setHref(getPortletURL(), "tabs1", "denied");
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "denied"));
			}
		).build();
	}

	public String getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_httpServletRequest,
			SiteMembershipsPortletKeys.SITE_MEMBERSHIPS_ADMIN, "date");

		return _orderByCol;
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_httpServletRequest,
			SiteMembershipsPortletKeys.SITE_MEMBERSHIPS_ADMIN, "asc");

		return _orderByType;
	}

	public PortletURL getPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCPath(
			"/view_membership_requests.jsp"
		).setTabs1(
			() -> {
				String tabs1 = getTabs1();

				if (Validator.isNotNull(tabs1)) {
					return tabs1;
				}

				return null;
			}
		).setParameter(
			"displayStyle",
			() -> {
				String displayStyle = getDisplayStyle();

				if (Validator.isNotNull(displayStyle)) {
					return displayStyle;
				}

				return null;
			}
		).setParameter(
			"groupId",
			() -> {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)_httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				return themeDisplay.getSiteGroupIdOrLiveGroupId();
			}
		).setParameter(
			"orderByCol",
			() -> {
				String orderByCol = getOrderByCol();

				if (Validator.isNotNull(orderByCol)) {
					return orderByCol;
				}

				return null;
			}
		).setParameter(
			"orderByType",
			() -> {
				String orderByType = getOrderByType();

				if (Validator.isNotNull(orderByType)) {
					return orderByType;
				}

				return null;
			}
		).buildPortletURL();
	}

	public SearchContainer<MembershipRequest>
		getSiteMembershipSearchContainer() {

		if (_siteMembershipSearchContainer != null) {
			return _siteMembershipSearchContainer;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		SearchContainer<MembershipRequest> siteMembershipSearchContainer =
			new SearchContainer(
				_renderRequest, getPortletURL(), null,
				"no-requests-were-found");

		siteMembershipSearchContainer.setOrderByCol(getOrderByCol());

		boolean orderByAsc = false;

		if (Objects.equals(getOrderByType(), "asc")) {
			orderByAsc = true;
		}

		siteMembershipSearchContainer.setOrderByComparator(
			MembershipRequestCreateDateComparator.getInstance(orderByAsc));
		siteMembershipSearchContainer.setOrderByType(getOrderByType());
		siteMembershipSearchContainer.setResultsAndTotal(
			() -> MembershipRequestLocalServiceUtil.search(
				themeDisplay.getSiteGroupIdOrLiveGroupId(), getStatusId(),
				siteMembershipSearchContainer.getStart(),
				siteMembershipSearchContainer.getEnd(),
				siteMembershipSearchContainer.getOrderByComparator()),
			MembershipRequestLocalServiceUtil.searchCount(
				themeDisplay.getSiteGroupIdOrLiveGroupId(), getStatusId()));

		_siteMembershipSearchContainer = siteMembershipSearchContainer;

		return _siteMembershipSearchContainer;
	}

	public int getStatusId() {
		if (Objects.equals(getTabs1(), "approved")) {
			return MembershipRequestConstants.STATUS_APPROVED;
		}
		else if (Objects.equals(getTabs1(), "denied")) {
			return MembershipRequestConstants.STATUS_DENIED;
		}

		return MembershipRequestConstants.STATUS_PENDING;
	}

	public String getTabs1() {
		if (_tabs1 != null) {
			return _tabs1;
		}

		_tabs1 = ParamUtil.getString(_httpServletRequest, "tabs1", "pending");

		return _tabs1;
	}

	private String _displayStyle;
	private final HttpServletRequest _httpServletRequest;
	private String _orderByCol;
	private String _orderByType;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private SearchContainer<MembershipRequest> _siteMembershipSearchContainer;
	private String _tabs1;

}