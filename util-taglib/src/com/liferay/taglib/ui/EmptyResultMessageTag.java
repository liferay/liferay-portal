/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.ui;

import com.liferay.portal.kernel.util.Validator;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Eudaldo Alonso
 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
 *             com.liferay.frontend.taglib.servlet.taglib.EmptyResultMessageTag}
 */
@Deprecated
public class EmptyResultMessageTag extends IncludeTag {

	public String getMessage() {
		return _message;
	}

	public boolean isCompact() {
		return _compact;
	}

	public boolean isSearch() {
		return _search;
	}

	public void setCompact(boolean compact) {
		_compact = compact;
	}

	public void setCssClass(String cssClass) {
		_cssClass = cssClass;
	}

	public void setMessage(String message) {
		_message = message;
	}

	public void setSearch(boolean search) {
		_search = search;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_compact = false;
		_cssClass = null;
		_message = null;
		_search = false;
	}

	protected String getCssClass() {
		if (Validator.isNotNull(_cssClass)) {
			return _cssClass;
		}

		if (_search) {
			return "taglib-empty-search-result-message-header";
		}

		return "taglib-empty-result-message-header";
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
		httpServletRequest.setAttribute(
			"liferay-ui:empty-result-message:compact",
			String.valueOf(_compact));
		httpServletRequest.setAttribute(
			"liferay-ui:empty-result-message:cssClass", getCssClass());
		httpServletRequest.setAttribute(
			"liferay-ui:empty-result-message:message", _message);
		httpServletRequest.setAttribute(
			"liferay-ui:empty-result-message:search", String.valueOf(_search));
	}

	private static final String _END_PAGE =
		"/html/taglib/ui/empty_result_message/end.jsp";

	private static final String _START_PAGE =
		"/html/taglib/ui/empty_result_message/start.jsp";

	private boolean _compact;
	private String _cssClass;
	private String _message;
	private boolean _search;

}