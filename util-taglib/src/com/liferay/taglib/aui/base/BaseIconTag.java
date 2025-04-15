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
 * @generated
 */
public abstract class BaseIconTag extends com.liferay.taglib.util.IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		return super.doStartTag();
	}

	public java.lang.String getAriaLabel() {
		return _ariaLabel;
	}

	public java.lang.String getCssClass() {
		return _cssClass;
	}

	public java.util.Map<java.lang.String, java.lang.Object> getData() {
		return _data;
	}

	public java.lang.String getId() {
		return _id;
	}

	public java.lang.String getImage() {
		return _image;
	}

	public java.lang.String getLabel() {
		return _label;
	}

	public java.lang.String getMarkupView() {
		return _markupView;
	}

	public java.lang.String getSrc() {
		return _src;
	}

	public java.lang.String getTarget() {
		return _target;
	}

	public java.lang.String getUrl() {
		return _url;
	}

	public void setAriaLabel(java.lang.String ariaLabel) {
		_ariaLabel = ariaLabel;
	}

	public void setCssClass(java.lang.String cssClass) {
		_cssClass = cssClass;
	}

	public void setData(java.util.Map<java.lang.String, java.lang.Object> data) {
		_data = data;
	}

	public void setId(java.lang.String id) {
		_id = id;
	}

	public void setImage(java.lang.String image) {
		_image = image;
	}

	public void setLabel(java.lang.String label) {
		_label = label;
	}

	public void setMarkupView(java.lang.String markupView) {
		_markupView = markupView;
	}

	public void setSrc(java.lang.String src) {
		_src = src;
	}

	public void setTarget(java.lang.String target) {
		_target = target;
	}

	public void setUrl(java.lang.String url) {
		_url = url;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_ariaLabel = null;
		_cssClass = null;
		_data = null;
		_id = null;
		_image = null;
		_label = null;
		_markupView = null;
		_src = null;
		_target = null;
		_url = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest request) {
		setNamespacedAttribute(request, "ariaLabel", _ariaLabel);
		setNamespacedAttribute(request, "cssClass", _cssClass);
		setNamespacedAttribute(request, "data", _data);
		setNamespacedAttribute(request, "id", _id);
		setNamespacedAttribute(request, "image", _image);
		setNamespacedAttribute(request, "label", _label);
		setNamespacedAttribute(request, "markupView", _markupView);
		setNamespacedAttribute(request, "src", _src);
		setNamespacedAttribute(request, "target", _target);
		setNamespacedAttribute(request, "url", _url);
	}

	protected static final String _ATTRIBUTE_NAMESPACE = "aui:icon:";

	private static final String _PAGE =
		"/html/taglib/aui/icon/page.jsp";

	private java.lang.String _ariaLabel = null;
	private java.lang.String _cssClass = null;
	private java.util.Map<java.lang.String, java.lang.Object> _data = null;
	private java.lang.String _id = null;
	private java.lang.String _image = null;
	private java.lang.String _label = null;
	private java.lang.String _markupView = null;
	private java.lang.String _src = null;
	private java.lang.String _target = null;
	private java.lang.String _url = null;

}