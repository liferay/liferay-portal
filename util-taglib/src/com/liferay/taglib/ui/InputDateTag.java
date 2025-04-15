/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.ui;

import com.liferay.petra.string.StringPool;
import com.liferay.taglib.BaseValidatorTagSupport;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Brian Wing Shun Chan
 */
public class InputDateTag extends BaseValidatorTagSupport {

	public String getAutoComplete() {
		return _autoComplete;
	}

	public String getCssClass() {
		return _cssClass;
	}

	public String getDateTogglerCheckboxLabel() {
		return _dateTogglerCheckboxLabel;
	}

	public String getDayParam() {
		return _dayParam;
	}

	public int getDayValue() {
		return _dayValue;
	}

	public int getFirstDayOfWeek() {
		return _firstDayOfWeek;
	}

	public Date getFirstEnabledDate() {
		return _firstEnabledDate;
	}

	public String getFormName() {
		return _formName;
	}

	@Override
	public String getInputName() {
		return _name;
	}

	public Date getLastEnabledDate() {
		return _lastEnabledDate;
	}

	public String getMonthAndYearParam() {
		return _monthAndYearParam;
	}

	public String getMonthParam() {
		return _monthParam;
	}

	public int getMonthValue() {
		return _monthValue;
	}

	public String getName() {
		return _name;
	}

	public String getYearParam() {
		return _yearParam;
	}

	public int getYearValue() {
		return _yearValue;
	}

	public boolean isAutoFocus() {
		return _autoFocus;
	}

	public boolean isDisabled() {
		return _disabled;
	}

	public boolean isDisableNamespace() {
		return _disableNamespace;
	}

	public boolean isNullable() {
		return _nullable;
	}

	public boolean isRequired() {
		return _required;
	}

	public boolean isShowDisableCheckbox() {
		return _showDisableCheckbox;
	}

	public boolean isUseNamespace() {
		return _useNamespace;
	}

	public void setAutoComplete(String autoComplete) {
		_autoComplete = autoComplete;
	}

	public void setAutoFocus(boolean autoFocus) {
		_autoFocus = autoFocus;
	}

	public void setCssClass(String cssClass) {
		_cssClass = cssClass;
	}

	public void setDateTogglerCheckboxLabel(String dateTogglerCheckboxLabel) {
		_dateTogglerCheckboxLabel = dateTogglerCheckboxLabel;
	}

	public void setDayParam(String dayParam) {
		_dayParam = dayParam;
	}

	public void setDayValue(int dayValue) {
		_dayValue = dayValue;
	}

	public void setDisabled(boolean disabled) {
		_disabled = disabled;
	}

	public void setDisableNamespace(boolean disableNamespace) {
		_disableNamespace = disableNamespace;
	}

	public void setFirstDayOfWeek(int firstDayOfWeek) {
		_firstDayOfWeek = firstDayOfWeek;
	}

	public void setFirstEnabledDate(Date firstEnabledDate) {
		_firstEnabledDate = firstEnabledDate;
	}

	public void setFormName(String formName) {
		_formName = formName;
	}

	public void setLastEnabledDate(Date lastEnabledDate) {
		_lastEnabledDate = lastEnabledDate;
	}

	public void setMonthAndYearParam(String monthAndYearParam) {
		_monthAndYearParam = monthAndYearParam;
	}

	public void setMonthParam(String monthParam) {
		_monthParam = monthParam;
	}

	public void setMonthValue(int monthValue) {
		_monthValue = monthValue;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setNullable(boolean nullable) {
		_nullable = nullable;
	}

	public void setRequired(boolean required) {
		_required = required;
	}

	public void setShowDisableCheckbox(boolean showDisableCheckbox) {
		_showDisableCheckbox = showDisableCheckbox;
	}

	public void setUseNamespace(boolean useNamespace) {
		_useNamespace = useNamespace;
	}

	public void setYearParam(String yearParam) {
		_yearParam = yearParam;
	}

	public void setYearValue(int yearValue) {
		_yearValue = yearValue;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_autoComplete = null;
		_autoFocus = false;
		_cssClass = null;
		_dateTogglerCheckboxLabel = null;
		_dayParam = null;
		_dayValue = 0;
		_disabled = false;
		_disableNamespace = false;
		_firstDayOfWeek = Calendar.SUNDAY - 2;
		_firstEnabledDate = null;
		_formName = "fm";
		_lastEnabledDate = null;
		_monthAndYearParam = StringPool.BLANK;
		_monthParam = null;
		_monthValue = -1;
		_name = null;
		_nullable = false;
		_required = false;
		_showDisableCheckbox = true;
		_useNamespace = true;
		_yearParam = null;
		_yearValue = 0;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-ui:input-date:autoComplete", _autoComplete);
		httpServletRequest.setAttribute(
			"liferay-ui:input-date:autoFocus", String.valueOf(_autoFocus));
		httpServletRequest.setAttribute(
			"liferay-ui:input-date:cssClass", _cssClass);
		httpServletRequest.setAttribute(
			"liferay-ui:input-date:dateTogglerCheckboxLabel",
			_dateTogglerCheckboxLabel);
		httpServletRequest.setAttribute(
			"liferay-ui:input-date:dayParam", _dayParam);
		httpServletRequest.setAttribute(
			"liferay-ui:input-date:dayValue", String.valueOf(_dayValue));
		httpServletRequest.setAttribute(
			"liferay-ui:input-date:disabled", String.valueOf(_disabled));
		httpServletRequest.setAttribute(
			"liferay-ui:input-date:disableNamespace",
			String.valueOf(_disableNamespace));
		httpServletRequest.setAttribute(
			"liferay-ui:input-date:firstDayOfWeek",
			String.valueOf(_firstDayOfWeek));
		httpServletRequest.setAttribute(
			"liferay-ui:input-date:firstEnabledDate", _firstEnabledDate);
		httpServletRequest.setAttribute(
			"liferay-ui:input-date:formName", _formName);
		httpServletRequest.setAttribute(
			"liferay-ui:input-date:lastEnabledDate", _lastEnabledDate);
		httpServletRequest.setAttribute(
			"liferay-ui:input-date:monthAndYearParam", _monthAndYearParam);
		httpServletRequest.setAttribute(
			"liferay-ui:input-date:monthParam", _monthParam);
		httpServletRequest.setAttribute(
			"liferay-ui:input-date:monthValue", String.valueOf(_monthValue));
		httpServletRequest.setAttribute("liferay-ui:input-date:name", _name);
		httpServletRequest.setAttribute(
			"liferay-ui:input-date:nullable", String.valueOf(_nullable));
		httpServletRequest.setAttribute(
			"liferay-ui:input-date:required", String.valueOf(_required));
		httpServletRequest.setAttribute(
			"liferay-ui:input-date:showDisableCheckbox",
			String.valueOf(_showDisableCheckbox));
		httpServletRequest.setAttribute(
			"liferay-ui:input-date:useNamespace",
			String.valueOf(_useNamespace));
		httpServletRequest.setAttribute(
			"liferay-ui:input-date:yearParam", _yearParam);
		httpServletRequest.setAttribute(
			"liferay-ui:input-date:yearValue", String.valueOf(_yearValue));
	}

	private static final String _PAGE = "/html/taglib/ui/input_date/page.jsp";

	private String _autoComplete;
	private boolean _autoFocus;
	private String _cssClass;
	private String _dateTogglerCheckboxLabel;
	private String _dayParam;
	private int _dayValue;
	private boolean _disabled;
	private boolean _disableNamespace;
	private int _firstDayOfWeek = Calendar.SUNDAY - 2;
	private Date _firstEnabledDate;
	private String _formName = "fm";
	private Date _lastEnabledDate;
	private String _monthAndYearParam = StringPool.BLANK;
	private String _monthParam;
	private int _monthValue = -1;
	private String _name;
	private boolean _nullable;
	private boolean _required;
	private boolean _showDisableCheckbox = true;
	private boolean _useNamespace = true;
	private String _yearParam;
	private int _yearValue;

}