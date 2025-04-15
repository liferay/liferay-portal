/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.internal.servlet.taglib.BaseContainerTag;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.jsp.JspException;

import java.util.Set;

/**
 * @author Chema Balsas
 */
public class SheetTag extends BaseContainerTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		return super.doStartTag();
	}

	public String getSize() {
		return _size;
	}

	public void setSize(String size) {
		_size = size;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_size = "lg";
	}

	@Override
	protected String processCssClasses(Set<String> cssClasses) {
		cssClasses.add("sheet");

		if (Validator.isNotNull(_size)) {
			cssClasses.add("sheet-" + _size);
		}

		return super.processCssClasses(cssClasses);
	}

	private static final String _ATTRIBUTE_NAMESPACE = "clay:sheet:";

	private String _size = "lg";

}