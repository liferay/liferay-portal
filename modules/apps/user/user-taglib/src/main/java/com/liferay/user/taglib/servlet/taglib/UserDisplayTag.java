/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.taglib.servlet.taglib;

import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.taglib.util.IncludeTag;
import com.liferay.user.taglib.internal.servlet.ServletContextUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Brian Wing Shun Chan
 */
public class UserDisplayTag extends IncludeTag {

	@Override
	public int doEndTag() throws JspException {
		if (_getUser() == null) {
			return SKIP_BODY;
		}

		return super.doEndTag();
	}

	@Override
	public int doStartTag() throws JspException {
		if (_getUser() == null) {
			return SKIP_BODY;
		}

		return super.doStartTag();
	}

	public String getUrl() {
		return _url;
	}

	public long getUserId() {
		return _userId;
	}

	public String getUserName() {
		return _userName;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setUrl(String url) {
		_url = url;
	}

	public void setUserId(long userId) {
		_userId = userId;
	}

	public void setUserName(String userName) {
		_userName = userName;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_url = null;
		_user = null;
		_userId = 0;
		_userName = null;
	}

	@Override
	protected String getEndPage() {
		return _END_PAGE;
	}

	@Override
	protected String getStartPage() {
		return _START_PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute("liferay-user:user-display:url", _url);
		httpServletRequest.setAttribute(
			"liferay-user:user-display:user", _getUser());
		httpServletRequest.setAttribute(
			"liferay-user:user-display:userName", _userName);
	}

	private User _getUser() {
		if (_user != null) {
			return _user;
		}

		User user = UserLocalServiceUtil.fetchUserById(_userId);

		if ((user != null) && user.isGuestUser()) {
			user = null;
		}

		_user = user;

		return _user;
	}

	private static final String _END_PAGE = "/user_display/end.jsp";

	private static final String _START_PAGE = "/user_display/start.jsp";

	private String _url;
	private User _user;
	private long _userId;
	private String _userName;

}