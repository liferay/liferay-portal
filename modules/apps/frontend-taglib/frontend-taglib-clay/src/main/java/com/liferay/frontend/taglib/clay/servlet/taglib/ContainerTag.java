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
public class ContainerTag extends BaseContainerTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		return super.doStartTag();
	}

	public boolean getFluid() {
		return _fluid;
	}

	public String getSize() {
		return _size;
	}

	public void setFluid(boolean fluid) {
		_fluid = fluid;
	}

	public void setSize(String size) {
		_size = size;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_fluid = false;
		_size = null;
	}

	@Override
	protected String processCssClasses(Set<String> cssClasses) {
		if (!_fluid) {
			cssClasses.add("container");
		}
		else {
			cssClasses.add("container-fluid");

			if (Validator.isNotNull(_size)) {
				cssClasses.add("container-fluid-max-" + _size);
			}
		}

		return super.processCssClasses(cssClasses);
	}

	private static final String _ATTRIBUTE_NAMESPACE = "clay:container:";

	private boolean _fluid;
	private String _size;

}