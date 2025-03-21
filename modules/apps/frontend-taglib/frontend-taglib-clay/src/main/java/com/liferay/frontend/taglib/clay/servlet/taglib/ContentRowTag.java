/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.internal.servlet.taglib.BaseContainerTag;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.jsp.JspException;

import java.util.Set;

/**
 * @author Chema Balsas
 */
public class ContentRowTag extends BaseContainerTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		return super.doStartTag();
	}

	public String getFloatElements() {
		return _floatElements;
	}

	public String getNoGutters() {
		return _noGutters;
	}

	public boolean getPadded() {
		return _padded;
	}

	public String getVerticalAlign() {
		return _verticalAlign;
	}

	public void setFloatElements(String floatElements) {
		_floatElements = floatElements;
	}

	public void setNoGutters(String noGutters) {
		_noGutters = noGutters;
	}

	public void setPadded(boolean padded) {
		_padded = padded;
	}

	public void setVerticalAlign(String verticalAlign) {
		_verticalAlign = verticalAlign;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_floatElements = null;
		_noGutters = null;
		_padded = false;
		_verticalAlign = null;
	}

	@Override
	protected String processCssClasses(Set<String> cssClasses) {
		cssClasses.add("autofit-row");

		if (_floatElements != null) {
			if (_floatElements.equals(StringPool.BLANK)) {
				cssClasses.add("autofit-float");
			}
			else {
				cssClasses.add("autofit-float-" + _floatElements);
			}
		}

		if (_padded) {
			cssClasses.add("autofit-padded");
		}

		if (_noGutters != null) {
			if (_noGutters.equals("x") || _noGutters.equals("y")) {
				cssClasses.add("autofit-padded-no-gutters-" + _noGutters);
			}
			else {
				cssClasses.add("autofit-padded-no-gutters");
			}
		}

		if (Validator.isNotNull(_verticalAlign)) {
			cssClasses.add("autofit-row-" + _verticalAlign);
		}

		return super.processCssClasses(cssClasses);
	}

	private static final String _ATTRIBUTE_NAMESPACE = "clay:content-row:";

	private String _floatElements;
	private String _noGutters;
	private boolean _padded;
	private String _verticalAlign;

}