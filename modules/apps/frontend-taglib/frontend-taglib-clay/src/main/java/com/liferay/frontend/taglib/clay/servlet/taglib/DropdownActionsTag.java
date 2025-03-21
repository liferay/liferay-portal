/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.clay.servlet.taglib;

import jakarta.servlet.jsp.JspException;

import java.util.Map;
import java.util.Set;

/**
 * @author Chema Balsas
 */
public class DropdownActionsTag extends DropdownMenuTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		setDisplayType("unstyled");
		setIcon("ellipsis-v");
		setMonospaced(true);

		return super.doStartTag();
	}

	@Override
	protected Map<String, Object> prepareProps(Map<String, Object> props) {
		props.put("actionsDropdown", true);

		return super.prepareProps(props);
	}

	@Override
	protected String processCssClasses(Set<String> cssClasses) {
		cssClasses.add("component-action");

		return super.processCssClasses(cssClasses);
	}

	private static final String _ATTRIBUTE_NAMESPACE = "clay:dropdown-actions:";

}