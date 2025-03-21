/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.theme;

import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Miguel Pastor
 */
public class PortletMessagesTag extends IncludeTag {

	public Group getGroup() {
		return _group;
	}

	public Portlet getPortlet() {
		return _portlet;
	}

	public void setGroup(Group group) {
		_group = group;
	}

	public void setPortlet(Portlet portlet) {
		_portlet = portlet;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-theme:portlet-messages:group", _group);
		httpServletRequest.setAttribute(
			"liferay-theme:portlet-messages:portlet", _portlet);
	}

	private static final String _PAGE =
		"/html/taglib/theme/portlet_messages/page.jsp";

	private Group _group;
	private Portlet _portlet;

}