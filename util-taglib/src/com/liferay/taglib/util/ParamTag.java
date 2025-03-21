/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.util;

import com.liferay.taglib.TagSupport;

import jakarta.servlet.jsp.JspException;

/**
 * @author Brian Wing Shun Chan
 * @author Neil Griffin
 */
public class ParamTag extends TagSupport {

	@Override
	public int doStartTag() throws JspException {
		ParamAncestorTag paramAncestorTag =
			(ParamAncestorTag)findAncestorWithClass(
				this, ParamAncestorTag.class);

		if (paramAncestorTag == null) {
			throw new JspException();
		}

		if (paramAncestorTag instanceof TypedParamAccessorTag) {
			TypedParamAccessorTag typedParamAccessorTag =
				(TypedParamAccessorTag)paramAncestorTag;

			typedParamAccessorTag.addParam(_name, _type, _value);
		}
		else {
			paramAncestorTag.addParam(_name, _value);
		}

		return SKIP_BODY;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setType(String type) {
		_type = type;
	}

	public void setValue(String value) {
		_value = value;
	}

	private String _name;
	private String _type;
	private String _value;

}