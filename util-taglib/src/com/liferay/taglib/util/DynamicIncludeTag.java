/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.util;

import com.liferay.portal.kernel.servlet.taglib.DynamicIncludeUtil;
import com.liferay.taglib.TagSupport;
import com.liferay.taglib.servlet.PipingServletResponseFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspException;

/**
 * @author Carlos Sierra Andrés
 */
public class DynamicIncludeTag extends TagSupport {

	@Override
	public int doEndTag() throws JspException {
		DynamicIncludeUtil.include(
			getRequest(), getResponse(), getKey(), _ascendingPriority);

		return super.doEndTag();
	}

	@Override
	public int doStartTag() {
		if (!DynamicIncludeUtil.hasDynamicInclude(getKey())) {
			return SKIP_BODY;
		}

		return EVAL_BODY_INCLUDE;
	}

	public boolean getAscendingPriority() {
		return _ascendingPriority;
	}

	public String getKey() {
		return _key;
	}

	public void setAscendingPriority(boolean ascendingPriority) {
		_ascendingPriority = ascendingPriority;
	}

	public void setKey(String key) {
		_key = key;
	}

	protected HttpServletRequest getRequest() {
		return (HttpServletRequest)pageContext.getRequest();
	}

	protected HttpServletResponse getResponse() {
		return PipingServletResponseFactory.createPipingServletResponse(
			pageContext);
	}

	private boolean _ascendingPriority = true;
	private String _key;

}