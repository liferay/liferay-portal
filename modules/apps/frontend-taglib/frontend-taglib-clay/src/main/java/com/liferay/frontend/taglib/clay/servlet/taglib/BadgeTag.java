/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.internal.servlet.taglib.BaseContainerTag;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

import java.util.Set;

/**
 * @author Chema Balsas
 */
public class BadgeTag extends BaseContainerTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		if (getContainerElement() == null) {
			setContainerElement("span");
		}

		return super.doStartTag();
	}

	public String getDisplayType() {
		return _displayType;
	}

	public String getLabel(String label) {
		return _label;
	}

	public void setDisplayType(String displayType) {
		_displayType = displayType;
	}

	public void setLabel(String label) {
		_label = label;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_displayType = "primary";
		_label = null;
	}

	@Override
	protected String processCssClasses(Set<String> cssClasses) {
		cssClasses.add("badge");
		cssClasses.add("badge-" + _displayType);

		return super.processCssClasses(cssClasses);
	}

	@Override
	protected int processStartTag() throws Exception {
		super.processStartTag();

		JspWriter jspWriter = pageContext.getOut();

		jspWriter.write("<span");
		jspWriter.write(" class=\"badge-item badge-item-expand");
		jspWriter.write("\">");
		jspWriter.write(_label);
		jspWriter.write("</span>");

		return SKIP_BODY;
	}

	private static final String _ATTRIBUTE_NAMESPACE = "clay:badge:";

	private String _displayType = "primary";
	private String _label;

}