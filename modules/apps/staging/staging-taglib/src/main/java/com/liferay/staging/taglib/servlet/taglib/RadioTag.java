/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.taglib.servlet.taglib;

import com.liferay.petra.string.StringPool;
import com.liferay.staging.taglib.internal.servlet.ServletContextUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Péter Borkuti
 */
public class RadioTag extends IncludeTag {

	public String getDescription() {
		return _description;
	}

	public String getId() {
		return _id;
	}

	public String getLabel() {
		return _label;
	}

	public String getName() {
		return _name;
	}

	public String getPopover() {
		return _popover;
	}

	public String getValue() {
		return _value;
	}

	public boolean isChecked() {
		return _checked;
	}

	public boolean isDisabled() {
		return _disabled;
	}

	public boolean isIgnoreRequestValue() {
		return _ignoreRequestValue;
	}

	public boolean isInline() {
		return _inline;
	}

	public void setChecked(boolean checked) {
		_checked = checked;
	}

	public void setDescription(String description) {
		_description = description;
	}

	public void setDisabled(boolean disabled) {
		_disabled = disabled;
	}

	public void setId(String id) {
		_id = id;
	}

	public void setIgnoreRequestValue(boolean ignoreRequestValue) {
		_ignoreRequestValue = ignoreRequestValue;
	}

	public void setInline(boolean inline) {
		_inline = inline;
	}

	public void setLabel(String label) {
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

	public void setPopover(String popover) {
		_popover = popover;
	}

	public void setValue(String value) {
		_value = value;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_checked = false;
		_description = StringPool.BLANK;
		_disabled = false;
		_id = StringPool.BLANK;
		_ignoreRequestValue = false;
		_inline = false;
		_label = StringPool.BLANK;
		_name = StringPool.BLANK;
		_popover = StringPool.BLANK;
		_value = StringPool.BLANK;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-staging:radio:checked", _checked);
		httpServletRequest.setAttribute(
			"liferay-staging:radio:description", _description);
		httpServletRequest.setAttribute(
			"liferay-staging:radio:disabled", _disabled);
		httpServletRequest.setAttribute("liferay-staging:radio:id", _id);
		httpServletRequest.setAttribute(
			"liferay-staging:radio:ignoreRequestValue", _ignoreRequestValue);
		httpServletRequest.setAttribute(
			"liferay-staging:radio:inline", _inline);
		httpServletRequest.setAttribute("liferay-staging:radio:label", _label);
		httpServletRequest.setAttribute("liferay-staging:radio:name", _name);
		httpServletRequest.setAttribute(
			"liferay-staging:radio:popover", _popover);
		httpServletRequest.setAttribute("liferay-staging:radio:value", _value);
	}

	private static final String _PAGE = "/radio/page.jsp";

	private boolean _checked;
	private String _description = StringPool.BLANK;
	private boolean _disabled;
	private String _id = StringPool.BLANK;
	private boolean _ignoreRequestValue;
	private boolean _inline;
	private String _label = StringPool.BLANK;
	private String _name = StringPool.BLANK;
	private String _popover = StringPool.BLANK;
	private String _value = StringPool.BLANK;

}