/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.frontend.taglib.servlet.taglib;

import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.layout.admin.constants.LayoutScreenNavigationEntryConstants;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.LayoutSet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = "screen.navigation.entry.order:Integer=1",
	service = ScreenNavigationEntry.class
)
public class LayoutSetDesignScreenNavigationEntry
	implements ScreenNavigationEntry<LayoutSet> {

	@Override
	public String getCategoryKey() {
		return LayoutScreenNavigationEntryConstants.CATEGORY_KEY_DESIGN;
	}

	@Override
	public String getEntryKey() {
		return LayoutScreenNavigationEntryConstants.ENTRY_KEY_DESIGN;
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, getEntryKey());
	}

	@Override
	public String getScreenNavigationKey() {
		return LayoutScreenNavigationEntryConstants.
			SCREEN_NAVIGATION_KEY_LAYOUT_SET;
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		_jspRenderer.renderJSP(
			httpServletRequest, httpServletResponse,
			"/layout_set/screen/navigation/entries/design.jsp");
	}

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference
	private Language _language;

}