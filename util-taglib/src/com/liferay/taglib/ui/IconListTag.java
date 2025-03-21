/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.ui;

import com.liferay.portal.kernel.servlet.FileAvailabilityUtil;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.taglib.BaseBodyTagSupport;
import com.liferay.taglib.util.PortalIncludeUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.BodyTag;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
public class IconListTag extends BaseBodyTagSupport implements BodyTag {

	@Override
	public int doAfterBody() {
		HttpServletRequest httpServletRequest =
			(HttpServletRequest)pageContext.getRequest();

		IntegerWrapper iconCount =
			(IntegerWrapper)httpServletRequest.getAttribute(
				"liferay-ui:icon-list:icon-count");

		Boolean singleIcon = (Boolean)httpServletRequest.getAttribute(
			"liferay-ui:icon-list:single-icon");

		if ((iconCount != null) && (iconCount.getValue() == 1) &&
			(singleIcon == null)) {

			bodyContent.clearBody();

			httpServletRequest.setAttribute(
				"liferay-ui:icon-list:single-icon", Boolean.TRUE);

			return EVAL_BODY_AGAIN;
		}

		return SKIP_BODY;
	}

	@Override
	public int doEndTag() throws JspException {
		try {
			HttpServletRequest httpServletRequest =
				(HttpServletRequest)pageContext.getRequest();

			IntegerWrapper iconCount =
				(IntegerWrapper)httpServletRequest.getAttribute(
					"liferay-ui:icon-list:icon-count");

			httpServletRequest.removeAttribute(
				"liferay-ui:icon-list:icon-count");

			Boolean singleIcon = (Boolean)httpServletRequest.getAttribute(
				"liferay-ui:icon-list:single-icon");

			httpServletRequest.removeAttribute(
				"liferay-ui:icon-list:single-icon");

			JspWriter jspWriter = pageContext.getOut();

			if ((iconCount != null) && (iconCount.getValue() > 1) &&
				((singleIcon == null) || _showWhenSingleIcon)) {

				if (!FileAvailabilityUtil.isAvailable(
						pageContext.getServletContext(), getStartPage())) {

					jspWriter.write("<ul class=\"list-unstyled ");
					jspWriter.write("taglib-icon-list\" role=\"menu\">");
				}
				else {
					PortalIncludeUtil.include(pageContext, _startPage);
				}
			}

			writeBodyContent(jspWriter);

			if ((iconCount != null) && (iconCount.getValue() > 1) &&
				((singleIcon == null) || _showWhenSingleIcon)) {

				if (!FileAvailabilityUtil.isAvailable(
						pageContext.getServletContext(), getEndPage())) {

					jspWriter.write("</ul>");
				}
				else {
					PortalIncludeUtil.include(pageContext, _endPage);
				}
			}

			httpServletRequest.removeAttribute(
				"liferay-ui:icon-list:showWhenSingleIcon");

			return EVAL_PAGE;
		}
		catch (Exception exception) {
			throw new JspException(exception);
		}
		finally {
			_endPage = null;
			_showWhenSingleIcon = false;
			_startPage = null;
		}
	}

	@Override
	public int doStartTag() {
		HttpServletRequest httpServletRequest =
			(HttpServletRequest)pageContext.getRequest();

		httpServletRequest.setAttribute(
			"liferay-ui:icon-list:icon-count", new IntegerWrapper());
		httpServletRequest.setAttribute(
			"liferay-ui:icon-list:showWhenSingleIcon",
			String.valueOf(_showWhenSingleIcon));

		return EVAL_BODY_BUFFERED;
	}

	public void setEndPage(String endPage) {
		_endPage = endPage;
	}

	public void setShowWhenSingleIcon(boolean showWhenSingleIcon) {
		_showWhenSingleIcon = showWhenSingleIcon;
	}

	public void setStartPage(String startPage) {
		_startPage = startPage;
	}

	protected String getEndPage() {
		if (Validator.isNull(_endPage)) {
			return _END_PAGE;
		}

		return _endPage;
	}

	protected String getStartPage() {
		if (Validator.isNull(_startPage)) {
			return _START_PAGE;
		}

		return _startPage;
	}

	private static final String _END_PAGE = "/html/taglib/ui/icon_list/end.jsp";

	private static final String _START_PAGE =
		"/html/taglib/ui/icon_list/start.jsp";

	private String _endPage;
	private boolean _showWhenSingleIcon;
	private String _startPage;

}