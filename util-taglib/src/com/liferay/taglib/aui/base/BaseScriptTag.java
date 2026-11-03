/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
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
public abstract class BaseScriptTag extends PositionTagSupport {

	public int doStartTag() throws JspException {
		return super.doStartTag();
	}

	public boolean getAsync() {
		return _async;
	}

	public String getBlocking() {
		return _blocking;
	}

	public String getCrossOrigin() {
		return _crossOrigin;
	}

	public boolean getDefer() {
		return _defer;
	}

	public String getFetchPriority() {
		return _fetchPriority;
	}

	public boolean getHashedFile() {
		return _hashedFile;
	}

	public String getId() {
		return _id;
	}

	public String getIntegrity() {
		return _integrity;
	}

	public String getReferrerPolicy() {
		return _referrerPolicy;
	}

	public String getRequire() {
		return _require;
	}

	public boolean getSandbox() {
		return _sandbox;
	}

	public String getSenna() {
		return _senna;
	}

	public String getSrc() {
		return _src;
	}

	public String getType() {
		return _type;
	}

	public String getUse() {
		return _use;
	}

	public void setAsync(boolean async) {
		_async = async;
	}

	public void setBlocking(String blocking) {
		_blocking = blocking;
	}

	public void setCrossOrigin(String crossOrigin) {
		_crossOrigin = crossOrigin;
	}

	public void setDefer(boolean defer) {
		_defer = defer;
	}

	public void setFetchPriority(String fetchPriority) {
		_fetchPriority = fetchPriority;
	}

	public void setHashedFile(boolean hashedFile) {
		_hashedFile = hashedFile;
	}

	public void setId(String id) {
		_id = id;
	}

	public void setIntegrity(String integrity) {
		_integrity = integrity;
	}

	public void setReferrerPolicy(String referrerPolicy) {
		_referrerPolicy = referrerPolicy;
	}

	public void setRequire(String require) {
		_require = require;
	}

	public void setSandbox(boolean sandbox) {
		_sandbox = sandbox;
	}

	public void setSenna(String senna) {
		_senna = senna;
	}

	public void setSrc(String src) {
		_src = src;
	}

	public void setType(String type) {
		_type = type;
	}

	public void setUse(String use) {
		_use = use;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_async = false;
		_blocking = null;
		_crossOrigin = null;
		_defer = false;
		_fetchPriority = null;
		_hashedFile = false;
		_id = null;
		_integrity = null;
		_referrerPolicy = null;
		_require = null;
		_sandbox = false;
		_senna = null;
		_src = null;
		_type = null;
		_use = null;
	}

	protected String getPage() {
		return _PAGE;
	}

	private static final String _PAGE = "/html/taglib/aui/script/page.jsp";

	private boolean _async;
	private String _blocking;
	private String _crossOrigin;
	private boolean _defer;
	private String _fetchPriority;
	private boolean _hashedFile;
	private String _id;
	private String _integrity;
	private String _referrerPolicy;
	private String _require;
	private boolean _sandbox;
	private String _senna;
	private String _src;
	private String _type;
	private String _use;

}