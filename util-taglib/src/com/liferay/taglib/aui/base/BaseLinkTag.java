/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.aui.base;

import com.liferay.taglib.util.PositionTagSupport;

import jakarta.servlet.jsp.JspException;

/**
 * @author Eduardo Lundgren
 * @author Bruno Basto
 * @author Nathan Cavanaugh
 * @author Julio Camarero
 */
public abstract class BaseLinkTag extends PositionTagSupport {

	public int doStartTag() throws JspException {
		return super.doStartTag();
	}

	public String getCrossOrigin() {
		return _crossOrigin;
	}

	public String getCssClass() {
		return _cssClass;
	}

	public boolean getHashedFile() {
		return _hashedFile;
	}

	public String getHref() {
		return _href;
	}

	public String getId() {
		return _id;
	}

	public String getIntegrity() {
		return _integrity;
	}

	public String getRel() {
		return _rel;
	}

	public String getSenna() {
		return _senna;
	}

	public String getType() {
		return _type;
	}

	public void setCrossOrigin(String crossOrigin) {
		_crossOrigin = crossOrigin;
	}

	public void setCssClass(String cssClass) {
		_cssClass = cssClass;
	}

	public void setHashedFile(boolean hashedFile) {
		_hashedFile = hashedFile;
	}

	public void setHref(String href) {
		_href = href;
	}

	public void setId(String id) {
		_id = id;
	}

	public void setIntegrity(String integrity) {
		_integrity = integrity;
	}

	public void setRel(String rel) {
		_rel = rel;
	}

	public void setSenna(String senna) {
		_senna = senna;
	}

	public void setType(String type) {
		_type = type;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_crossOrigin = null;
		_cssClass = null;
		_hashedFile = false;
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

	private static final String _PAGE = "/html/taglib/aui/style/page.jsp";

	private String _crossOrigin;
	private String _cssClass;
	private boolean _hashedFile;
	private String _href;
	private String _id;
	private String _integrity;
	private String _rel;
	private String _senna;
	private String _type;

}