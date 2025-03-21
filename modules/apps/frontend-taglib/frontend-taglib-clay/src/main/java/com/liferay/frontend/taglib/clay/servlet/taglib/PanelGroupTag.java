/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.internal.servlet.taglib.BaseContainerTag;

import jakarta.servlet.jsp.JspException;

import java.util.Map;
import java.util.Set;

/**
 * @author Eduardo Allegrini
 */
public class PanelGroupTag extends BaseContainerTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		return super.doStartTag();
	}

	public Boolean getFluid() {
		return _fluid;
	}

	public Boolean getFluidFirst() {
		return _fluidFirst;
	}

	public Boolean getFluidLast() {
		return _fluidLast;
	}

	public Boolean getFlush() {
		return _flush;
	}

	public Boolean getSmall() {
		return _small;
	}

	public void setFluid(Boolean fluid) {
		_fluid = fluid;
	}

	public void setFluidFirst(Boolean fluidFirst) {
		_fluidFirst = fluidFirst;
	}

	public void setFluidLast(Boolean fluidLast) {
		_fluidLast = fluidLast;
	}

	public void setFlush(Boolean flush) {
		_flush = flush;
	}

	public void setSmall(Boolean small) {
		_small = small;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_fluid = false;
		_fluidFirst = false;
		_fluidLast = false;
		_flush = false;
		_small = false;
	}

	@Override
	protected Map<String, Object> prepareProps(Map<String, Object> props) {
		props.put("fluid", _fluid);
		props.put("fluidFirst", _fluidFirst);
		props.put("fluidLast", _fluidLast);
		props.put("flush", _flush);
		props.put("small", _small);

		return super.prepareProps(props);
	}

	@Override
	protected String processCssClasses(Set<String> cssClasses) {
		cssClasses.add("panel-group");

		if (_fluid) {
			cssClasses.add("panel-group-fluid");
		}

		if (_fluidFirst) {
			cssClasses.add("panel-group-fluid-first");
		}

		if (_fluidLast) {
			cssClasses.add("panel-group-fluid-last");
		}

		if (_flush) {
			cssClasses.add("panel-group-flush");
		}

		if (_small) {
			cssClasses.add("panel-group-sm");
		}

		return super.processCssClasses(cssClasses);
	}

	private static final String _ATTRIBUTE_NAMESPACE = "clay:panel-group:";

	private boolean _fluid;
	private boolean _fluidFirst;
	private boolean _fluidLast;
	private boolean _flush;
	private boolean _small;

}