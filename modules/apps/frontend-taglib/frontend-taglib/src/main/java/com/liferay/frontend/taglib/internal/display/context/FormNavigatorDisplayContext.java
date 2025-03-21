/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.internal.display.context;

import com.liferay.frontend.taglib.form.navigator.FormNavigatorEntry;
import com.liferay.frontend.taglib.form.navigator.FormNavigatorEntryProvider;
import com.liferay.frontend.taglib.form.navigator.constants.FormNavigatorConstants;
import com.liferay.frontend.taglib.internal.servlet.ServletContextUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class FormNavigatorDisplayContext {

	public FormNavigatorDisplayContext(HttpServletRequest httpServletRequest) {
		_httpServletRequest = httpServletRequest;
	}

	public String getBackURL() {
		return (String)_httpServletRequest.getAttribute(
			"liferay-frontend:form-navigator:backURL");
	}

	public String[] getCategoryKeys() {
		return (String[])_httpServletRequest.getAttribute(
			"liferay-frontend:form-navigator:categoryKeys");
	}

	public String getFieldSetCssClass() {
		return (String)_httpServletRequest.getAttribute(
			"liferay-frontend:form-navigator:fieldSetCssClass");
	}

	public Object getFormModelBean() {
		return _httpServletRequest.getAttribute(
			"liferay-frontend:form-navigator:formModelBean");
	}

	public List<FormNavigatorEntry<Object>> getFormNavigatorEntries() {
		FormNavigatorEntryProvider formNavigatorEntryProvider =
			ServletContextUtil.getFormNavigatorEntryProvider();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return formNavigatorEntryProvider.getFormNavigatorEntries(
			getId(), themeDisplay.getUser(), getFormModelBean());
	}

	public List<FormNavigatorEntry<Object>> getFormNavigatorEntries(
		String categoryKey) {

		FormNavigatorEntryProvider formNavigatorEntryProvider =
			ServletContextUtil.getFormNavigatorEntryProvider();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return formNavigatorEntryProvider.getFormNavigatorEntries(
			getId(), categoryKey, themeDisplay.getUser(), getFormModelBean());
	}

	public String getId() {
		return (String)_httpServletRequest.getAttribute(
			"liferay-frontend:form-navigator:id");
	}

	public String getSectionId(String name) {
		return TextFormatter.format(name, TextFormatter.M);
	}

	public String getTabs1Param() {
		String randomNamespace = PortalUtil.generateRandomKey(
			_httpServletRequest, "taglib_ui_form_navigator_init");

		return randomNamespace + "_tabs1";
	}

	public FormNavigatorConstants.FormNavigatorType getType() {
		return (FormNavigatorConstants.FormNavigatorType)
			_httpServletRequest.getAttribute(
				"liferay-frontend:form-navigator:type");
	}

	public boolean isShowButtons() {
		return GetterUtil.getBoolean(
			(String)_httpServletRequest.getAttribute(
				"liferay-frontend:form-navigator:showButtons"));
	}

	private final HttpServletRequest _httpServletRequest;

}