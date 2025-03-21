/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.ui;

import com.liferay.portal.kernel.dao.search.ResultRow;
import com.liferay.portal.kernel.util.GetterUtil;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspTagException;
import jakarta.servlet.jsp.tagext.TagSupport;

/**
 * @author Brian Wing Shun Chan
 */
public class SearchContainerRowParameterTag<R> extends TagSupport {

	@Override
	public int doStartTag() throws JspException {
		SearchContainerRowTag<R> searchContainerRowTag =
			(SearchContainerRowTag<R>)findAncestorWithClass(
				this, SearchContainerRowTag.class);

		if (searchContainerRowTag == null) {
			throw new JspTagException(
				"Requires liferay-ui:search-container-row");
		}

		ResultRow resultRow = searchContainerRowTag.getRow();

		if (_name.equals("className")) {
			resultRow.setClassName((String)_value);
		}
		else if (_name.equals("classHoverName")) {
			resultRow.setClassHoverName((String)_value);
		}
		else if (_name.equals("restricted")) {
			resultRow.setRestricted(GetterUtil.getBoolean((String)_value));
		}
		else {
			resultRow.setParameter(_name, _value);
		}

		return EVAL_BODY_INCLUDE;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setValue(Object value) {
		_value = value;
	}

	private String _name;
	private Object _value;

}