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
public class ContentColTag extends BaseContainerTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		return super.doStartTag();
	}

	public boolean getExpand() {
		return _expand;
	}

	public boolean getGutters() {
		return _gutters;
	}

	public void setExpand(boolean expand) {
		_expand = expand;
	}

	public void setGutters(boolean gutters) {
		_gutters = gutters;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_expand = false;
		_gutters = false;
	}

	@Override
	protected String processCssClasses(Set<String> cssClasses) {
		cssClasses.add("autofit-col");

		if (_expand) {
			cssClasses.add("autofit-col-expand");
		}

		if (_gutters) {
			cssClasses.add("autofit-col-gutters");
		}

		return super.processCssClasses(cssClasses);
	}

	private static final String _ATTRIBUTE_NAMESPACE = "clay:content-col:";

	private boolean _expand;
	private boolean _gutters;

}