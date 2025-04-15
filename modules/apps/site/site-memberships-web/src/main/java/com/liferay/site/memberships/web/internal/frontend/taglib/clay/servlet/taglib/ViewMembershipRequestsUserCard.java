/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.memberships.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.servlet.taglib.BaseUserCard;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.MembershipRequest;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;

/**
 * @author Eudaldo Alonso
 */
public class ViewMembershipRequestsUserCard extends BaseUserCard {

	public ViewMembershipRequestsUserCard(
		MembershipRequest membershipRequest, User user,
		RenderRequest renderRequest, RenderResponse renderResponse) {

		super(user, renderRequest, null);

		_membershipRequest = membershipRequest;
		_renderResponse = renderResponse;

		_httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
	}

	@Override
	public String getHref() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCPath(
			"/preview_membership_request.jsp"
		).setRedirect(
			themeDisplay.getURLCurrent()
		).setParameter(
			"membershipRequestId", _membershipRequest.getMembershipRequestId()
		).buildString();
	}

	@Override
	public String getSubtitle() {
		String replier = _getReplier();

		Date replyDate = _membershipRequest.getReplyDate();

		String replyDateDescription = LanguageUtil.getTimeDescription(
			_httpServletRequest,
			System.currentTimeMillis() - replyDate.getTime(), true);

		if (Validator.isNull(replier)) {
			return LanguageUtil.format(
				_httpServletRequest, "replied-x-ago", replyDateDescription);
		}

		return LanguageUtil.format(
			_httpServletRequest, "replied-by-x-x-ago",
			new String[] {replier, replyDateDescription});
	}

	private String _getReplier() {
		User membershipRequestReplierUser = UserLocalServiceUtil.fetchUserById(
			_membershipRequest.getReplierUserId());

		if (membershipRequestReplierUser == null) {
			return StringPool.BLANK;
		}

		if (!membershipRequestReplierUser.isGuestUser()) {
			return HtmlUtil.escape(membershipRequestReplierUser.getFullName());
		}

		try {
			Company membershipRequestReplierCompany =
				CompanyLocalServiceUtil.getCompanyById(
					membershipRequestReplierUser.getCompanyId());

			return HtmlUtil.escape(membershipRequestReplierCompany.getName());
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return StringPool.BLANK;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ViewMembershipRequestsUserCard.class);

	private final HttpServletRequest _httpServletRequest;
	private final MembershipRequest _membershipRequest;
	private final RenderResponse _renderResponse;

}