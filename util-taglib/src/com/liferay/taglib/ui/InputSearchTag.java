/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.ui;

import com.liferay.portal.kernel.dao.search.DisplayTerms;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.taglib.BaseValidatorTagSupport;
import com.liferay.taglib.util.TagResourceBundleUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ResourceBundle;

/**
 * @author Roberto Díaz
 */
public class InputSearchTag extends BaseValidatorTagSupport {

	public String getButtonLabel() {
		return _buttonLabel;
	}

	public String getCssClass() {
		return _cssClass;
	}

	public String getId() {
		return _id;
	}

	@Override
	public String getInputName() {
		return _name;
	}

	public String getMarkupView() {
		return null;
	}

	public String getName() {
		return _name;
	}

	public String getPlaceholder() {
		return _placeholder;
	}

	public String getTitle() {
		return _title;
	}

	public boolean isAutoFocus() {
		return _autoFocus;
	}

	public boolean isShowButton() {
		return _showButton;
	}

	public boolean isUseNamespace() {
		return _useNamespace;
	}

	public void setAutoFocus(boolean autoFocus) {
		_autoFocus = autoFocus;
	}

	public void setButtonLabel(String buttonLabel) {
		_buttonLabel = buttonLabel;
	}

	public void setCssClass(String cssClass) {
		_cssClass = cssClass;
	}

	public void setId(String id) {
		_id = id;
	}

	public void setMarkupView(String markupView) {
	}

	public void setName(String name) {
		_name = name;
	}

	public void setPlaceholder(String placeholder) {
		_placeholder = placeholder;
	}

	public void setShowButton(boolean showButton) {
		_showButton = showButton;
	}

	public void setTitle(String title) {
		_title = title;
	}

	public void setUseNamespace(boolean useNamespace) {
		_useNamespace = useNamespace;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_autoFocus = false;
		_buttonLabel = null;
		_cssClass = null;
		_id = null;
		_name = null;
		_placeholder = null;
		_showButton = true;
		_title = null;
		_useNamespace = true;
	}

	@Override
	protected String getPage() {
		return "/html/taglib/ui/input_search/page.jsp";
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		String buttonLabel = _buttonLabel;

		ResourceBundle resourceBundle = TagResourceBundleUtil.getResourceBundle(
			pageContext);

		if (Validator.isNull(buttonLabel)) {
			buttonLabel = LanguageUtil.get(resourceBundle, "search");
		}

		String cssClass = _cssClass;

		if (Validator.isNull(_name)) {
			_name = DisplayTerms.KEYWORDS;
		}

		String id = _id;

		if (Validator.isNull(id)) {
			id = _name;
		}

		String placeholder = _placeholder;

		if (Validator.isNull(placeholder)) {
			placeholder = buttonLabel;
		}

		String title = _title;

		if (title == null) {
			title = LanguageUtil.get(resourceBundle, "search");
		}

		httpServletRequest.setAttribute(
			"liferay-ui:input-search:autoFocus", String.valueOf(_autoFocus));
		httpServletRequest.setAttribute(
			"liferay-ui:input-search:buttonLabel", buttonLabel);
		httpServletRequest.setAttribute(
			"liferay-ui:input-search:cssClass", cssClass);
		httpServletRequest.setAttribute("liferay-ui:input-search:id", id);
		httpServletRequest.setAttribute("liferay-ui:input-search:name", _name);
		httpServletRequest.setAttribute(
			"liferay-ui:input-search:placeholder", placeholder);
		httpServletRequest.setAttribute(
			"liferay-ui:input-search:showButton", _showButton);
		httpServletRequest.setAttribute("liferay-ui:input-search:title", title);
		httpServletRequest.setAttribute(
			"liferay-ui:input-search:useNamespace", _useNamespace);
	}

	private boolean _autoFocus;
	private String _buttonLabel;
	private String _cssClass;
	private String _id;
	private String _name;
	private String _placeholder;
	private boolean _showButton = true;
	private String _title;
	private boolean _useNamespace = true;

}