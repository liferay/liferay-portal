/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.expando.taglib.servlet.taglib;

import com.liferay.expando.taglib.internal.servlet.ServletContextUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

import java.util.Locale;
import java.util.Set;

/**
 * @author Brian Wing Shun Chan
 */
public class CustomAttributeTag extends IncludeTag {

	public Set<Locale> getAvailableLocales() {
		return _availableLocales;
	}

	public String getClassName() {
		return _className;
	}

	public long getClassPK() {
		return _classPK;
	}

	public String getName() {
		return _name;
	}

	public boolean isEditable() {
		return _editable;
	}

	public boolean isLabel() {
		return _label;
	}

	public void setAvailableLocales(Set<Locale> availableLocales) {
		_availableLocales = availableLocales;
	}

	public void setClassName(String className) {
		_className = className;
	}

	public void setClassPK(long classPK) {
		_classPK = classPK;
	}

	public void setEditable(boolean editable) {
		_editable = editable;
	}

	public void setLabel(boolean label) {
		_label = label;
	}

	public void setName(String name) {
		_name = name;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_availableLocales = null;
		_className = null;
		_classPK = 0;
		_editable = false;
		_label = false;
		_name = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-expando:custom-attribute:availableLocales",
			_availableLocales);
		httpServletRequest.setAttribute(
			"liferay-expando:custom-attribute:className", _className);
		httpServletRequest.setAttribute(
			"liferay-expando:custom-attribute:classPK",
			String.valueOf(_classPK));
		httpServletRequest.setAttribute(
			"liferay-expando:custom-attribute:editable",
			String.valueOf(_editable));
		httpServletRequest.setAttribute(
			"liferay-expando:custom-attribute:label", String.valueOf(_label));
		httpServletRequest.setAttribute(
			"liferay-expando:custom-attribute:name", _name);
	}

	private static final String _PAGE = "/custom_attribute/page.jsp";

	private Set<Locale> _availableLocales;
	private String _className;
	private long _classPK;
	private boolean _editable;
	private boolean _label;
	private String _name;

}