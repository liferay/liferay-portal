/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.web.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.seo.studio.web.internal.display.context.EditPageSpeedConfigurationDisplayContext;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;

/**
 * @author Kiana Suetani
 */
@Component(service = FragmentRenderer.class)
public class EditPageSpeedConfigurationFragmentRenderer
	extends BaseFragmentRenderer<EditPageSpeedConfigurationDisplayContext> {

	@Override
	public String getCollectionKey() {
		return "sections";
	}

	@Override
	public String getLabel(Locale locale) {
		return language.get(locale, "google-pagespeed-configuration");
	}

	@Override
	protected EditPageSpeedConfigurationDisplayContext getDisplayContext(
		HttpServletRequest httpServletRequest) {

		return new EditPageSpeedConfigurationDisplayContext(httpServletRequest);
	}

	@Override
	protected String getJSPPath() {
		return "/edit_pagespeed_configuration.jsp";
	}

}