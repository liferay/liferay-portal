/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.memberships.web.internal.servlet.taglib.util;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.MembershipRequest;
import com.liferay.portal.kernel.model.MembershipRequestConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class ViewMembershipRequetsPendingActionDropdownItemsProvider {

	public ViewMembershipRequetsPendingActionDropdownItemsProvider(
		MembershipRequest membershipRequest, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_membershipRequest = membershipRequest;
		_renderResponse = renderResponse;

		_httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);

		_themeDisplay = (ThemeDisplay)_httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getActionDropdownItems() throws Exception {
		return DropdownItemListBuilder.add(
			() ->
				(_membershipRequest.getStatusId() ==
					MembershipRequestConstants.STATUS_PENDING) &&
				GroupPermissionUtil.contains(
					_themeDisplay.getPermissionChecker(),
					_themeDisplay.getSiteGroupIdOrLiveGroupId(),
					ActionKeys.ASSIGN_MEMBERS),
			_getReplyRequestActionUnsafeConsumer()
		).build();
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getReplyRequestActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.setHref(
				_renderResponse.createRenderURL(), "p_u_i_d",
				_membershipRequest.getUserId(), "mvcPath",
				"/reply_membership_request.jsp", "groupId",
				_themeDisplay.getSiteGroupIdOrLiveGroupId(),
				"membershipRequestId",
				_membershipRequest.getMembershipRequestId());
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "reply"));
		};
	}

	private final HttpServletRequest _httpServletRequest;
	private final MembershipRequest _membershipRequest;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}