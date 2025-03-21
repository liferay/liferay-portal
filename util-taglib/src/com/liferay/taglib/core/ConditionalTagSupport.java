/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.core;

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.taglib.TagSupport;

import jakarta.servlet.jsp.JspTagException;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Shuyang Zhou
 */
public abstract class ConditionalTagSupport extends TagSupport {

	@Override
	public int doStartTag() throws JspTagException {
		_result = condition();

		if (_var != null) {
			pageContext.setAttribute(_var, _result, _scope);
		}

		if (_result) {
			return EVAL_BODY_INCLUDE;
		}

		return SKIP_BODY;
	}

	@Override
	public void release() {
		super.release();

		_result = false;
		_scope = PageContext.PAGE_SCOPE;
		_var = null;
	}

	public void setScope(String scope) {
		String scopeLowerCase = StringUtil.toLowerCase(scope);

		if (scopeLowerCase.equals("application")) {
			_scope = PageContext.APPLICATION_SCOPE;
		}
		else if (scopeLowerCase.equals("page")) {
			_scope = PageContext.PAGE_SCOPE;
		}
		else if (scopeLowerCase.equals("request")) {
			_scope = PageContext.REQUEST_SCOPE;
		}
		else if (scopeLowerCase.equals("session")) {
			_scope = PageContext.SESSION_SCOPE;
		}
	}

	public void setVar(String var) {
		_var = var;
	}

	protected abstract boolean condition();

	private boolean _result;
	private int _scope = PageContext.PAGE_SCOPE;
	private String _var;

}