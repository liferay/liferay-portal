/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.trash.taglib.servlet.taglib;

import com.liferay.taglib.util.IncludeTag;
import com.liferay.trash.taglib.internal.servlet.ServletContextUtil;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

/**
 * Creates a component for users to permanently delete items (articles, images,
 * etc.) from the Recycle Bin.
 *
 * @author Sergio González
 */
public class EmptyTag extends IncludeTag {

	@Override
	public int doStartTag() {
		return EVAL_BODY_INCLUDE;
	}

	public String getConfirmMessage() {
		return _confirmMessage;
	}

	public String getEmptyMessage() {
		return _emptyMessage;
	}

	public String getInfoMessage() {
		return _infoMessage;
	}

	public String getPortletURL() {
		return _portletURL;
	}

	public int getTotalEntries() {
		return _totalEntries;
	}

	public void setConfirmMessage(String confirmMessage) {
		_confirmMessage = confirmMessage;
	}

	public void setEmptyMessage(String emptyMessage) {
		_emptyMessage = emptyMessage;
	}

	public void setInfoMessage(String infoMessage) {
		_infoMessage = infoMessage;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setPortletURL(PortletURL portletURL) {
		_portletURL = portletURL.toString();
	}

	public void setPortletURL(String portletURL) {
		_portletURL = portletURL;
	}

	public void setTotalEntries(int totalEntries) {
		_totalEntries = totalEntries;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_confirmMessage = _CONFIRM_MESSAGE;
		_emptyMessage = _EMPTY_MESSAGE;
		_infoMessage = null;
		_portletURL = null;
		_totalEntries = 0;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected boolean isCleanUpSetAttributes() {
		return _CLEAN_UP_SET_ATTRIBUTES;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-trash:empty:confirmMessage", _confirmMessage);
		httpServletRequest.setAttribute(
			"liferay-trash:empty:emptyMessage", _emptyMessage);
		httpServletRequest.setAttribute(
			"liferay-trash:empty:infoMessage", _infoMessage);
		httpServletRequest.setAttribute(
			"liferay-trash:empty:portletURL", _portletURL);
		httpServletRequest.setAttribute(
			"liferay-trash:empty:totalEntries", _totalEntries);
	}

	private static final boolean _CLEAN_UP_SET_ATTRIBUTES = true;

	private static final String _CONFIRM_MESSAGE =
		"are-you-sure-you-want-to-empty-the-recycle-bin";

	private static final String _EMPTY_MESSAGE = "empty-the-recycle-bin";

	private static final String _PAGE = "/empty/page.jsp";

	private String _confirmMessage = _CONFIRM_MESSAGE;
	private String _emptyMessage = _EMPTY_MESSAGE;
	private String _infoMessage;
	private String _portletURL;
	private int _totalEntries;

}