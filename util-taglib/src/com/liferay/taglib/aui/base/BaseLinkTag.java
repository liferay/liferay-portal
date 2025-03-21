/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.taglib.aui.base;

import jakarta.servlet.jsp.JspException;

/**
 * @author Eduardo Lundgren
 * @author Bruno Basto
 * @author Nathan Cavanaugh
 * @author Julio Camarero
 * @generated
 */
public abstract class BaseLinkTag extends com.liferay.taglib.util.PositionTagSupport {

	public int doStartTag() throws JspException {
		return super.doStartTag();
	}

	public String getCssClass() {
		return _cssClass;
	}

	public void setCssClass(String cssClass) {
		_cssClass = cssClass;
	}

	public String getCrossOrigin() {
		return _crossOrigin;
	}

	public void setCrossOrigin(String crossOrigin) {
		_crossOrigin = crossOrigin;
	}

	public String getHref() {
		return _href;
	}

	public void setHref(String href) {
		_href = href;
	}

	public String getId() {
		return _id;
	}

	public void setId(String id) {
		_id = id;
	}

	public String getIntegrity() {
		return _integrity;
	}

	public void setIntegrity(String integrity) {
		_integrity = integrity;
	}

	public String getRel() {
		return _rel;
	}

	public void setRel(String rel) {
		_rel = rel;
	}

	public String getSenna() {
		return _senna;
	}

	public void setSenna(String senna) {
		_senna = senna;
	}

	public String getType() {
		return _type;
	}

	public void setType(String type) {
		_type = type;
	}

	protected void cleanUp() {
		_cssClass = null;
		_crossOrigin = null;
		_href = null;
		_id = null;
		_integrity = null;
		_rel = null;
		_senna = null;
		_type = null;
	}

	protected String getPage() {
		return _PAGE;
	}

	private static final String _PAGE =
		"/html/taglib/aui/style/page.jsp";

	private String _cssClass = null;
	private String _crossOrigin = null;
	private String _href = null;
	private String _id = null;
	private String _integrity = null;
	private String _rel = null;
	private String _senna = null;
	private String _type = null;

}