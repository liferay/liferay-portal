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
public class ColTag extends BaseContainerTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		return super.doStartTag();
	}

	public String getLg() {
		return _lg;
	}

	public String getMd() {
		return _md;
	}

	public String getSize() {
		return _size;
	}

	public String getSm() {
		return _sm;
	}

	public String getXl() {
		return _xl;
	}

	public void setLg(String lg) {
		_lg = lg;
	}

	public void setMd(String md) {
		_md = md;
	}

	public void setSize(String size) {
		_size = size;
	}

	public void setSm(String sm) {
		_sm = sm;
	}

	public void setXl(String xl) {
		_xl = xl;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_lg = null;
		_md = null;
		_size = null;
		_sm = null;
		_xl = null;
	}

	@Override
	protected String processCssClasses(Set<String> cssClasses) {
		if (Validator.isNotNull(_size)) {
			cssClasses.add("col-" + _size);
		}

		if (Validator.isNotNull(_lg)) {
			cssClasses.add("col-lg-" + _lg);
		}

		if (Validator.isNotNull(_md)) {
			cssClasses.add("col-md-" + _md);
		}

		if (Validator.isNotNull(_sm)) {
			cssClasses.add("col-sm-" + _sm);
		}

		if (Validator.isNotNull(_xl)) {
			cssClasses.add("col-xl-" + _xl);
		}

		if (cssClasses.isEmpty()) {
			cssClasses.add("col");
		}

		return super.processCssClasses(cssClasses);
	}

	private static final String _ATTRIBUTE_NAMESPACE = "clay:col:";

	private String _lg;
	private String _md;
	private String _size;
	private String _sm;
	private String _xl;

}