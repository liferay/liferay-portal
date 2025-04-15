/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.map.openstreetmap.internal;

import com.liferay.map.BaseJSPMapProvider;
import com.liferay.map.MapProvider;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(service = MapProvider.class)
public class OpenStreetMapMapProvider extends BaseJSPMapProvider {

	@Override
	public String getConfigurationJspPath() {
		return null;
	}

	@Override
	public String getHelpMessage() {
		return null;
	}

	@Override
	public String getJspPath() {
		return "/view.jsp";
	}

	@Override
	public String getKey() {
		return "OpenStreetMap";
	}

	@Override
	public String getLabel(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return _language.get(resourceBundle, "openstreetmap");
	}

	@Override
	protected ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	protected void prepareRequest(HttpServletRequest httpServletRequest) {
	}

	@Reference
	private Language _language;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.map.openstreetmap)")
	private ServletContext _servletContext;

}