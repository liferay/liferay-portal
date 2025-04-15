/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.web.internal.display.context;

import com.liferay.client.extension.type.CET;
import com.liferay.client.extension.type.annotation.CETProperty;
import com.liferay.client.extension.web.internal.display.context.util.CETLabelUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.CamelCaseUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropertiesUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Iván Zaera Avellón
 */
public class ViewClientExtensionEntryDisplayContext<T extends CET> {

	public ViewClientExtensionEntryDisplayContext(
		T cet, PortletRequest portletRequest) {

		_cet = cet;
		_portletRequest = portletRequest;
	}

	public T getCET() {
		return _cet;
	}

	public String getDescription() {
		return _cet.getDescription();
	}

	public String getExternalReferenceCode() {
		return _cet.getExternalReferenceCode();
	}

	public String getLabel(Method method) {
		CETProperty cetProperty = method.getAnnotation(CETProperty.class);

		String label = cetProperty.label();

		if (Validator.isBlank(label)) {
			label = CamelCaseUtil.fromCamelCase(cetProperty.name());
		}

		return LanguageUtil.get(_getHttpServletRequest(), label);
	}

	public Collection<Method> getMethods() {
		List<Method> methods = new ArrayList<>();

		Class<? extends CET> clazz = _cet.getClass();

		for (Class<?> interfaceClass : clazz.getInterfaces()) {
			if ((interfaceClass == _CLASS) ||
				!_CLASS.isAssignableFrom(interfaceClass)) {

				continue;
			}

			for (Method method : interfaceClass.getDeclaredMethods()) {
				if (method.getAnnotation(CETProperty.class) != null) {
					methods.add(method);
				}
			}
		}

		Collections.sort(
			methods, Comparator.comparing(method -> getLabel(method)));

		return methods;
	}

	public String getName() {
		ThemeDisplay themeDisplay = _getThemeDisplay();

		return _cet.getName(themeDisplay.getLocale());
	}

	public String getProperties() {
		return PropertiesUtil.toString(_cet.getProperties());
	}

	public String getRedirect() {
		return ParamUtil.getString(_portletRequest, "redirect");
	}

	public String getSourceCodeURL() {
		return _cet.getSourceCodeURL();
	}

	public String getTitle() {
		ThemeDisplay themeDisplay = _getThemeDisplay();

		return _cet.getName(themeDisplay.getLocale());
	}

	public String getType() {
		return _cet.getType();
	}

	public String getTypeLabel() {
		ThemeDisplay themeDisplay = _getThemeDisplay();

		return LanguageUtil.get(
			_getHttpServletRequest(),
			CETLabelUtil.getTypeLabel(themeDisplay.getLocale(), getType()));
	}

	public <T> T getValue(Method method) {
		try {
			return (T)method.invoke(_cet);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	public String getViewJSP() {
		return _cet.getViewJSP();
	}

	public boolean isPropertiesVisible() {
		return _cet.hasProperties();
	}

	private HttpServletRequest _getHttpServletRequest() {
		return PortalUtil.getHttpServletRequest(_portletRequest);
	}

	private ThemeDisplay _getThemeDisplay() {
		HttpServletRequest httpServletRequest = _getHttpServletRequest();

		return (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	private static final Class<CET> _CLASS = CET.class;

	private final T _cet;
	private final PortletRequest _portletRequest;

}