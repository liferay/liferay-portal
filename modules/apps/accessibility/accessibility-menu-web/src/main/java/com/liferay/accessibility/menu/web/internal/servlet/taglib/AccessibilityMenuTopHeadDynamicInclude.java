/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.accessibility.menu.web.internal.servlet.taglib;

import com.liferay.accessibility.menu.web.internal.util.AccessibilitySettingsUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.servlet.taglib.BaseDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.ui.QuickAccessEntry;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Evan Thibodeau
 */
@Component(service = DynamicInclude.class)
public class AccessibilityMenuTopHeadDynamicInclude extends BaseDynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		if (!AccessibilitySettingsUtil.isAccessibilityMenuEnabled(
				httpServletRequest, _configurationProvider)) {

			return;
		}

		List<QuickAccessEntry> quickAccessEntries =
			(List<QuickAccessEntry>)httpServletRequest.getAttribute(
				WebKeys.PORTLET_QUICK_ACCESS_ENTRIES);

		if (quickAccessEntries == null) {
			quickAccessEntries = new ArrayList<>();

			httpServletRequest.setAttribute(
				WebKeys.PORTLET_QUICK_ACCESS_ENTRIES, quickAccessEntries);
		}

		QuickAccessEntry quickAccessEntry = new QuickAccessEntry();

		quickAccessEntry.setId(StringUtil.randomId());
		quickAccessEntry.setLabel(
			_language.get(httpServletRequest, "open-accessibility-menu"));
		quickAccessEntry.setOnClick("Liferay.fire('openAccessibilityMenu');");

		quickAccessEntries.add(quickAccessEntry);
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register("/html/common/themes/top_head.jsp#pre");
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Language _language;

}