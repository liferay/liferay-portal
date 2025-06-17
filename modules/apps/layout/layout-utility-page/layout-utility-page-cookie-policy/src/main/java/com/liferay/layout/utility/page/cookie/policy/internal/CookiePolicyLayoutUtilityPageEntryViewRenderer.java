/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.utility.page.cookie.policy.internal;

import com.liferay.layout.utility.page.kernel.LayoutUtilityPageEntryViewRenderer;
import com.liferay.layout.utility.page.kernel.constants.LayoutUtilityPageEntryConstants;
import com.liferay.portal.kernel.language.Language;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = "utility.page.type=" + LayoutUtilityPageEntryConstants.TYPE_COOKIE_POLICY,
	service = LayoutUtilityPageEntryViewRenderer.class
)
public class CookiePolicyLayoutUtilityPageEntryViewRenderer
	implements LayoutUtilityPageEntryViewRenderer {

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "cookie-policy");
	}

	@Override
	public String getType() {
		return LayoutUtilityPageEntryConstants.TYPE_COOKIE_POLICY;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Reference
	private Language _language;

}