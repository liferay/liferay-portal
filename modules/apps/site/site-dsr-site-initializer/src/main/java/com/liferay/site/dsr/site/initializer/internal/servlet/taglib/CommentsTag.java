/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.site.initializer.internal.servlet.taglib;

import com.liferay.site.dsr.site.initializer.internal.servlet.ServletContextUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Stefano Motta
 */
public class CommentsTag extends IncludeTag {

	public long getRoomId() {
		return _roomId;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setRoomId(long roomId) {
		_roomId = roomId;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_roomId = 0;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-site-dsr-site-initializer:comments:roomId", _roomId);
	}

	private static final String _PAGE = "/comments/page.jsp";

	private long _roomId;

}