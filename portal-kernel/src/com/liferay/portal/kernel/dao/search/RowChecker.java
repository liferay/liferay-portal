/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.dao.search;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyHTMLRewriterUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 */
public class RowChecker {

	public static final String ALIGN = "left";

	public static final String ALL_ROW_IDS = "allRowIds";

	public static final int COLSPAN = 1;

	public static final String CSS_CLASS = StringPool.BLANK;

	public static final String FORM_NAME = "fm";

	public static final String ROW_IDS = "rowIds";

	public static final String VALIGN = "middle";

	public RowChecker(PortletResponse portletResponse) {
		_portletResponse = portletResponse;

		_allRowIds = portletResponse.getNamespace() + ALL_ROW_IDS;
		_formName = portletResponse.getNamespace() + FORM_NAME;
		_rowIds = portletResponse.getNamespace() + ROW_IDS;
	}

	public String getAlign() {
		return _align;
	}

	public String getAllRowIds() {
		return _allRowIds;
	}

	public String getAllRowsCheckBox() {
		return getAllRowsCheckBox(null);
	}

	public String getAllRowsCheckBox(HttpServletRequest httpServletRequest) {
		return getAllRowsCheckbox(
			httpServletRequest, _allRowIds, StringUtil.quote(_rowIds));
	}

	public String getAllRowsId() {
		return getAllRowIds();
	}

	public int getColspan() {
		return _colspan;
	}

	public String getCssClass() {
		return _cssClass;
	}

	public Map<String, Object> getData(Object object) {
		return _data;
	}

	public String getFormName() {
		return _formName;
	}

	public String getRememberCheckBoxStateURLRegex() {
		return _rememberCheckBoxStateURLRegex;
	}

	public String getRowCheckBox(
		HttpServletRequest httpServletRequest, boolean checked,
		boolean disabled, String primaryKey) {

		return getRowCheckBox(
			httpServletRequest, checked, disabled, _rowIds, primaryKey,
			StringUtil.quote(_rowIds), StringUtil.quote(_allRowIds),
			StringPool.BLANK);
	}

	public String getRowCheckBox(
		HttpServletRequest httpServletRequest, ResultRow resultRow) {

		return getRowCheckBox(
			httpServletRequest, isChecked(resultRow.getObject()),
			isDisabled(resultRow.getObject()), resultRow.getPrimaryKey());
	}

	public String getRowId() {
		return getRowIds();
	}

	public String getRowIds() {
		return _rowIds;
	}

	public String getRowSelector() {
		return _rowSelector;
	}

	public String getValign() {
		return _valign;
	}

	public boolean isChecked(Object object) {
		return false;
	}

	public boolean isDisabled(Object object) {
		return false;
	}

	public boolean isRememberCheckBoxState() {
		return _rememberCheckBoxState;
	}

	public void setAlign(String align) {
		_align = align;
	}

	public void setAllRowIds(String allRowIds) {
		_allRowIds = getNamespacedValue(allRowIds);
	}

	public void setColspan(int colspan) {
		_colspan = colspan;
	}

	public void setCssClass(String cssClass) {
		_cssClass = cssClass;
	}

	public void setData(Map<String, Object> data) {
		_data = data;
	}

	public void setFormName(String formName) {
		_formName = getNamespacedValue(formName);
	}

	public void setRememberCheckBoxState(boolean rememberCheckBoxState) {
		_rememberCheckBoxState = rememberCheckBoxState;
	}

	public void setRememberCheckBoxStateURLRegex(
		String rememberCheckBoxStateURLRegex) {

		_rememberCheckBoxStateURLRegex = rememberCheckBoxStateURLRegex;
	}

	public void setRowIds(String rowIds) {
		_rowIds = getNamespacedValue(rowIds);
	}

	public void setRowSelector(String rowSelector) {
		_rowSelector = getNamespacedValue(rowSelector);
	}

	public void setValign(String valign) {
		_valign = valign;
	}

	protected String getAllRowsCheckbox(
		HttpServletRequest httpServletRequest, String name,
		String checkBoxRowIds) {

		if (Validator.isNull(name)) {
			return StringPool.BLANK;
		}

		return StringBundler.concat(
			"<label>",
			ContentSecurityPolicyHTMLRewriterUtil.rewriteInlineAttributes(
				StringBundler.concat(
					"<input name=\"", name, "\" title=\"",
					LanguageUtil.get(
						getLocale(httpServletRequest), "select-all"),
					"\" type=\"checkbox\" ", HtmlUtil.buildData(_data),
					"onClick=\"Liferay.Util.checkAll(AUI().one(this).ancestor(",
					"'.table'), ", checkBoxRowIds,
					", this, 'tr:not(.d-none)');\">"),
				httpServletRequest, false),
			"</label>");
	}

	protected Locale getLocale(HttpServletRequest httpServletRequest) {
		Locale locale = null;

		if (httpServletRequest != null) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			locale = themeDisplay.getLocale();
		}
		else {
			locale = LocaleThreadLocal.getThemeDisplayLocale();
		}

		if (locale == null) {
			locale = LocaleUtil.getDefault();
		}

		return locale;
	}

	protected String getNamespacedValue(String value) {
		if (Validator.isNull(value)) {
			return StringPool.BLANK;
		}

		if (!value.startsWith(_portletResponse.getNamespace())) {
			value = _portletResponse.getNamespace() + value;
		}

		return value;
	}

	protected String getOnClick(
		String checkBoxRowIds, String checkBoxAllRowIds,
		String checkBoxPostOnClick) {

		StringBundler sb = new StringBundler(9);

		sb.append("onClick=\"Liferay.Util.checkAllBox(");
		sb.append("AUI().one(this).ancestor('.table'),");
		sb.append(checkBoxRowIds);
		sb.append(",");
		sb.append(checkBoxAllRowIds);
		sb.append("); AUI().one(this).ancestor('tr:not(.d-none)')?.");
		sb.append("toggleClass('info');");

		if (Validator.isNotNull(checkBoxPostOnClick)) {
			sb.append(checkBoxPostOnClick);
		}

		sb.append("\"");

		return sb.toString();
	}

	protected String getRowCheckBox(
		HttpServletRequest httpServletRequest, boolean checked,
		boolean disabled, String name, String value, String checkBoxRowIds,
		String checkBoxAllRowIds, String checkBoxPostOnClick) {

		return StringBundler.concat(
			"<label>",
			_getInput(
				httpServletRequest, checked, disabled, name, value,
				checkBoxRowIds, checkBoxAllRowIds, checkBoxPostOnClick),
			"</label>");
	}

	private String _getInput(
		HttpServletRequest httpServletRequest, boolean checked,
		boolean disabled, String name, String value, String checkBoxRowIds,
		String checkBoxAllRowIds, String checkBoxPostOnClick) {

		StringBundler sb = new StringBundler(17);

		sb.append("<input ");

		String rowElementId = (String)httpServletRequest.getAttribute(
			"liferay-ui:search-container-row:rowElementId");

		if (rowElementId != null) {
			sb.append("aria-labelledby=\"");
			sb.append(rowElementId);
			sb.append("\" ");
		}

		if (checked) {
			sb.append("checked ");
		}

		sb.append("class=\"");
		sb.append(_cssClass);

		if (disabled) {
			sb.append("disabled ");
		}

		sb.append("\" name=\"");
		sb.append(name);
		sb.append("\" title=\"");
		sb.append(LanguageUtil.get(httpServletRequest.getLocale(), "select"));
		sb.append("\" type=\"checkbox\" value=\"");
		sb.append(HtmlUtil.escapeAttribute(value));
		sb.append("\" ");

		if (Validator.isNotNull(_allRowIds)) {
			sb.append(
				getOnClick(
					checkBoxRowIds, checkBoxAllRowIds, checkBoxPostOnClick));
		}

		sb.append(">");

		return ContentSecurityPolicyHTMLRewriterUtil.rewriteInlineAttributes(
			sb.toString(), httpServletRequest, false);
	}

	private String _align = ALIGN;
	private String _allRowIds;
	private int _colspan = COLSPAN;
	private String _cssClass = CSS_CLASS;
	private Map<String, Object> _data;
	private String _formName;
	private final PortletResponse _portletResponse;
	private boolean _rememberCheckBoxState = true;
	private String _rememberCheckBoxStateURLRegex;
	private String _rowIds;
	private String _rowSelector;
	private String _valign = VALIGN;

}