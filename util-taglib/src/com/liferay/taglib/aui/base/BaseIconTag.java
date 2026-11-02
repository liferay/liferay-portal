/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.aui.base;

import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

import java.util.Map;

/**
 * @author Eduardo Lundgren
 * @author Bruno Basto
 * @author Nathan Cavanaugh
 * @author Julio Camarero
 */
public abstract class BaseIconTag extends IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		return super.doStartTag();
	}

	public String getAriaLabel() {
		return _ariaLabel;
	}

	public String getCssClass() {
		return _cssClass;
	}

	public Map<String, Object> getData() {
		return _data;
	}

	public String getId() {
		return _id;
	}

	public String getImage() {
		return _image;
	}

	public String getLabel() {
		return _label;
	}

	public String getMarkupView() {
		return _markupView;
	}

	public String getSrc() {
		return _src;
	}

	public String getTarget() {
		return _target;
	}

	public String getUrl() {
		return _url;
	}

	public void setAriaLabel(String ariaLabel) {
		_ariaLabel = ariaLabel;
	}

	public void setCssClass(String cssClass) {
		_cssClass = cssClass;
	}

	public void setData(Map<String, Object> data) {
		_data = data;
	}

	public void setId(String id) {
		_id = id;
	}

	public void setImage(String image) {
		_image = image;
	}

	public void setLabel(String label) {
		_label = label;
	}

	public void setMarkupView(String markupView) {
		_markupView = markupView;
	}

	public void setSrc(String src) {
		_src = src;
	}

	public void setTarget(String target) {
		_target = target;
	}

	public void setUrl(String url) {
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
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		setNamespacedAttribute(httpServletRequest, "ariaLabel", _ariaLabel);
		setNamespacedAttribute(httpServletRequest, "cssClass", _cssClass);
		setNamespacedAttribute(httpServletRequest, "data", _data);
		setNamespacedAttribute(httpServletRequest, "id", _id);
		setNamespacedAttribute(httpServletRequest, "image", _image);
		setNamespacedAttribute(httpServletRequest, "label", _label);
		setNamespacedAttribute(httpServletRequest, "markupView", _markupView);
		setNamespacedAttribute(httpServletRequest, "src", _src);
		setNamespacedAttribute(httpServletRequest, "target", _target);
		setNamespacedAttribute(httpServletRequest, "url", _url);
	}

	private static final String _ATTRIBUTE_NAMESPACE = "aui:icon:";

	private static final String _PAGE = "/html/taglib/aui/icon/page.jsp";

	private String _ariaLabel;
	private String _cssClass;
	private Map<String, Object> _data;
	private String _id;
	private String _image;
	private String _label;
	private String _markupView;
	private String _src;
	private String _target;
	private String _url;

}