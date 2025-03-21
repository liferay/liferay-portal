/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.ui;

import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;

import java.util.TimeZone;

/**
 * @author Brian Wing Shun Chan
 */
public class InputTimeZoneTag extends IncludeTag {

	public InputTimeZoneTag() {
		TimeZone timeZone = TimeZoneUtil.getDefault();

		_value = timeZone.getID();
	}

	public String getCssClass() {
		return _cssClass;
	}

	public int getDisplayStyle() {
		return _displayStyle;
	}

	public String getName() {
		return _name;
	}

	public String getValue() {
		return _value;
	}

	public boolean isAutoFocus() {
		return _autoFocus;
	}

	public boolean isDaylight() {
		return _daylight;
	}

	public boolean isDisabled() {
		return _disabled;
	}

	public boolean isNullable() {
		return _nullable;
	}

	public void setAutoFocus(boolean autoFocus) {
		_autoFocus = autoFocus;
	}

	public void setCssClass(String cssClass) {
		_cssClass = cssClass;
	}

	public void setDaylight(boolean daylight) {
		_daylight = daylight;
	}

	public void setDisabled(boolean disabled) {
		_disabled = disabled;
	}

	public void setDisplayStyle(int displayStyle) {
		_displayStyle = displayStyle;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setNullable(boolean nullable) {
		_nullable = nullable;
	}

	public void setValue(String value) {
		_value = value;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_autoFocus = false;
		_cssClass = null;
		_daylight = false;
		_disabled = false;
		_displayStyle = TimeZone.LONG;
		_name = null;
		_nullable = false;

		TimeZone timeZone = TimeZoneUtil.getDefault();

		_value = timeZone.getID();
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-ui:input-time-zone:autoFocus", String.valueOf(_autoFocus));
		httpServletRequest.setAttribute(
			"liferay-ui:input-time-zone:cssClass", _cssClass);
		httpServletRequest.setAttribute(
			"liferay-ui:input-time-zone:daylight", String.valueOf(_daylight));
		httpServletRequest.setAttribute(
			"liferay-ui:input-time-zone:disabled", String.valueOf(_disabled));
		httpServletRequest.setAttribute(
			"liferay-ui:input-time-zone:displayStyle",
			String.valueOf(_displayStyle));
		httpServletRequest.setAttribute(
			"liferay-ui:input-time-zone:name", _name);
		httpServletRequest.setAttribute(
			"liferay-ui:input-time-zone:nullable", String.valueOf(_nullable));
		httpServletRequest.setAttribute(
			"liferay-ui:input-time-zone:value", _value);
	}

	private static final String _PAGE =
		"/html/taglib/ui/input_time_zone/page.jsp";

	private boolean _autoFocus;
	private String _cssClass;
	private boolean _daylight;
	private boolean _disabled;
	private int _displayStyle = TimeZone.LONG;
	private String _name;
	private boolean _nullable;
	private String _value;

}