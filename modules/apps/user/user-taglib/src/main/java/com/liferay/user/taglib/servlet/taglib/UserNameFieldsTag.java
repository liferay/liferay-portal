/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.taglib.servlet.taglib;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.taglib.util.IncludeTag;
import com.liferay.user.taglib.internal.servlet.ServletContextUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Pei-Jung Lan
 */
public class UserNameFieldsTag extends IncludeTag {

	public Object getBean() {
		return _bean;
	}

	public Contact getContact() {
		return _contact;
	}

	public void setBean(Object bean) {
		_bean = bean;
	}

	public void setContact(Contact contact) {
		_contact = contact;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setUser(User user) {
		_user = user;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_bean = null;
		_contact = null;
		_user = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	protected User getUser() {
		if (_user == null) {
			try {
				return PortalUtil.getSelectedUser(getRequest());
			}
			catch (PortalException portalException) {
				_log.error(portalException);
			}
		}

		return _user;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		if (_bean == null) {
			_bean = pageContext.getAttribute("aui:model-context:bean");
		}

		httpServletRequest.setAttribute(
			"liferay-user:user-name-fields:bean", _bean);
		httpServletRequest.setAttribute(
			"liferay-user:user-name-fields:contact", _contact);
		httpServletRequest.setAttribute(
			"liferay-user:user-name-fields:user", getUser());
	}

	private static final String _PAGE = "/user_name_fields/page.jsp";

	private static final Log _log = LogFactoryUtil.getLog(
		UserNameFieldsTag.class);

	private Object _bean;
	private Contact _contact;
	private User _user;

}