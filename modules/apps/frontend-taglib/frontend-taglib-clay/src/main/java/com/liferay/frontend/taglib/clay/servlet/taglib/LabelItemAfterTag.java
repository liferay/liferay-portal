/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.internal.servlet.taglib.BaseContainerTag;

import jakarta.servlet.jsp.JspException;

import java.util.Set;

/**
 * @author Chema Balsas
 */
public class LabelItemAfterTag extends BaseContainerTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		if (getContainerElement() == null) {
			setContainerElement("span");
		}

		return super.doStartTag();
	}

	@Override
	protected String processCssClasses(Set<String> cssClasses) {
		cssClasses.add("label-item");
		cssClasses.add("label-item-after");

		return super.processCssClasses(cssClasses);
	}

	private static final String _ATTRIBUTE_NAMESPACE = "clay:label-item-after:";

}