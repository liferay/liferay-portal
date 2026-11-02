/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.aui.base;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

/**
 * @author Eduardo Lundgren
 * @author Bruno Basto
 * @author Nathan Cavanaugh
 * @author Julio Camarero
 */
public abstract class BaseAlertTag extends com.liferay.taglib.util.IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		return super.doStartTag();
	}

	public boolean getAnimated() {
		return _animated;
	}

	public boolean getCloseable() {
		return _closeable;
	}

	public String getCssClass() {
		return _cssClass;
	}

	public boolean getDestroyOnHide() {
		return _destroyOnHide;
	}

	public Object getDuration() {
		return _duration;
	}

	public String getId() {
		return _id;
	}

	public String getType() {
		return _type;
	}

	public void setAnimated(boolean animated) {
		_animated = animated;
	}

	public void setCloseable(boolean closeable) {
		_closeable = closeable;
	}

	public void setCssClass(String cssClass) {
		_cssClass = cssClass;
	}

	public void setDestroyOnHide(boolean destroyOnHide) {
		_destroyOnHide = destroyOnHide;
	}

	public void setDuration(Object duration) {
		_duration = duration;
	}

	public void setId(String id) {
		_id = id;
	}

	public void setType(String type) {
		_type = type;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_animated = false;
		_closeable = true;
		_cssClass = null;
		_destroyOnHide = false;
		_duration = 0.15;
		_id = null;
		_type = "info";
	}

	@Override
	protected String getEndPage() {
		return _END_PAGE;
	}

	@Override
	protected String getStartPage() {
		return _START_PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest request) {
		setNamespacedAttribute(request, "animated", _animated);
		setNamespacedAttribute(request, "closeable", _closeable);
		setNamespacedAttribute(request, "cssClass", _cssClass);
		setNamespacedAttribute(request, "destroyOnHide", _destroyOnHide);
		setNamespacedAttribute(request, "duration", _duration);
		setNamespacedAttribute(request, "id", _id);
		setNamespacedAttribute(request, "type", _type);
	}

	protected static final String _ATTRIBUTE_NAMESPACE = "aui:alert:";

	private static final String _END_PAGE = "/html/taglib/aui/alert/end.jsp";

	private static final String _START_PAGE =
		"/html/taglib/aui/alert/start.jsp";

	private boolean _animated;
	private boolean _closeable = true;
	private String _cssClass;
	private boolean _destroyOnHide;
	private Object _duration = 0.15;
	private String _id;
	private String _type = "info";

}