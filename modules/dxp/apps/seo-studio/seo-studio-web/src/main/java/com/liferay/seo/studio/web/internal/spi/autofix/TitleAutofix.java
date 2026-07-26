/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.web.internal.spi.autofix;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.seo.studio.spi.autofix.Autofix;
import com.liferay.seo.studio.spi.autofix.BaseAutofix;

import org.osgi.service.component.annotations.Component;

/**
 * @author David Truong
 */
@Component(service = Autofix.class)
public class TitleAutofix extends BaseAutofix {

	@Override
	public String getInsightType() {
		return "missingOrEmptyTitleTag";
	}

	@Override
	protected String getPatchBody(
		JSONObject currentPageSettingsJSONObject, String languageId,
		String value) {

		return buildPatchBody(
			currentPageSettingsJSONObject, "htmlTitle_i18n", languageId, value);
	}

	@Override
	protected String[] getVerificationPaths(String languageId) {
		return new String[] {
			"JSONObject/pageSettings", "JSONObject/seoSettings",
			"JSONObject/htmlTitle_i18n", "Object/" + languageId
		};
	}

}